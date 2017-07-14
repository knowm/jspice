package org.knowm.jspice;

import java.io.IOException;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.SimulationPlotter;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOPConfig;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;
import org.knowm.jspice.simulate.dcsweep.DCSweep;
import org.knowm.jspice.simulate.dcsweep.DCSweepConfig;
import org.knowm.jspice.simulate.transientanalysis.TransientAnalysis;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.BaseValidator;

public class JSpice {

  public static void main(String[] args) throws IOException, ConfigurationException {

    if (args.length == 0) {
      System.out.println("Proper Usage is: java -jar jspice <filename>");
      System.exit(0);
    }

    simulate(args[0]);

  }

  public static SimulationResult simulate(String fileName) throws IOException, ConfigurationException {

    ConfigurationFactory<Netlist> yamlConfigurationFactory = new YamlConfigurationFactory<Netlist>(Netlist.class, BaseValidator.newValidator(),
        Jackson.newObjectMapper(), "");

    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    Netlist netlist = yamlConfigurationFactory.build(provider, fileName);

    System.out.println("netList: \n" + netlist);

    //    DCOperatingPointResult dcOpResult = new DCOperatingPoint(netlist).run();
    //    System.out.println(dcOpResult.toString());

    // 3. Run it  
    return simulate(netlist);

  }

  public static SimulationResult simulate(Netlist netlist) {

    SimulationConfig simulationConfig = netlist.getSimulationConfig();

    if (simulationConfig == null || simulationConfig instanceof DCOPConfig) {

      DCOperatingPointResult dcOpResult = new DCOperatingPoint(netlist).run();
      System.out.println(dcOpResult.toString());
      return null;

    } else if (simulationConfig instanceof DCSweepConfig) {

      DCSweepConfig dcSweepConfig = (DCSweepConfig) simulationConfig;

      // run DC sweep
      DCSweep dcSweep = new DCSweep(netlist);
      dcSweep.addSweepConfig(dcSweepConfig);
      SimulationResult simulationResult = dcSweep.run(dcSweepConfig.getObserveID());
      System.out.println(simulationResult.toString());
      SimulationPlotter.plot(simulationResult, new String[]{dcSweepConfig.getObserveID()});
      return simulationResult;
    }

    else if (simulationConfig instanceof TransientConfig) {

      TransientConfig simulationConfigTransient = (TransientConfig) simulationConfig;

      // run TransientAnalysis
      TransientAnalysis transientAnalysis = new TransientAnalysis(netlist, simulationConfigTransient);
      SimulationResult simulationResult = transientAnalysis.run();
      System.out.println(simulationResult.toString());
      // plot
      SimulationPlotter.plotAll(simulationResult);
      return simulationResult;

    } else {
      return null;
    }

  }

}
