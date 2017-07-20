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
package org.knowm.jspice.simulate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SimulationResult {

  private final String xDataLabel;
  private final String yDataLabel;
  private final Map<String, SimulationPlotData> simulationDataMap;

  /**
   * Constructor
   *
   * @param xDataLabel
   * @param yDataLabel
   * @param simulationDataMap
   */
  public SimulationResult(String xDataLabel, String yDataLabel, Map<String, SimulationPlotData> simulationDataMap) {

    this.xDataLabel = xDataLabel;
    this.yDataLabel = yDataLabel;
    this.simulationDataMap = simulationDataMap;
  }

  public String getxDataLabel() {

    return xDataLabel;
  }

  public String getyDataLabel() {

    return yDataLabel;
  }

  public Map<String, SimulationPlotData> getSimulationPlotDataMap() {

    return simulationDataMap;
  }

  @Override
  public String toString() {

    String returnString = System.getProperty("line.separator");

    StringBuilder sb = new StringBuilder();
    sb.append("SimulationResult");
    sb.append(returnString);
    sb.append("xDataLabel=" + xDataLabel);
    sb.append(returnString);
    sb.append("yDataLabel=" + yDataLabel);
    sb.append(returnString);
    for (Entry<String, SimulationPlotData> entrySet : simulationDataMap.entrySet()) {
      String observableValueID = entrySet.getKey();
      SimulationPlotData timeSeriesData = entrySet.getValue();
      sb.append("observableValueID=" + observableValueID);
      sb.append(returnString);
      sb.append("timeSeriesData=" + timeSeriesData);
      sb.append(returnString);
    }
    return sb.toString();
  }

  public String toXyceString() {

    int count = 0;
    StringBuilder sb = new StringBuilder();

    String returnString = System.getProperty("line.separator");
    sb.append("Index");
    sb.append("\t");
    sb.append("Time");
    sb.append("\t");
    for (Entry<String, SimulationPlotData> entrySet : simulationDataMap.entrySet()) {
      sb.append(entrySet.getKey());
      sb.append("\t");
    }
    sb.append(returnString);

    List<Number> xData = simulationDataMap.values().iterator().next().getxData();
    do {
      sb.append(count);
      sb.append("\t");
      sb.append(xData.get(count));
      sb.append("\t");
      for (Entry<String, SimulationPlotData> entrySet : simulationDataMap.entrySet()) {
        sb.append(entrySet.getValue().getyData().get(count));
        sb.append("\t");
      }
      sb.append(returnString);
    } while (++count < xData.size());
    sb.append("End of JSpice Simulation");
    return sb.toString();
  }

}
