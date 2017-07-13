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
import org.knowm.jspice.netlist.NetlistCapacitor;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;

public class V1R8C8 extends Netlist {

  public V1R8C8() {

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(new NetlistDCVoltage("V1", 10.0, "1", "0"));
    addNetListComponent(new NetlistResistor("R1", 2000, "1", "2"));
    addNetListComponent(new NetlistResistor("R2", 2000, "2", "3"));
    addNetListComponent(new NetlistResistor("R3", 2000, "3", "4"));
    addNetListComponent(new NetlistResistor("R4", 2000, "4", "5"));
    addNetListComponent(new NetlistResistor("R5", 2000, "5", "6"));
    addNetListComponent(new NetlistResistor("R6", 2000, "6", "7"));
    addNetListComponent(new NetlistResistor("R7", 2000, "7", "8"));
    addNetListComponent(new NetlistResistor("R8", 2000, "8", "9"));

    addNetListComponent(new NetlistCapacitor("C1", 2E-9, "2", "0"));
    addNetListComponent(new NetlistCapacitor("C2", 2E-9, "3", "0"));
    addNetListComponent(new NetlistCapacitor("C3", 2E-9, "4", "0"));
    addNetListComponent(new NetlistCapacitor("C4", 2E-9, "5", "0"));
    addNetListComponent(new NetlistCapacitor("C5", 2E-9, "6", "0"));
    addNetListComponent(new NetlistCapacitor("C6", 2E-9, "7", "0"));
    addNetListComponent(new NetlistCapacitor("C7", 2E-9, "8", "0"));
    addNetListComponent(new NetlistCapacitor("C8", 2E-9, "9", "0"));
  }
}
