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

import org.knowm.jspice.component.element.memristor.RSMemristor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistRSMemristor extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("schottky_forward_alpha")
  double schottkyForwardAlpha;

  @Valid
  @NotNull
  @JsonProperty("schottky_forward_beta")
  double schottkyForwardBeta;

  @Valid
  @NotNull
  @JsonProperty("schottky_reverse_alpha")
  double schottkyReverseAlpha;

  @Valid
  @NotNull
  @JsonProperty("schottky_reverse_beta")
  double schottkyReverseBeta;

  @Valid
  @NotNull
  @JsonProperty("phi")
  double phi;

  public NetlistRSMemristor(String id, double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha,
      double schottkyReverseBeta, double phi, String... nodes) {

    super(new RSMemristor(id, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha, schottkyReverseBeta, phi), nodes);
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
    this.phi = phi;
  }

  @JsonCreator
  public NetlistRSMemristor(@JsonProperty("id") String id, @JsonProperty("schottky_forward_alpha") double schottkyForwardAlpha,
      @JsonProperty("schottky_forward_beta") double schottkyForwardBeta, @JsonProperty("schottky_reverse_alpha") double schottkyReverseAlpha,
      @JsonProperty("schottky_reverse_beta") double schottkyReverseBeta, @JsonProperty("phi") double phi, @JsonProperty("nodes") String nodes) {

    super(new RSMemristor(id, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha, schottkyReverseBeta, phi), nodes);
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
    this.phi = phi;
  }
}
