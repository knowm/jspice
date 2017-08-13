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

import java.util.Random;

/**
 * This class represents a memristor that is based on Alex's "collection of meta-stable switches" memristor model. This version is different from
 * MSSMemristorV1 in that it defines key params as Ron, Roff, Von, Voff, and x. Extenders of this class are just one particular device with a set of
 * real-value model parameters.
 *
 * @author timmolter
 */
public class MSSMemristor extends Memristor {

  private final static Random RANDOM = new Random();

  // DEVICE PARAMETERS

  /**
   * characteristic time scale of the device
   */
  private double tau;

  /**
   * the number of MSSs
   */
  private double n;

  private double rInit;
  private double rOff;
  private double rOn;

  /**
   * between 0 and 1, what percentage of the current comes from the MSS model? the rest comes from schottkey barrier exponential current
   */
  private double phi;

  private double schottkyForwardAlpha;
  private double schottkyForwardBeta;
  private double schottkyReverseAlpha;
  private double schottkyReverseBeta;

  /**
   * barrier potentials
   */
  private double vOn;
  private double vOff;

  // DEVICE DYNAMIC VARIABLES

  private double x;

  /**
   * Constructor
   */
  public MSSMemristor(String id, double rInit, double rOn, double rOff, double n, double tau, double vOn, double vOff, double phi,
      double schottkyForwardAlpha, double schottkyForwardBeta, double schottkyReverseAlpha, double schottkyReverseBeta) {

    super(id);

    this.rInit = rInit;
    if (rInit - .0001 > rOff || rInit + .0001 < rOn) { // considering double imprecision errors
      throw new IllegalArgumentException("Memristance must be between rOn and rOff, inclusive!!! rInit= " + rInit);
    }

    // init the device in a certain state
    // x = memristance; // percent of switches ON

    x = (rOn * (rInit - rOff)) / (rInit * (rOn - rOff));
    // System.out.println("xInit = " + x);

    this.tau = tau;
    this.n = n;
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

    // System.out.println("voltage = " + voltage);
    // System.out.println("pOff2on = " + pOff2on);
    // System.out.println("pOn2Off = " + pOn2Off);

    // Gaussian mean
    double uOn = (n - x * n) * pOff2on;
    double uOff = (x * n) * pOn2Off;

    // Gaussian standard deviation (sigma)
    double sigmaOn = Math.sqrt((n - x * n) * pOff2on * (1 - pOff2on)); // variance = np(1-p)
    double sigmaOff = Math.sqrt((x * n) * pOn2Off * (1 - pOn2Off));

    // Number of switches making a transition
    double n0ff2on = Math.round(normal(uOn, sigmaOn)); // from Off to On
    double nOn2Off = Math.round(normal(uOff, sigmaOff)); // from On to Off

    // update the state of the memristor, contained in this one variable
    x += (n0ff2on - nOn2Off) / n;
    if (x > 1) {
      x = 1;
    } else if (x < 0) {
      x = 0;
    }
    // System.out.println(x);
  }

  /**
   * Equation 21. The probability that the MSS will transition from the Off state to the On state
   *
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
   * Gaussian/Normal distribution
   *
   * @param u
   * @param stdv
   * @return
   */
  private double normal(double u, double stdv) {

    return stdv * RANDOM.nextGaussian() + u;
  }

  /**
   * Equation 23. all variables are constant except X
   *
   * @return
   */
  @Override
  public double getConductance() {

    double G = (x / rOn + (1 - x) / rOff);
    //     System.out.println("R= " + 1 / G);
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

  public double getN() {

    return n;
  }

  public void setN(double n) {

    this.n = n;
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

  public double getvA() {

    return vOn;
  }

  public void setvA(double vA) {

    this.vOn = vA;
  }

  public double getvB() {

    return vOff;
  }

  public void setvB(double vB) {

    this.vOff = vB;
  }

  public double getPhi() {

    return phi;
  }

  public void setPhi(double phi) {

    this.phi = phi;
  }

  public double getrInit() {
    return rInit;
  }

  public double getrOff() {
    return rOff;
  }

  public double getrOn() {
    return rOn;
  }

  public double getvOn() {
    return vOn;
  }

  public double getvOff() {
    return vOff;
  }

  public double getX() {
    return x;
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
