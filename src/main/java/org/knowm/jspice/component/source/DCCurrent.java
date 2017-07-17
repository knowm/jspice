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
package org.knowm.jspice.component.source;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author timmolter
 */
public class DCCurrent extends Source {

  @Valid
  @NotNull
  @JsonProperty("current")
  private double dcCurrent;

  /**
   * Constructor
   *
   * @param id
   * @param dcCurrent
   */
  @JsonCreator
  public DCCurrent(@JsonProperty("id") String id, @JsonProperty("current") double dcCurrent) {

    super(id);
    this.dcCurrent = dcCurrent;
  }

  @Override
  public void setSweepValue(double value) {

    this.dcCurrent = value;
  }

  @Override
  public double getSweepableValue() {

    return dcCurrent;
  }

  @Override
  public String toString() {

    return "DCCurrent [id=" + getId() + ", dcCurrent=" + dcCurrent + "]";
  }

  @Override
  public Set<String> getGMatrixColumnIDs(String[] nodes, Double timeStep) {

    // Do nothing
    return Collections.emptySet();
  }

  @Override
  public void modifyUnknowmQuantitiesVector(String[] columnQuantities, String[] nodes, Double timeStep) {

    for (int i = 0; i < columnQuantities.length; i++) {
      if (columnQuantities[i].equals(nodes[0]) || columnQuantities[i].equals(nodes[1])) {
        columnQuantities[i] = "V(" + columnQuantities[i] + ")";
      } else if (columnQuantities[i].equals(getId())) {
        columnQuantities[i] = "I(" + columnQuantities[i] + ")";
      }
    }
  }

  @Override
  public void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      String[] nodes, Double timeStep) {

    // Do nothing
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes,
      Double timeStep) {

    //    System.out.println("dcCurrent " + dcCurrent);

    int idxA = nodeID2ColumnIdxMap.get(nodes[0]);
    int idxB = nodeID2ColumnIdxMap.get(nodes[1]);

    double[] stamp = new double[2];
    stamp[0] = -1 * dcCurrent;
    stamp[1] = dcCurrent;

    RHS[idxA] += stamp[0];
    RHS[idxB] += stamp[1];
  }
}
