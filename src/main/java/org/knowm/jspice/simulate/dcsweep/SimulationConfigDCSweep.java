package org.knowm.jspice.simulate.dcsweep;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.simulate.SimulationConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimulationConfigDCSweep extends SimulationConfig {

  @Valid
  @NotNull
  @JsonProperty("sweep_id")
  private String sweepID;

  @Valid
  @NotNull
  @JsonProperty("observe_id")
  private String observeID;

  @Valid
  @NotNull
  @JsonProperty("start_value")
  private double startValue;

  @Valid
  @NotNull
  @JsonProperty("end_value")
  private double endValue;

  @Valid
  @NotNull
  @JsonProperty("step_size")
  private double stepSize;

}