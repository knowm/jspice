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
import org.knowm.jspice.netlist.NetlistResistor;

/**
 * @author timmolter
 */
public class V2R5 extends Netlist {

  public V2R5() {

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(new NetlistDCVoltage("x", 10.0, "4", "0"));
    addNetListComponent(new NetlistDCVoltage("y", 5.0, "1", "4"));
    addNetListComponent(new NetlistResistor("R5", 1000, "3", "0"));
    addNetListComponent(new NetlistResistor("R4", 100, "1", "2"));
    addNetListComponent(new NetlistResistor("R3", 1000, "2", "3"));
    addNetListComponent(new NetlistResistor("R2", 1000, "3", "0"));
    addNetListComponent(new NetlistResistor("R1", 100, "2", "0"));
  }
}
