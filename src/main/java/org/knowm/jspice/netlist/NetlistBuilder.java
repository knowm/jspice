package org.knowm.jspice.netlist;

import java.util.ArrayList;
import java.util.List;

import org.knowm.jspice.NetlistDCCurrent;
import org.knowm.jspice.NetlistResistor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class NetlistBuilder {

  NetList netList;

  public List<NetlistComponent> components = new ArrayList<>();

  public NetlistBuilder addNetlistResistor(String id, double resistance, String[] nodes) {

    components.add(new NetlistResistor(id, resistance, nodes));
    return this;
  }

  public NetlistBuilder addNetlistDCCurrent(String id, double current, String[] nodes) {

    components.add(new NetlistDCCurrent(id, current, nodes));
    return this;
  }

  public NetList build() {

    netList = new NetList(this);

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
