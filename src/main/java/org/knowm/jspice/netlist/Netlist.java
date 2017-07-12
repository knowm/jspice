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

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.circuit.SubCircuit;
import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.NonlinearComponent;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.element.memristor.Memristor;
import org.knowm.jspice.component.element.nonlinear.Diode;
import org.knowm.jspice.component.element.nonlinear.MOSFET;
import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.component.element.reactive.Inductor;
import org.knowm.jspice.component.element.reactive.ReactiveElement;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCCurrentArbitrary;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.DCVoltageArbitrary;
import org.knowm.jspice.component.source.VCCS;
import org.knowm.jspice.component.source.VCVS;
import org.knowm.jspice.simulate.SimulationConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Netlist {

  /**
   * Component List
   */
  @Valid
  @NotNull
  @JsonProperty("components")
  protected List<NetlistComponent> netlistComponents = new ArrayList<>();

  @Valid
  @Nullable
  @JsonProperty("sim")
  SimulationConfig simulationConfig;

  /**
   * componentId, Component
   */
  @JsonIgnore
  protected Map<String, Component> componentIDMap = new HashMap<>();

  /**
   * Resistor List
   */
  @JsonIgnore
  protected List<NetlistComponent> netListResistors = new ArrayList<>();

  /**
   * DCVoltage Source List
   */
  @JsonIgnore
  protected List<NetlistComponent> netListDCVoltageSources = new ArrayList<>();

  /**
   * DCCurrent Source List
   */
  @JsonIgnore
  protected List<NetlistComponent> netListDCCurrentSources = new ArrayList<>();

  /**
   * Diode List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListDiodes = new ArrayList<>();

  /**
   * NMOS List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListMOSFETs = new ArrayList<>();

  /**
   * Capacitor List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListCapacitors = new ArrayList<>();

  /**
   * Inductor List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListInductors = new ArrayList<>();

  /**
   * Memristor List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListMemristors = new ArrayList<>();

  /**
   * VCCS List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListVCCSs = new ArrayList<>();

  /**
   * VCVS List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListVCVSs = new ArrayList<>();

  /**
   * DCVoltageArbitrary List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListDCVoltageArbitrarys = new ArrayList<>();

  /**
   * DCCurrentArbitrary List
   */
  @JsonIgnore
  private final List<NetlistComponent> netListDCCurrentArbitrarys = new ArrayList<>();

  @JsonIgnore
  private boolean isNonlinearCircuit = false;

  @JsonIgnore
  private boolean isInitialConditions = false;

  /**
   * no-args Constructor - need this!
   */
  public Netlist() {

  }

  /**
   * Constructor
   *
   * @param netlistBuilder
   */
  public Netlist(NetlistBuilder netlistBuilder) {

    this.simulationConfig = netlistBuilder.simulationConfig;
    for (NetlistComponent netlistComponent : netlistBuilder.netlistComponents) {
      addNetListComponent(netlistComponent.getComponent(), netlistComponent.getNodesAsArray());
    }
  }

  /**
   * Add a NetListComponent to the Netlist
   *
   * @param netListComponent
   */
  public void addNetListComponent(Component component, String... nodes) {

    if (component instanceof NonlinearComponent) {
      this.isNonlinearCircuit = true;
    }
    if (component instanceof ReactiveElement) {

      ReactiveElement reactiveElement = (ReactiveElement) component;
      if (reactiveElement.getInitialCondition() != null) {
        this.isInitialConditions = true;
      }
    }

    // make sure node names are not the exact same
    for (int i = 0; i < nodes.length - 1; i++) {
      if (nodes[i].equals(nodes[i + 1])) {
        throw new IllegalArgumentException("A component cannot be connected to the same node twice!");
      }
    }

    // make sure no components have the same name
    Component existingComponent = componentIDMap.get(component.getId());
    if (existingComponent != null) {
      throw new IllegalArgumentException("The component ID " + component.getId() + " is not unique!");
    }

    NetlistComponent netListComponent = new NetlistComponent(component, nodes);

    // add to component list
    //    netListComponents.add(netListComponent);

    // add to Resistor list
    if (component instanceof Resistor) {

      netListComponent = new NetlistResistor(component.getId(), component.getSweepableValue(), nodes);
      netListResistors.add(netListComponent);
      netlistComponents.add(netListComponent);
      componentIDMap.put(netListComponent.getComponent().getId(), netListComponent.getComponent());
    }

    // add to DCVoltage list
    else if (component instanceof DCVoltage) {
      if (component instanceof DCVoltageArbitrary) {
        netListDCVoltageArbitrarys.add(netListComponent);
      } else {

        netListComponent = new NetlistDCVoltage(component.getId(), component.getSweepableValue(), nodes);
        netListDCVoltageSources.add(netListComponent);
        netlistComponents.add(netListComponent);
        componentIDMap.put(netListComponent.getComponent().getId(), netListComponent.getComponent());
      }
      netlistComponents.add(netListComponent);
    }

    // add to DCCurrent list
    else if (component instanceof DCCurrent) {
      if (component instanceof DCCurrentArbitrary) {
        netlistComponents.add(netListComponent);
        netListDCCurrentArbitrarys.add(netListComponent);
      } else {
        netListComponent = new NetlistDCCurrent(component.getId(), component.getSweepableValue(), nodes);
        netListDCCurrentSources.add(netListComponent);
        netlistComponents.add(netListComponent);
        componentIDMap.put(netListComponent.getComponent().getId(), netListComponent.getComponent());
      }
    }

    // add to Diode list
    else if (component instanceof Diode) {

      netListDiodes.add(netListComponent);
      netlistComponents.add(netListComponent);
    }

    // add to Capacitor list
    else if (component instanceof Capacitor) {

      netListCapacitors.add(netListComponent);
      netlistComponents.add(netListComponent);
    }
    // add to Inductor list
    else if (component instanceof Inductor) {

      netListInductors.add(netListComponent);
      netlistComponents.add(netListComponent);
    }

    // add to Memristor list
    else if (component instanceof Memristor) {

      netListMemristors.add(netListComponent);
      netlistComponents.add(netListComponent);
    }
    // add to VCCS list
    else if (component instanceof VCCS) {

      netListVCCSs.add(netListComponent);
      netlistComponents.add(netListComponent);
    } // add to VCVS list
    else if (component instanceof VCVS) {

      netListVCVSs.add(netListComponent);
      netlistComponents.add(netListComponent);
    }
    // add to MOSFET list
    else if (component instanceof MOSFET) {
      netListMOSFETs.add(netListComponent);
      netlistComponents.add(netListComponent);
    } else {
      throw new IllegalArgumentException("Unknown Component Type!");
    }

  }

  public List<NetlistComponent> getNetlistComponents() {

    return netlistComponents;
  }

  public Map<String, Component> getComponentIDMap() {

    return componentIDMap;
  }

  public Component getComponent(String id) {

    return componentIDMap.get(id);
  }

  public List<NetlistComponent> getNetListResistors() {

    return netListResistors;
  }

  public List<NetlistComponent> getNetListDCVoltageSources() {

    return netListDCVoltageSources;
  }

  public List<NetlistComponent> getNetListDCVoltageArbitrarys() {

    return netListDCVoltageArbitrarys;
  }

  public List<NetlistComponent> getNetListDCCurrentSources() {

    return netListDCCurrentSources;
  }

  public List<NetlistComponent> getNetListDCCurrentArbitrarys() {

    return netListDCCurrentArbitrarys;
  }

  public List<NetlistComponent> getNetListDiodes() {

    return netListDiodes;
  }

  public List<NetlistComponent> getNetListMOSFETs() {

    return netListMOSFETs;
  }

  public List<NetlistComponent> getNetListCapacitors() {

    return netListCapacitors;
  }

  public List<NetlistComponent> getNetListInductors() {

    return netListInductors;
  }

  public List<NetlistComponent> getNetListMemristors() {

    return netListMemristors;
  }

  public List<NetlistComponent> getNetListVCCSs() {

    return netListVCCSs;
  }

  public List<NetlistComponent> getNetListVCVSs() {

    return netListVCVSs;
  }

  @Override
  public String toString() {

    String returnString = System.getProperty("line.separator");

    StringBuilder sb = new StringBuilder();
    for (NetlistComponent component : netListResistors) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCVoltageSources) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : getNetListDCVoltageArbitrarys()) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCCurrentSources) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCCurrentArbitrarys) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListCapacitors) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListInductors) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDiodes) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListMOSFETs) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListMemristors) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListVCCSs) {
      sb.append(component.toString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListVCVSs) {
      sb.append(component.toString());
      sb.append(returnString);
    }

    sb.append("isNonlinearCircuit: ");
    sb.append(isNonlinearCircuit);
    sb.append(returnString);

    sb.append("isInitialConditions: ");
    sb.append(isInitialConditions);
    sb.append(returnString);

    return sb.toString();
  }

  public String toSpiceString() {

    String returnString = System.getProperty("line.separator");

    StringBuilder sb = new StringBuilder();
    for (NetlistComponent component : netListResistors) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCVoltageSources) {

      // DCVoltage dcVoltage = (DCVoltage) component.getComponent();
      // sb.append(dcVoltage.getID().toLowerCase() + " ");
      // sb.append(component.getNodeA() + " ");
      // sb.append(component.getNodeB() + " ");
      // sb.append("dc " + dcVoltage.getSweepableValue()); // inject dc here
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCVoltageArbitrarys) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCCurrentSources) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDCCurrentArbitrarys) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListCapacitors) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListInductors) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListDiodes) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListMOSFETs) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListMemristors) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListVCCSs) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }
    for (NetlistComponent component : netListVCVSs) {
      sb.append(component.toSpiceString());
      sb.append(returnString);
    }

    return sb.toString();
  }

  /**
   * NetList sanity checks
   */
  public void verifyCircuit() {

    // make sure netlist contains at least two net list parts
    if (getNetlistComponents().size() < 2) {
      throw new IllegalArgumentException("Must have at least 2 NetListParts!");
    }

    // make sure all nodes have at minimum two components connected to it
    Map<String, Integer> nodeId2CountMap = new HashMap<String, Integer>();
    for (NetlistComponent netListComponent : getNetlistComponents()) {
      // System.out.println(netListComponent.getComponent().getID() + ": " + netListComponent.getNodeA() + "-" + netListComponent.getNodeB());

      for (int i = 0; i < netListComponent.getNodesAsArray().length; i++) {
        String nodeID = netListComponent.getNodesAsArray()[i];
        Integer count = nodeId2CountMap.get(nodeID);
        if (count == null) {
          nodeId2CountMap.put(nodeID, 1);
        } else {
          nodeId2CountMap.put(nodeID, count + 1);
        }
      }
    }
    for (Entry<String, Integer> entry : nodeId2CountMap.entrySet()) {
      String nodeID = entry.getKey();
      Integer count = entry.getValue();
      if (count < 2) {
        throw new IllegalArgumentException("Must have at least 2 Connections for node " + nodeID + "!");
      }
    }

    // check that one of the nodes is "0". This is the ground node.
    boolean node0exists = false;
    for (Entry<String, Integer> entry : nodeId2CountMap.entrySet()) {
      String nodeID = entry.getKey();
      if (nodeID.equals("0")) {
        node0exists = true;
        break;
      }
    }
    if (!node0exists) {
      throw new IllegalArgumentException("Node \"0\" must be part of the netlist representing ground!");
    }

    // TODO i think current sources can be in series if there is another conductive component connected as well.
    // make sure there are no series current sources
    for (NetlistComponent dcCurrentSource1 : getNetListDCCurrentSources()) {
      for (NetlistComponent dcCurrentSource2 : getNetListDCCurrentSources()) {
        // if one current source shares exactly one nodes with another, they are in series
        if (dcCurrentSource1.equals(dcCurrentSource2)) {
          // System.out.println("equals");
          continue;
        } else {
          // System.out.println("comparing");
          String node1A = dcCurrentSource1.getNodesAsArray()[0];
          String node1B = dcCurrentSource1.getNodesAsArray()[1];
          String node2A = dcCurrentSource2.getNodesAsArray()[0];
          String node2B = dcCurrentSource2.getNodesAsArray()[1];

          if (node1A.equals(node2A) || node1A.equals(node2B)) { // if one of the nodes matches
            if (node1B.equals(node2A) || node1B.equals(node2B)) {
              // all good parallel
            } else {
              // only one of the nodes matched, not both!
              if (!node1A.equals("0") && !node1B.equals("0")) { // we don't cound the ground node
                throw new IllegalArgumentException("Current sources cannot be in series!");
              }
            }
          }
        }
      }
    }

    // make sure there are no parallel voltage sources
    for (NetlistComponent dcVoltageSource1 : getNetListDCVoltageSources()) {

      for (NetlistComponent dcVoltageSource2 : getNetListDCVoltageSources()) {

        // if one current source shares exactly one nodes with another, they are in series
        if (dcVoltageSource1.equals(dcVoltageSource2)) {
          // System.out.println("equals");
          continue;
        } else {
          // System.out.println("comparing");
          String node1A = dcVoltageSource1.getNodesAsArray()[0];
          String node1B = dcVoltageSource1.getNodesAsArray()[1];
          String node2A = dcVoltageSource2.getNodesAsArray()[0];
          String node2B = dcVoltageSource2.getNodesAsArray()[1];

          if (node1A.equals(node2A) || node1A.equals(node2B)) { // if one of the nodes matches
            if (node1B.equals(node2A) || node1B.equals(node2B)) {
              // System.out.println("WARNING! Voltage sources cannot be in parallel!");
              throw new IllegalArgumentException("Voltage sources cannot be in parallel!");
            }
          }
        }
      }
    }

    // TODO make sure no circuit elements have a name that matches a node label!

  }

  /**
   * Add components from a subcircuit to the netlist
   *
   * @param subCircuit
   */
  public void addSubCircuit(SubCircuit subCircuit) {

    for (NetlistComponent netlistComponent : subCircuit.getNetlist().getNetlistComponents()) {
      Component component = netlistComponent.getComponent();
      addNetListComponent(component, netlistComponent.getNodesAsArray());
    }
  }

  @JsonIgnore
  public boolean isNonlinearCircuit() {

    return isNonlinearCircuit;
  }

  @JsonIgnore
  public boolean isInitialConditions() {

    return isInitialConditions;
  }

  @JsonIgnore
  public void setInitialConditions(boolean isInitialConditions) {

    this.isInitialConditions = isInitialConditions;
  }

  public SimulationConfig getSimulationConfig() {
    return simulationConfig;
  }

  /**
   * This is called by the YML deserializer
   * 
   * @param netlistComponents
   */
  public void setNetlistComponents(List<NetlistComponent> netlistComponents) {

    for (NetlistComponent netlistComponent : netlistComponents) {
      addNetListComponent(netlistComponent.getComponent(), netlistComponent.getNodesAsArray());
    }
  }

}
