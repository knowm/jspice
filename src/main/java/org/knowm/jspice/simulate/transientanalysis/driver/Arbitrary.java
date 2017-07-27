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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Arbitrary extends Driver {

  private final String[] activePhases;

  /**
   * Constructor
   *
   * @param matchingSourceId
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   * @param activePhases
   */
  public Arbitrary(@JsonProperty("id") String matchingSourceId,
      @JsonProperty("dc_offset") double dcOffset,
      @JsonProperty("phase") String phase,
      @JsonProperty("amplitude") double amplitude,
      @JsonProperty("frequency") String frequency,
      @JsonProperty("active") String[] activePhases) {

    super(matchingSourceId, dcOffset, phase, amplitude, frequency);
    this.activePhases = activePhases;
  }

  @Override
  public double getSignal(BigDecimal time) {

    BigDecimal remainderTime = (time.add(phaseBD)).remainder(T);
    boolean isActive = false;
    for (int i = 0; i < activePhases.length; i = i + 2) {
      BigDecimal start = new BigDecimal(activePhases[i]);
      BigDecimal end = new BigDecimal(activePhases[i + 1]);
      if (remainderTime.compareTo(T .multiply(start)) >= 0 && remainderTime.compareTo(T .multiply(end)) < 0 ) {
        isActive = true;
      }
    }
    if (isActive) {
      return dcOffset + amplitude;
    } else {
      return 0.0;
    }
  }
}
