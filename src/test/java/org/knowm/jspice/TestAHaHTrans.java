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
package org.knowm.jspice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.knowm.jspice.component.element.memristor.MMSSMemristor;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.spice.SPICENetlistBuilder;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;

import io.dropwizard.configuration.ResourceConfigurationSourceProvider;

public class TestAHaHTrans {

  @Test
  public void test4() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("ahah2-1_pulse_netlist.cir", new ResourceConfigurationSourceProvider());

    System.out.println("netlist " + netlist);

    // DC voltage sources
    assertThat(netlist.getNetListDCVoltageSources()).hasSize(2);

    // Resistors
    assertThat(netlist.getNetListResistors()).hasSize(1);

    // transient
    assertThat(netlist.getSimulationConfig()).isInstanceOf(TransientConfig.class);
    TransientConfig config = (TransientConfig) netlist.getSimulationConfig();
    assertThat(config.getStopTime()).isEqualTo("0.15");
    assertThat(config.getTimeStep()).isEqualTo("0.00148515");
    assertThat(config.getDrivers()[0].getAmplitude()).isEqualTo(.5);
    assertThat(config.getDrivers()[0].getDcOffset()).isEqualTo(.5);

    // memristor
    assertThat(netlist.getNetListMemristors()).hasSize(2);
    MMSSMemristor memristor = (MMSSMemristor) netlist.getNetListMemristors().get(0).getComponent();
    assertThat(memristor.getTau()).isEqualTo(0.0001);
    System.out.println("netlist " + netlist);

    JSpice.simulate(netlist);
  }

}
