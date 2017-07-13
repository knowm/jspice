package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.source.DCVoltage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistDCVoltage extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("voltage")
  double voltage;

  public NetlistDCVoltage(String id, double voltage, String... nodes) {

    super(new DCVoltage(id, voltage), nodes);
    this.voltage = voltage;
  }

  @JsonCreator
  public NetlistDCVoltage(@JsonProperty("id") String id, @JsonProperty("voltage") double voltage, @JsonProperty("nodes") String nodes) {

    super(new DCVoltage(id, voltage), nodes);
    this.voltage = voltage;
  }

}
