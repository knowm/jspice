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
package org.knowm.jspice.simulate.dcoperatingpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.knowm.jspice.component.element.reactive.ReactiveElement;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistComponent;

/**
 * @author timmolter
 */
public class CircuitMatrixSolver {

  /**
   * Find all the nodes (and dc voltage sources that need a column) in the circuit, and maps the given node labels to an array index
   *
   * @param netlist
   * @param timeStep
   * @return
   */
  public static Map<String, Integer> getNodeID2ColumnIdxMap(Netlist netlist, Double timeStep) {

    Map<String, Integer> nodeID2ColumnIdxMap = new TreeMap<>(); // return value

    // A Set so duplicates are not added
    Set<String> nodeNameSet = new HashSet<>();

    for (NetlistComponent netlistComponent : netlist.getNetlistComponents()) {
      nodeNameSet.addAll(netlistComponent.getGMatrixColumnIDs(timeStep));
    }

    // sort the node names, and assign ascending ints to them
    List<String> nodeNameList = new ArrayList<>(nodeNameSet);
    nodeNameList = new ArrayList<>(nodeNameSet);
    Collections.sort(nodeNameList);
    int idx = 0;
    for (String nodeName : nodeNameList) {
      nodeID2ColumnIdxMap.put(nodeName, idx);
      idx++;
    }

    return nodeID2ColumnIdxMap;
  }

  /**
   * Get the unknowns we are solving for, aka the `v` vector
   *
   * @param nodeID2ColumnIdxMap
   * @param netlist
   * @param timeStep
   * @return
   */
  public static String[] getUnknownVariableNames(Map<String, Integer> nodeID2ColumnIdxMap, Netlist netlist, Double timeStep) {

    String[] nodeIDs = nodeID2ColumnIdxMap.keySet().toArray(new String[nodeID2ColumnIdxMap.keySet().size()]);

    for (NetlistComponent netlistComponent : netlist.getNetlistComponents()) {
      netlistComponent.modifyUnknownQuantitiesVector(nodeIDs, timeStep);
    }
    return Arrays.copyOfRange(nodeIDs, 1, nodeIDs.length); // trim V(0) off the front of the array
  }

  /**
   * builds G for G v = i
   *
   * @param nodeID2ColumnIdxMap
   * @param netlist
   * @param dcOperatingPointResult
   * @param timeStep
   * @return
   */
  public static double[][] getG(Map<String, Integer> nodeID2ColumnIdxMap, Netlist netlist, DCOperatingPointResult dcOperatingPointResult,
      Double timeStep) {

    double[][] G = new double[nodeID2ColumnIdxMap.size()][nodeID2ColumnIdxMap.size()];
    // System.out.println("G= " + GtoString(G));

    for (NetlistComponent netlistComponent : netlist.getNetlistComponents()) {
      //      System.out.println("netlistComponent " + netlistComponent);
      netlistComponent.stampG(G, netlist, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);
    }

    return G;
  }

  public static String GtoString(double[][] G) {

    StringBuilder sb = new StringBuilder();
    for (double[] row : G) {
      sb.append("[ ");
      for (double j : row) {
        sb.append(j + " ");
      }
      sb.append("]");
      sb.append(System.getProperty("line.separator"));
    }
    return sb.toString();
  }

  public static double[] getInitialConditionsSolutionVector(Map<String, Integer> nodeID2ColumnIdxMap, Netlist netlist, double[] RHS_trimmed) {

    double[] solutionVector = new double[RHS_trimmed.length];

    for (NetlistComponent netlistComponent : netlist.getNetListCapacitors()) {
      ((ReactiveElement) netlistComponent.getComponent()).stampSolutionVector(solutionVector, nodeID2ColumnIdxMap,
          netlistComponent.getNodesAsArray());
    }

    for (NetlistComponent netlistComponent : netlist.getNetListInductors()) {
      ((ReactiveElement) netlistComponent.getComponent()).stampSolutionVector(solutionVector, nodeID2ColumnIdxMap,
          netlistComponent.getNodesAsArray());
    }
    return solutionVector;
  }

