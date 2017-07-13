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
