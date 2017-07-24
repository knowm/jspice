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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.knowm.jspice.component.Component;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;
import org.knowm.konfig.Konfigurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = NetlistResistor.class, name = "resistor"), @Type(value = NetlistDCCurrent.class, name = "dc_current"),
                  @Type(value = NetlistDCVoltage.class, name = "dc_voltage"), @Type(value = NetlistCapacitor.class, name = "capacitor"),
                  @Type(value = NetlistInductor.class, name = "inductor"), @Type(value = NetlistDiode.class, name = "diode"),
                  @Type(value = NetlistNMOS.class, name = "nmos"), @Type(value = NetlistPMOS.class, name = "pmos"), @Type(value = NetlistVCCS.class, name = "vccs"),
                  @Type(value = NetlistVCVS.class, name = "vcvs"), @Type(value = NetlistRSMemristor.class, name = "rs_mem"),
                  @Type(value = NetlistMSSMemristor.class, name = "mss_mem"), @Type(value = NetlistMMSSMemristor.class, name = "mmss_mem"),
                  @Type(value = NetlistJoglekarMemristor.class, name = "jog_mem")})
@JsonPropertyOrder({"nodes"})
public class NetlistComponent implements Konfigurable {

  @JsonIgnore
  private Component component;

  @Valid
  @NotNull
  @JsonProperty("id")
  String id;

  @Valid
  @NotNull
  String nodes;

  @JsonIgnore
  String[] nodesAsArray;

  /**
   * Constructor
   */
  public NetlistComponent() {

  }

  /**
   * Constructor
   *
   * @param component
   * @param nodesAsArray
   */
  public NetlistComponent(Component component, String... nodesAsArray) {

    this.component = component;
    this.id = component.getId();
    this.nodesAsArray = nodesAsArray;
    this.nodes = StringUtils.join(nodesAsArray, ",");
  }

  /**
   * Constructor
   *
   * @param component
   * @param nodes
   */
  public NetlistComponent(Component component, String nodes) {

    this.component = component;
    this.id = component.getId();
    this.nodesAsArray = nodes.split(",");
    this.nodes = nodes;
  }

  public Set<String> getGMatrixColumnIDs(Double timeStep) {

    return component.getGMatrixColumnIDs(nodesAsArray, timeStep);
  }

  public void modifyUnknownQuantitiesVector(String[] nodeIDs, Double timeStep) {

    component.modifyUnknowmQuantitiesVector(nodeIDs, nodesAsArray, timeStep);
  }

  public void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      Double timeStep) {

    component.stampG(G, netList, dcOperatingPointResult, nodeID2ColumnIdxMap, nodesAsArray, timeStep);
  }

  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, Double timeStep) {

    component.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, nodesAsArray, timeStep);
  }

  public Component getComponent() {

    return component;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonIgnore
  public String[] getNodesAsArray() {
    return nodesAsArray;
  }

  @JsonProperty("nodes")
  public String getNodes() {
    return nodes;
  }

  @JsonIgnore
  public void setNodes(String[] nodesAsArray) {
    this.nodesAsArray = nodesAsArray;
    this.nodes = StringUtils.join(nodesAsArray, ",");
  }

  @JsonProperty("nodes")
  public void setNodes(String nodesCSV) {
    this.nodesAsArray = nodes.split(",");
    this.nodes = nodesCSV;
  }

  public String toSpiceString() {

    StringBuilder sb = new StringBuilder();
    sb.append(component.getId().toLowerCase() + " ");
    for (String aNodesAsArray : nodesAsArray) {
      sb.append(aNodesAsArray + " ");
    }
    sb.append(component.getSweepableValue());
    return sb.toString();
  }

  @Override
  public String toString() {
    return "NetlistComponent [component=" + component + ", id=" + id + ", nodes=" + Arrays.toString(getNodesAsArray()) + "]";
  }
}
