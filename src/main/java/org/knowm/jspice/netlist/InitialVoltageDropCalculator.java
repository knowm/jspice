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
package org.knowm.jspice.netlist;

import java.util.List;

/**
 * This class is used to help set Vgs on MOSFETs in a circuit in order to initialize the transistors better than random guessing.
 *
 * @author timmolter
 */
// TODO add a method for diodes
public class InitialVoltageDropCalculator {

  /**
   * @param netlist
   * @param gateNode
   * @param sourceNode
   * @param defaultValue
   * @param isNMOS
   * @return
   */
  public static double attemptToDetermineVgs(Netlist netlist, String gateNode, String sourceNode, double defaultValue, boolean isNMOS) {

    //    System.out.println("isNMOS " + isNMOS);
    //    System.out.println("gate= " + gateNode);
    //    System.out.println("source= " + sourceNode);

    double initialVoltage = defaultValue;

    Double Vg = null;
    Double Vs = null;

    // check for directly connected voltage sources
    List<NetlistComponent> dcVoltageSources = netlist.getNetListDCVoltageSources();
    for (NetlistComponent dcVoltageSource : dcVoltageSources) {
      //      System.out.println(dcVoltageSource.getNodes()[0]);
      //      System.out.println(dcVoltageSource.getNodes()[1]);
      if (dcVoltageSource.getNodesAsArray()[0].equals(gateNode)) {
        Vg = dcVoltageSource.getComponent().getSweepableValue();
        //        System.out.println("Vg " + Vg);

      }
      if (dcVoltageSource.getNodesAsArray()[0].equals(sourceNode)) {
        Vs = dcVoltageSource.getComponent().getSweepableValue();
        //        System.out.println("Vs " + Vs);
      }
    }

    // check for connections to ground, then v=0
    if (gateNode.equals("0")) {
      Vg = 0.0;
    }
    if (sourceNode.equals("0")) {
      Vs = 0.0;
    }
    //    System.out.println("Vg " + Vg);
    //    System.out.println("Vs " + Vs);

    if (Vg != null && Vs != null) { // could determine Vgs!
      initialVoltage = Vg - Vs;
      //      System.out.println("initialVoltage " + initialVoltage);
    } else {
      if (Vg != null && Vg == 0.0 && isNMOS) { // infer that if PMOS, and Vg = 0, then it was intended to be off
        initialVoltage = (0.9 * initialVoltage);
      } else if (Vg != null && Vg == 0.0 && !isNMOS) { // infer that if PMOS, and Vg = 0, then it was intended to be on
        initialVoltage = (1.1 * initialVoltage);
      }
      // System.out.println("could not determine, therefore inferring!");
    }

    return initialVoltage;
  }
}
