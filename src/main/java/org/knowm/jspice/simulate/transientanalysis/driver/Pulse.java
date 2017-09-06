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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pulse extends Driver {

  private final String dutyCycle;
  private final BigDecimal dutyCycleBD;

  /**
   * Constructor
   *
   * @param id
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   * @param dutyCycle between 0 and 1
   */
  public Pulse(@JsonProperty("id") String id, @JsonProperty("dc_offset") double dcOffset, @JsonProperty("phase") String phase,
      @JsonProperty("amplitude") double amplitude, @JsonProperty("frequency") String frequency, @JsonProperty("duty") String dutyCycle) {

    super(id, dcOffset, phase, amplitude, frequency);
    this.dutyCycle = dutyCycle;
    this.dutyCycleBD = new BigDecimal(dutyCycle);
  }

  @Override
  public double getSignal(BigDecimal time) {

    BigDecimal remainderTime = (time.add(phaseBD)).remainder(T).multiply(point5).divide(dutyCycleBD, MathContext.DECIMAL128);

    // up phase
    if (BigDecimal.ZERO.compareTo(remainderTime) <= 0
        && remainderTime.multiply(T).compareTo(point5.divide(frequencyBD, MathContext.DECIMAL128).multiply(T)) < 0) {
      return amplitude + dcOffset;
    }

    // down phase
    else {
      return -1.0 * amplitude + dcOffset;
    }
  }

  public String getDutyCycle() {

    return dutyCycle;
  }

  public BigDecimal getDutyCycleBD() {

    return dutyCycleBD;
  }

  @Override
  public String toString() {

    return "Pulse [dutyCycle=" + dutyCycle + ", id=" + id + ", dcOffset=" + dcOffset + ", phase=" + phase + ", amplitude=" + amplitude
        + ", frequency=" + frequency + "]";
  }
}
