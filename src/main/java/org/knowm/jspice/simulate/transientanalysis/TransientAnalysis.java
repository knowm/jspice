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
package org.knowm.jspice.simulate.transientanalysis;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.spice.SPICEUtils;
import org.knowm.jspice.simulate.SimulationPlotData;
import org.knowm.jspice.simulate.SimulationPreCheck;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;
import org.knowm.jspice.simulate.dcoperatingpoint.NodalAnalysisConvergenceException;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;

public class TransientAnalysis {

  private final Netlist netlist;
  private final TransientConfig transientAnalysisDefinition;

  /**
   * Constructor
   *
   * @param netlist
   * @param transientAnalysisDefinition
   */
  public TransientAnalysis(Netlist netlist, TransientConfig transientAnalysisDefinition) {

    this.netlist = netlist;
    this.transientAnalysisDefinition = transientAnalysisDefinition;
  }

  public SimulationResult run() {

    // long start = System.currentTimeMillis();

    // sanity checks
    verify(transientAnalysisDefinition);

    // add single sweep result to SimulationResult
    SimulationResult simulationResult = new SimulationResult("Time [s]", "", getSingleTransientAnalyisResult());

    // System.out.println("transientAnalyis= " + (System.currentTimeMillis() - start));

    return simulationResult;
  }

  private Map<String, SimulationPlotData> getSingleTransientAnalyisResult() {

    Map<String, SimulationPlotData> timeSeriesDataMap = new LinkedHashMap<>();

    BigDecimal firstPoint = BigDecimal.ZERO;
    BigDecimal timeStep = SPICEUtils.bigDecimalFromString(transientAnalysisDefinition.getTimeStep());
    BigDecimal stopTime = SPICEUtils.bigDecimalFromString(transientAnalysisDefinition.getStopTime());

    DCOperatingPointResult dCOperatingPointResult = null;

    // for each time step
    for (BigDecimal t = firstPoint; t.compareTo(stopTime) < 0; t = t.add(timeStep)) {

      // update drivers' values
      for (int i = 0; i < transientAnalysisDefinition.getDrivers().length; i++) {

        Driver driver = transientAnalysisDefinition.getDrivers()[i];
        double signal = driver.getSignal(t);
//        System.out.println(t);
//        System.out.println(signal);
//        System.out.println("---");

        Component sweepableComponent = netlist.getComponent(transientAnalysisDefinition.getDrivers()[i].getId());
        //        System.out.println("sweepableComponent " + sweepableComponent);
        sweepableComponent.setSweepValue(signal);
      }

      if (dCOperatingPointResult == null) { // initial DC operating point, no reactive component linear companion models

        // get operating point to generate a node list for keeping track of time series data map
        dCOperatingPointResult = new DCOperatingPoint(netlist).run();
        //        System.out.println(dCOperatingPointResult.toString());

        for (String nodeLabel : dCOperatingPointResult.getNodeLabels2Value().keySet()) {
          timeSeriesDataMap.put(nodeLabel, new SimulationPlotData());
        }
        for (String deviceID : dCOperatingPointResult.getDeviceLabels2Value().keySet()) {
          timeSeriesDataMap.put(deviceID, new SimulationPlotData());
        }
        continue;
      }

      // ////////////////////////////////////////////////////

      try {
        netlist.setInitialConditions(false);

        // solve DC operating point
        dCOperatingPointResult = new DCOperatingPoint(dCOperatingPointResult, netlist, SPICEUtils.bigDecimalFromString(transientAnalysisDefinition.getTimeStep()).doubleValue())
            .run();
        //        System.out.println(dCOperatingPointResult.toString());

        // add all node voltage values
        for (String nodeLabel : dCOperatingPointResult.getNodeLabels2Value().keySet()) {
          if (timeSeriesDataMap.get(nodeLabel) != null) {
            timeSeriesDataMap.get(nodeLabel).getxData().add(t);
            timeSeriesDataMap.get(nodeLabel).getyData().add(dCOperatingPointResult.getNodeLabels2Value().get(nodeLabel));
          }
        }
        // add all device current values
        for (String deviceID : dCOperatingPointResult.getDeviceLabels2Value().keySet()) {
          timeSeriesDataMap.get(deviceID).getxData().add(t);
          timeSeriesDataMap.get(deviceID).getyData().add(dCOperatingPointResult.getDeviceLabels2Value().get(deviceID));
        }
      } catch (NodalAnalysisConvergenceException e) {
        System.out.println("skipping value at t= " + t + " because of failure to converge!");
      }
    }

    // return the timeseries data
    return timeSeriesDataMap;
  }

  /**
   * sanity checks a SweepDefinition
   */
  private void verify(TransientConfig transientAnalysisDefinition) {

    // make sure componentToSweepID is actually in the circuit netlist
    for (int j = 0; j < transientAnalysisDefinition.getDrivers().length; j++) {
      SimulationPreCheck.verifyComponentToSweepOrDriveId(netlist, transientAnalysisDefinition.getDrivers()[j].getId());
    }
  }
}
