package org.knowm.jspice;

import java.io.IOException;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

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

    JSpice jSpice = new JSpice();
    jSpice.go(args[0]);

  }

  private void go(String fileName) throws IOException, ConfigurationException {

    // 1. Get file

    //    // jar location
    //    final URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
    //    System.out.println("location " + location);
    // 2. Parse it

    ConfigurationFactory<Netlist> yamlConfigurationFactory = new YamlConfigurationFactory<Netlist>(Netlist.class, BaseValidator.newValidator(),
        Jackson.newObjectMapper(), "");

    ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

    Netlist netList = yamlConfigurationFactory.build(provider, fileName);

    System.out.println("config: " + netList);

    // 3. Run it  
    //    Circuit circuit = new Circuit(netList);
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(netList).run();
    System.out.println(dcOpResult.toString());
  }

}
