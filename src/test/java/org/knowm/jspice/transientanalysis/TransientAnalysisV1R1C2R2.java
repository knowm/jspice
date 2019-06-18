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

import org.knowm.jspice.JSpice;
import org.knowm.jspice.circuits.V1R1C1;
import org.knowm.jspice.circuits.V1R1C1R2;
import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistCapacitor;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;
import org.knowm.jspice.simulate.SimulationPlotter;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Square;

public class TransientAnalysisV1R1C2R2 {

  public static void main(String[] args) {

    long t = System.currentTimeMillis();
    Netlist netlist = new V1R1C1R2();
    TransientConfig transientConfig = new TransientConfig(".000025", ".000002", new Square("V1", .035, "0", .035, "20000"));
    netlist.setSimulationConfig(transientConfig);

    long t1 = System.currentTimeMillis();
    SimulationResult simulationResult = JSpice.simulate(netlist);
    long t2 = System.currentTimeMillis();

    SimulationPlotter.plot(simulationResult, new String[]{"V(1)", "V(2)"});
    long t3 = System.currentTimeMillis();

    System.out.println("netListCreation = " + (t1 - t));
    System.out.println("simulation = " + (t2 - t1));
    System.out.println("plot = " + (t3 - t2));

  }

}
