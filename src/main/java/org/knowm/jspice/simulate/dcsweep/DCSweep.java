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

import org.knowm.jspice.circuit.Circuit;
import org.knowm.jspice.component.Component;
import org.knowm.jspice.component.element.linear.Resistor;
import org.knowm.jspice.component.source.DCCurrent;
import org.knowm.jspice.component.source.DCVoltage;
import org.knowm.jspice.component.source.VCCS;
import org.knowm.jspice.component.source.VCVS;
import org.knowm.jspice.simulate.SimulationData;
import org.knowm.jspice.simulate.SimulationPreCheck;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPoint;
import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;
import org.knowm.jspice.simulate.dcoperatingpoint.NodalAnalysisConvergenceException;

/**
 * @author timmolter
 */
public class DCSweep {

  private final Circuit circuit;

  private SweepDefinition sweepDef1;
  private SweepDefinition sweepDefOrthoganol;

  /**
   * Constructor
   *
   * @param circuit
   */
  public DCSweep(Circuit circuit) {

    this.circuit = circuit;
  }

  /**
   * Add a SweepDefinition
   *
   * @param sweepDef
   */
  public void addSweepDef(SweepDefinition sweepDef) {

    // sanity check
    if (this.sweepDef1 == null) {
      this.sweepDef1 = sweepDef;
      verify(sweepDef);
    }
    else if (this.sweepDefOrthoganol == null) {
      this.sweepDefOrthoganol = sweepDef;
      verify(sweepDef);
    }
    else {
      throw new IllegalArgumentException("Only two SweepDefinitions maximum allowed!");
    }
  }

  /**
   * sanity checks a SweepDefinition
   *
   * @param sweepDefinition
   */
  private void verify(SweepDefinition sweepDefinition) {

    // make sure componentToSweepID is actually in the circuit netlist
    SimulationPreCheck.verifyComponentToSweepOrDriveId(circuit, sweepDefinition.getComponentToSweepID());
  }

  public SimulationResult run(String observable) {

    circuit.verifyCircuit();

    if (sweepDef1 == null) {
      throw new IllegalArgumentException("No sweepDef found! Use addSweepDef() to add one!");
    }

    // 1. load variable to sweep, from sweepDef1
    Component sweepableComponent1 = circuit.getNetlist().getComponent(sweepDef1.getComponentToSweepID());
    SimulationResult dcSweepResult = null;

    if (sweepDefOrthoganol == null) {
      dcSweepResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable, getSingleDCSweepResult(sweepDef1, sweepableComponent1, observable));
    }
    else {

      Map<String, SimulationData> combinedSimulationDataMap = new LinkedHashMap<String, SimulationData>();

      Component sweepableComponent2 = circuit.getNetlist().getComponent(sweepDefOrthoganol.getComponentToSweepID());
      String sweepLabel2 = getSweepLabel(sweepableComponent2);
      for (double i = sweepDefOrthoganol.getStartValue(); i <= sweepDefOrthoganol.getEndValue(); i += sweepDefOrthoganol.getStepSize()) {

        // change component value in sweep 1 to i
        circuit.getNetlist().getComponent(sweepDefOrthoganol.getComponentToSweepID()).setSweepValue(i);
        String orthoganolValue = (sweepLabel2 + " = " + i);
        // System.out.println(orthoganolValue);
        SimulationResult singleSimulationResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable, getSingleDCSweepResult(sweepDef1, sweepableComponent1, observable));
        for (Entry<String, SimulationData> entrySet : singleSimulationResult.getSimulationDataMap().entrySet()) {
          String observableValueID = entrySet.getKey();
          SimulationData simulationData = entrySet.getValue();
          combinedSimulationDataMap.put(orthoganolValue, simulationData);
        }
      }
      dcSweepResult = new SimulationResult(getSweepLabel(sweepableComponent1), observable, combinedSimulationDataMap);
    }
    return dcSweepResult;
  }

  /**
   * @param sweepDefinition
   * @param sweepableComponent
   * @param observable
   * @return
   */
  private Map<String, SimulationData> getSingleDCSweepResult(SweepDefinition sweepDefinition, Component sweepableComponent, String observable) {

    Map<String, SimulationData> simulationDataMap = new LinkedHashMap<String, SimulationData>();

    simulationDataMap.put(observable, new SimulationData());

    // 2. for each step, get DC Operating Point
    BigDecimal firstPoint = BigDecimal.valueOf(sweepDefinition.getStartValue());
    BigDecimal stepSize = BigDecimal.valueOf(sweepDefinition.getStepSize());
    BigDecimal stopValue = BigDecimal.valueOf(sweepDefinition.getEndValue());
    for (BigDecimal i = firstPoint; i.compareTo(stopValue) <= 0; i = i.add(stepSize)) {

      sweepableComponent.setSweepValue(i.doubleValue());

      // System.out.println("i= " + i);

      // Note: sometimes the DC Op will not converge. Therefore we catch the NodalAnalysisConvergenceException and just skip it
      try {
        DCOperatingPointResult dCOperatingPointResult = new DCOperatingPoint(circuit).run();
        // System.out.println(dcOpResult.toString());

        simulationDataMap.get(observable).getxData().add(i);
        simulationDataMap.get(observable).getyData().add(dCOperatingPointResult.getValue(observable));
      } catch (NodalAnalysisConvergenceException e) {
        System.out.println("skipping value " + i + " because of failure to converge!");
        continue;
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
    }
    else if (sweepableComponent instanceof DCCurrent) {
      return "I(" + sweepableComponent.getId() + ")";
    }
    else if (sweepableComponent instanceof DCVoltage) {
      return "V(" + sweepableComponent.getId() + ")";
    }
    else if (sweepableComponent instanceof VCCS) {
      return "G(" + sweepableComponent.getId() + ")";
    }
    else if (sweepableComponent instanceof VCVS) {
      return "E(" + sweepableComponent.getId() + ")";
    }
    else {
      throw new IllegalArgumentException(Component.class.getCanonicalName() + " not yet supported in DCSweep!");
    }
  }
}
