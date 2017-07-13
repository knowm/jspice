package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.memristor.MMSSMemristor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistMMSSMemristor extends NetlistComponent {

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

  public NetlistMMSSMemristor(String id, double rInit, double rOn, double rOff, double tau, double vOn, double vOff, double phi,
      double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta, String... nodes) {

    super(new MMSSMemristor(id, rInit, rOn, rOff, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha,
        schottkyReverseBeta), nodes);
    this.rInit = rInit;
    this.rOn = rOn;
    this.rOff = rOff;
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
  public NetlistMMSSMemristor(@JsonProperty("id") String id, @JsonProperty("r_init") double rInit, @JsonProperty("r_on") double rOn,
      @JsonProperty("r_off") double rOff, @JsonProperty("n") double n, @JsonProperty("tau") double tau, @JsonProperty("v_on") double vOn,
      @JsonProperty("v_off") double vOff, @JsonProperty("phi") double phi, @JsonProperty("schottky_forward_alpha") double schottkyForwardAlpha,
      @JsonProperty("schottky_forward_beta") double schottkyForwardBeta, @JsonProperty("schottky_reverse_alpha") double schottkyReverseAlpha,
      @JsonProperty("schottky_reverse_beta") double schottkyReverseBeta, @JsonProperty("nodes") String nodes) {

    super(new MMSSMemristor(id, rInit, rOn, rOff, tau, vOn, vOff, phi, schottkyForwardAlpha, schottkyForwardBeta, schottkyReverseAlpha,
        schottkyReverseBeta), nodes);
    this.rInit = rInit;
    this.rOn = rOn;
    this.rOff = rOff;
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
