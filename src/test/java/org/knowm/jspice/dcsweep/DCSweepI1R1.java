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

import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistBuilder;

/**
 * @author timmolter
 */
public class DCSweepI1R1 {

  public static void main(String[] args) {

    //    // Circuit
    //    Netlist circuit = new I1R1();
    //
    //    // SweepDef
    //    String componentToSweepID = "a";
    //    double startValue = 0.0;
    //    double endValue = 10.0;
    //    double stepSize = 1.0;
    //    SweepDefinition sweepDef = new SweepDefinition(componentToSweepID, startValue, endValue, stepSize);
    //
    //    // run DC sweep
    //    DCSweep dcSweep = new DCSweep(circuit);
    //    dcSweep.addSweepDef(sweepDef);
    //    SimulationResult dcSweepResult = dcSweep.run("I(R1)");
    //    System.out.println(dcSweepResult.toString());
    //
    //    // plot
    //    SimulationPlotter.plot(dcSweepResult, new String[]{"I(R1)"});

    // run via NetlistBuilder
    NetlistBuilder builder = new NetlistBuilder().addNetlistDCCurrent("a", 1.0, "1", "0").addNetlistResistor("R1", 1000, "0", "1")
        .addDCSweepSimulationConfig("a", "I(R1)", 0.0, 10.0, 1.0);
    Netlist netlist = builder.build();
    System.out.println("builder.getYAML() " + builder.getYAML());
    JSpice.simulate(netlist);
  }
}
