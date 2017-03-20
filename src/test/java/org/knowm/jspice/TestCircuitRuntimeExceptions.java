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
package org.knowm.jspice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.knowm.jspice.circuit.Circuit;
import org.knowm.jspice.component.element.linear.LinearElement;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.Source;

/**
 * @author timmolter
 */
public class TestCircuitRuntimeExceptions {

  @Test
  public void testNoNode0Exception() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcCurrentSource = new DCCurrent("a", 1.0);

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);
    LinearElement resistor2 = new Resistor("R2", 1000);
    LinearElement resistor3 = new Resistor("R3", 1000);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcCurrentSource, "99", "1");
    circuit.addNetListComponent(resistor1, "1", "99");
    circuit.addNetListComponent(resistor2, "1", "2");
    circuit.addNetListComponent(resistor3, "2", "99");

    try {
      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("Node \"0\" must be part of the netlist representing ground!")));
    }
  }

  @Test
  public void testDanglingNodeException() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcCurrentSource = new DCCurrent("a", 1.0);

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);
    LinearElement resistor2 = new Resistor("R2", 1000);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcCurrentSource, "0", "1");
    circuit.addNetListComponent(resistor1, "1", "0");
    circuit.addNetListComponent(resistor2, "1", "2");

    try {
      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("Must have at least 2 Connections for node 2!")));
    }
  }

  @Test
  public void testMinimumNetListSizeException() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcCurrentSource = new DCCurrent("a", 1.0);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcCurrentSource, "0", "1");

    try {
      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("Must have at least 2 NetListParts!")));
    }
  }

  @Test
  public void testNonUniqueNameException() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcCurrentSource = new DCCurrent("a", 1.0);

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);
    LinearElement resistor2 = new Resistor("R2", 1000);
    LinearElement resistor3 = new Resistor("R1", 1000);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcCurrentSource, "0", "1");
    circuit.addNetListComponent(resistor1, "1", "0");
    circuit.addNetListComponent(resistor2, "1", "2");
    try {
      circuit.addNetListComponent(resistor3, "2", "0");

      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("The component ID R1 is not unique!")));
    }
  }

  @Test
  public void testConnectedToSameNodeException() {

    Circuit circuit = new Circuit();

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);

    try {
      circuit.addNetListComponent(resistor1, "2", "2");

      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("A component cannot be connected to the same node twice!")));
    }
  }

  @Test
  public void testSeriesCurrentSourcesException() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcCurrentSourceA = new DCCurrent("a", 1.0);
    Source dcCurrentSourceB = new DCCurrent("b", 1.0);

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcCurrentSourceA, "0", "1");
    circuit.addNetListComponent(dcCurrentSourceB, "1", "2");
    circuit.addNetListComponent(resistor1, "0", "2");

    try {
      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("Current sources cannot be in series!")));
    }
  }

  @Test
  public void testParalleVoltageSourcesException() {

    Circuit circuit = new Circuit();

    // define current source
    Source dcVoltageSourceA = new DCVoltage("a", 10.0);
    Source dcVoltageSourceB = new DCVoltage("b", 10.0);

    // define resistors
    LinearElement resistor1 = new Resistor("R1", 10);

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    circuit.addNetListComponent(dcVoltageSourceA, "0", "1");
    circuit.addNetListComponent(dcVoltageSourceB, "0", "1");
    circuit.addNetListComponent(resistor1, "0", "1");

    try {
      circuit.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is(equalTo("Voltage sources cannot be in parallel!")));
    }
  }
}
