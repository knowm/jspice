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
package org.knowm.jspice.simulate.transientanalysis.driver;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = Pulse.class, name = "pulse"), @Type(value = Arbitrary.class, name = "arbitrary"), @Type(value = DC.class, name = "dc"),
                  @Type(value = Sawtooth.class, name = "sawtooth"), @Type(value = Square.class, name = "square"),
                  @Type(value = StreamingArbitrary.class, name = "streaming_arbitrary"), @Type(value = Triangle.class, name = "triangle"),
                  @Type(value = Sine.class, name = "sine")})
public abstract class Driver {

  @Valid
  @NotNull
  @JsonProperty("id")
  protected final String id;

  @Valid
  @NotNull
  @JsonProperty("dc_offset")
  protected final double dcOffset;

  @Valid
  @NotNull
  @JsonProperty("phase")
  protected final double phase;

  @Valid
  @NotNull
  @JsonProperty("amplitude")
  protected final double amplitude;

  @Valid
  @NotNull
  @JsonProperty("frequency")
  protected final double frequency;

  /**
   * Constructor
   *
   * @param id
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public Driver(@JsonProperty("id") String id, @JsonProperty("dc_offset") double dcOffset, @JsonProperty("phase") double phase,
      @JsonProperty("amplitude") double amplitude, @JsonProperty("frequency") double frequency) {

    this.id = id;
    this.dcOffset = dcOffset;
    this.phase = phase;
    this.amplitude = amplitude;
    this.frequency = frequency;
  }

  public String getId() {

    return id;
  }

  public double getDcOffset() {

    return dcOffset;
  }

  public double getPhase() {

    return phase;
  }

  public double getAmplitude() {

    return amplitude;
  }

  public double getFrequency() {

    return frequency;
  }

  public abstract double getSignal(double time);
}
