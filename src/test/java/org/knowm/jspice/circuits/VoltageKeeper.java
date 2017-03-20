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

import org.knowm.jspice.circuit.Circuit;
import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.nonlinear.NMOS;
import org.knowm.jspice.component.element.nonlinear.PMOS;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;

/**
 * @author timmolter
 */
public class VoltageKeeper extends Circuit {

  public VoltageKeeper() {

    // define voltage source
    Source vDD = new DCVoltage("VDD", 5.0);
    Source vIn = new DCVoltage("Vin", 0.0);
    // Source vIn = new DCVoltage("Vin", 2.5);

    // define components
    Component p1 = new PMOS("P1", 2.5);
    Component n1 = new NMOS("N1", 2.5);
    Component p2 = new PMOS("P2", 2.5);
    Component n2 = new NMOS("N2", 2.5);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(vDD, "Vdd", "0");
    addNetListComponent(vIn, "in", "0");
    addNetListComponent(p1, "in", "out", "Vdd"); // G, D, S
    addNetListComponent(n1, "in", "out", "0"); // G, D, S
    addNetListComponent(p2, "out", "in", "Vdd"); // G, D, S
    addNetListComponent(n2, "out", "in", "0"); // G, D, S
  }
}
