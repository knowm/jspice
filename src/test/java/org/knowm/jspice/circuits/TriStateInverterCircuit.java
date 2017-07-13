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

import org.knowm.jspice.circuit.subcircuit.TriStateInverter;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistDCVoltage;

/**
 * @author timmolter
 */
public class TriStateInverterCircuit extends Netlist {

  public TriStateInverterCircuit() {

    // define voltage source
    Source vDD = new DCVoltage("VDD", 5.0);
    Source vIn = new DCVoltage("Vin", 0.3);
    Source vCLK = new DCVoltage("Vclk", 5.0);
    Source vCLKBar = new DCVoltage("VclkBar", 0.0);

    addNetListComponent(new NetlistDCVoltage("VDD", 5.0, "Vdd", "0"));
    addNetListComponent(new NetlistDCVoltage("Vin", 0.3, "in", "0"));
    addNetListComponent(new NetlistDCVoltage("Vclk", 5.0, "CLK", "0"));
    addNetListComponent(new NetlistDCVoltage("VclkBar", 0.0, "CLKBAR", "0"));

    addSubCircuit(new TriStateInverter("Vdd", "0", "in", "out", "CLK", "CLKBAR", 2.5));
  }
}
