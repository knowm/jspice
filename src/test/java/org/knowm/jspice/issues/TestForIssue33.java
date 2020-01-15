package org.knowm.jspice.issues;

import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.NetlistBuilder;

public class TestForIssue33 {

  public static void main(String[] args) {

    NetlistBuilder generated = new NetlistBuilder()
        .addNetlistResistor("g1", 0, "c1", "0")
        .addNetlistResistor("g2", 0, "c2", "0")
        .addNetlistResistor("r1", 10, "c2", "c3")
        .addNetlistDCVoltage("b", 9.0, "c3", "c1")//<-- specifically this component id
        .addDCOPSimulationConfig();
    System.out.println(generated.build());
    JSpice.simulate(generated.build());

  }
}
