package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.nonlinear.NMOS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistNMOS extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("threshold")
  double threshold;

  public NetlistNMOS(String id, double threshold, String... nodes) {

    super(new NMOS(id, threshold), nodes);
    this.threshold = threshold;
  }

  @JsonCreator
  public NetlistNMOS(@JsonProperty("id") String id, @JsonProperty("threshold") double threshold, @JsonProperty("nodes") String nodes) {

    super(new NMOS(id, threshold), nodes);
    this.threshold = threshold;
  }
}
