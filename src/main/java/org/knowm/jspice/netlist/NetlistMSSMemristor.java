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

import org.knowm.jspice.component.element.memristor.MSSMemristor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistMSSMemristor extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("r_init")
  private double rInit;
  @Valid
  @NotNull
  @JsonProperty("r_on")
  private double rOn;

  @Valid
  @NotNull
  @JsonProperty("r_off")
  private double rOff;

  @Valid
  @NotNull
  @JsonProperty("n")
  private double n;

  @Valid
  @NotNull
  @JsonProperty("tau")
  private double tau;

  @Valid
  @NotNull
  @JsonProperty("v_on")
  private double vOn;

  @Valid
  @NotNull
  @JsonProperty("v_off")
  private double vOff;

  @Valid
  @NotNull
  @JsonProperty("phi")
  private double phi;

  @Valid
  @NotNull
  @JsonProperty("schottky_forward_alpha")
  private double schottkyForwardAlpha;

  @Valid
  @NotNull
  @JsonProperty("schottky_forward_beta")
  private double schottkyForwardBeta;

  @Valid
  @NotNull
  @JsonProperty("schottky_reverse_alpha")
  private double schottkyReverseAlpha;

  @Valid
  @NotNull
  @JsonProperty("schottky_reverse_beta")
  private double schottkyReverseBeta;

  public NetlistMSSMemristor(String id, double rInit, double rOn, double rOff, double n, double tau, double vOn, double vOff, double phi,
      double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta, String... nodes) {

    super(new MSSMemristor(id, rInit, rOn, rOff, n, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha,
        schottkyReverseBeta), nodes);
    this.rInit = rInit;
    this.rOn = rOn;
    this.rOff = rOff;
    this.n = n;
    this.tau = tau;
    this.vOn = vOn;
    this.vOff = vOff;
    this.phi = phi;
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
  }

  @JsonCreator
  public NetlistMSSMemristor(@JsonProperty("id") String id, @JsonProperty("r_init") double rInit, @JsonProperty("r_on") double rOn,
      @JsonProperty("r_off") double rOff, @JsonProperty("n") double n, @JsonProperty("tau") double tau, @JsonProperty("v_on") double vOn,
      @JsonProperty("v_off") double vOff, @JsonProperty("phi") double phi, @JsonProperty("schottky_forward_alpha") double schottkyForwardAlpha,
      @JsonProperty("schottky_forward_beta") double schottkyForwardBeta, @JsonProperty("schottky_reverse_alpha") double schottkyReverseAlpha,
      @JsonProperty("schottky_reverse_beta") double schottkyReverseBeta, @JsonProperty("nodes") String nodes) {

    super(new MSSMemristor(id, rInit, rOn, rOff, n, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha,
        schottkyReverseBeta), nodes);
    this.rInit = rInit;
    this.rOn = rOn;
    this.rOff = rOff;
    this.n = n;
    this.tau = tau;
    this.vOn = vOn;
    this.vOff = vOff;
    this.phi = phi;
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
  }
}
