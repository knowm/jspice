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

public class JoglekarMemristor extends Memristor {

  private final double Rinit;
  private double Ron;
  private double Roff;
  private double D;
  private double uv;
  private double p;
  private double k;
  private final double borderBump = 10e-18;

  /**
   * Dynamic state variable
   */
  private double w;

  /**
   * Constructor
   *
   * @param id
   * @param Rinit
   * @param Ron
   * @param Roff
   * @param D
   * @param uv
   * @param p
   */
  public JoglekarMemristor(String id, double Rinit, double Ron, double Roff, double D, double uv, double p) {

    super(id);
    this.Rinit = Rinit;
    this.Ron = Ron;
    this.Roff = Roff;
    this.D = D;
    this.uv = uv;
    this.p = p;
    recalculatek();
//    this.k = uv * Ron / D;
    this.w = (Roff - Rinit) / (Roff - Ron) * D;
    System.out.println("w initial = " + w);
  }

  public double getConductance() {

    return 1 / (Ron * w / D + Roff * (1 - w / D));
  }

  @Override
  public double getCurrent(double voltage) {

    return getConductance() * voltage;
  }

  @Override
  public void dG(double voltage, double dt) {

    double current = getCurrent(voltage);
    // boundary condition reversal
    int direction = 0;
    if (current > 0) {
      if (w <= 0) {
        direction = 1;
      }
    } else {
      if (w >= D) {
        direction = -1;
      }
    }
//        System.out.println("voltage: " + voltage);

    double dw = k * getCurrent(voltage) * (1 - (Math.pow(2 * w / D - 1, 2 * p))) * dt;
//    System.out.println("getCurrent(voltage): " + getCurrent(voltage));
//    System.out.println("dw: " + dw);
//    System.out.println("dt: " + dt);
//     System.out.println("direction: " + direction);
    w += dw + (double) direction * borderBump;
//     System.out.println("w: " + w);
  }

  public double getRon() {

    return Ron;
  }

  public void setRon(double ron) {

    Ron = ron;
    recalculatek();
  }

  public double getRoff() {

    return Roff;
  }

  public void setRoff(double roff) {

    Roff = roff;
  }

  public double getD() {

    return D;
  }

  public void setD(double d) {

    D = d;
    recalculatek();
  }

  public double getUv() {

    return uv;
  }

  public void setUv(double uv) {

    this.uv = uv;
    recalculatek();
  }

  public double getP() {

    return p;
  }

  public void setP(double p) {

    this.p = p;
  }

  private void recalculatek() {

    this.k = uv * Ron / D;
  }

  @Override
  public void setSweepValue(double value) {

    w = value;
  }

  @Override
  public double getSweepableValue() {

    return w;
  }
}
