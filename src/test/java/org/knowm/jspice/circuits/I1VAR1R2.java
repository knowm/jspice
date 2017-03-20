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
import org.knowm.jspice.component.source.DCVoltageArbitrary;
import org.knowm.jspice.component.source.Source;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public class I1VAR1R2 extends Circuit {

  public I1VAR1R2() {

    // define voltage source
    Source dcVoltageSourceX = new DCCurrent("x", 2.0);
    Source dcVoltageSourceArbitrary = new DCVoltageArbitrary("y") {

      public double getArbitraryVoltage(DCOperatingPointResult dcOperatingPointResult) {

        return dcOperatingPointResult.getValue("V(1)") * dcOperatingPointResult.getValue("V(1)");
        // return dcOperatingPointResult.getValue("I(R1)") * dcOperatingPointResult.getValue("I(R1)");
      }
    };

    // define resistors
    Component resistor1 = new Resistor("R1", 1);
    Component resistor2 = new Resistor("R2", 1);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(dcVoltageSourceX, "1", "0");
    addNetListComponent(resistor1, "1", "0");
    addNetListComponent(dcVoltageSourceArbitrary, "2", "0");
    addNetListComponent(resistor2, "2", "0");
  }
}
