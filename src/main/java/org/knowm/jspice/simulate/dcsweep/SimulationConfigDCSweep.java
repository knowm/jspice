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

  public SimulationConfigDCSweep(@JsonProperty("sweep_id") String sweepID, @JsonProperty("observe_id") String observeID,
      @JsonProperty("start_value") double startValue, @JsonProperty("end_value") double endValue, @JsonProperty("step_size") double stepSize) {

    this.sweepID = sweepID;
    this.observeID = observeID;
    this.startValue = startValue;
    this.endValue = endValue;
    this.stepSize = stepSize;
  }

  public String getSweepID() {
    return sweepID;
  }

  public String getObserveID() {
    return observeID;
  }

  public double getStartValue() {
    return startValue;
  }

  public double getEndValue() {
    return endValue;
  }

  public double getStepSize() {
    return stepSize;
  }

  @Override
  public String toString() {
    return "SimulationConfigDCSweep [sweepID=" + sweepID + ", observeID=" + observeID + ", startValue=" + startValue + ", endValue=" + endValue
        + ", stepSize=" + stepSize + "]";
  }

}