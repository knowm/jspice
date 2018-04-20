package org.knowm.jspice.memristor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.knowm.configuration.ConfigurationException;
import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistBuilder;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.driver.DC;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class kTSynapse_FFRA {

  private final static String STOP_TIME = "10us";
  private final static String TIME_STEP = "2us";
  private static final double Y_THRESH = 0.5;

  /**
   * characteristic time scale of the device
   */
  private static final double TAU = 0.0001;

  /**
   * the number of MSS's
   */
  private static final double N = 1000;
  private static final double R_OFF = 5000;
  private static final double R_ON = 500;

  /**
   * barrier potentials
   */
  private static final double V_OFF = .27;
  private static final double V_ON = .27;

  private final static double sa = 0; // N/A
  private final static double sb = 0; // N/A
  private final static double phi = 1;

  public static void main(String[] args) throws IOException, ConfigurationException {

    kTSynapse_FFRA kTSynapse_ffra = new kTSynapse_FFRA();
    kTSynapse_ffra.go(true);
  }

  private void go(boolean fromCirFile) throws IOException, ConfigurationException {

    List<Number> dataVy = new ArrayList<>();
    List<Number> dataRa = new ArrayList<>();
    List<Number> dataRb = new ArrayList<>();

    double Vy;
    double R_A = 1200;
    double R_B = 550;

    for (int i = 0; i < 100; i++) {

      // 1. create FF netlist
      Netlist netlist;
      // 2. run FF trans.
      SimulationResult simulationResult;
      if (fromCirFile) {
        readReplaceWrite("FF-kTSynapse-netlist.cir", R_A, R_B);
        simulationResult = JSpice.simulate("FF-kTSynapse-netlist.tmp.cir");
      } else {
        netlist = getFFNetlist(R_A, R_B);
        simulationResult = JSpice.simulate(netlist);
      }
      //            System.out.println("simulationResult = " + simulationResult);
      double[] VyRaRb;
      // 3. get Vy, R_A and R_B
      VyRaRb = getVyRaRb(simulationResult);
      dataVy.add(VyRaRb[0]);
      dataRa.add(VyRaRb[1]);
      dataRb.add(VyRaRb[2]);
      Vy = VyRaRb[0];
      R_A = VyRaRb[1];
      R_B = VyRaRb[2];

      // RH
      if (VyRaRb[0] < Y_THRESH) {
        // 1. create RH netlist
        // 2. run RH trans.
        if (fromCirFile) {
          readReplaceWrite("RH-kTSynapse-netlist.cir", R_A, R_B);
          simulationResult = JSpice.simulate("RH-kTSynapse-netlist.tmp.cir");
        } else {
          netlist = getRHNetlist(R_A, R_B);
          simulationResult = JSpice.simulate(netlist);
        }

        R_B = getR(simulationResult, "R(MB)");
        //        System.out.println("RH");
      }
      // RL
      else {
        // 1. create RL netlist
        // 2. run RL trans.
        if (fromCirFile) {
          readReplaceWrite("RL-kTSynapse-netlist.cir", R_A, R_B);
          simulationResult = JSpice.simulate("RL-kTSynapse-netlist.tmp.cir");
        } else {
          netlist = getRLNetlist(R_A, R_B);
          simulationResult = JSpice.simulate(netlist);
        }
        R_A = getR(simulationResult, "R(MA)");
        //        System.out.println("RL");
      }
    }

    // Plot results
    XYChart chartVy = new XYChartBuilder().build();
    chartVy.addSeries("Vy", dataVy);
    new SwingWrapper(chartVy).displayChart();

    XYChart chartR = new XYChartBuilder().build();
    chartR.addSeries("Ra", dataRa);
    chartR.addSeries("Rb", dataRb);
    new SwingWrapper(chartR).displayChart();
  }

  private void readReplaceWrite(String cirTemplate, double R_A, double R_B) {

    //    System.out.println("cirTemplate = " + cirTemplate);
    String out = getClass().getClassLoader().getResource(cirTemplate).getPath().replace(".cir", ".tmp.cir");
    //    System.out.println("out = " + out);
    // read, replace, write from resources folder
    try (InputStreamReader isReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(cirTemplate));
        BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
      String buf = null;
      BufferedReader rdr = new BufferedReader(isReader);
      while ((buf = rdr.readLine()) != null) {
        // Apply regex on buf
        buf = buf.replaceAll("\\$\\{R_A\\}", Double.toString(R_A));
        buf = buf.replaceAll("\\$\\{R_B\\}", Double.toString(R_B));
        bw.write(buf);
        bw.write("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private double[] getVyRaRb(SimulationResult simulationResult) {

    List<Number> vYData = simulationResult.getSimulationPlotDataMap().get("V(y)").getyData();
    double Vy = vYData.get(vYData.size() - 1).doubleValue();
    List<Number> rAData = simulationResult.getSimulationPlotDataMap().get("R(MA)").getyData();
    double R_A = rAData.get(rAData.size() - 1).doubleValue();
    List<Number> rBData = simulationResult.getSimulationPlotDataMap().get("R(MB)").getyData();
    double R_B = rBData.get(rBData.size() - 1).doubleValue();
    //    System.out.println("Vy = " + Vy);
    //    System.out.println("R_A = " + R_A);
    //    System.out.println("R_B = " + R_B);
    return new double[]{Vy, R_A, R_B};
  }

  private double getR(SimulationResult simulationResult, String memristorKey) {

    List<Number> rAData = simulationResult.getSimulationPlotDataMap().get(memristorKey).getyData();
    double r = rAData.get(rAData.size() - 1).doubleValue();
    return r;
  }

  Netlist getFFNetlist(double R_A, double R_B) {

    Netlist netlist = new NetlistBuilder().addNetlistMSSMemristor("MA", R_A, R_ON, R_OFF, N, TAU, V_ON, V_OFF, phi, sa, sb, sa, sb, "A", "y")
        .addNetlistMSSMemristor("MB", R_B, R_ON, R_OFF, N, TAU, V_ON, V_OFF, phi, sa, sb, sa, sb, "y", "0").addNetlistDCVoltage("VA", 0, "A", "0")
        .addTransientSimulationConfig(STOP_TIME, TIME_STEP, new DC("VA", 2 * Y_THRESH)).build();
    return netlist;
  }

  Netlist getRHNetlist(double R_A, double R_B) {

    Netlist netlist = new NetlistBuilder().addNetlistMSSMemristor("MB", R_B, R_ON, R_OFF, N, TAU, V_ON, V_OFF, phi, sa, sb, sa, sb, "y", "0")
        .addNetlistDCVoltage("VY", 0, "y", "0").addTransientSimulationConfig(STOP_TIME, TIME_STEP, new DC("VY", -Y_THRESH)).build();
    return netlist;
  }

  Netlist getRLNetlist(double R_A, double R_B) {

    Netlist netlist = new NetlistBuilder().addNetlistMSSMemristor("MA", R_A, R_ON, R_OFF, N, TAU, V_ON, V_OFF, phi, sa, sb, sa, sb, "A", "0")
        .addNetlistDCVoltage("VA", 0, "A", "0").addTransientSimulationConfig(STOP_TIME, TIME_STEP, new DC("VA", -Y_THRESH)).build();
    return netlist;
  }

}
