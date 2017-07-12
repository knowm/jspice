package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.source.DCCurrent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistDCCurrent extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("current")
  double current;

  public NetlistDCCurrent(@JsonProperty("id") String id, @JsonProperty("current") double current, @JsonProperty("nodes") String... nodes) {

    super(new DCCurrent(id, current), nodes);
    this.current = current;
  }

  @JsonCreator
  public NetlistDCCurrent(@JsonProperty("id") String id, @JsonProperty("current") double current, @JsonProperty("nodes") String nodes) {

    super(new DCCurrent(id, current), nodes);
    this.current = current;
  }

}
