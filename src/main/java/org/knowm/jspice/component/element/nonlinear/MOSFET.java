/**
 * jspice is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the jspice
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of jspice which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.jspice.component.element.nonlinear;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.NonlinearComponent;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.netlist.InitialVoltageDropCalculator;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistComponent;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public abstract class MOSFET extends Component implements NonlinearComponent {

  public enum Mode {
    CUTOFF, TRIODE, SATURATION
  }

  protected double Vthresh;
  protected final double lambda = 0.0125; // 1/early voltage, lower lambda, greater saturation mode slope
  protected final double K = 0.17; // mu*Cox*W/L [A/V^2]
  //  protected final double K = 5; // mu*Cox*W/L [A/V^2], A.K.A. beta
  private final double n = 1.45; // typical values are between 1.4 and 1.5

  private double[] VgsVdsGuess = null;

  /**
   * Constructor
   *
   * @param id
   */
  protected MOSFET(String id, double Vthresh) {

    super(id);
    this.Vthresh = Vthresh;
    if (Vthresh < 0.0) {
      throw new IllegalArgumentException("Threshold voltage must be positive!");
    }
  }

  @Override
  public void setSweepValue(double value) {

    Vthresh = value;
  }

  @Override
  public double getSweepableValue() {

    return Vthresh;
  }

  public double getGmVgsCurrent(double vgsGuess, double vdsGuess) {

    //    System.out.println("vgsGuess= " + vgsGuess);
    //    System.out.println("vdsGuess= " + vdsGuess);

    // determine operating region
    Mode mode = getOperationMode(vgsGuess, vdsGuess);

    if (mode == Mode.CUTOFF) { // cutoff
      // double Ids = getIdsCutoff(vgsGuess, vdsGuess);
      // System.out.println("Ids= " + Ids);
      // double gm = getGmCutoff(Ids);
      // System.out.println("gm= " + gm);
      // return gm * vgsGuess;
      return 0.000000001;
    } else if (mode == Mode.SATURATION) { // saturation
      double Ids = getIdsSaturation(vgsGuess, vdsGuess);
      double gm = getGmSaturation(Ids);
      return gm * vgsGuess;
    } else { // triode
      double gm = getGmTriode(vdsGuess);
      return gm * vgsGuess;
    }
  }

  public double getRo(double vgsGuess, double vdsGuess) {

    // determine operating region
    Mode mode = getOperationMode(vgsGuess, vdsGuess);

    if (mode == Mode.CUTOFF) { // cutoff

      // return 1 / getGoCutoff(vgsGuess, vdsGuess);
      return 1000000000000.0; // 10^12 Ohm
    } else if (mode == Mode.SATURATION) { // saturation
      double Ids = getIdsSaturation(vgsGuess, vdsGuess);
      return 1 / getGoSaturation(Ids);
    } else { // triode
      return 1 / getGoTriode(vgsGuess, vdsGuess);
    }
  }

  public double getEquivalentCurrent(double vgsGuess, double vdsGuess) {

    // determine operating region
    Mode mode = getOperationMode(vgsGuess, vdsGuess);

    if (mode == Mode.CUTOFF) { // cutoff

      // double Ids = getIdsCutoff(vgsGuess, vdsGuess);
      // double gm = getGmCutoff(Ids);
      // double go = getGoCutoff(vgsGuess, vdsGuess);
      // return Ids - gm * vgsGuess - go * vdsGuess;
      return 0.000000001;
    } else if (mode == Mode.SATURATION) { // saturation
      double Ids = getIdsSaturation(vgsGuess, vdsGuess);
      double gm = getGmSaturation(Ids);
      double go = getGoSaturation(Ids);
      return Ids - gm * vgsGuess - go * vdsGuess;
    } else { // triode
      double Ids = getIdsTriode(vgsGuess, vdsGuess);
      double gm = getGmTriode(vdsGuess);
      double go = getGoTriode(vgsGuess, vdsGuess);
      return Ids - gm * vgsGuess - go * vdsGuess;
    }
  }

  /**
   * Get the current thru this MOSFET
   *
   * @return
   */
  public double getCurrent(double Vgs, double Vds) {

    // determine operating region
    Mode mode = getOperationMode(Vgs, Vds);

    if (mode == Mode.CUTOFF) { // cutoff
      // System.out.println("cutoff");
      // return getIdsCutoff(Vgs, Vds);
      return 0.000000001;
    } else if (mode == Mode.SATURATION) { // saturation
      // System.out.println("saturation");
      return getIdsSaturation(Vgs, Vds);
    } else { // triode
      // System.out.println("triode");
      return getIdsTriode(Vgs, Vds);
    }
  }

  public Mode getOperationMode(double vgs, double vds) {

    //    System.out.println("vgs " + vgs);
    //    System.out.println("vds " + vds);

    // determine operating region
    if (vgs - .00000000000005 <= Vthresh) { // cutoff, .00000000000005 for bug due to double imprecision
      //      System.out.println("cutoff");
      return Mode.CUTOFF;
    } else if (vds <= (vgs - Vthresh)) { // triode
      //      System.out.println("triode");
      return Mode.TRIODE;
    } else if (vds >= (vgs - Vthresh)) { // saturation
      //      System.out.println("saturation");
      return Mode.SATURATION;
    } else {
      // return Mode.CUTOFF;
      return Mode.TRIODE;
      // return Mode.SATURATION;
      // throw new RuntimeException("Strange condition occurred in determining NMOS operation mode! Vgs= " + vgs + ", Vds=  " + vds);
    }
  }

  private double getGmCutoff(double Ids) {

    return Ids / (n * Component.VT);
  }

  private double getGmSaturation(double Ids) {

    // double Gm = Math.sqrt(Math.abs(2 * K * Ids));
    double Gm = Math.sqrt(2 * K * Ids);
    return Gm;
  }

  private double getGmTriode(double vdsGuess) {

    // if (vdsGuess < 0) {
    // return 0.0;
    // }

    // double Gm = Math.abs(K * vdsGuess);
    double Gm = K * vdsGuess;
    return Gm;
  }

  private double getGoCutoff(double vgsGuess, double vdsGuess) {

    // System.out.println("vgsGuess= " + vgsGuess);
    // System.out.println("vdsGuess= " + vdsGuess);
    double Ids0Part = K * Component.VT * Component.VT * Math.exp(1.8);
    // double Ids0Part = getIdsSaturation(vgsGuess, vgsGuess - Vthresh);
    // System.out.println("Ids0Part= " + Ids0Part);

    double IgsPart = Math.exp((vgsGuess - Vthresh) / (n * Component.VT));
    // System.out.println("IgsPart= " + IgsPart);

    if (vdsGuess < 0) {
      vdsGuess = 0;
    }
    double Go = Ids0Part * IgsPart / Component.VT * Math.exp(-1.0 * vdsGuess / Component.VT);
    // System.out.println("Go= " + Go);
    return Go;
  }

  private double getGoSaturation(double Ids) {

    // double Go = Math.abs(lambda * Ids);
    double Go = lambda * Ids;
    return Go;
  }

  private double getGoTriode(double vgsGuess, double vdsGuess) {

    double Go = K * ((vgsGuess - Vthresh) - vdsGuess);
    return Go;
  }

  private double getIdsCutoff(double vgsGuess, double vdsGuess) {

    double Ids0Part = K * Component.VT * Component.VT * Math.exp(1.8);
    // double Ids0Part = getIdsSaturation(vgsGuess, vgsGuess - Vthresh);
    // System.out.println(Ids0Part);
    double IgsPart = Math.exp((vgsGuess - Vthresh) / (n * Component.VT));
    double IdsPart = 1 - Math.exp(-1.0 * vdsGuess / Component.VT);
    double Ids = Ids0Part * IgsPart * IdsPart;
    return Ids;
  }

  private double getIdsSaturation(double vgsGuess, double vdsGuess) {

    //    System.out.println("vgsGuess " + vgsGuess);
    //    System.out.println("vdsGuess " + vdsGuess);

    double Ids = (K / 2) * (vgsGuess - Vthresh) * (vgsGuess - Vthresh) * (1 + (vdsGuess - (vgsGuess - Vthresh)) * lambda);
    return Ids;
  }

  private double getIdsTriode(double vgsGuess, double vdsGuess) {

    //    System.out.println("vgsGuess " + vgsGuess);
    //    System.out.println("vdsGuess " + vdsGuess);

    double Ids = K * ((vgsGuess - Vthresh) - vdsGuess / 2) * vdsGuess;
    return Ids;
  }

  @Override
  public Set<String> getGMatrixColumnIDs(String[] nodes, Double timeStep) {

    // current source
    // no contribution

    // resistor
    Set<String> set = new HashSet<>(2);
    set.add(nodes[0]);
    set.add(nodes[1]);

    return set;
  }

  @Override
  public void modifyUnknowmQuantitiesVector(String[] columnQuantities, String[] nodes, Double timeStep) {

    // current source
    // no contribution

    // resistor
    for (int i = 0; i < columnQuantities.length; i++) {
      if (columnQuantities[i].equals(nodes[0]) || columnQuantities[i].equals(nodes[1])) {
        columnQuantities[i] = "V(" + columnQuantities[i] + ")";
      }
    }
  }

  @Override
  public void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      String[] nodes, Double timeStep) {

    //    System.out.println("stampG");
    // current source
    // no contribution

    //    System.out.println("null = " + (dcOperatingPointResult == null));

    // resistor
    this.VgsVdsGuess = getVgsVdsGuess(netList, dcOperatingPointResult, nodes);
    double VgsGuess = VgsVdsGuess[0];
    double VdsGuess = VgsVdsGuess[1];

    //    System.out.println("VgsGuess= " + VgsGuess);
    //    System.out.println("VdsGuess= " + VdsGuess);

    // Ro
    NetlistComponent req;
    if (this instanceof NMOS) {
      Resistor resistance = new Resistor((getId() + "_Ro"), getRo(VgsGuess, VdsGuess));
      //      System.out.println("NMOS Res.= " + resistance);
      req = new NetlistComponent(resistance, nodes[1], nodes[2]);
    } else {
      Resistor resistance = new Resistor((getId() + "_Ro"), getRo(-1.0 * VgsGuess, -1.0 * VdsGuess));
      req = new NetlistComponent(resistance, nodes[2], nodes[1]);
      //      System.out.println("PMOS Res.= " + resistance);
    }

    req.stampG(G, netList, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes,
      Double timeStep) {

    // current sources

    double VgsGuess = VgsVdsGuess[0];
    double VdsGuess = VgsVdsGuess[1];

    // GmVgs
    NetlistComponent GmVgs;
    if (this instanceof NMOS) {
      DCCurrent dcCurrent = new DCCurrent((getId() + "_GmVgs"), getGmVgsCurrent(VgsGuess, VdsGuess));
      GmVgs = new NetlistComponent(dcCurrent, nodes[1], nodes[2]);
    } else {
      DCCurrent dcCurrent = new DCCurrent((getId() + "_GmVgs"), getGmVgsCurrent(-1.0 * VgsGuess, -1.0 * VdsGuess));
      GmVgs = new NetlistComponent(dcCurrent, nodes[2], nodes[1]);
    }
    GmVgs.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);

    // Id,Eq
    NetlistComponent Ideq;
    if (this instanceof NMOS) {
      DCCurrent dcCurrent = new DCCurrent((getId() + "_Id,eq"), getEquivalentCurrent(VgsGuess, VdsGuess));
      Ideq = new NetlistComponent(dcCurrent, nodes[1], nodes[2]);
    } else {
      DCCurrent dcCurrent = new DCCurrent((getId() + "_Id,eq"), getEquivalentCurrent(-1.0 * VgsGuess, -1.0 * VdsGuess));
      Ideq = new NetlistComponent(dcCurrent, nodes[2], nodes[1]);
      //      System.out.println(dcCurrent.toString());
    }
    Ideq.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);

    // resistor
    // no contribution
  }

  private double[] getVgsVdsGuess(Netlist netList, DCOperatingPointResult dcOperatingPointResult, String[] nodes) {

    double VgsGuess = 0.0;
    double VdsGuess = 0.0;

    if (dcOperatingPointResult == null) { // first iteration

      // double defaultVgsGuess = mosfet.getSweepableValue() + getRandomKick(1); // threshold voltage
      double defaultVgsGuess = getSweepableValue(); // threshold voltage

      // Vgs Guess
      VgsGuess = InitialVoltageDropCalculator.attemptToDetermineVgs(netList, nodes[0], nodes[2], defaultVgsGuess, this instanceof NMOS);
      //      System.out.println("initialVgs(" + this.getId() + ")= " + VgsGuess);

      // Vds Guess (if Vgs turns MOSFET on, set voltage drop Vds to zero)
      VdsGuess = defaultVgsGuess;
      if (this instanceof PMOS) {
        if (VgsGuess <= defaultVgsGuess) { // PMOS on, Vds=0
          VdsGuess = 0.0;
        }
      } else if (this instanceof NMOS) {
        if (VgsGuess >= defaultVgsGuess) { // NMOS on, Vds=0
          VdsGuess = 0.0;
        }
      }
      // System.out.println("initialVds(" + mosfet.getID() + ")= " + VdsGuess);

      //      System.out.println("vgsGuess= " + VgsGuess);
      //      System.out.println("vdsGuess= " + VdsGuess);

    } else {

      double Vg; // gate
      double Vd; // drain
      double Vs; // source
      Vg = dcOperatingPointResult.getValue("V(" + nodes[0] + ")"); // gate
      Vd = dcOperatingPointResult.getValue("V(" + nodes[1] + ")"); // drain
      Vs = dcOperatingPointResult.getValue("V(" + nodes[2] + ")"); // source

      VgsGuess = Vg - Vs;
      VdsGuess = Vd - Vs;
      //      System.out.println("vgsGuess= " + VgsGuess);
      //      System.out.println("Vd= " + Vd);
      //      System.out.println("Vs= " + Vs);
      //      System.out.println("vdsGuess= " + VdsGuess);

      // System.out.println("Vgs(" + mosfet.getID() + ")= " + VgsGuess);
      // System.out.println("Vds(" + mosfet.getID() + ")= " + VdsGuess);
      // System.out.println("mode(" + mosfet.getID() + ")= " + mosfet.getOperationMode(mosfet instanceof PMOS ? -1.0 * VgsGuess : VgsGuess,
      // VdsGuess));

    }

    return new double[]{VgsGuess, VdsGuess};
  }
}
