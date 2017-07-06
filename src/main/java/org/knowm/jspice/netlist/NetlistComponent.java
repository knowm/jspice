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

import org.knowm.jspice.component.Component;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public class NetlistComponent {

  private final Component component;
  private final String[] nodes; // order matters!

  /**
   * @param component
   * @param nodes
   */
  public NetlistComponent(Component component, String[] nodes) {

    this.component = component;
    this.nodes = nodes;
  }

  public Set<String> getGMatrixColumnIDs(Double timeStep) {

    return component.getGMatrixColumnIDs(nodes, timeStep);
  }

  public void modifyUnknownQuantitiesVector(String[] nodeIDs, Double timeStep) {

    component.modifyUnknowmQuantitiesVector(nodeIDs, nodes, timeStep);
  }

  public void stampG(double[][] G, NetList netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      Double timeStep) {

    component.stampG(G, netList, dcOperatingPointResult, nodeID2ColumnIdxMap, nodes, timeStep);
  }

  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, Double timeStep) {

    component.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, nodes, timeStep);
  }

  public Component getComponent() {

    return component;
  }

  public String[] getNodes() {

    return nodes;
  }

  public String toSpiceString() {

    StringBuilder sb = new StringBuilder();
    sb.append(component.getId().toLowerCase() + " ");
    for (int i = 0; i < nodes.length; i++) {
      sb.append(nodes[i] + " ");
    }
    sb.append(component.getSweepableValue());
    return sb.toString();
  }

  @Override
  public String toString() {
    return "NetlistComponent [component=" + component + ", nodes=" + Arrays.toString(nodes) + "]";
  }

}
