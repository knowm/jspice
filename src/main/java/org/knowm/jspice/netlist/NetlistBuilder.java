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
package org.knowm.jspice.netlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOPConfig;
import org.knowm.jspice.simulate.dcsweep.DCSweepConfig;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;
import org.knowm.jspice.simulate.transientanalysis.driver.Sine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class NetlistBuilder {

  private Netlist netlist;

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

  public NetlistBuilder addNetlistDCVoltage(String id, double voltage, String... nodes) {

    netlistComponents.add(new NetlistDCVoltage(id, voltage, nodes));
    return this;
  }

  public NetlistBuilder addNetlistRSMemristor(String id, double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha,
      double schottkyReverseBeta, double phi, String... nodes) {

    netlistComponents
        .add(new NetlistRSMemristor(id, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha, schottkyReverseBeta, phi, nodes));
    return this;
  }

  public NetlistBuilder addNetlistMMSSMemristor(String id, double rInit, double rOn, double rOff, double tau, double vOn, double vOff, double phi,
      double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta, String... nodes) {

    netlistComponents.add(new NetlistMMSSMemristor(id, rInit, rOn, rOff, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta,
        schottkyReverseAlpha, schottkyReverseBeta, nodes));
    return this;
  }

  public NetlistBuilder addDCOPSimulationConfig() {

    this.simulationConfig = new DCOPConfig();
    return this;
  }

  public NetlistBuilder addDCSweepSimulationConfig(String sweepID, String observeID, double startValue, double endValue, double stepSize) {

    this.simulationConfig = new DCSweepConfig(sweepID, observeID, startValue, endValue, stepSize);
    return this;
  }

  public NetlistBuilder addTransientSimulationConfig(double stopTime, double timeStep, Driver... drivers) {

    this.simulationConfig = new TransientConfig(stopTime, timeStep, drivers);
    return this;
  }

  public Netlist build() {

    netlist = new Netlist(this);

    return netlist;
  }

  public String getJSON() {

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    // create JSON
    String json = null;
    try {
      json = mapper.writeValueAsString(netlist);

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
      yaml = mapper.writeValueAsString(netlist);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return yaml;
  }

  public static Netlist buildFromSPICENetlist(List<String> netlistLines) {

    NetlistBuilder netlistBuilder = new NetlistBuilder();
    List<Driver> drivers = new ArrayList<>();
    Map<String, Double> paramsMap = new HashMap<>();
    Map<String, String> memristorsMap = new HashMap<>();
    Map<String, Map<String, String>> memristorsModelsMap = new HashMap<>();

    for (int i = 0; i < netlistLines.size(); i++) {

      String line = netlistLines.get(i);
      //      System.out.println("line " + line);

      if (line.startsWith(".PARAM") || line.startsWith(".param")) {

        String paramString = line.substring(6);
        paramString = paramString.replaceAll("\\s+", "");
        String[] paramSplit = paramString.split("=");
        paramsMap.put(paramSplit[0], fromString(paramSplit[1], 0));

      } else if (line.startsWith("V") || line.startsWith("v")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        // DC
        int dcStartIndex = line.indexOf("DC");
        tokens = line.substring(dcStartIndex).split("\\s+");
        double dc = fromString(tokens[1], 0);
        netlistBuilder.addNetlistDCVoltage(id, dc, nodeA, nodeB);

        // Sin
        int sinStartIndex = line.indexOf("SIN");
        if (sinStartIndex >= 0) {
          String sineDef = line.substring(sinStartIndex + 4, line.indexOf(")"));

          tokens = sineDef.split("\\s+");
          String v0 = ifExists(tokens, 0);
          String amplitude = ifExists(tokens, 1);
          String freq = ifExists(tokens, 2);
          String phase = ifExists(tokens, 5);
          drivers.add(new Sine(id, fromString(v0, 0), fromString(phase, 0), fromString(amplitude, 0), fromString(freq, 0)));
        }

        //        SIN(I0 IA FREQ TD THETA PHASE)
        //        SIN(V0 VA FREQ TD THETA PHASE)

      } else if (line.startsWith(".TRAN") || line.startsWith(".tran")) {

        // .tran
        String[] tokens = line.split("\\s+");
        String stepSize = tokens[1];
        String endTime = tokens[2];
        netlistBuilder.addTransientSimulationConfig(fromString(endTime, 1), fromString(stepSize, .1), drivers.toArray(new Driver[drivers.size()]));

      } else if (line.startsWith("YMEMRISTOR") || line.startsWith("ymemristor")) {

        // memristor
        String[] tokens = line.split("\\s+");
        String id = tokens[1];
        memristorsMap.put(id, line);

      } else if (line.startsWith(".MODEL") || line.startsWith(".model")) {

        // memristor
        String[] tokens = line.split("\\s+");

        String modelID = tokens[1];
        String modelLine = line.substring(line.indexOf("(") + 1, line.indexOf(")") - 1).trim();
        String[] modelTokens = modelLine.split("\\s+");

        Map<String, String> modelMap = new HashMap<>();
        for (String modelParam : modelTokens) {
          String[] keyValue = modelParam.split("=");
          modelMap.put(keyValue[0], keyValue[1]);
        }
        memristorsModelsMap.put(modelID, modelMap);
      }
    }

    // finally loop through all memristors, find their matching model and add it to the netlist
    for (Entry<String, String> entry : memristorsMap.entrySet()) {

      String memristorID = entry.getKey();
      String memristorLine = entry.getValue();
      String[] tokens = memristorLine.split("\\s+");
      //      String ID = tokens[1];
      String nodeA = tokens[2];
      String nodeB = tokens[3];
      String modelID = tokens[4];

      Map<String, String> modelMap = memristorsModelsMap.get(modelID);
      //      System.out.println("modelLine " + modelLine);

      netlistBuilder.addNetlistMMSSMemristor(memristorID, paramsMap.get("Rinit"), fromString(modelMap.get("Ron")), fromString(modelMap.get("Roff")),
          fromString(modelMap.get("Tau")), fromString(modelMap.get("Von")), fromString(modelMap.get("Voff")), 1, 0, 0, 0, 0, nodeA, nodeB);
    }

    return netlistBuilder.build();
  }

  private static String ifExists(String[] array, int index) {

    if (array.length > index) {
      return array[index];
    } else {
      return null;
    }

  }

  private static double fromString(String value) {

    // take care of units
    if (value.endsWith("M")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) + 1_000_000;
    } else {

      return Double.parseDouble(value.trim());
    }
  }

  private static double fromString(String value, double defaultValue) {

    if (value == null) {
      return defaultValue;
    }

    // take care of units
    if (value.endsWith("M")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1000;
    } else {

      return Double.parseDouble(value.trim());
    }
  }
}
