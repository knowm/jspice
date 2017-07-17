/**
 * jspice is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the jspice
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of jspice which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.jspice.simulate.dcsweep;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.simulate.SimulationConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DCSweepConfig extends SimulationConfig {

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
  @Min(0)
  private double startValue;

  @Valid
  @NotNull
  @JsonProperty("end_value")
  @Min(0)
  private double endValue;

  @Valid
  @NotNull
  @JsonProperty("step_size")
  @Min(0)
  private double stepSize;

  public DCSweepConfig(@JsonProperty("sweep_id") String sweepID, @JsonProperty("observe_id") String observeID,
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