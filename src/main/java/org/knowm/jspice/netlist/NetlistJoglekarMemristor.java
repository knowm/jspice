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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.memristor.JoglekarMemristor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistJoglekarMemristor extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("r_init")
  private final double Rinit;

  @Valid
  @NotNull
  @JsonProperty("r_on")
  private double Ron;

  @Valid
  @NotNull
  @JsonProperty("r_off")
  private double Roff;

  @Valid
  @NotNull
  @JsonProperty("d")
  private double D;

  @Valid
  @NotNull
  @JsonProperty("uv")
  private double uv;

  @Valid
  @NotNull
  @JsonProperty("p")
  private double p;

  public NetlistJoglekarMemristor(String id, double Rinit, double Ron, double Roff, double D, double uv, double p, String... nodes) {

    super(new JoglekarMemristor(id, Rinit, Ron, Roff, D, uv, p), nodes);
    this.Rinit = Rinit;
    this.Ron = Ron;
    this.Roff = Roff;
    this.D = D;
    this.uv = uv;
    this.p = p;
  }

  @JsonCreator
  public NetlistJoglekarMemristor(@JsonProperty("id") String id, @JsonProperty("r_init") double Rinit, @JsonProperty("r_on") double Ron,
      @JsonProperty("r_off") double Roff, @JsonProperty("d") double D, @JsonProperty("uv") double uv, @JsonProperty("p") double p,
      @JsonProperty("nodes") String nodes) {

    super(new JoglekarMemristor(id, Rinit, Ron, Roff, D, uv, p), nodes);
    this.Rinit = Rinit;
    this.Ron = Ron;
    this.Roff = Roff;
    this.D = D;
    this.uv = uv;
    this.p = p;
  }
}
