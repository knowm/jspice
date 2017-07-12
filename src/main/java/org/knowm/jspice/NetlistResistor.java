package org.knowm.jspice;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.netlist.NetlistComponent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonTypeName("resistor")
public class NetlistResistor extends NetlistComponent {

  @Valid
  @NotNull
  @JsonProperty("resistance")
  double resistance;

  public NetlistResistor(@JsonProperty("id") String id, @JsonProperty("resistance") double resistance, @JsonProperty("nodes") String[] nodesAsArray) {

    super(new Resistor(id, resistance), nodesAsArray);
    this.resistance = resistance;
  }

  @JsonCreator
  public NetlistResistor(@JsonProperty("id") String id, @JsonProperty("resistance") double resistance, @JsonProperty("nodes") String nodes) {

    super(new Resistor(id, resistance), nodes);
    this.resistance = resistance;
  }
}
