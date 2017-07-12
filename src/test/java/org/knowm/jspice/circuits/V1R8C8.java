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

import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;
import org.knowm.jspice.netlist.Netlist;

/**
 * @author timmolter
 */
public class V1R8C8 extends Netlist {

  /**
   * Constructor
   */
  public V1R8C8() {

    // define voltage source
    Source dcVoltageSource = new DCVoltage("V1", 10.0);

    // define resistor
    Component resistor1 = new Resistor("R1", 2000);
    Component resistor2 = new Resistor("R2", 2000);
    Component resistor3 = new Resistor("R3", 2000);
    Component resistor4 = new Resistor("R4", 2000);
    Component resistor5 = new Resistor("R5", 2000);
    Component resistor6 = new Resistor("R6", 2000);
    Component resistor7 = new Resistor("R7", 2000);
    Component resistor8 = new Resistor("R8", 2000);

    // define capacitor
    Component capacitor1 = new Capacitor("C1", 2E-9);
    Component capacitor2 = new Capacitor("C2", 2E-9);
    Component capacitor3 = new Capacitor("C3", 2E-9);
    Component capacitor4 = new Capacitor("C4", 2E-9);
    Component capacitor5 = new Capacitor("C5", 2E-9);
    Component capacitor6 = new Capacitor("C6", 2E-9);
    Component capacitor7 = new Capacitor("C7", 2E-9);
    Component capacitor8 = new Capacitor("C8", 2E-9);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(dcVoltageSource, "1", "0");
    addNetListComponent(resistor1, "1", "2");
    addNetListComponent(resistor2, "2", "3");
    addNetListComponent(resistor3, "3", "4");
    addNetListComponent(resistor4, "4", "5");
    addNetListComponent(resistor5, "5", "6");
    addNetListComponent(resistor6, "6", "7");
    addNetListComponent(resistor7, "7", "8");
    addNetListComponent(resistor8, "8", "9");

    addNetListComponent(capacitor1, "2", "0");
    addNetListComponent(capacitor2, "3", "0");
    addNetListComponent(capacitor3, "4", "0");
    addNetListComponent(capacitor4, "5", "0");
    addNetListComponent(capacitor5, "6", "0");
    addNetListComponent(capacitor6, "7", "0");
    addNetListComponent(capacitor7, "8", "0");
    addNetListComponent(capacitor8, "9", "0");
  }
}
