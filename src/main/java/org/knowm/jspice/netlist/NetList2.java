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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.element.memristor.Memristor;
import org.knowm.jspice.component.element.nonlinear.Diode;
import org.knowm.jspice.component.element.nonlinear.MOSFET;
import org.knowm.jspice.component.element.reactive.Capacitor;
import org.knowm.jspice.component.element.reactive.Inductor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCCurrentArbitrary;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.DCVoltageArbitrary;
import org.knowm.jspice.component.source.VCCS;
import org.knowm.jspice.component.source.VCVS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author timmolter
 */
public class NetList2 {

  /**
   * Component List
   */
  @Valid
  @NotNull
  @JsonProperty("components")
  protected List<NetlistComponent> netListComponents = new ArrayList<>();

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

  /**
   * no-args Constructor - need this!
   */
  public NetList2() {

  }

  /**
   * Constructor
   *
   * @param jSpiceNetlistBuilder
   */
  public NetList2(NetlistBuilder jSpiceNetlistBuilder) {

    this.netListComponents = jSpiceNetlistBuilder.components;
  }

  /**
   * Add a NetListComponent to the Netlist
   *
   * @param netListComponent
   */
  public void addNetListComponent(Component component, String[] nodes) {

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
    } else {
      componentIDMap.put(component.getId(), component);
    }

    NetlistComponent netListComponent = new NetlistComponent(component, nodes);

    // add to component list
    netListComponents.add(netListComponent);

    // add to Resistor list
    if (component instanceof Resistor) {
      netListResistors.add(netListComponent);
    }

    // add to DCVoltage list
    else if (component instanceof DCVoltage) {
      if (component instanceof DCVoltageArbitrary) {
        netListDCVoltageArbitrarys.add(netListComponent);
      } else {
        netListDCVoltageSources.add(netListComponent);
      }
    }

    // add to DCCurrent list
    else if (component instanceof DCCurrent) {
      if (component instanceof DCCurrentArbitrary) {
        netListDCCurrentArbitrarys.add(netListComponent);
      } else {
        netListDCCurrentSources.add(netListComponent);
      }
    }

    // add to Diode list
    else if (component instanceof Diode) {

      netListDiodes.add(netListComponent);
    }

    // add to Capacitor list
    else if (component instanceof Capacitor) {

      netListCapacitors.add(netListComponent);
    }
    // add to Inductor list
    else if (component instanceof Inductor) {

      netListInductors.add(netListComponent);
    }

    // add to Memristor list
    else if (component instanceof Memristor) {

      netListMemristors.add(netListComponent);
    }
    // add to VCCS list
    else if (component instanceof VCCS) {

      netListVCCSs.add(netListComponent);
    } // add to VCVS list
    else if (component instanceof VCVS) {

      netListVCVSs.add(netListComponent);
    }
    // add to MOSFET list
    else if (component instanceof MOSFET) {
      netListMOSFETs.add(netListComponent);
    } else {
      throw new IllegalArgumentException("Unknown  Component Type!");
    }
  }

  public List<NetlistComponent> getNetListComponents() {

    return netListComponents;
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
}
