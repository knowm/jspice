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

import java.util.Calendar;
import java.util.Date;
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
   */  public SimulationResult(String xDataLabel, String yDataLabel, Map<String, SimulationPlotData> simulationDataMap) {

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
  public String toXyceRawString(String sourceFile) {
    
    int count = 0;
    StringBuilder sb = new StringBuilder();
    Date today = Calendar.getInstance().getTime();

    List<Number> xData = simulationDataMap.values().iterator().next().getxData();
        
    String returnString = System.getProperty("line.separator");

    sb.append("Title: " + sourceFile);
    sb.append(returnString);

    sb.append("Date: " + today.toString());
    sb.append(returnString);
    sb.append("Plotname: Transient Analysis");
    sb.append(returnString);
    sb.append("Flags: real");
    sb.append(returnString);
    sb.append("No. Variables: ");
    sb.append(simulationDataMap.values().size()+1);
    sb.append(returnString);
    sb.append("No. Points: ");
    sb.append(xData.size());
    sb.append(returnString);
    sb.append("Variables:");
    sb.append(returnString);
    
    // plot data labels    
    sb.append("\t0\tTime\ttime");
    sb.append(returnString);
    count = 1;
    for (Entry<String, SimulationPlotData> entrySet : simulationDataMap.entrySet()) {
      // 0  Time     time
      // 1   I(VPR1) current
      // 2   V(VIN)  voltage
      // ...
      String label = entrySet.getKey();
     
      sb.append("\t");
      sb.append(count);
      sb.append("\t");
      sb.append(label);
      sb.append("\t");
      // data type
      if (label.startsWith("I")) {
        sb.append("current");
      } else if (label.startsWith("V")) {
        sb.append("voltage");
      } else if (label.startsWith("R")) {
        sb.append("resistance");
      }
      count++;
      sb.append(returnString);
    }
    
    sb.append("Values:");
    sb.append(returnString);
    // write first time datum followed by Y-data values
    count = 0;
    do {
      sb.append(count);
      sb.append("\t");
      sb.append(xData.get(count));
      sb.append(returnString);
      for (Entry<String, SimulationPlotData> entrySet : simulationDataMap.entrySet()) {
        String label = entrySet.getKey();
        sb.append("\t");
        sb.append(entrySet.getValue().getyData().get(count));
        sb.append(returnString);
      } 
     
    sb.append(returnString);
      
    } while (++count < xData.size());
    //sb.append("End of JSpice Simulation");
    return sb.toString();
  }
  
}
