package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.nonlinear.PMOS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistPMOS extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("threshold")
  double threshold;

  public NetlistPMOS(String id, double threshold, String... nodes) {

    super(new PMOS(id, threshold), nodes);
    this.threshold = threshold;
  }

  @JsonCreator
  public NetlistPMOS(@JsonProperty("id") String id, @JsonProperty("threshold") double threshold, @JsonProperty("nodes") String nodes) {

    super(new PMOS(id, threshold), nodes);
    this.threshold = threshold;
  }
}
