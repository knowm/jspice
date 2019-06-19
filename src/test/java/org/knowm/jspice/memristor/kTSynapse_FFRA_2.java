package org.knowm.jspice.memristor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.knowm.configuration.ConfigurationException;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistBuilder;
import org.knowm.jspice.simulate.transientanalysis.driver.DC;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class kTSynapse_FFRA_2 {

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

    kTSynapse_FFRA_2 kTSynapse_ffra = new kTSynapse_FFRA_2();
    kTSynapse_ffra.go();
  }

  private void go() throws IOException, ConfigurationException {

    List<Number> dataVy = new ArrayList<>();
    List<Number> dataRa = new ArrayList<>();
    List<Number> dataRb = new ArrayList<>();

    double Vy;
    double R_A = 1200;
    double R_B = 550;

    for (int i = 0; i < 100; i++) {

      // 1. create FF netlist
      InputStream in;
      Process proc;
      // 2. run FF trans.
      readReplaceWrite("FF-kTSynapse-netlist.cir", R_A, R_B);
      proc = Runtime.getRuntime().exec("java -jar target/jspice.jar FF-kTSynapse-netlist.tmp.cir");
      in = proc.getInputStream();
      double[] VyRaRb;
      // 3. get Vy, R_A and R_B
      VyRaRb = getVyRaRb(in);
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
        readReplaceWrite("RH-kTSynapse-netlist.cir", R_A, R_B);
        proc = Runtime.getRuntime().exec("java -jar target/jspice.jar RH-kTSynapse-netlist.tmp.cir");
        in = proc.getInputStream();

        R_B = getR(in, "R(MB)");
        //        System.out.println("RH");
      }
      // RL
      else {
        // 1. create RL netlist
        // 2. run RL trans.
        readReplaceWrite("RL-kTSynapse-netlist.cir", R_A, R_B);
        proc = Runtime.getRuntime().exec("java -jar target/jspice.jar RL-kTSynapse-netlist.tmp.cir");
        in = proc.getInputStream();

        R_A = getR(in, "R(MA)");
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
    String out = (cirTemplate).replace(".cir", ".tmp.cir");
    //    System.out.println("out = " + out);
    // read, replace, write from resources folder
    try (InputStreamReader isReader = new InputStreamReader(new FileInputStream(cirTemplate));
        BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {

      String header = null;
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

  private double[] getVyRaRb(InputStream simulationResult) {

    String header = null;
    String lastline = null;
    try (InputStreamReader isReader = new InputStreamReader(simulationResult)) {

      String buf = null;
      BufferedReader rdr = new BufferedReader(isReader);
      while ((buf = rdr.readLine()) != null) {
        // Apply regex on buf
        //        System.out.println("buf = " + buf);
        if (header == null) {
          header = buf;
        }
        if (buf.contains("End")) {
          break;
        }
        lastline = buf;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    String[] headerTokens = header.split("\t");
    //    System.out.println("Arrays.toString(headerTokens) = " + Arrays.toString(headerTokens));
    //get target indices
    int VyIdx = 0;
    int RaIdx = 0;
    int RbIdx = 0;
    for (int i = 0; i < headerTokens.length; i++) {
      if (headerTokens[i].equals("V(y)")) {
        VyIdx = i;
      } else if (headerTokens[i].equals("R(MA)")) {
        RaIdx = i;
      } else if (headerTokens[i].equals("R(MB)")) {
        RbIdx = i;
      }
    }
    String[] valueTokens = lastline.split("\t");
    double Vy = Double.parseDouble(valueTokens[VyIdx]);
    double R_A = Double.parseDouble(valueTokens[RaIdx]);
    double R_B = Double.parseDouble(valueTokens[RbIdx]);

    return new double[]{Vy, R_A, R_B};
  }

  private double getR(InputStream simulationResult, String memristorKey) {

    String header = null;
    String lastline = null;
    try (InputStreamReader isReader = new InputStreamReader(simulationResult)) {

      String buf = null;
      BufferedReader rdr = new BufferedReader(isReader);
      while ((buf = rdr.readLine()) != null) {
        // Apply regex on buf
        //        System.out.println("buf = " + buf);
        if (header == null) {
          header = buf;
        }
        if (buf.contains("End")) {
          break;
        }
        lastline = buf;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    String[] headerTokens = header.split("\t");
    //    System.out.println("Arrays.toString(headerTokens) = " + Arrays.toString(headerTokens));
    //get target indices
    int RIdx = 0;
    for (int i = 0; i < headerTokens.length; i++) {
      if (headerTokens[i].equals(memristorKey)) {
        RIdx = i;
      }
    }
    String[] valueTokens = lastline.split("\t");
    double R = Double.parseDouble(valueTokens[RIdx]);

    return R;

    //    List<Number> rAData = simulationResult.getSimulationPlotDataMap().get(memristorKey).getyData();
    //    double r = rAData.get(rAData.size() - 1).doubleValue();
    //    return r;
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
