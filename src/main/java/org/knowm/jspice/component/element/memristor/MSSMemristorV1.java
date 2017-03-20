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
 * This class represents a memristor that is based on Alex's "collection of meta-stable switches" memristor model. Extenders of this class are just one particular device with a set of
 * real-value model parameters.
 *
 * @author timmolter
 */
public class MSSMemristorV1 extends Memristor {

  private final static Random RANDOM = new Random();

  // DEVICE PARAMETERS

  /**
   * characteristic time scale of the device
   */
  private double tau;

  /**
   * the number of MSS's
   */
  private double n;

  private double GA;
  private double GB;
  /**
   * conductance contributed from each MSS
   */
  private double Ga;
  private double Gb;

  /**
   * between 0 and 1, what percentage of the current comes from the MSS model? the rest comes from Schottky barrier exponential current
   */
  private double phi;

  private double schottkyForwardAlpha;
  private double schottkyForwardBeta;
  private double schottkyReverseAlpha;
  private double schottkyReverseBeta;

  /**
   * barrier potentials
   */
  private double vA;
  private double vB;

  // DEVICE DYNAMIC VARIABLES

  /**
   * Nb is the number of MSS's in the B state,
   * the state of the memristor, contained in this one variable
   */
  private double Nb;

  /**
   * Constructor
   */
  public MSSMemristorV1(String id, double memristance, double GB, double GA, int n, double tau, double vA, double vB, double phi, double schottkyForwardAlpha, double schottkyForwardBeta,
                        double schottkyReverseAlpha, double schottkyReverseBeta) {

    super(id);

    if (memristance > 1.0 || memristance < 0.0) {
      throw new IllegalArgumentException("Memristance must be between 0 and 1, inclusive!!!");
    }

    // init the device in a certain state
    Nb = (1 - memristance) * n; // note: (1- memristance) so that a memristance of 1 give a higher resistance than memristance of 0.

    this.tau = tau;
    this.n = n;
    this.GA = GA;
    this.GB = GB;
    this.Ga = GA / n;
    this.Gb = GB / n;
    this.vA = vA;
    this.vB = vB;
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
   * @param dt      - how much time passed since the last update
   */
  public void dG(double voltage, double dt) {

    // Probabilities
    double Pa = Pa(voltage, dt);
    double Pb = Pb(voltage, dt);

    // Gaussian mean
    double mu_a = (n - Nb) * Pa; // Na * Pa
    double mu_b = (Nb) * Pb;

    // Gaussian standard deviation
    double sigma_a = Math.sqrt((n - Nb) * Pa * (1 - Pa));
    double sigma_b = Math.sqrt((Nb) * Pb * (1 - Pb));

    // Number of switches making a transition
    double Na2b = Math.round(normal(mu_a, sigma_a)); // from A to B
    double Nb2a = Math.round(normal(mu_b, sigma_b)); // from B to A

    // update the state of the memristor, contained in this one variable
    Nb += (Na2b - Nb2a);
    if (Nb > n) {
      Nb = n;
    }
    else if (Nb < 0) {
      Nb = 0;
    }
  }

  /**
   * Equation 21. The probability that the MSS will transition from the A state to the B state
   *
   * @param voltage - the voltage across the device
   * @param dt
   * @return
   */
  public double Pa(double v, double dt) {

    double exponent = -1 * (v - vA) / getVt();
    double alpha = dt / tau;
    double Pa = alpha / (1.0 + Math.exp(exponent));

    return Pa;
  }

  /**
   * Equation 22. The probability that the MSS will transition from the B state to the A state
   *
   * @param v
   * @param dt
   * @return
   */
  public double Pb(double v, double dt) {

    double exponent = -1 * (v + vB) / getVt();
    double alpha = dt / tau;
    double Pb = alpha * (1.0 - 1.0 / (1.0 + Math.exp(exponent)));

    return Pb;
  }

  /**
   * Gaussian/Normal distribution
   *
   * @param u
   * @param sigma
   * @return
   */
  private double normal(double u, double sigma) {

    return sigma * RANDOM.nextGaussian() + u;
  }

  /**
   * Equation 23. all variables are constant except Nb
   *
   * @return
   */
  public double getConductance() {

    // return Nb * (Gb - Ga) + n * Ga;
    return Nb * Gb + (n - Nb) * Ga;
  }

  /**
   * Get the current thru this memristor
   *
   * @return the combined MSS and Schottkey current
   */
  public double getCurrent(double voltage) {

    double mssCurrent = voltage * getConductance();
    double schottkeyCurrent = getSchottkyCurrent(voltage);
    // System.out.println("mssCurrent" + mssCurrent);
    // System.out.println("schottkeyCurrent" + schottkeyCurrent);

    return phi * mssCurrent + (1 - phi) * schottkeyCurrent;
  }

  public double getSchottkyCurrent(double voltage) {

    return schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage)) + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage));
  }

  public double getSchottkyCurrentWithPhi(double voltage) {

    double schottkeyCurrent = (1 - phi) * (schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage)) + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage)));
    return schottkeyCurrent;
    // return (1 - phi) * (schottkyReverseAlpha * (-1 * Math.exp(-1 * schottkyReverseBeta * voltage)) + schottkyForwardAlpha * (Math.exp(schottkyForwardBeta * voltage)));
  }

  public double getTau() {

    return tau;
  }

  public void setTau(double tc) {

    this.tau = tc;
  }

  public double getN() {

    return n;
  }

  public void setN(double n) {

    this.n = n;
  }

  public double getGa() {

    return Ga;
  }

  public void setGa(double ga) {

    Ga = ga;
  }

  public double getGb() {

    return Gb;
  }

  public void setGb(double gb) {

    Gb = gb;
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

    return vA;
  }

  public void setvA(double vA) {

    this.vA = vA;
  }

  public double getvB() {

    return vB;
  }

  public void setvB(double vB) {

    this.vB = vB;
  }

  public double getPhi() {

    return phi;
  }

  public void setPhi(double phi) {

    this.phi = phi;
  }

  public double getGA() {

    return GA;
  }

  public void setGA(double GA) {

    this.GA = GA;
    this.Ga = GA / n;
  }

  public double getGB() {

    return GB;
  }

  public void setGB(double GB) {

    this.GB = GB;
    this.Gb = GB / n;
  }

  @Override
  public void setSweepValue(double value) {

    this.Nb = value;
  }

  @Override
  public double getSweepableValue() {

    return this.Nb;
  }
}
