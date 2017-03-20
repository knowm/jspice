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
package org.knowm.jspice.dcop;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.knowm.jspice.circuit.Circuit;
import org.knowm.jspice.circuits.I1V1R6;
import org.knowm.jspice.simulate.dcoperatingpoint.CircuitMatrixSolver;

/**
 * @author timmolter
 */
public class TestCircuitMatrixSolver {

  Circuit circuit = new I1V1R6();

  @Test
  public void testGetNodeID2ColumnIdxMap() {

    Map<String, Integer> nodeID2ColumnIdxMap = CircuitMatrixSolver.getNodeID2ColumnIdxMap(circuit, null);

    // for (Entry<String, Integer> entry : nodeID2ColumnIdxMap.entrySet()) {
    // System.out.println(entry.getKey());
    // System.out.println(entry.getValue());
    // }

    assertThat(nodeID2ColumnIdxMap.get("0"), is(equalTo(0)));
    assertThat(nodeID2ColumnIdxMap.get("1"), is(equalTo(1)));
    assertThat(nodeID2ColumnIdxMap.get("2"), is(equalTo(2)));
    assertThat(nodeID2ColumnIdxMap.get("3"), is(equalTo(3)));
    assertThat(nodeID2ColumnIdxMap.get("4"), is(equalTo(4)));
    assertThat(nodeID2ColumnIdxMap.get("5"), is(equalTo(5)));
    assertThat(nodeID2ColumnIdxMap.get("x"), is(equalTo(6)));
  }

  @Test
  public void testGetUnknownVariableNames() {

    Map<String, Integer> nodeID2ColumnIdxMap = CircuitMatrixSolver.getNodeID2ColumnIdxMap(circuit, null);

    String[] unknownVariableNames = CircuitMatrixSolver.getUnknownVariableNames(nodeID2ColumnIdxMap, circuit, null);
    // for (int i = 0; i < unknownVariableNames.length; i++) {
    // System.out.println(unknownVariableNames[i]);
    // }
    assertThat(unknownVariableNames.length, is(equalTo(6)));
  }

  @Test
  public void testGetG() {

    Map<String, Integer> nodeID2ColumnIdxMap = CircuitMatrixSolver.getNodeID2ColumnIdxMap(circuit, null);

    double[][] G = CircuitMatrixSolver.getG(nodeID2ColumnIdxMap, circuit, null, null);

    // System.out.println( CircuitMatrixSolver.GtoString(G));

    assertThat(G.length, is(equalTo(7)));
    assertThat(G[0].length, is(equalTo(7)));
  }

  @Test
  public void testGetI() {

    Map<String, Integer> nodeID2ColumnIdxMap = CircuitMatrixSolver.getNodeID2ColumnIdxMap(circuit, null);

    double[] RHS = CircuitMatrixSolver.getRHS(nodeID2ColumnIdxMap, circuit, null, null);

    // for (int j = 0; j < I.length; j++) {
    // System.out.println(I[j]);
    // }
    // assertThat(I.length, is(equalTo(7)));

  }
}