  /**
   * builds i for G v = i
   *
   * @param nodeID2ColumnIdxMap
   * @param netlist
   * @param timeStep
   * @return
   */
  public static double[] getRHS(Map<String, Integer> nodeID2ColumnIdxMap, Netlist netlist, DCOperatingPointResult dcOperatingPointResult,
      Double timeStep) {

    /////////////////////////////////////////////////////////////////////////////////////////
    // current sources
    /////////////////////////////////////////////////////////////////////////////////////////
    double[] RHS = new double[nodeID2ColumnIdxMap.size()];

    // System.out.println("G= " + GtoString(G));

    for (NetlistComponent netlistComponent : netlist.getNetlistComponents()) {
      //      System.out.println("netlistComponent " + netlistComponent);
      netlistComponent.stampRHS(RHS, dcOperatingPointResult, nodeID2ColumnIdxMap, timeStep);
    }

    return RHS;
  }

  /**
   * gets rid of the ground node column and row
   *
   * @param G
   * @param nodeID2ColumnIdxMap
   * @return
   */
  public static double[][] trimG(double[][] G, Map<String, Integer> nodeID2ColumnIdxMap) {

    if (nodeID2ColumnIdxMap.get("0") != null) {

      int groundIdx = nodeID2ColumnIdxMap.get("0");

      double[][] G2 = new double[nodeID2ColumnIdxMap.size() - 1][nodeID2ColumnIdxMap.size() - 1];
      int a = 0;
      int b = 0;
      for (int i = 0; i < G.length; i++) {
        if (i != groundIdx) {
          b = 0;
          for (int j = 0; j < G.length; j++) {
            if (j != groundIdx) {
              G2[a][b] = G[i][j];
              b++;
            }
          }
          a++;
        }
      }
      return G2;
    } else {
      return G;
    }
  }

  /**
   * gets rid of the ground node row
   *
   * @param v
   * @param nodeID2ColumnIdxMap
   * @return
   */
  public static double[] trimVector(double[] v, Map<String, Integer> nodeID2ColumnIdxMap) {

    if (nodeID2ColumnIdxMap.get("0") != null) {

      int groundIdx = nodeID2ColumnIdxMap.get("0");

      double[] v2 = new double[nodeID2ColumnIdxMap.size() - 1];
      int a = 0;
      for (int i = 0; i < v.length; i++) {
        if (i != groundIdx) {
          v2[a] = v[i];
          a++;
        }
      }
      return v2;
    } else {
      return v;
    }
  }

  /**
   * Solves v for G v = RHS
   *
   * @param G
   * @param unknownVariableNames
   * @param RHS
   * @return
   */
  public static DCOperatingPointResult solveMatrix(double[][] G, String[] unknownVariableNames, double[] RHS) throws SingularMatrixException {

    // long start = System.currentTimeMillis();

    RealMatrix coefficients = new Array2DRowRealMatrix(G, false);
    DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
    // DecompositionSolver solver = new QRDecomposition(coefficients).getSolver();

    if (!solver.isNonSingular()) {
      // solver = new QRDecomposition(coefficients).getSolver();
      solver = new SingularValueDecomposition(coefficients).getSolver();
    }

    RealVector constants = new ArrayRealVector(RHS, false);
    RealVector solution = solver.solve(constants);

    // System.out.println("node voltages: " + Arrays.toString(solution.toArray()));

    DCOperatingPointResult dcOperatingPointResult = new DCOperatingPointResult(unknownVariableNames, G, solution.toArray(), RHS);

    // System.out.println("dcOperatingPoint= " + (System.currentTimeMillis() - start));

    return dcOperatingPointResult;
  }

  public static DCOperatingPointResult solveMatrixWithInitialConditions(double[] solutionVector, double[][] G, String[] unknownVariableNames,
      double[] RHS) throws SingularMatrixException {

    DCOperatingPointResult dcOperatingPointResult = new DCOperatingPointResult(unknownVariableNames, G, solutionVector, RHS);

    return dcOperatingPointResult;
  }
}
