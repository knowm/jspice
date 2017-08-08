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
import java.util.Map.Entry;

/**
 * @author timmolter
 */
public class ConvergenceTracker {

  private static final int MAX_NUM_ITERATIONS = 10000;

  /**
   * relative tolerance
   */
  private static final double RELTOL = 0.001; // 0.1 percent

  /**
   * absolute voltage tolerance
   */
  private static final double VNTOL = 0.000001; // 1 microVolt

  /**
   * absolute current tolerance
   */
  private static final double ABSTOL = .0000000001; // 100 pA

  private final boolean nonLinearCircuit;
  private final boolean isInitialConditions;

  private double[] oldValues = null;

  private int iterationCounter = 0;

  /**
   * Constructor
   *
   * @param nonLinearCircuit
   * @param isInitialConditions
   */
  public ConvergenceTracker(boolean nonLinearCircuit, boolean isInitialConditions) {

    this.nonLinearCircuit = nonLinearCircuit;
    this.isInitialConditions = isInitialConditions;
  }

  public boolean update(DCOperatingPointResult dcOperatingPointResult) {

    if (!nonLinearCircuit || isInitialConditions) { // a linear circuit, or initial conditions are provided
      return true;
    }

    if (iterationCounter++ > MAX_NUM_ITERATIONS) {
      throw new NodalAnalysisConvergenceException();
      // return true;
    }

    Map<String, Double> nodeLabels2Value = dcOperatingPointResult.getNodeLabels2Value(); // <"V(1)", 39.2>
    if (oldValues == null) { // just populate the old values array
      oldValues = new double[nodeLabels2Value.size()];
      int idx = 0;
      for (Entry<String, Double> entry : nodeLabels2Value.entrySet()) {

        String nodeLabel = entry.getKey();
        Double nodeValue = entry.getValue();
        // System.out.println("nodeLabel= " + nodeLabel);
        // System.out.println("nodeValue= " + nodeValue);

        oldValues[idx++] = nodeValue;
      }
      // System.out.println("---");
      return false;
    } else {
      boolean converged = true;
      int idx = 0;
      for (Entry<String, Double> entry : nodeLabels2Value.entrySet()) {

        String nodeLabel = entry.getKey();
        Double nodeValue = entry.getValue();
        // System.out.println("nodeLabel= " + nodeLabel);
        // System.out.println("nodeValue= " + nodeValue);

        // compare old to new
        double difference = Math.abs(nodeValue - oldValues[idx]);
        // System.out.println(nodeLabel + " difference= " + difference);
        if (nodeLabel.startsWith("V")) {
          if (difference > Math.abs(nodeValue) * RELTOL + VNTOL) {
            // System.out.println("toleranceV= " + Math.abs(nodeValue) * RELTOL + VNTOL);
            // System.out.println("nodeValue= " + nodeValue);
            // System.out.println("oldValues[idx]= " + oldValues[idx]);
            // System.out.println(nodeLabel + " difference= " + difference);
            converged = false;
          }
        } else if (nodeLabel.startsWith("I")) {
          if (difference > Math.abs(nodeValue) * RELTOL + ABSTOL) {
            // System.out.println("toleranceI= " + Math.abs(nodeValue) * RELTOL + VNTOL);
            // System.out.println(nodeLabel + " difference= " + difference);
            converged = false;
          }
        } else {
          throw new IllegalArgumentException("Unknown node label type encountered!");
        }

        // rewrite value
        oldValues[idx] = nodeValue;
        idx++;
      }
      // set converged or not
      return converged;
    }
  }

  public int getNumIterationsToConvergence() {

    return iterationCounter;
  }
}