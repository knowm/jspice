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
public class Sine extends Driver {

  /**
   * Constructor
   *
   * @param name
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public Sine(String name, double dcOffset, double phase, double amplitude, double frequency) {

    super(name, dcOffset, phase, amplitude, frequency);
  }

  @Override
  public double getSignal(double time) {

    return amplitude * Math.sin(2 * Math.PI * frequency * time - phase) + dcOffset;
  }
}
