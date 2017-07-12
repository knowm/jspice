package org.knowm.jspice.netlist;

import java.util.ArrayList;
import java.util.List;

import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.dcoperatingpoint.SimulationConfigDCOP;
import org.knowm.jspice.simulate.dcsweep.SimulationConfigDCSweep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class NetlistBuilder {

  private Netlist netList;

  List<NetlistComponent> netlistComponents = new ArrayList<>();

  SimulationConfig simulationConfig;

  public NetlistBuilder addNetlistResistor(String id, double resistance, String... nodes) {

    netlistComponents.add(new NetlistResistor(id, resistance, nodes));
    return this;
  }

  public NetlistBuilder addNetlistDCCurrent(String id, double current, String... nodes) {

    netlistComponents.add(new NetlistDCCurrent(id, current, nodes));
    return this;
  }

  public NetlistBuilder addDCOPSimulationConfig() {

    this.simulationConfig = new SimulationConfigDCOP();
    return this;
  }

  public NetlistBuilder addDCSweepSimulationConfig(String sweepID, String observeID, double startValue, double endValue, double stepSize) {

    this.simulationConfig = new SimulationConfigDCSweep(sweepID, observeID, startValue, endValue, stepSize);
    return this;
  }

  public Netlist build() {

    netList = new Netlist(this);

    return netList;
  }

  public String getJSON() {

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    // create JSON
    String json = null;
    try {
      json = mapper.writeValueAsString(netList);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return json;
  }

  public String getYAML() {

    YAMLFactory yf = new YAMLFactory();
    yf.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
    yf.enable(Feature.MINIMIZE_QUOTES);

    ObjectMapper mapper = new ObjectMapper(yf);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    // create YAML
    String yaml = null;
    try {
      yaml = mapper.writeValueAsString(netList);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return yaml;
  }
}
