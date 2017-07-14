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
package org.knowm.jspice.component.element.memristor;

public class RSMemristor extends Memristor {

  private final double alpha = 10000f;
  private final double Va = .25f;
  private final double Vb = .15f;
  private final double Gmax = 1E-3f;
  private final double Gmin = 1E-7f;

  /**
   * between 0 and 1, what percentage of the current comes from the MSS model? the rest comes from schottkey barrier exponential current
   */
  private double phi = 1;

  private double schottkyForwardAlpha;
  private double schottkyForwardBeta;
  private double schottkyReverseAlpha;
  private double schottkyReverseBeta;

  // runtime

  private double g = Gmin;

  public RSMemristor(String id, double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta,
      double phi) {

    super(id);
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
    this.phi = phi;
  }

  @Override
  public double getCurrent(double voltage) {

    double memristorCurrent = voltage * getConductance();
    double schottkeyCurrent = getSchottkyCurrent(voltage);

    return phi * memristorCurrent + (1 - phi) * schottkeyCurrent;
  }

  @Override
  public void dG(double voltage, double dt) {

    if (voltage > 0) {
      this.g += alpha * Vt(voltage) * dt * (Gmax - g);
    } else {
      this.g += alpha * Vt(voltage) * dt * (Gmin - g);
    }
  }

  @Override
  public double getConductance() {

    return g;
  }

  public double getSchottkyCurrent(double voltage) {

    return schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage))
        + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage));
  }

  private double Vt(double v) {

    if (v > Va) {
      return (v - Va);
    } else if (v < -Vb) {
      return -(v + Vb);
    } else {
      return 0;
    }
  }

  @Override
  public void setSweepValue(double g) {

    this.g = g;

  }

  @Override
  public double getSweepableValue() {

    return g;
  }
}
