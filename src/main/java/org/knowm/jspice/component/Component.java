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
package org.knowm.jspice.component;

import java.util.Map;
import java.util.Set;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Component implements Sweepable {

  /**
   * Boltzman's constant
   */
  public static final double K = 1.3806503E-23;
  /**
   * electron charge
   */
  public static final double Q = 1.60217646E-19;
  public static final double TEMP_25_CELCIUS = 298.0;
  public static final double BETA = Q / (K * TEMP_25_CELCIUS);
  public static final double VT = 1.0 / BETA;

  @JsonProperty("id")
  private final String id;

  private double temperature = TEMP_25_CELCIUS;
  private double beta = BETA;
  private double vt = VT;

  /**
   * Constructor
   *
   * @param id
   */
  public Component(String id) {

    this.id = id;
  }

  public abstract Set<String> getGMatrixColumnIDs(String[] nodes, Double timeStep);

  public abstract void modifyUnknowmQuantitiesVector(String[] nodeIDs, String[] nodes, Double timeStep);

  public abstract void stampG(double[][] G, Netlist netList, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap,
      String[] nodes, Double timeStep);

  public abstract void stampRHS(double[] RHS, DCOperatingPointResult dcOperatingPointResult, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes,
      Double timeStep);

  public String getId() {

    return id;
  }

  public void setTemperature(double temperatureInKelvin) {

    temperature = temperatureInKelvin;
    beta = Q / (K * temperature);
    vt = 1.0 / beta;
  }

  @JsonIgnore
  public double getTemperature() {

    return temperature;
  }

  @JsonIgnore
  public double getBeta() {

    return beta;
  }

  @JsonIgnore
  public double getVt() {

    return vt;
  }
}
