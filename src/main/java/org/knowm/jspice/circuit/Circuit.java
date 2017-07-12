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
package org.knowm.jspice.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.NonlinearComponent;
import org.knowm.jspice.component.element.reactive.ReactiveElement;
import org.knowm.jspice.netlist.NetList;
import org.knowm.jspice.netlist.NetlistComponent;

/**
 * @author timmolter
 */
// TODO get rid of this class?? It just wraps NetList.
public class Circuit {

  private NetList netlist = new NetList();
  private boolean isNonlinearCircuit = false;
  private boolean isInitialConditions = false;

  public Circuit() {

  }

  public Circuit(NetList jSpiceNetlist) {

    for (NetlistComponent netlistComponent : jSpiceNetlist.getNetListComponents()) {
      addNetListComponent(netlistComponent.getComponent(), netlistComponent.getNodesAsArray());
    }
  }

  public void addNetListComponent(Component component, String[] nodes) {

    if (component instanceof NonlinearComponent) {
      isNonlinearCircuit = true;
    }
    if (component instanceof ReactiveElement) {

      ReactiveElement reactiveElement = (ReactiveElement) component;
      if (reactiveElement.getInitialCondition() != null) {
        isInitialConditions = true;
      }
    }
    netlist.addNetListComponent(component, nodes);
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB) {

    addNetListComponent(component, new String[]{nodeA, nodeB});
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB, String nodeC) {

    addNetListComponent(component, new String[]{nodeA, nodeB, nodeC});
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB, String nodeC, String nodeD) {

    addNetListComponent(component, new String[]{nodeA, nodeB, nodeC, nodeD});
  }

  /**
   * Add components from a subcircuit to the netlist
   *
   * @param subCircuit
   */
  public void addSubCircuit(SubCircuit subCircuit) {

    for (NetlistComponent netlistComponent : subCircuit.getNetlist().getNetListComponents()) {
      Component component = netlistComponent.getComponent();
      addNetListComponent(component, netlistComponent.getNodesAsArray());
    }
  }

  /**
   * NetList sanity checks
   */
  public void verifyCircuit() {

    // make sure netlist contains at least two net list parts
    if (netlist.getNetListComponents().size() < 2) {
      throw new IllegalArgumentException("Must have at least 2 NetListParts!");
    }

    // make sure all nodes have at minimum two components connected to it
    Map<String, Integer> nodeId2CountMap = new HashMap<String, Integer>();
    for (NetlistComponent netListComponent : netlist.getNetListComponents()) {
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
    for (NetlistComponent dcCurrentSource1 : netlist.getNetListDCCurrentSources()) {
      for (NetlistComponent dcCurrentSource2 : netlist.getNetListDCCurrentSources()) {
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
    for (NetlistComponent dcVoltageSource1 : netlist.getNetListDCVoltageSources()) {

      for (NetlistComponent dcVoltageSource2 : netlist.getNetListDCVoltageSources()) {

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

  public boolean isNonlinearCircuit() {

    return isNonlinearCircuit;
  }

  public boolean isInitialConditions() {

    return isInitialConditions;
  }

  public void setInitialConditions(boolean isInitialConditions) {

    this.isInitialConditions = isInitialConditions;
  }

  public NetList getNetlist() {

    return netlist;
  }
}
