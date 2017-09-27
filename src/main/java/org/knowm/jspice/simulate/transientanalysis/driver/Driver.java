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

import java.math.BigDecimal;
import java.math.MathContext;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.knowm.jspice.netlist.spice.SPICEUtils;
import org.knowm.konfig.Konfigurable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = Pulse.class, name = "pulse"),
                  @Type(value = Arbitrary.class, name = "arbitrary"),
                  @Type(value = DC.class, name = "dc"),
                  @Type(value = Sawtooth.class, name = "sawtooth"),
                  @Type(value = Square.class, name = "square"),
                  @Type(value = StreamingArbitrary.class, name = "streaming_arbitrary"),
                  @Type(value = Triangle.class, name = "triangle"),
                  @Type(value = Sine.class, name = "sine")})
public abstract class Driver implements Konfigurable {

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
  protected final String phase;
  protected final BigDecimal phaseBD;

  @Valid
  @NotNull
  @JsonProperty("amplitude")
  protected final double amplitude;

  @Valid
  @NotNull
  @JsonProperty("frequency")
  protected final String frequency;
  protected final BigDecimal frequencyBD;

  /**
   * Helpful Constants
   */
  protected final BigDecimal point5 = new BigDecimal("0.5");
  protected final BigDecimal point25 = new BigDecimal("0.25");
  protected final BigDecimal point75 = new BigDecimal("0.75");
  protected final BigDecimal twopi = new BigDecimal(Math.PI).multiply(new BigDecimal("2"));
  protected final BigDecimal T;

  /**
   * Constructor
   *
   * @param id
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public Driver(@JsonProperty("id") String id,
      @JsonProperty("dc_offset") double dcOffset,
      @JsonProperty("phase") String phase,
      @JsonProperty("amplitude") double amplitude,
      @JsonProperty("frequency") String frequency) {

    this.id = id;
    this.dcOffset = dcOffset;
    this.phase = phase;
    this.phaseBD = SPICEUtils.bigDecimalFromString(phase, "0");
    this.amplitude = amplitude;
    this.frequency = frequency;
    this.frequencyBD = SPICEUtils.bigDecimalFromString(frequency, "0");
    if (!frequencyBD.equals(BigDecimal.ZERO)) {
      //this.T = BigDecimal.ONE.divide(frequencyBD, MathContext.DECIMAL128);
      this.T = BigDecimal.ONE.divide(frequencyBD, MathContext.DECIMAL128);
    } else {
      this.T = BigDecimal.ZERO;
    }
  }

  public String getId() {

    return id;
  }

  public double getDcOffset() {

    return dcOffset;
  }

  public String getPhase() {

    return phase;
  }

  public BigDecimal getPhaseBD() {
    return phaseBD;
  }

  public double getAmplitude() {

    return amplitude;
  }

  public String getFrequency() {

    return frequency;
  }

  public BigDecimal getFrequencyBD() {
    return frequencyBD;
  }

  public abstract double getSignal(BigDecimal time);
}
