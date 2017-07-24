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

import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistCapacitor;
import org.knowm.jspice.netlist.NetlistDCCurrentArbitrary;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;

/**
 * @author timmolter
 */
public class Integrator2 extends Netlist {

  public Integrator2() {

    // State variable X
    Capacitor capacitorX = new Capacitor("Cx", 1);
    capacitorX.setInitialCondition(.4);

    addNetListComponent(new NetlistDCVoltage("V1", 1.0, "1", "0"));
    addNetListComponent(new NetlistResistor("R1", 1, "1", "0"));

    addNetListComponent(new NetlistDCCurrentArbitrary("Gx", "V(1)", "0", "x"));
    addNetListComponent(new NetlistCapacitor("Cx", 1, "x", "0"));
    addNetListComponent(new NetlistResistor("Rx", 1_000_000_000, "x", "0"));
  }
}
