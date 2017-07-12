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
package org.knowm.jspice.component.element.memristor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

public abstract class Memristor extends Component {

  /**
   * Constructor
   *
   * @param id
   */
  public Memristor(String id) {

    super(id);
  }

  public abstract double getCurrent(double voltage);

  public abstract void dG(double voltage, double dt);

  public abstract double getConductance();

  public double getResistance() {

    return 1.0 / getConductance();
  }

  @Override
  public Set<String> getGMatrixColumnIDs(String[] nodes, Double timeStep) {

    Set<String> set = new HashSet<>(2);
    set.add(nodes[0]);
    set.add(nodes[1]);

    return set;
  }

  @Override
  public void modifyUnknowmQuantitiesVector(String[] nodeIDs, String[] nodes, Double timeStep) {

    for (int i = 0; i < nodeIDs.length; i++) {
      if (nodeIDs[i].equals(nodes[0]) || nodeIDs[i].equals(nodes[1])) {
        nodeIDs[i] = "V(" + nodeIDs[i] + ")";
      }
    }
  }

  @Override
  public void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes, Double timeStep) {

    if (timeStep != null) {
      double voltageA = dcOperatingPointResult.getValue("V(" + nodes[0] + ")");
      double voltageB = dcOperatingPointResult.getValue("V(" + nodes[1] + ")");
      double Vmemristor = voltageA - voltageB;
      dG(Vmemristor, timeStep);
    }

    double[][] stamp = new double[2][2];

    double conductance = getConductance();

    // create stamp
    stamp[0][0] = conductance;
    stamp[0][1] = -1 * conductance;
    stamp[1][0] = -1 * conductance;
    stamp[1][1] = conductance;

    // apply stamp
    int idxA = nodeID2ColumnIdxMap.get(nodes[0]);
    int idxB = nodeID2ColumnIdxMap.get(nodes[1]);
    // int idxI = nodeID2ColumnIdxMap.get(id);
    G[idxA][idxA] += stamp[0][0];
    G[idxA][idxB] += stamp[0][1];
    G[idxB][idxA] += stamp[1][0];
    G[idxB][idxB] += stamp[1][1];
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes, Double timeStep) {

    // Do nothing
  }
}
