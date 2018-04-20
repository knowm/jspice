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
package org.knowm.jspice.transientanalysis;

import java.io.IOException;

import org.knowm.configuration.ConfigurationException;
import org.knowm.jspice.JSpice;
import org.knowm.jspice.memristor.V1RSMemristor1;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotter;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Sine;

public class TransientAnalysisRSMem {

  private final static double schottkeyAlpha = 0; // N/A
  private final static double schottkeyBeta = 0; // N/A
  private final static double phi = 1;

  public static void main(String[] args) throws IOException, ConfigurationException {

    Netlist netlist = new V1RSMemristor1();
    TransientConfig transientConfig = new TransientConfig("5.0E-3", "1E-5", new Sine("Vdd", 0.0, "0", 1.2, "2000.0"));
    netlist.setSimulationConfig(transientConfig);
    SimulationResult simulationResult = JSpice.simulate(netlist);
    SimulationPlotter.plot(simulationResult, "I(M1)");

    // run via NetlistBuilder
    //    NetlistBuilder builder = new NetlistBuilder().addNetlistDCVoltage("Vdd", 1.0, "VDD", "0")
    //        .addNetlistRSMemristor("M1", schottkeyAlpha, schottkeyBeta, schottkeyAlpha, schottkeyBeta, phi, "VDD", "0")
    //        .addTransientSimulationConfig(1.0E-3, 1E-5, new Sine("Vdd", 0.0, 0, 1.2, 2000.0));
    //    Netlist netlist = builder.build();
    //    System.out.println("builder.getYAML() " + builder.getYAML());
    //    SimulationResult simulationResult = JSpice.simulate(netlist);
    //    SimulationPlotter.plot(simulationResult, "I(M1)");

    //    // run via Yml file
    //    SimulationResult simulationResult = JSpice.simulate("RSMem.yml");
    //    SimulationPlotter.plot(simulationResult, "I(M1)");

    // run via jar
    //     java -jar jspice.jar RSMem.yml

  }
}
