package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.source.VCCS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistVCCS extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("transconductance")
  double transconductance;

  public NetlistVCCS(String id, double transconductance, String... nodes) {

    super(new VCCS(id, transconductance), nodes);
    this.transconductance = transconductance;
  }

  @JsonCreator
  public NetlistVCCS(@JsonProperty("id") String id, @JsonProperty("transconductance") double transconductance, @JsonProperty("nodes") String nodes) {

    super(new VCCS(id, transconductance), nodes);
    this.transconductance = transconductance;
  }

}
