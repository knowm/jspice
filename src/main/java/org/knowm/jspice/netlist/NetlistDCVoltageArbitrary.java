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
package org.knowm.jspice.netlist;

import org.knowm.jspice.component.source.DCCurrentArbitrary;
import org.knowm.jspice.component.source.DCVoltageArbitrary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistDCVoltageArbitrary extends NetlistComponent {

  public NetlistDCVoltageArbitrary(String id, String expression, String... nodes) {

    super(new DCVoltageArbitrary(id, expression), nodes);
  }

  @JsonCreator
  public NetlistDCVoltageArbitrary(@JsonProperty("id") String id, @JsonProperty("expression") String expression, @JsonProperty("nodes") String nodes) {

    super(new DCCurrentArbitrary(id, expression), nodes);
  }
}
