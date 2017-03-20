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
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.Source;

/**
 * @author timmolter
 */
public class I2R6 extends Circuit {

  public I2R6() {

    // define current source
    Source dcCurrentSourceA = new DCCurrent("a", 1.0);
    Source dcCurrentSourceB = new DCCurrent("b", 0.5);

    // define resistors
    Component resistor1 = new Resistor("R1", 100);
    Component resistor2 = new Resistor("R2", 1000);
    Component resistor3 = new Resistor("R3", 1000);
    Component resistor4 = new Resistor("R4", 100);
    Component resistor5 = new Resistor("R5", 1000);
    Component resistor6 = new Resistor("R6", 10000);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(dcCurrentSourceA, "0", "4");
    addNetListComponent(dcCurrentSourceB, "5", "2");
    addNetListComponent(resistor1, "5", "0");
    addNetListComponent(resistor2, "0", "3");
    addNetListComponent(resistor3, "2", "3");
    addNetListComponent(resistor4, "1", "2");
    addNetListComponent(resistor5, "3", "0");
    addNetListComponent(resistor6, "1", "4");
  }
}
