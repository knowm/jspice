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
