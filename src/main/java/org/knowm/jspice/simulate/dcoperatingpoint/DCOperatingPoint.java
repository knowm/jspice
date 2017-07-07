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

import java.util.Map;

import org.knowm.jspice.circuit.Circuit;

/**
 * @author timmolter
 */
public final class DCOperatingPoint {

  private final Circuit circuit;
  private final Double timeStep;
  private final DCOperatingPointResult previousDcOperatingPointResult;
  private ConvergenceTracker convergenceTracker;

  /**
   * Constructor
   *
   * @param circuit
   */
  public DCOperatingPoint(Circuit circuit) {

    this(circuit, null);
  }

  /**
   * Constructor
   *
   * @param circuit
   * @param timeStep
   */
  public DCOperatingPoint(Circuit circuit, Double timeStep) {

    this(null, circuit, timeStep);
  }

  /**
   * @param previousDcOperatingPointResult
   * @param circuit
   * @param timeStep
   */
  public DCOperatingPoint(DCOperatingPointResult previousDcOperatingPointResult, Circuit circuit, Double timeStep) {

    this.previousDcOperatingPointResult = previousDcOperatingPointResult;
    this.circuit = circuit;
    this.timeStep = timeStep;
  }

  /**
   * Run the DC Operating Point Analysis
   *
   * @return DCOperatingPointResult - an object containing the matrix data used for the analysis including the node voltages
   */
  public DCOperatingPointResult run() {

    // long start = System.currentTimeMillis();

    circuit.verifyCircuit();

    DCOperatingPointResult dcOperatingPointResult = previousDcOperatingPointResult;

    convergenceTracker = new ConvergenceTracker(circuit.isNonlinearCircuit(), circuit.isInitialConditions());

    // determine array indices
    Map<String, Integer> nodeID2ColumnIdxMap = CircuitMatrixSolver.getNodeID2ColumnIdxMap(circuit, timeStep); // <nodeName, array index>
    // System.out.println("nodeID2ColumnIdxMap= " + nodeID2ColumnIdxMap);

    // unknown Quantity Names
    String[] unknownQuantityNames = CircuitMatrixSolver.getUnknownVariableNames(nodeID2ColumnIdxMap, circuit, timeStep);
    // System.out.println("unknownQuantities= " + Arrays.toString(unknownQuantities));
    // System.out.println("unknownQuantities.length= " + unknownQuantities.length);

    do {

      //      System.out.println("------------DCOP-------------");
      // G
      // at this point all the non-linear and reactive component have been converted to resistors, dc voltage and current sources/
      double[][] G = CircuitMatrixSolver.getG(nodeID2ColumnIdxMap, circuit, dcOperatingPointResult, timeStep);
      // System.out.println("G= " + CircuitMatrixSolver.GtoString(G));

      // RHS
      double[] RHS = CircuitMatrixSolver.getRHS(nodeID2ColumnIdxMap, circuit, dcOperatingPointResult, timeStep);
      // System.out.println("I= " + Arrays.toString(I));

      // trim G, remove "O"th row and column
      double[][] G_trimmed = CircuitMatrixSolver.trimG(G, nodeID2ColumnIdxMap);
      // for (int i = 0; i < G2.length; i++) {
      // for (int j = 0; j < G2[i].length; j++) {
      // if (G2[i][j] == 0.0) {
      // G2[i][j] = 0.000000000001; // 10^-12
      // }
      // }
      // }
      // System.out.println("G_trimmed= " + CircuitMatrixSolver.GtoString(G_trimmed));

      // trim I, remove "O"th row
      double[] RHS_trimmed = CircuitMatrixSolver.trimVector(RHS, nodeID2ColumnIdxMap);
      // System.out.println("RHS_trimmed= " + Arrays.toString(RHS_trimmed));

      if (circuit.isInitialConditions()) {

        double[] solutionVector = CircuitMatrixSolver.getInitialConditionsSolutionVector(nodeID2ColumnIdxMap, circuit, RHS);
        double[] solutionVector_trimmed = CircuitMatrixSolver.trimVector(solutionVector, nodeID2ColumnIdxMap);
        dcOperatingPointResult = CircuitMatrixSolver.solveMatrixWithInitialConditions(solutionVector_trimmed, G_trimmed, unknownQuantityNames,
            RHS_trimmed);
      } else {

        dcOperatingPointResult = CircuitMatrixSolver.solveMatrix(G_trimmed, unknownQuantityNames, RHS_trimmed);
      }
      //      System.out.println(dcOperatingPointResult.getNodalAnalysisMatrix());

      dcOperatingPointResult.generateDeviceCurrents(circuit);
    } while (!convergenceTracker.update(dcOperatingPointResult));

    // System.out.println("Iterations= " + convergenceTracker.getNumIterationsToConvergence());

    // System.out.println(dcOperatingPointResult.getNodalAnalysisMatrix());

    //    dcOperatingPointResult.generateDeviceCurrents(circuit);

    // System.out.println("dcOperatingPoint= " + (System.currentTimeMillis() - start));

    return dcOperatingPointResult;
  }
}
