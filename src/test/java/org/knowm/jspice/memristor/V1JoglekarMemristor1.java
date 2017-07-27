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
package org.knowm.jspice.memristor;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistJoglekarMemristor;

public class V1JoglekarMemristor1 extends Netlist {

  private final double Rinit = 11_000;
  private final double Ron = 100;
  private final double Roff = 16_000;
  private final double D = 10e-9;
  private final double uv = 10e-15;
  private final double p = 1;

  public V1JoglekarMemristor1() {

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    addNetListComponent(new NetlistDCVoltage("Vdd", 1.0, "VDD", "0"));
    addNetListComponent(new NetlistJoglekarMemristor("M1", Rinit, Ron, Roff, D, uv, p, "VDD", "0"));
  }
}
