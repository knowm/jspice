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

import java.util.Map;

import org.knowm.jspice.component.NonlinearComponent;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public class DCVoltageArbitrary extends DCVoltage implements NonlinearComponent {

  private final String expression;

  /**
   * Constructor
   *
   * @param id
   */
  public DCVoltageArbitrary(String id, String expression) {

    super(id, 0);
    this.expression = expression;
  }

  @Override
  public String toString() {

    return "DCVoltageArbitrary [id=" + getId() + "]";
  }

  @Override
  public void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes, Double timeStep) {

    // create stamp
    double[] stamp = new double[3];

    stamp[0] = 0.0;
    stamp[1] = 0.0;

    double value = 1.0;
    if (dcOperatingPointResult != null) {
      value = ArbitraryUtils.getArbitraryValue(dcOperatingPointResult);
    }
    stamp[2] = value;

    // apply stamp
    int idxA = nodeID2ColumnIdxMap.get(nodes[0]);
    int idxB = nodeID2ColumnIdxMap.get(nodes[1]);
    int idxI = nodeID2ColumnIdxMap.get(getId());
    RHS[idxA] += stamp[0];
    RHS[idxB] += stamp[1];
    RHS[idxI] += stamp[2];

    setSweepValue(value);
  }

}
