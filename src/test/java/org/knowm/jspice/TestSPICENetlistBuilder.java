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
import static org.assertj.core.api.Assertions.within;

import java.io.IOException;

import org.junit.Test;
import org.knowm.configuration.provider.ResourceConfigurationSourceProvider;
import org.knowm.jspice.component.element.memristor.MMSSMemristor;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.spice.SPICENetlistBuilder;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;

public class TestSPICENetlistBuilder {

  @Test
  public void test1() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("knowm_mem1_sine.cir", new ResourceConfigurationSourceProvider());

    //    System.out.println("netlist " + netlist);

    // DC voltage sources
    assertThat(netlist.getNetListDCVoltageSources()).hasSize(2);

    // transient
    assertThat(netlist.getSimulationConfig()).isInstanceOf(TransientConfig.class);
    TransientConfig config = (TransientConfig) netlist.getSimulationConfig();
    assertThat(config.getStopTime()).isEqualTo("0.5");
    assertThat(config.getTimeStep()).isEqualTo("0.0049505");
    assertThat(config.getDrivers()[0].getAmplitude()).isEqualTo(0.5);
    assertThat(config.getDrivers()[0].getFrequency()).isEqualTo("10");

    // memristor
    assertThat(netlist.getNetListMemristors()).hasSize(1);
    MMSSMemristor memristor = (MMSSMemristor) netlist.getNetListMemristors().get(0).getComponent();
    assertThat(memristor.getTau()).isEqualTo(0.0001);
  }

  @Test
  public void test2() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("knowm_mem1_sine_model.cir", new ResourceConfigurationSourceProvider());

    //    System.out.println("netlist " + netlist);

    // DC voltage sources
    assertThat(netlist.getNetListDCVoltageSources()).hasSize(2);

    // transient
    assertThat(netlist.getSimulationConfig()).isInstanceOf(TransientConfig.class);
    TransientConfig config = (TransientConfig) netlist.getSimulationConfig();
    assertThat(config.getStopTime()).isEqualTo("0.5");
    assertThat(config.getTimeStep()).isEqualTo("0.0049505");
    assertThat(config.getDrivers()[0].getAmplitude()).isEqualTo(0.5);
    assertThat(config.getDrivers()[0].getFrequency()).isEqualTo("10");

    // memristor
    assertThat(netlist.getNetListMemristors()).hasSize(1);
    MMSSMemristor memristor = (MMSSMemristor) netlist.getNetListMemristors().get(0).getComponent();
    assertThat(memristor.getTau()).isEqualTo(0.0001);
  }

  @Test
  public void test3() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("knowm_mem1R1_pulse_netlist.cir", new ResourceConfigurationSourceProvider());

    System.out.println("netlist " + netlist);

    // DC voltage sources
    assertThat(netlist.getNetListDCVoltageSources()).hasSize(2);

    // Resistors
    assertThat(netlist.getNetListResistors()).hasSize(1);

    // transient
    assertThat(netlist.getSimulationConfig()).isInstanceOf(TransientConfig.class);
    TransientConfig config = (TransientConfig) netlist.getSimulationConfig();
    assertThat(config.getStopTime()).isEqualTo("50us");
    assertThat(config.getTimeStep()).isEqualTo("1us");
    assertThat(config.getDrivers()[0].getAmplitude()).isEqualTo(1);
    assertThat(config.getDrivers()[0].getDcOffset()).isEqualTo(1);

    // memristor
    assertThat(netlist.getNetListMemristors()).hasSize(1);
    MMSSMemristor memristor = (MMSSMemristor) netlist.getNetListMemristors().get(0).getComponent();
    assertThat(memristor.getTau()).isEqualTo(0.0001);

    //Print
    assertThat(netlist.getResultsFormat()).isEqualTo("raw");
  }

  @Test
  public void test4() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("knowm_mem1R1_neg_pulse_netlist.cir", new ResourceConfigurationSourceProvider());

    System.out.println("netlist " + netlist);

    // DC voltage sources
    assertThat(netlist.getNetListDCVoltageSources()).hasSize(2);

    // Resistors
    assertThat(netlist.getNetListResistors()).hasSize(1);

    // transient
    assertThat(netlist.getSimulationConfig()).isInstanceOf(TransientConfig.class);
    TransientConfig config = (TransientConfig) netlist.getSimulationConfig();
    assertThat(config.getStopTime()).isEqualTo("50us");
    assertThat(config.getTimeStep()).isEqualTo("1us");
    assertThat(config.getDrivers()[0].getAmplitude()).isEqualTo(.5);
    assertThat(config.getDrivers()[0].getDcOffset()).isEqualTo(-.5);
    assertThat(config.getDrivers()[0].getPhaseBD().doubleValue()).isCloseTo(0.00002, within(0.000000000000001));

    assertThat(netlist.getNetListMemristors()).hasSize(1);
    MMSSMemristor memristor = (MMSSMemristor) netlist.getNetListMemristors().get(0).getComponent();
    assertThat(memristor.getTau()).isEqualTo(0.0001);

    //Print
    assertThat(netlist.getResultsFormat()).isEqualTo("raw");
  }

  @Test
  public void test5() throws IOException {

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

    JSpice.simulate(netlist);
  }

  @Test
  public void test6() throws IOException {

    Netlist netlist = SPICENetlistBuilder.buildFromSPICENetlist("knowm_mem1R1_pulse_netlist_2us.cir", new ResourceConfigurationSourceProvider());

    JSpice.simulate(netlist);
  }

}
