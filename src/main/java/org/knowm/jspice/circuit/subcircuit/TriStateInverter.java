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
import org.knowm.jspice.component.element.reactive.Capacitor;

/**
 * <p>
 * terminals are:
 * </p>
 * <ul>
 * <li>Vdd</li>
 * <li>0</li>
 * <li>in</li>
 * <li>out</li>
 * <li>clk</li>
 * <li>clkBar</li>
 * </ul>
 *
 * @author timmolter
 */
public class TriStateInverter extends SubCircuit {

  /**
   * Constructor
   *
   * @param Vdd
   * @param gnd
   * @param in
   * @param out
   * @param clk
   * @param clkBar
   * @param Vthreshold
   */
  public TriStateInverter(String Vdd, String gnd, String in, String out, String clk, String clkBar, double Vthreshold) {

    String deviceId = UUID.randomUUID().toString();

    Component p1 = new PMOS(deviceId + "_" + "P1", Vthreshold);
    Component p2 = new PMOS(deviceId + "_" + "P2", Vthreshold + .01);
    Component n1 = new NMOS(deviceId + "_" + "N1", Vthreshold + .01);
    // Component p2 = new PMOS(deviceId + "_" + "P2", Vthreshold);
    // Component n1 = new NMOS(deviceId + "_" + "N1", Vthreshold);
    Component n2 = new NMOS(deviceId + "_" + "N2", Vthreshold);

    Component c0 = new Capacitor("C0", 1E-16);
    Component c1 = new Capacitor("C1", 1E-16);

    String uniqueNodeIdA = deviceId + "_" + "a";
    String uniqueNodeIdB = deviceId + "_" + "b";

    addNetListComponent(p1, in, uniqueNodeIdA, Vdd); // G, D, S
    addNetListComponent(p2, clkBar, out, uniqueNodeIdA); // G, D, S
    addNetListComponent(n1, clk, out, uniqueNodeIdB); // G, D, S
    addNetListComponent(n2, in, uniqueNodeIdB, gnd); // G, D, S

    addNetListComponent(c0, uniqueNodeIdA, gnd);
    addNetListComponent(c1, uniqueNodeIdB, gnd);
  }
}
