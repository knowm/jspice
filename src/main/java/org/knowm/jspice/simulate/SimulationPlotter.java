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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * @author timmolter
 */
public class SimulationPlotter {

  /**
   * Plot the Simulation results on one plot
   *
   * @param title
   * @param simulationResult
   * @param plotInputSignals
   */
  public static void plotAll(SimulationResult simulationResult) {

    //    System.out.println("simulationResult " + simulationResult);

    // Create Chart
    XYChart chart = new XYChart(600, 300);
    chart.setXAxisTitle(simulationResult.getxDataLabel());
    chart.setYAxisTitle(simulationResult.getyDataLabel());
    //    Set<String> yLabelsSet = new TreeSet<>();

    for (Entry<String, SimulationPlotData> entrySet : simulationResult.getSimulationPlotDataMap().entrySet()) {
      String observableValueID = entrySet.getKey();
      //      yLabelsSet.add(observableValueID.indexOf(" ") == -1 ? observableValueID : observableValueID.substring(0, observableValueID.indexOf(" ")));
      SimulationPlotData simulationData = entrySet.getValue();
      chart.addSeries(observableValueID, simulationData.getxData(), simulationData.getyData());
    }
    //    chart.setYAxisTitle(Arrays.toString(yLabelsSet.toArray()));

    new SwingWrapper<XYChart>(chart).displayChart();
  }

  /**
   * Plot the Simulation results on one plot
   *
   * @param title
   * @param simulationResult
   * @param valuesToPlot
   */
  public static void plot(SimulationResult simulationResult, String[] valuesToPlot) {

    // Create Chart
    XYChart chart = new XYChart(600, 300);
    chart.setXAxisTitle(simulationResult.getxDataLabel());
    chart.setYAxisTitle(simulationResult.getyDataLabel());

    for (int i = 0; i < valuesToPlot.length; i++) {

      SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get(valuesToPlot[i]);

      if (simulationData == null) {
        throw new IllegalArgumentException(
            valuesToPlot[i] + " is not a valid node value! Please choose from these values: " + simulationResult.getSimulationPlotDataMap().keySet());
      }

      chart.addSeries(valuesToPlot[i], simulationData.getxData(), simulationData.getyData());
    }

    new SwingWrapper<>(chart).displayChart();
  }

  /**
   * Plot all the Simulation results on separate plots
   *
   * @param simulationResult
   */
  public static void plotAllSeparate(SimulationResult simulationResult) {

    final int width = 600;
    final int height = 200;

    List<XYChart> charts = new ArrayList<XYChart>();

    int rows = 0;

    // Create Chart
    for (Entry<String, SimulationPlotData> entrySet : simulationResult.getSimulationPlotDataMap().entrySet()) {
      String observableValueID = entrySet.getKey();
      SimulationPlotData simulationData = entrySet.getValue();
      XYChart chart = new XYChart(width, height);
      chart.setYAxisTitle(observableValueID);
      XYSeries series = chart.addSeries(observableValueID, simulationData.getxData(), simulationData.getyData());
      series.setMarker(SeriesMarkers.NONE);
      chart.getStyler().setLegendVisible(false);
      charts.add(chart);
      rows++;
    }

    new SwingWrapper<XYChart>(charts, rows, 1).displayChartMatrix();
  }

  /**
   * @param title
   * @param simulationResult
   * @param valuesToPlot
   */
  public static void plotSeparate(SimulationResult simulationResult, String[] valuesToPlot) {

    final int width = 600;
    final int height = 200;

    List<XYChart> charts = new ArrayList<XYChart>();

    int rows = 0;

    // Create Chart
    for (int i = 0; i < valuesToPlot.length; i++) {

      SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get(valuesToPlot[i]);

      if (simulationData == null) {
        throw new IllegalArgumentException(
            valuesToPlot[i] + " is not a valid node value! Please choose from these values: " + simulationResult.getSimulationPlotDataMap().keySet());
      }

      XYChart chart = new XYChart(width, height);
      chart.setXAxisTitle(simulationResult.getxDataLabel());
      chart.setYAxisTitle(valuesToPlot[i]);
      XYSeries series = chart.addSeries(valuesToPlot[i], simulationData.getxData(), simulationData.getyData());
      series.setMarker(SeriesMarkers.NONE);
      chart.getStyler().setLegendVisible(false);
      charts.add(chart);
      rows++;
    }

    new SwingWrapper<XYChart>(charts, rows, 1).displayChartMatrix();
  }

  /**
   * Plot In versus Out. Use this for a hysteresis curve. This uses the first driver signal as the x-Axis
   *
   * @param title
   * @param simulationResult
   */
  public static void plotTransientInOutCurve(String title, SimulationResult simulationResult, String[] valuesToPlot) {

    // Create Chart
    XYChart chart = new XYChart(600, 400);
    chart.setTitle(title);
    chart.setXAxisTitle(valuesToPlot[0]);
    chart.setYAxisTitle(valuesToPlot[1]);

    SimulationPlotData simulationDataX = simulationResult.getSimulationPlotDataMap().get(valuesToPlot[0]);
    if (simulationDataX == null) {
      throw new IllegalArgumentException(
          valuesToPlot[0] + " is not a valid node value! Please choose from these values: " + simulationResult.getSimulationPlotDataMap().keySet());
    }
    SimulationPlotData simulationDataY = simulationResult.getSimulationPlotDataMap().get(valuesToPlot[1]);
    if (simulationDataY == null) {
      throw new IllegalArgumentException(
          valuesToPlot[1] + " is not a valid node value! Please choose from these values: " + simulationResult.getSimulationPlotDataMap().keySet());
    }

    XYSeries series = chart.addSeries("X/Y", simulationDataX.getyData(), simulationDataY.getyData());
    series.setMarker(SeriesMarkers.NONE);

    chart.getStyler().setLegendVisible(false);
    new SwingWrapper(chart).displayChart();
  }
}
