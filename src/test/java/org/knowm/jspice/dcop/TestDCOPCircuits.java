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

import org.junit.Test;
import org.knowm.jspice.circuits.CMOSInverterCircuit;
import org.knowm.jspice.circuits.I1IAR1R2;
import org.knowm.jspice.circuits.I1R1;
import org.knowm.jspice.circuits.I1R3;
import org.knowm.jspice.circuits.I1R4;
import org.knowm.jspice.circuits.I1V1R6;
import org.knowm.jspice.circuits.I1VAR1R2;
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

    assertThat(dcOpResult.getV()[0]).isEqualTo(1000.0);
  }

  @Test
  public void testI1R3() {

    Netlist circuit = new I1R3();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(9.95, within(.001));
    assertThat(dcOpResult.getV()[1]).isCloseTo(4.975, within(.001));
  }

  @Test
  public void testI1R4() {

    Netlist circuit = new I1R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(195.23, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(95.23, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(47.61, within(.01));
  }

  @Test
  public void testI2R4() {

    Netlist circuit = new I2R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(292.85, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(142.85, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(71.42, within(.01));
  }

  @Test
  public void testI2R6() {

    Netlist circuit = new I2R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(2350, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(2250, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(750, within(.01));
    assertThat(dcOpResult.getV()[3]).isCloseTo(12350, within(.01));
    assertThat(dcOpResult.getV()[4]).isCloseTo(-50.0, within(.01));
  }

  @Test
  public void testI1V1R6() {

    Netlist circuit = new I1V1R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(13.25, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(11.25, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(3.75, within(.01));
    assertThat(dcOpResult.getV()[3]).isCloseTo(213.25, within(.01));
    assertThat(dcOpResult.getV()[4]).isCloseTo(1.25, within(.01));
  }

  @Test
  public void testV1R1() {

    Netlist circuit = new V1R1();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isEqualTo(10.0);
  }

  @Test
  public void testV1R3() {

    Netlist circuit = new V1R3();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isEqualTo(10.0);
    assertThat(dcOpResult.getV()[1]).isEqualTo(5.0);
  }

  @Test
  public void testV1R4() {

    Netlist circuit = new V1R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isEqualTo(10.0);
    assertThat(dcOpResult.getV()[1]).isCloseTo(4.87, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(2.43, within(.01));
  }

  @Test
  public void testV2R4() {

    Netlist circuit = new V2R4();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isEqualTo(15.0);
    assertThat(dcOpResult.getV()[1]).isCloseTo(7.317, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(3.65, within(.01));
    assertThat(dcOpResult.getV()[3]).isEqualTo(10.0);
  }

  @Test
  public void testV2R5() {

    Netlist circuit = new V2R5();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isEqualTo(15.0);
    assertThat(dcOpResult.getV()[1]).isCloseTo(7.25, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(2.41, within(.01));
    assertThat(dcOpResult.getV()[3]).isEqualTo(10.0);
  }

  @Test
  public void testV2R6() {

    Netlist circuit = new V2R6();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getV()[0]).isCloseTo(4.78, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(4.73, within(.01));
    assertThat(dcOpResult.getV()[2]).isCloseTo(1.57, within(.01));
    assertThat(dcOpResult.getV()[3]).isEqualTo(10.0);
  }

  @Test
  public void testV1D1() {

    Netlist circuit = new V1D1();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());

    assertThat(dcOpResult.getValue("V(1)")).isCloseTo(0.95, within(.01));
    assertThat(dcOpResult.getValue("I(D1)")).isCloseTo(11.65, within(.1));
  }

  @Test
  public void testNMOSInverter() {

    Netlist circuit = new NMOSInverter();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());
    // System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getV()[0]).isCloseTo(0, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(5.0, within(.1));
  }

  @Test
  public void testPMOSInverter() {

    Netlist circuit = new PMOSInverter();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    // System.out.println(dcOpResult.toString());
    // System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getV()[0]).isCloseTo(0, within(.01));
    assertThat(dcOpResult.getV()[1]).isCloseTo(5.0, within(.1));
  }

  @Test
  public void testCMOSInverter() {

    Netlist circuit = new CMOSInverterCircuit();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());
    //    System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getValue("V(out)")).isCloseTo(0, within(.01));
  }

  @Test
  public void testI1IAR1R2() {

    Netlist circuit = new I1IAR1R2();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());
    //    System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getValue("V(2)")).isCloseTo(-4, within(.01));
  }

  @Test
  public void testI1VAR1R2() {

    Netlist circuit = new I1VAR1R2();
    DCOperatingPointResult dcOpResult = new DCOperatingPoint(circuit).run();
    //    System.out.println(dcOpResult.toString());
    //    System.out.println(Arrays.toString(dcOpResult.getV()));

    assertThat(dcOpResult.getValue("V(2)")).isCloseTo(4, within(.01));
  }
}
