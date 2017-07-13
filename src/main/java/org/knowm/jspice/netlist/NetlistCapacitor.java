package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.reactive.Capacitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistCapacitor extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("capacitance")
  double capacitance;

  public NetlistCapacitor(String id, double capacitance, String... nodes) {

    super(new Capacitor(id, capacitance), nodes);
    this.capacitance = capacitance;
  }

  @JsonCreator
  public NetlistCapacitor(@JsonProperty("id") String id, @JsonProperty("capacitance") double capacitance, @JsonProperty("nodes") String nodes) {

    super(new Capacitor(id, capacitance), nodes);
    this.capacitance = capacitance;
  }
}
