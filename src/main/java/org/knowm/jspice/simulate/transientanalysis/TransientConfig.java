package org.knowm.jspice.simulate.transientanalysis;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransientConfig extends SimulationConfig {

  @Valid
  @NotNull
  @JsonProperty("stop_time")
  @Min(0)
  double stopTime;

  @Valid
  @NotNull
  @JsonProperty("time_step")
  @Min(0)
  double timeStep;

  @Valid
  @NotNull
  @JsonProperty("drivers")
  @Min(0)
  Driver[] drivers;

  public TransientConfig(@JsonProperty("stop_time") double stopTime, @JsonProperty("time_step") double timeStep,
      @JsonProperty("drivers") Driver[] drivers) {
    this.stopTime = stopTime;
    this.timeStep = timeStep;
    this.drivers = drivers;
  }

  public double getStopTime() {
    return stopTime;
  }

  public double getTimeStep() {
    return timeStep;
  }

  public Driver[] getDrivers() {
    return drivers;
  }

}
