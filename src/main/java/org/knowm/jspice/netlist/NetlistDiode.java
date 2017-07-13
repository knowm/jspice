package org.knowm.jspice.netlist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.nonlinear.Diode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetlistDiode extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("saturation_current")
  double saturationCurrent;

  public NetlistDiode(String id, double saturationCurrent, String... nodes) {

    super(new Diode(id, saturationCurrent), nodes);
    this.saturationCurrent = saturationCurrent;
  }

  @JsonCreator
  public NetlistDiode(@JsonProperty("id") String id, @JsonProperty("saturation_current") double saturationCurrent,
      @JsonProperty("nodes") String nodes) {

    super(new Diode(id, saturationCurrent), nodes);
    this.saturationCurrent = saturationCurrent;
  }
}
