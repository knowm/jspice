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

/**
 * @author timmolter
 */
public class Pulse extends Driver {

  private final double dutyCycle;

  /**
   * Constructor
   *
   * @param name
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   * @param dutyCycle between 0 and 1
   */
  public Pulse(String name, double dcOffset, double phase, double amplitude, double frequency, double dutyCycle) {

    super(name, dcOffset, phase, amplitude, frequency);
    this.dutyCycle = dutyCycle;
  }

  @Override
  public double getSignal(double time) {

    double T = 1 / frequency;
    double remainderTime = (time + phase) % T * 0.5 / dutyCycle;

    // up phase
    if (0 <= remainderTime && remainderTime * T < .50 / frequency * T) {
      return amplitude + dcOffset;
    }

    // down phase
    else {
      return -1.0 * amplitude + dcOffset;
    }
  }

  public double getDutyCycle() {

    return dutyCycle;
  }

  @Override
  public String toString() {

    return "Pulse [dutyCycle=" + dutyCycle + ", id=" + id + ", dcOffset=" + dcOffset + ", phase=" + phase + ", amplitude=" + amplitude + ", frequency=" + frequency + "]";
  }
}
