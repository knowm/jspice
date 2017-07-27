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
package org.knowm.jspice.memristor;

import org.knowm.jspice.component.element.memristor.MSSMemristor;

/**
 * @author timmolter
 */
public class IdealMSSMemristor extends MSSMemristor {

  /**
   * characteristic time scale of the device
   */
  private static final double TC = 0.0001;

  /**
   * the number of MSS's
   */
  private static final int N = 100000;

  private static final double G_OFF = 1E-6;
  private static final double G_ON = 10E-6;

  /**
   * barrier potentials
   */
  private static final double VA = .2;// on threshold
  private static final double VB = .2;// off threshold

  final static double schottkeyAlpha = 0;
  final static double schottkeyBeta = 0;
  final static double phi = 1;

  /**
   * Constructor
   *
   * @param id
   * @param memristance
   */
  public IdealMSSMemristor(String id, double memristance) {

    super(id, memristance, G_OFF, G_ON, N, TC, VA, VB, phi, schottkeyAlpha, schottkeyBeta, schottkeyAlpha, schottkeyBeta);
  }
}
