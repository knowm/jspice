package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.source.VCVS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistVCVS extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("gain")
  double gain;

  public NetlistVCVS(String id, double gain, String... nodes) {

    super(new VCVS(id, gain), nodes);
    this.gain = gain;
  }

  @JsonCreator
  public NetlistVCVS(@JsonProperty("id") String id, @JsonProperty("gain") double gain, @JsonProperty("nodes") String nodes) {

    super(new VCVS(id, gain), nodes);
    this.gain = gain;
  }

}
