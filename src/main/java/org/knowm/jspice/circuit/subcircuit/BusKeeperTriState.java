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
package org.knowm.jspice.circuit.subcircuit;

import java.util.UUID;

import org.knowm.jspice.circuit.SubCircuit;
import org.knowm.jspice.netlist.NetlistNMOS;
import org.knowm.jspice.netlist.NetlistPMOS;

public class BusKeeperTriState extends SubCircuit {

  public BusKeeperTriState(String Vdd, String gnd, String in, String out, String clk, String clkBar, double Vthreshold) {

    String deviceId = UUID.randomUUID().toString();

    String uniqueNodeIdA = deviceId + "_" + "a";
    String uniqueNodeIdB = deviceId + "_" + "b";
    String uniqueNodeIdC = deviceId + "_" + "c";
    String uniqueNodeIdD = deviceId + "_" + "d";

    addNetListComponent(new NetlistPMOS(deviceId + "_" + "P1", Vthreshold, in, uniqueNodeIdA, Vdd)); // G, D, S
    addNetListComponent(new NetlistPMOS(deviceId + "_" + "P2", Vthreshold + .02, clkBar, out, uniqueNodeIdA)); // G, D, S
    addNetListComponent(new NetlistNMOS(deviceId + "_" + "N1", Vthreshold + .02, clk, out, uniqueNodeIdB)); // G, D, S
    addNetListComponent(new NetlistNMOS(deviceId + "_" + "N2", Vthreshold)); // G, D, S

    addNetListComponent(new NetlistPMOS(deviceId + "_" + "P3", Vthreshold, out, uniqueNodeIdC, Vdd)); // G, D, S
    addNetListComponent(new NetlistPMOS(deviceId + "_" + "P4", Vthreshold + .02, clkBar, in, uniqueNodeIdC)); // G, D, S
    addNetListComponent(new NetlistNMOS(deviceId + "_" + "N3", Vthreshold + .02, clk, in, uniqueNodeIdD)); // G, D, S
    addNetListComponent(new NetlistNMOS(deviceId + "_" + "N4", Vthreshold, out, uniqueNodeIdD, gnd)); // G, D, S
  }
}
