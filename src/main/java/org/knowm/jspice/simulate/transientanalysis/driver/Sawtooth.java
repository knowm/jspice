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

public class Sawtooth extends Driver {

  /**
   * Constructor
   *
   * @param id
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public Sawtooth(@JsonProperty("id") String id, @JsonProperty("dc_offset") double dcOffset, @JsonProperty("phase") String phase,
      @JsonProperty("amplitude") double amplitude, @JsonProperty("frequency") String frequency) {

    super(id, dcOffset, phase, amplitude, frequency);
  }

  @Override
  public double getSignal(BigDecimal time) {

    BigDecimal remainderTime = (time.add(phaseBD)).remainder(T);

    // up phase
    if (BigDecimal.ZERO.compareTo(remainderTime) <= 0
        && remainderTime.multiply(T).compareTo(point5.divide(frequencyBD, MathContext.DECIMAL128).multiply(T)) < 0) {
      return 2 * frequencyBD.doubleValue() * amplitude * (remainderTime.doubleValue()) + dcOffset;
    }

    // up phase
    else {
      return 2 * frequencyBD.doubleValue() * amplitude * (remainderTime.doubleValue()) - 2 * amplitude + dcOffset;
    }
  }
}
