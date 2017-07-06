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

import org.knowm.jspice.circuit.Circuit;
import org.knowm.jspice.circuits.I1V1R6;
import org.knowm.jspice.simulate.SimulationPlotter;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcsweep.DCSweep;
import org.knowm.jspice.simulate.dcsweep.SweepDefinition;

/**
 * @author timmolter
 */
public class DCSweepI1V1R6 {

  public static void main(String[] args) {

    // Circuit
    Circuit circuit = new I1V1R6();

    // SweepDef
    String componentToSweepID = "x";
    double startValue = 0.0;
    double endValue = 10.0;
    double stepSize = 1.0;
    SweepDefinition sweepDef = new SweepDefinition(componentToSweepID, startValue, endValue, stepSize);

    // run DC sweep
    DCSweep dcSweep = new DCSweep(circuit);
    dcSweep.addSweepDef(sweepDef);
    SimulationResult dcSweepResult = dcSweep.run();
    System.out.println(dcSweepResult.toString());

    // plot
    SimulationPlotter.plotAll("DC Sweep", dcSweepResult);
  }
}
