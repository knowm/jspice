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
package org.knowm.jspice.simulate.dcoperatingpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.element.memristor.Memristor;
import org.knowm.jspice.component.element.nonlinear.Diode;
import org.knowm.jspice.component.element.nonlinear.MOSFET;
import org.knowm.jspice.component.element.nonlinear.NMOS;
import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.component.element.reactive.Inductor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCCurrentArbitrary;
import org.knowm.jspice.component.source.VCCS;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistComponent;

/**
 * @author timmolter
 */
public final class DCOperatingPointResult {

  private final String[] unknownQuantityNames; // decorated i.e. V(1)
  private final double[][] G;
  private final double[] unknownQuantities;
  private final double[] RHS;

  private final Map<String, Double> nodeLabels2Value; // decorated i.e. V(1)

  private Map<String, Double> deviceLabels2Value;

  /**
   * Constructor
   *
   * @param unknownQuantities
   * @param G
   * @param unknownQuantities
   * @param RHS
   */
  public DCOperatingPointResult(String[] unknownQuantityNames, double[][] G, double[] unknownQuantities, double[] RHS) {

    this.unknownQuantityNames = unknownQuantityNames;
    // System.out.println(Arrays.toString(unknownQuantityNames));
    this.G = G;
    this.unknownQuantities = unknownQuantities;
    // System.out.println(Arrays.toString(unknownQuantities));
    this.RHS = RHS;

    nodeLabels2Value = new TreeMap<>();
    for (int j = 0; j < unknownQuantities.length; j++) {
      nodeLabels2Value.put(unknownQuantityNames[j], unknownQuantities[j]);
    }
  }

  public String[] getNodeLabels() {

    return unknownQuantityNames;
  }

  public Map<String, Double> getNodeLabels2Value() {

    return nodeLabels2Value;
  }

  public Map<String, Double> getDeviceLabels2Value() {

    return deviceLabels2Value;
  }

  public double[][] getG() {

    return G;
  }

  public double[] getV() {

    return unknownQuantities;
  }

  public double[] getRHS() {

    return RHS;
  }

  /**
   * Get numerical results for both Nodes in G and device values
   *
   * @param nodeOrDeviceLabel
   * @return
   */
  public Double getValue(String nodeOrDeviceLabel) {

    // System.out.println("nodeOrDeviceLabel= " + nodeOrDeviceLabel);

    if (nodeOrDeviceLabel.equals("V(0)")) { // ground
      return 0.0;
    } else {
      if (nodeLabels2Value.get(nodeOrDeviceLabel) != null) {
        return nodeLabels2Value.get(nodeOrDeviceLabel);
      } else if (deviceLabels2Value.get(nodeOrDeviceLabel) != null) {
        return deviceLabels2Value.get(nodeOrDeviceLabel);
      } else {
        Set<String> possibleValues = new HashSet<>();
        possibleValues.addAll(nodeLabels2Value.keySet());
        possibleValues.addAll(deviceLabels2Value.keySet());
        throw new RuntimeException("Node or device with label: " + nodeOrDeviceLabel + " contains no value! Possible choices are: " + possibleValues);
      }
    }
  }

  public String getNodalAnalysisMatrix() {

    return "DCOperatingPointResult: [nodeLabels=" + Arrays.toString(unknownQuantityNames) + ", G=" + GtoString(G) + ", v="
        + Arrays.toString(unknownQuantities) + ", i=" + Arrays.toString(RHS) + "]";
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append(System.getProperty("line.separator"));
    sb.append("----nodes----");
    sb.append(System.getProperty("line.separator"));

    // for (int i = 0; i < unknownVariableNames.length; i++) {
    //
    // sb.append(unknownVariableNames[i] + " = " + v[i]);
    // sb.append(System.getProperty("line.separator"));
    // }

    for (Entry<String, Double> entry : nodeLabels2Value.entrySet()) {
      sb.append(entry.getKey() + " = " + entry.getValue());
      sb.append(System.getProperty("line.separator"));
    }

    sb.append("----components----");
    sb.append(System.getProperty("line.separator"));

    for (Entry<String, Double> entry : deviceLabels2Value.entrySet()) {
      sb.append(entry.getKey() + " = " + entry.getValue());
      sb.append(System.getProperty("line.separator"));
    }
    sb.append("-------------");

    return sb.toString();
  }

  private String GtoString(double[][] G) {

    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (double[] row : G) {
      sb.append("[");
      for (double j : row) {
        sb.append(j + " ");
      }
      sb.append("]");
    }
    sb.append("]");
    return sb.toString();
  }

