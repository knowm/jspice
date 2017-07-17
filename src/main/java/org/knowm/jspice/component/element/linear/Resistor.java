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
package org.knowm.jspice.component.element.linear;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A constant-value resistor. No temperature dependence or higher-order dynamic behavior
 *
 * @author timmolter
 */
public class Resistor extends LinearElement {

  @Valid
  @NotNull
  @JsonProperty("resistance")
  @Min(0)
  private double resistance;

  /**
   * Constructor
   *
   * @param id
   * @param resistance
   */
  public Resistor(@JsonProperty("id") String id, @JsonProperty("resistance") double resistance) {

    super(id);
    this.resistance = resistance;
  }

  @Override
  public void setSweepValue(double value) {

    this.resistance = value;
  }

  @Override
  public double getSweepableValue() {

    return resistance;
  }

  /**
   * Get the current thru this resistor
   *
   * @return
   */
  public double getCurrent(double voltage) {

    return voltage / this.resistance;
  }

  @Override
  public String toString() {

    return "Resistor [id=" + getId() + ", resistance=" + resistance + "]";
  }

  @Override
  public Set<String> getGMatrixColumnIDs(String[] nodes, Double timeStep) {

    Set<String> set = new HashSet<>(2);
    set.add(nodes[0]);
    set.add(nodes[1]);

    return set;
  }

  @Override
  public void modifyUnknowmQuantitiesVector(String[] columnQuantities, String[] nodes, Double timeStep) {

    for (int i = 0; i < columnQuantities.length; i++) {
      if (columnQuantities[i].equals(nodes[0]) || columnQuantities[i].equals(nodes[1])) {
        columnQuantities[i] = "V(" + columnQuantities[i] + ")";
      }
    }
  }

  @Override
  public void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      String[] nodes, Double timeStep) {

    int idxA = nodeID2ColumnIdxMap.get(nodes[0]);
    int idxB = nodeID2ColumnIdxMap.get(nodes[1]);

    double[][] stamp = new double[2][2];

    double conductance = 1 / this.resistance;

    // create stamp
    stamp[0][0] = conductance;
    stamp[0][1] = -1 * conductance;
    stamp[1][0] = -1 * conductance;
    stamp[1][1] = conductance;

    // apply stamp
    G[idxA][idxA] += stamp[0][0];
    G[idxA][idxB] += stamp[0][1];
    G[idxB][idxA] += stamp[1][0];
    G[idxB][idxB] += stamp[1][1];
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes,
      Double timeStep) {

    // Do nothing

  }
}
