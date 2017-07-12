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
package org.knowm.jspice.circuit.subcircuit;

import org.knowm.jspice.component.element.memristor.Memristor;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;
import org.knowm.jspice.netlist.Netlist;

/**
 * @author timmolter
 */
public class MemristorCircuitV1M1 extends Netlist {

  /**
   * Constructor
   */
  public MemristorCircuitV1M1(Memristor memristor) {

    // define voltage source
    Source dcVoltageSource = new DCVoltage("Vdd", 1.0);

    addNetListComponent(dcVoltageSource, "1", "0");
    addNetListComponent(memristor, "1", "0");
  }
}
