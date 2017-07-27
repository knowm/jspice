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
package org.knowm.jspice.simulate.transientanalysis;

import java.util.Arrays;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.simulate.SimulationConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransientConfig extends SimulationConfig {

  @Valid
  @NotNull
  @JsonProperty("stop_time")
  String stopTime;

  @Valid
  @NotNull
  @JsonProperty("time_step")
  String timeStep;

  @Valid
  @NotNull
  @JsonProperty("drivers")
  Driver[] drivers;

  public TransientConfig(@JsonProperty("stop_time") String stopTime, @JsonProperty("time_step") String timeStep,
      @JsonProperty("drivers") Driver... drivers) {
    this.stopTime = stopTime;
    this.timeStep = timeStep;
    this.drivers = drivers;
  }

  public String getStopTime() {
    return stopTime;
  }

  public String getTimeStep() {
    return timeStep;
  }

  public Driver[] getDrivers() {
    return drivers;
  }

  @Override
  public String toString() {
    return "TransientConfig{" +
        "stopTime=" + stopTime +
        ", timeStep=" + timeStep +
        ", drivers=" + Arrays.toString(drivers) +
        "} ";
  }
}
