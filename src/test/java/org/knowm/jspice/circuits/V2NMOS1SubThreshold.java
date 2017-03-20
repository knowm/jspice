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
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;

/**
 * @author timmolter
 */
public class V2NMOS1SubThreshold extends Circuit {

  public V2NMOS1SubThreshold() {

    // define voltage source
    Source dcVoltageSourceDS = new DCVoltage("Vds", 2);
    Source dcVoltageSourceGS = new DCVoltage("Vgs", 1.0);

    // define components
    Component nmos1 = new NMOS("NMOS1", 2.5);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(dcVoltageSourceDS, "1", "0");
    addNetListComponent(dcVoltageSourceGS, "2", "0");
    addNetListComponent(nmos1, "2", "1", "0");
  }
}
