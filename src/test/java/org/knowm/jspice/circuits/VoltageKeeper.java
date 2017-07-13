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
package org.knowm.jspice.circuits;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistNMOS;
import org.knowm.jspice.netlist.NetlistPMOS;

public class VoltageKeeper extends Netlist {

  public VoltageKeeper() {

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(new NetlistDCVoltage("VDD", 5.0, "Vdd", "0"));
    addNetListComponent(new NetlistDCVoltage("Vin", 0.0, "in", "0"));
    addNetListComponent(new NetlistPMOS("P1", 2.5, "in", "out", "Vdd")); // G, D, S
    addNetListComponent(new NetlistNMOS("N1", 2.5, "in", "out", "0")); // G, D, S
    addNetListComponent(new NetlistPMOS("P2", 2.5, "out", "in", "Vdd")); // G, D, S
    addNetListComponent(new NetlistNMOS("N2", 2.5, "out", "in", "0")); // G, D, S
  }
}
