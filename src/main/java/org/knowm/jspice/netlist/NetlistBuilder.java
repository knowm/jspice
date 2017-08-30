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
import java.util.List;

import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOPConfig;
import org.knowm.jspice.simulate.dcsweep.DCSweepConfig;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;

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

  String sourceFile;
  String resultsFile;
  String resultsFormat;

  public NetlistBuilder addNetlistResistor(String id, double resistance, String... nodes) {

    netlistComponents.add(new NetlistResistor(id, resistance, nodes));
    return this;
  }

  public NetlistBuilder addNetlistCapacitor(String id, double capacitance, String... nodes) {

    netlistComponents.add(new NetlistCapacitor(id, capacitance, nodes));
    return this;
  }

  public NetlistBuilder addNetlistInductor(String id, double inductance, String... nodes) {

    netlistComponents.add(new NetlistCapacitor(id, inductance, nodes));
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

  public NetlistBuilder addNetlistMSSMemristor(String id, double rInit, double rOn, double rOff, double n, double tau, double vOn, double vOff, double
      phi,
      double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta, String... nodes) {

    netlistComponents.add(new NetlistMSSMemristor(id, rInit, rOn, rOff, n, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta,
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

  public NetlistBuilder addTransientSimulationConfig(String stopTime, String timeStep, Driver... drivers) {

    this.simulationConfig = new TransientConfig(stopTime, timeStep, drivers);
    return this;
  }

  public NetlistBuilder setSourceFile(String sourceFile) {
    this.sourceFile = sourceFile;
    return this;
  }

  public NetlistBuilder setResultsFile(String resultsFile) {
    this.resultsFile = resultsFile;
    return this;
  }

  public NetlistBuilder setResultsFormat(String resultsFormat) {
    this.resultsFormat = resultsFormat;
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

}
