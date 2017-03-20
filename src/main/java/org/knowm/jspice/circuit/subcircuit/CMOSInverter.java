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

import java.util.UUID;

import org.knowm.jspice.circuit.SubCircuit;
import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.nonlinear.NMOS;
import org.knowm.jspice.component.element.nonlinear.PMOS;

/**
 * <p>
 * terminals are:
 * </p>
 * <ul>
 * <li>Vdd</li>
 * <li>0</li>
 * <li>in</li>
 * <li>out</li>
 * </ul>
 *
 * @author timmolter
 */
public class CMOSInverter extends SubCircuit {

  /**
   * Constructor
   *
   * @param Vdd
   * @param gnd
   * @param in
   * @param out
   * @param Vthreshold
   */
  public CMOSInverter(String Vdd, String gnd, String in, String out, double Vthreshold) {

    String deviceId = UUID.randomUUID().toString();

    // define components
    Component p1 = new PMOS(deviceId + "_" + "P1", Vthreshold);
    Component n1 = new NMOS(deviceId + "_" + "N1", Vthreshold);
    addNetListComponent(p1, in, out, Vdd); // G, D, S
    addNetListComponent(n1, in, out, gnd); // G, D, S
  }
}
