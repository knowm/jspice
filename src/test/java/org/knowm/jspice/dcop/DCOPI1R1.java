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
package org.knowm.jspice.dcop;

import java.io.IOException;

import org.knowm.configuration.ConfigurationException;
import org.knowm.jspice.JSpice;

/**
 * @author timmolter
 */
public class DCOPI1R1 {

  public static void main(String[] args) throws IOException, ConfigurationException {

    //    // run DC operating point
    //    Netlist netlist = new I1R1();
    //    netlist.setSimulationConfig(new SimulationConfigDCOP());
    //    JSpice.simulate(netlist);

    //    // run via NetlistBuilder
    //    NetlistBuilder builder = new NetlistBuilder().addNetlistDCCurrent("a", 1.0, "1", "0").addNetlistResistor("R1", 1000, "0", "1")
    //        .addDCOPSimulationConfig();
    //    Netlist netlist = builder.build();
    //    System.out.println("builder.getYAML() " + builder.getYAML());
    //    JSpice.simulate(netlist);

    // run via Yml file
    JSpice.simulate("dcopi1r1.yml");

    // run via jar
    // java -jar jspice.jar dcopi1r1.yml

  }

}
