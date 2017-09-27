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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistDCCurrent;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;

/**
 * @author timmolter
 */
public class TestCircuitRuntimeExceptions {

  @Test
  public void testNoNode0Exception() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCCurrent("a", 1.0, "99", "1"));
    netlist.addNetListComponent(new NetlistResistor("R1", 10, "1", "99"));
    netlist.addNetListComponent(new NetlistResistor("R2", 1000, "1", "2"));
    netlist.addNetListComponent(new NetlistResistor("R3", 1000, "2", "99"));

    try {
      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Node \"0\" must be part of the netlist representing ground!");
    }
  }

  @Test
  public void testDanglingNodeException() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCCurrent("a", 1.0, "0", "1"));
    netlist.addNetListComponent(new NetlistResistor("R1", 10, "1", "0"));
    netlist.addNetListComponent(new NetlistResistor("R2", 1000, "1", "2"));

    try {
      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Must have at least 2 Connections for node 2!");
    }
  }

  @Test
  public void testMinimumNetListSizeException() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCCurrent("a", 1.0, "0", "1"));

    try {
      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Must have at least 2 NetListParts!");
    }
  }

  @Test
  public void testNonUniqueNameException() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCCurrent("a", 1.0, "0", "1"));
    netlist.addNetListComponent(new NetlistResistor("R1", 10, "1", "0"));
    netlist.addNetListComponent(new NetlistResistor("R2", 1000, "1", "2"));
    try {
      netlist.addNetListComponent(new NetlistResistor("R1", 1000, "2", "0"));

      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("The component ID R1 is not unique!");
    }
  }

  @Test
  public void testConnectedToSameNodeException() {

    Netlist netlist = new Netlist();

    try {
      netlist.addNetListComponent(new NetlistResistor("R1", 10, "2", "2"));

      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("A component cannot be connected to the same node twice!");
    }
  }

  @Test
  public void testSeriesCurrentSourcesException() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCCurrent("a", 1.0, "0", "1"));
    netlist.addNetListComponent(new NetlistDCCurrent("b", 1.0, "1", "2"));
    netlist.addNetListComponent(new NetlistResistor("R1", 10, "0", "2"));

    try {
      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Current sources cannot be in series!");
    }
  }

  @Test
  public void testParalleVoltageSourcesException() {

    Netlist netlist = new Netlist();

    // build netlist, the nodes can be named anything except for ground whose node is always labeled "0"
    netlist.addNetListComponent(new NetlistDCVoltage("a", 10.0, "0", "1"));
    netlist.addNetListComponent(new NetlistDCVoltage("b", 10.0, "0", "1"));
    netlist.addNetListComponent(new NetlistResistor("R1", 10, "0", "1"));

    try {
      netlist.verifyCircuit();
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Voltage sources cannot be in parallel!");
    }
  }
}
