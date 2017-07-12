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
import static org.hamcrest.Matchers.closeTo;

import org.junit.Test;
import org.knowm.jspice.circuits.CMOSInverterCircuit;
import org.knowm.jspice.circuits.I1R1;
import org.knowm.jspice.circuits.I1R3;
import org.knowm.jspice.circuits.I1R4;
import org.knowm.jspice.circuits.I1V1R6;
import org.knowm.jspice.circuits.I2R4;
import org.knowm.jspice.circuits.I2R6;
import org.knowm.jspice.circuits.NMOSInverter;
import org.knowm.jspice.circuits.PMOSInverter;
import org.knowm.jspice.circuits.V1D1;
import org.knowm.jspice.circuits.V1R1;
import org.knowm.jspice.circuits.V1R3;
import org.knowm.jspice.circuits.V1R4;
import org.knowm.jspice.circuits.V2R4;
import org.knowm.jspice.circuits.V2R5;
import org.knowm.jspice.circuits.V2R6;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

/**
 * @author timmolter
 */
public class TestDCOPCircuits {

  @Test
  public void testI1R1() {

    Netlist circuit = new I1R1();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(1000.0)));
  }

  @Test
  public void testI1R3() {

    Netlist circuit = new I1R3();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(9.95, .001)));
    assertThat(dcOpResult.getV()[1], is(closeTo(4.975, .001)));
  }

  @Test
  public void testI1R4() {

    Netlist circuit = new I1R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(195.23, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(95.23, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(47.61, .01)));
  }

  @Test
  public void testI2R4() {

    Netlist circuit = new I2R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(292.85, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(142.85, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(71.42, .01)));
  }

  @Test
  public void testI2R6() {

    Netlist circuit = new I2R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(2350, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(2250, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(750, .01)));
    assertThat(dcOpResult.getV()[3], is(closeTo(12350, .01)));
    assertThat(dcOpResult.getV()[4], is(closeTo(-50.0, .01)));
  }

  @Test
  public void testI1V1R6() {

    Netlist circuit = new I1V1R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(13.25, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(11.25, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(3.75, .01)));
    assertThat(dcOpResult.getV()[3], is(closeTo(213.25, .01)));
    assertThat(dcOpResult.getV()[4], is(closeTo(1.25, .01)));
  }

  @Test
  public void testV1R1() {

    Netlist circuit = new V1R1();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(10.0)));
  }

  @Test
  public void testV1R3() {

    Netlist circuit = new V1R3();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(10.0)));
    assertThat(dcOpResult.getV()[1], is(equalTo(5.0)));
  }

  @Test
  public void testV1R4() {

    Netlist circuit = new V1R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(10.0)));
    assertThat(dcOpResult.getV()[1], is(closeTo(4.87, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(2.43, .01)));
  }

  @Test
  public void testV2R4() {

    Netlist circuit = new V2R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(15.0)));
    assertThat(dcOpResult.getV()[1], is(closeTo(7.317, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(3.65, .01)));
    assertThat(dcOpResult.getV()[3], is(equalTo(10.0)));
  }

  @Test
  public void testV2R5() {

    Netlist circuit = new V2R5();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(equalTo(15.0)));
    assertThat(dcOpResult.getV()[1], is(closeTo(7.25, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(2.41, .01)));
    assertThat(dcOpResult.getV()[3], is(equalTo(10.0)));
  }

  @Test
  public void testV2R6() {

    Netlist circuit = new V2R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0], is(closeTo(4.78, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(4.73, .01)));
    assertThat(dcOpResult.getV()[2], is(closeTo(1.57, .01)));
    assertThat(dcOpResult.getV()[3], is(equalTo(10.0)));
  }

  @Test
  public void testV1D1() {

    Netlist circuit = new V1D1();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getValue("V(1)"), is(closeTo(0.95, .01)));
    assertThat(dcOpResult.getValue("I(D1)"), is(closeTo(11.65, .1)));
  }

  @Test
  public void testNMOSInverter() {

    Netlist circuit = new NMOSInverter();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());
    // System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getV()[0], is(closeTo(0, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(5.0, .1)));
  }

  @Test
  public void testPMOSInverter() {

    Netlist circuit = new PMOSInverter();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());
    // System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getV()[0], is(closeTo(0, .01)));
    assertThat(dcOpResult.getV()[1], is(closeTo(5.0, .1)));
  }

  @Test
  public void testCMOSInverter() {

    Netlist circuit = new CMOSInverterCircuit();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());
    //    System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getValue("V(out)"), is(closeTo(0, .01)));
  }
}
