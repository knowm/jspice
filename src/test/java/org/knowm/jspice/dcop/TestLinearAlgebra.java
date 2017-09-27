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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

/**
 * @author timmolter
 */
public class TestLinearAlgebra {

  @Test
  public void test0() {

    RealMatrix coefficients = new Array2DRowRealMatrix(new double[][]{{0.001}}, false);
    DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();

    RealVector constants = new ArrayRealVector(new double[]{1}, false);
    RealVector solution = solver.solve(constants);

    // System.out.println(Arrays.toString(solution.toArray()));
    assertThat(solution.toArray()[0]).isEqualTo(1000.0);
  }

  @Test
  public void test1() {

    RealMatrix coefficients = new Array2DRowRealMatrix(new double[][]{{3, 2, -1}, {2, -2, 4}, {-1, .5, -1}}, false);
    DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();

    RealVector constants = new ArrayRealVector(new double[]{1, -2, 0}, false);
    RealVector solution = solver.solve(constants);

    // System.out.println(Arrays.toString(solution.toArray()));

    assertThat(solution.toArray()[0]).isEqualTo(1.0);
    assertThat(solution.toArray()[1]).isCloseTo(-2.0, within(.001));
    assertThat(solution.toArray()[2]).isCloseTo(-2.0, within(.001));
  }
}
