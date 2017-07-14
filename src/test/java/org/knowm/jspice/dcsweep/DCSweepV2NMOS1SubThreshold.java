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
package org.knowm.jspice.dcsweep;

import org.knowm.jspice.circuits.V2NMOS1SubThreshold;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotter;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcsweep.DCSweep;
import org.knowm.jspice.simulate.dcsweep.DCSweepConfig;

public class DCSweepV2NMOS1SubThreshold {

  public static void main(String[] args) {

    // Circuit
    Netlist circuit = new V2NMOS1SubThreshold();

    // SweepDef
    DCSweepConfig sweepDef1 = new DCSweepConfig("Vgs", "I(NMOS1)", 2.3, 2.6, .001);
    DCSweepConfig sweepDef2 = new DCSweepConfig("Vds", "I(NMOS1)", 0.001, .16, 0.04);

    // run DC sweep
    DCSweep dcSweep = new DCSweep(circuit);
    dcSweep.addSweepConfig(sweepDef1);
    dcSweep.addSweepConfig(sweepDef2);
    SimulationResult dcSweepResult = dcSweep.run("I(NMOS1)");
    System.out.println(dcSweepResult.toString());

    // plot
    SimulationPlotter.plotAll(dcSweepResult);
  }
}
