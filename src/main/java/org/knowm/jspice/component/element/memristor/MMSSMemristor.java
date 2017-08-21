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

/**
 * This class represents a memristor that is based on Alex's "collection of meta-stable switches" memristor model. Extenders of this class are just
 * one particular device with a set of real-value model parameters.
 *
 * @author timmolter
 */
public class MMSSMemristor extends Memristor {

  // DEVICE PARAMETERS

  /**
   * characteristic time scale of the device
   */
  private double tau;

  private double rOn;
  private double rOff;

  /**
   * barrier potentials
   */
  private double vOn;
  private double vOff;

  /**
   * between 0 and 1, what percentage of the current comes from the MSS model? the rest comes from schottkey barrier exponential current
   */
  private double phi;

  private double schottkyForwardAlpha;
  private double schottkyForwardBeta;
  private double schottkyReverseAlpha;
  private double schottkyReverseBeta;

  // DEVICE DYNAMIC VARIABLES

  private double x;

  /**
   * Constructor
   */
  public MMSSMemristor(String id, double rInit, double rOn, double rOff, double tau, double vOn, double vOff, double phi, double schottkyForwardAlpha,
      double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta) {

    super(id);

    if (rInit > rOff || rInit < rOn) {
      throw new IllegalArgumentException("Memristance must be between rOn and rOff, inclusive!!! rInit = " + rInit);
    }

    // init the device in a certain state
    // x = memristance; // percent of switches ON

    x = (rOn * (rInit - rOff)) / (rInit * (rOn - rOff));
    //        System.out.println(x);

    this.tau = tau;
    this.rOn = rOn;
    this.rOff = rOff;
    this.vOn = vOn;
    this.vOff = vOff;
    this.phi = phi;
    this.schottkyForwardAlpha = schottkyForwardAlpha;
    this.schottkyForwardBeta = schottkyForwardBeta;
    this.schottkyReverseAlpha = schottkyReverseAlpha;
    this.schottkyReverseBeta = schottkyReverseBeta;
  }

  /**
   * update device conductance
   *
   * @param voltage - the instantaneous voltage
   * @param dt - how much time passed since the last update
   */
  @Override
  public void dG(double voltage, double dt) {

    // Probabilities
    double pOff2on = p0ff2on(voltage, dt);
    double pOn2Off = pOn2Off(voltage, dt);

    //        System.out.println("voltage = " + voltage);
    //        System.out.println("pOff2on = " + pOff2on);
    //        System.out.println("pOn2Off = " + pOn2Off);

    // mean
    double n0ff2on = (1 - x) * pOff2on;
    double nOn2Off = x * pOn2Off;

    // update the state of the memristor, contained in this one variable
    x += (n0ff2on - nOn2Off);
    if (x > 1) {
      x = 1;
    } else if (x < 0) {
      x = 0;
    }
    //        System.out.println(x);
  }

  /**
   * Equation 21. The probability that the MSS will transition from the Off state to the On state
   *
   * @param v - the voltage across the device
   * @param dt
   * @return
   */
  public double p0ff2on(double v, double dt) {

    double exponent = -1 * (v - vOn) / getVt();
    double alpha = dt / tau;
    double Pa = alpha / (1.0 + Math.exp(exponent));

    return Pa;
  }

  /**
   * Equation 22. The probability that the MSS will transition from the On state to the Off state
   *
   * @param v
   * @param dt
   * @return
   */
  public double pOn2Off(double v, double dt) {

    double exponent = -1 * (v + vOff) / getVt();
    double alpha = dt / tau;
    double Pb = alpha * (1.0 - 1.0 / (1.0 + Math.exp(exponent)));

    return Pb;
  }

  /**
   * Equation 23. all variables are constant except X
   *
   * @return
   */
  @Override
  public double getConductance() {

    double G = (x / rOn + (1 - x) / rOff);
    //        System.out.println("R= " + 1 / G);
    return G;
  }

  /**
   * Get the current thru this memristor
   *
   * @return the combined MSS and Schottkey current
   */
  @Override
  public double getCurrent(double voltage) {

    double mssCurrent = voltage * getConductance();
    double schottkeyCurrent = getSchottkyCurrent(voltage);
    // System.out.println("mssCurrent" + mssCurrent);
    // System.out.println("schottkeyCurrent" + schottkeyCurrent);

    return phi * mssCurrent + (1 - phi) * schottkeyCurrent;
  }

  public double getSchottkyCurrent(double voltage) {

    return schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage))
        + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage));
  }

  public double getSchottkyCurrentWithPhi(double voltage) {

    double schottkeyCurrent = (1 - phi) * (schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage))
        + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage)));
    return schottkeyCurrent;
    // return (1 - phi) * (schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage)) + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage)));
  }

  public double getTau() {

    return tau;
  }

  public void setTau(double tau) {

    this.tau = tau;
  }

  public double getSchottkyForwardAlpha() {

    return schottkyForwardAlpha;
  }

  public void setSchottkyForwardAlpha(double schottkyForwardAlpha) {

    this.schottkyForwardAlpha = schottkyForwardAlpha;
  }

  public double getSchottkyForwardBeta() {

    return schottkyForwardBeta;
  }

  public void setSchottkyForwardBeta(double schottkyForwardBeta) {

    this.schottkyForwardBeta = schottkyForwardBeta;
  }

  public double getSchottkyReverseAlpha() {

    return schottkyReverseAlpha;
  }

  public void setSchottkyReverseAlpha(double schottkyReverseAlpha) {

    this.schottkyReverseAlpha = schottkyReverseAlpha;
  }

  public double getSchottkyReverseBeta() {

    return schottkyReverseBeta;
  }

  public void setSchottkyReverseBeta(double schottkyReverseBeta) {

    this.schottkyReverseBeta = schottkyReverseBeta;
  }

  public double getrOff() {

    return rOff;
  }

  public void setrOff(double rOff) {

    this.rOff = rOff;
  }

  public double getrOn() {

    return rOn;
  }

  public void setrOn(double rOn) {

    this.rOn = rOn;
  }

  public double getvOn() {

    return vOn;
  }

  public void setvOn(double vOn) {

    this.vOn = vOn;
  }

  public double getvOff() {

    return vOff;
  }

  public void setvOff(double vOff) {

    this.vOff = vOff;
  }

  public double getPhi() {

    return phi;
  }

  public void setPhi(double phi) {

    this.phi = phi;
  }

  @Override
  public void setSweepValue(double value) {

    this.x = value;
  }

  @Override
  public double getSweepableValue() {

    return this.x;
  }
}