  public void generateDeviceCurrents(Netlist netlist) {

    //    System.out.println("HERE");

    deviceLabels2Value = new TreeMap<>();

    // DC Current Sources
    for (NetlistComponent netListComponent : netlist.getNetListDCCurrentSources()) {

      DCCurrent dcCurrent = (DCCurrent) netListComponent.getComponent();

      double current = dcCurrent.getSweepableValue();
      deviceLabels2Value.put("I(" + dcCurrent.getId() + ")", current);
    }
    // DC Current Source Arbitrary
    for (NetlistComponent netListComponent : netlist.getNetListDCCurrentArbitrarys()) {

      DCCurrentArbitrary dcCurrent = (DCCurrentArbitrary) netListComponent.getComponent();
      double current = dcCurrent.getSweepableValue();
      deviceLabels2Value.put("I(" + dcCurrent.getId() + ")", current);
    }

    // resistors
    for (NetlistComponent netListComponent : netlist.getNetListResistors()) {

      Resistor resistor = (Resistor) netListComponent.getComponent();
      double voltageA = getValue("V(" + netListComponent.getNodesAsArray()[0] + ")");
      double voltageB = getValue("V(" + netListComponent.getNodesAsArray()[1] + ")");
      double voltageDiff = voltageA - voltageB;
      double current = resistor.getCurrent(voltageDiff);
      deviceLabels2Value.put("I(" + resistor.getId() + ")", current);
    }

    // diodes
    for (NetlistComponent netListComponent : netlist.getNetListDiodes()) {

      Diode diode = (Diode) netListComponent.getComponent();
      double voltageA = getValue("V(" + netListComponent.getNodesAsArray()[0] + ")");
      double voltageB = getValue("V(" + netListComponent.getNodesAsArray()[1] + ")");
      double voltageDiff = voltageA - voltageB;
      double current = diode.getCurrent(voltageDiff);
      deviceLabels2Value.put("I(" + diode.getId() + ")", current);
    }

    // MOSFETs
    for (NetlistComponent netListComponent : netlist.getNetListMOSFETs()) {

      MOSFET mosfet = (MOSFET) netListComponent.getComponent();
      double Vg = getValue("V(" + netListComponent.getNodesAsArray()[0] + ")"); // gate
      double Vd = getValue("V(" + netListComponent.getNodesAsArray()[1] + ")"); // drain
      double Vs = getValue("V(" + netListComponent.getNodesAsArray()[2] + ")"); // source
      double Vgs = Vg - Vs;
      double Vds = Vd - Vs;

      //      System.out.println("Vgs= " + Vgs);
      //      System.out.println("Vds= " + Vds);

      double current;
      if (mosfet instanceof NMOS) {
        current = mosfet.getCurrent(Vgs, Vds);
      } else {
        current = -1 * mosfet.getCurrent(-1.0 * Vgs, -1.0 * Vds);

      }
      // System.out.println("current= " + current);
      //      if (mosfet instanceof NMOS) {
      //        System.out.println("NMOS mode= " + mosfet.getOperationMode(Vgs, Vds));
      //      } else {
      //        System.out.println("PMOS mode= " + mosfet.getOperationMode(-1.0 * Vgs, -1.0 * Vds));
      //      }

      deviceLabels2Value.put("I(" + mosfet.getId() + ")", current);
    }

    // capacitors
    for (NetlistComponent netListComponent : netlist.getNetListCapacitors()) {

      Capacitor capacitor = (Capacitor) netListComponent.getComponent();
      deviceLabels2Value.put("I(" + capacitor.getId() + ")", nodeLabels2Value.get("I(" + capacitor.getId() + ")"));
    }

    // inductors
    for (NetlistComponent netListComponent : netlist.getNetListInductors()) {

      Inductor inductor = (Inductor) netListComponent.getComponent();
      if (nodeLabels2Value.get("I(" + inductor.getId() + ")") == null) {
        double voltageA = getValue("V(" + netListComponent.getNodesAsArray()[0] + ")");
        double voltageB = getValue("V(" + netListComponent.getNodesAsArray()[1] + ")");
        double voltageDiff = voltageA - voltageB;
        double current = voltageDiff / Inductor.INDUCTOR_DC_RESISTANCE;
        // System.out.println("voltageDiff = " + voltageDiff);
        // System.out.println("current = " + current);
        deviceLabels2Value.put("I(" + inductor.getId() + ")", current);
      } else {
        deviceLabels2Value.put("I(" + inductor.getId() + ")", nodeLabels2Value.get("I(" + inductor.getId() + ")"));
        // System.out.println("current = " + nodeLabels2Value.get("I(" + inductor.getID() + ")"));
      }
    }

    // memristors
    for (NetlistComponent netListComponent : netlist.getNetListMemristors()) {

      Memristor memristor = (Memristor) netListComponent.getComponent();
      double voltageA = getValue("V(" + netListComponent.getNodesAsArray()[0] + ")");
      double voltageB = getValue("V(" + netListComponent.getNodesAsArray()[1] + ")");
      double voltageDiff = voltageA - voltageB;
      double current = memristor.getCurrent(voltageDiff);
      deviceLabels2Value.put("I(" + memristor.getId() + ")", current);
      deviceLabels2Value.put("R(" + memristor.getId() + ")", memristor.getResistance());
    }

    // VCCSs
    for (NetlistComponent netListComponent : netlist.getNetListVCCSs()) {

      VCCS vCCS = (VCCS) netListComponent.getComponent();
      double voltageA = getValue("V(" + netListComponent.getNodesAsArray()[2] + ")");
      double voltageB = getValue("V(" + netListComponent.getNodesAsArray()[3] + ")");
      double voltageDiff = voltageA - voltageB;
      double current = vCCS.getCurrent(voltageDiff);
      deviceLabels2Value.put("I(" + vCCS.getId() + ")", current);
    }

    // // VCVSs
    // for (NetlistComponent netListComponent : netlist.getNetListVCVSs()) {
    //
    // VCVS vCVS = (VCVS) netListComponent.getComponent();
    // deviceLabels2Value.put("I(" + vCVS.getID() + ")", current);
    // }
    // }

    // invalidate all calculated results as these would be bogus anyway when in initial conditions are given
    if (netlist.isInitialConditions()) {
      for (String key : deviceLabels2Value.keySet()) {
        // deviceLabels2Value.put(key, null);
        deviceLabels2Value.put(key, 0.01);
      }
    }
  }
}
