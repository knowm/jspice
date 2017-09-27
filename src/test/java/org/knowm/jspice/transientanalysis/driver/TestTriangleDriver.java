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
package org.knowm.jspice.transientanalysis.driver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;
import org.knowm.jspice.simulate.transientanalysis.driver.Triangle;

public class TestTriangleDriver extends TestDrivers {

  /**
   * @param args
   */
  public static void main(String[] args) {

    TestTriangleDriver testDrivers = new TestTriangleDriver();
    testDrivers.test();
  }

  @Test
  public void test() {

    Driver driver = new Triangle("Triangle", 5, "0.25", 10, "2");
    BigDecimal stopTime = new BigDecimal("2");
    BigDecimal timeStep = new BigDecimal(".01");

    List<Number> xData = new ArrayList<>();
    List<Number> yData = new ArrayList<>();

    BigDecimal firstPoint = BigDecimal.ZERO;
    for (BigDecimal t = firstPoint; t.compareTo(stopTime) < 0; t = t.add(timeStep)) {
      if (counter == point2Verify) {
        y = driver.getSignal(t);
      }
      counter++;
      xData.add(t);
      yData.add(driver.getSignal(t));
    }

    // System.out.println(xData);
    // System.out.println(yData);
    // System.out.println(y);

    assertThat(xData.size()).isEqualTo(200);
    assertThat(y).isCloseTo(2.599, within( .01));

    //    plotData("V(in)", xData, yData);
  }
}
