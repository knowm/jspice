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
package org.knowm.jspice.simulate.dcsweep;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.VCCS;
import org.knowm.jspice.component.source.VCVS;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotData;
import org.knowm.jspice.simulate.SimulationPreCheck;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;
import org.knowm.jspice.simulate.dcoperatingpoint.NodalAnalysisConvergenceException;

public class DCSweep {

  private final Netlist netlist;

  private DCSweepConfig dcSweepConfig;
  private DCSweepConfig dcSweepConfigOrthoganol;

  /**
   * Constructor
   *
   * @param netlist
   */
  public DCSweep(Netlist netlist) {

    this.netlist = netlist;
  }

  /**
   * Add a SweepDefinition
   *
   * @param sweepDef
   */
  public void addSweepConfig(DCSweepConfig sweepDef) {

    // sanity check
    if (this.dcSweepConfig == null) {
      this.dcSweepConfig = sweepDef;
      verify(sweepDef);
    } else if (this.dcSweepConfigOrthoganol == null) {
      this.dcSweepConfigOrthoganol = sweepDef;
      verify(sweepDef);
    } else {
      throw new IllegalArgumentException("Only two SweepDefinitions maximum allowed!");
    }
  }

  /**
   * sanity checks a SweepDefinition
   *
   * @param sweepDefinition
   */
  private void verify(DCSweepConfig sweepDefinition) {

    // make sure componentToSweepID is actually in the circuit netlist
    SimulationPreCheck.verifyComponentToSweepOrDriveId(netlist, sweepDefinition.getSweepID());
  }

  public SimulationResult run(String observable) {

    netlist.verifyCircuit();
    //    System.out.println("netlist " + netlist);

    if (dcSweepConfig == null) {
      throw new IllegalArgumentException("No sweepDef found! Use addSweepDef() to add one!");
    }

    // 1. load variable to sweep, from sweepDef1
    Component sweepableComponent1 = netlist.getComponent(dcSweepConfig.getSweepID());
    SimulationResult dcSweepResult;

    if (dcSweepConfigOrthoganol == null) {
      dcSweepResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable,
          getSingleDCSweepResult(dcSweepConfig, sweepableComponent1, observable));
    } else {

      Map<String, SimulationPlotData> combinedSimulationPlotDataMap = new LinkedHashMap<>();

      Component sweepableComponent2 = netlist.getComponent(dcSweepConfigOrthoganol.getSweepID());
      String sweepLabel2 = getSweepLabel(sweepableComponent2);
      for (double i = dcSweepConfigOrthoganol.getStartValue(); i <= dcSweepConfigOrthoganol.getEndValue(); i += dcSweepConfigOrthoganol.getStepSize()) {

        // change component value in sweep 1 to i
        netlist.getComponent(dcSweepConfigOrthoganol.getSweepID()).setSweepValue(i);
        String orthoganolValue = (sweepLabel2 + " = " + i);
        // System.out.println(orthoganolValue);
        SimulationResult singleSimulationResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable,
            getSingleDCSweepResult(dcSweepConfig, sweepableComponent1, observable));
        for (Entry<String, SimulationPlotData> entrySet : singleSimulationResult.getSimulationPlotDataMap().entrySet()) {
          //          String observableValueID = entrySet.getKey();
          //          System.out.println("observableValueID " + observableValueID);
          SimulationPlotData simulationData = entrySet.getValue();
          combinedSimulationPlotDataMap.put(orthoganolValue, simulationData);
        }
      }
      //      System.out.println("observable " + observable);
      dcSweepResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable, combinedSimulationPlotDataMap);
      //      System.out.println("dcSweepResult " + dcSweepResult);

    }
    return dcSweepResult;
  }

  /**
   * @param sweepDefinition
   * @param sweepableComponent
   * @param observable
   * @return
   */
  private Map<String, SimulationPlotData> getSingleDCSweepResult(DCSweepConfig sweepDefinition, Component sweepableComponent,
      String observable) {

    Map<String, SimulationPlotData> simulationDataMap = new LinkedHashMap<>();
    //    System.out.println("sweepableComponent " + sweepableComponent);
    //    System.out.println("simulationDataMap " + simulationDataMap);

    simulationDataMap.put(sweepableComponent.getId(), new SimulationPlotData());
    simulationDataMap.put(observable, new SimulationPlotData());

    // 2. for each step, get DC Operating Point
    DCOperatingPointResult dCOperatingPointResult = null;
    BigDecimal firstPoint = BigDecimal.valueOf(sweepDefinition.getStartValue());
    BigDecimal stepSize = BigDecimal.valueOf(sweepDefinition.getStepSize());
    BigDecimal stopValue = BigDecimal.valueOf(sweepDefinition.getEndValue());
    for (BigDecimal i = firstPoint; i.compareTo(stopValue) <= 0; i = i.add(stepSize)) {

      sweepableComponent.setSweepValue(i.doubleValue());

      //      System.out.println("i= " + i);

      // Note: sometimes the DC Op will not converge. Therefore we catch the NodalAnalysisConvergenceException and just skip it
      try {
        dCOperatingPointResult = new DCOperatingPoint(netlist).run();
        //        System.out.println(dCOperatingPointResult.toString());

        simulationDataMap.get(sweepableComponent.getId()).getxData().add(i);
        simulationDataMap.get(sweepableComponent.getId()).getyData().add(i);

        simulationDataMap.get(observable).getxData().add(i);
        simulationDataMap.get(observable).getyData().add(dCOperatingPointResult.getValue(observable));

      } catch (NodalAnalysisConvergenceException e) {
        System.out.println("skipping value " + i + " because of failure to converge!");
      }
    }

    // 3. return the raw data
    return simulationDataMap;
  }

  /**
   * @param sweepableComponent
   * @return
   */
  private String getSweepLabel(Component sweepableComponent) {

    if (sweepableComponent instanceof Resistor) {
      return "R(" + sweepableComponent.getId() + ")";
    } else if (sweepableComponent instanceof DCCurrent) {
      return "I(" + sweepableComponent.getId() + ")";
    } else if (sweepableComponent instanceof DCVoltage) {
      return "V(" + sweepableComponent.getId() + ")";
    } else if (sweepableComponent instanceof VCCS) {
      return "G(" + sweepableComponent.getId() + ")";
    } else if (sweepableComponent instanceof VCVS) {
      return "E(" + sweepableComponent.getId() + ")";
    } else {
      throw new IllegalArgumentException(Component.class.getCanonicalName() + " not yet supported in DCSweep!");
    }
  }
}
