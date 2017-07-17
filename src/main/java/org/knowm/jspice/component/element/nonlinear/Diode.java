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
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistComponent;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public class Diode extends Component implements NonlinearComponent {

  /**
   * saturation current
   */
  private double IS;

  /**
   * Constructor
   *
   * @param id
   * @param saturationCurrent
   */
  public Diode(String id, double saturationCurrent) {

    super(id);
    this.IS = saturationCurrent;
  }

  @Override
  public void setSweepValue(double value) {

    IS = value;
  }

  @Override
  public double getSweepableValue() {

    return IS;
  }

  public double getEquivalentCurrent(double voltageGuess) {

    // double VdoGuess = 0.87;

    // System.out.println("IS= " + IS);
    double Ido = getCurrent(voltageGuess);
    // System.out.println("Ido= " + Ido);
    double equivalentG = Ido / Component.VT;
    // System.out.println("equivalentG= " + equivalentG);
    // double equivalentR = Component.VT / Ido;
    double equivalentI = Ido - equivalentG * voltageGuess;

    // System.out.println("equivalentI= " + equivalentI);
    return equivalentI;
  }

  public double getEquivalentResistance(double voltageGuess) {

    // double VdoGuess = 0.9;
    double Ido = getCurrent(voltageGuess);
    // double equivalentG = Ido / Component.VT;
    double equivalentR = Component.VT / Ido;
    // double equivalentI = Ido - equivalentG * voltageGuess;

    // System.out.println("equivalentR= " + equivalentR);
    return equivalentR;
  }

  /**
   * Get the current thru this diode
   *
   * @return
   */
  public double getCurrent(double voltage) {

    return IS * (Math.exp(voltage / Component.VT) - 1);
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

    // current source
    // no contribution

    // resistor
    double VdGuess = getVdGuess(dcOperatingPointResult, nodes);

    Resistor Req = new Resistor(getId() + "_Req", getEquivalentResistance(VdGuess));
    NetlistComponent req = new NetlistComponent(Req, nodes);
    req.stampG(G, netList, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes,
      Double timeStep) {

    // current source
    double VdGuess = getVdGuess(dcOperatingPointResult, nodes);
    DCCurrent Ieq = new DCCurrent((getId() + "_Ieq"), getEquivalentCurrent(VdGuess));
    NetlistComponent ieq = new NetlistComponent(Ieq, nodes);
    ieq.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);

    // resistor
    // no contribution
  }

  private double getVdGuess(DCOperatingPointResult dcOperatingPointResult, String[] nodes) {

    double VdGuess = 0.0; // Vd = voltage across diode
    if (dcOperatingPointResult == null) { // first iteration
      VdGuess = getInitialVoltageGuess();
    } else {
      double voltageA = dcOperatingPointResult.getValue("V(" + nodes[0] + ")");
      double voltageB = dcOperatingPointResult.getValue("V(" + nodes[1] + ")");
      VdGuess = voltageA - voltageB;
    }
    return VdGuess;
  }

  private double getInitialVoltageGuess() {

    return 0.9;
  }
}
