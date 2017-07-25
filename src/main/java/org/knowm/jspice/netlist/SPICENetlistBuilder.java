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
package org.knowm.jspice.netlist;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.knowm.jspice.simulate.transientanalysis.driver.Driver;
import org.knowm.jspice.simulate.transientanalysis.driver.Pulse;
import org.knowm.jspice.simulate.transientanalysis.driver.Sine;

import io.dropwizard.configuration.ConfigurationSourceProvider;

public class SPICENetlistBuilder {

  private static List<String> getPreProcessedLines(String fileName, ConfigurationSourceProvider configurationSourceProvider) throws IOException {

    List<String> netlistLines = new ArrayList<>();

    // create netlist from traditional SPICE netlist file.
    try (PeekableScanner scanner = new PeekableScanner(configurationSourceProvider.open(fileName))) {

      String multilineModelDef = null;

      while (scanner.hasNext()) {

        System.out.println("multilineModelDef = " + multilineModelDef);

        String nextLine = scanner.nextLine().trim();
        System.out.println("nextLine = " + nextLine);

        if (nextLine.startsWith(".END") || nextLine.startsWith(".end")) {
          break;
        }

        if (nextLine.startsWith("*")) {
          continue;
        }
        if (nextLine.startsWith(".STEP") || nextLine.startsWith(".step")) {
          continue;
        }
        if (nextLine.length() < 1) {
          continue;
        }

        // TODO implement print
        if (nextLine.startsWith(".PRINT") || nextLine.startsWith(".print")) {
          continue;
        }

        // multi-line model def start
        if (nextLine.startsWith(".model") && scanner.peek().startsWith("+")) {
          multilineModelDef = nextLine;
          continue;
        }

        // multi-line model def add
        if (multilineModelDef != null && scanner.peek().startsWith("+")) {
          multilineModelDef = multilineModelDef + nextLine.replace("+", " ");
          continue;
        }

        // multi-line model def end
        if (multilineModelDef != null && nextLine.endsWith(")")) {
          System.out.println("HERE");
          multilineModelDef = multilineModelDef + nextLine.replace("+", " ");
          String modelString = multilineModelDef;
          netlistLines.add(modelString);
          multilineModelDef = null;
          continue;
        }

        netlistLines.add(nextLine);

      }
    }
    return netlistLines;
  }

  private static class PeekableScanner implements Closeable {

    private Scanner scanner;
    private String nextLine;

    public PeekableScanner(InputStream source) {

      scanner = new Scanner(source);
      nextLine = (scanner.hasNext() ? scanner.nextLine().trim() : null);
    }

    public boolean hasNext() {
      return (nextLine != null);
    }

    public String nextLine() {
      String current = nextLine;
      nextLine = (scanner.hasNext() ? scanner.nextLine().trim() : null);
      return current;
    }

    public String peek() {
      return nextLine;
    }

    @Override
    public void close() throws IOException {
      scanner.close();
    }
  }

  public static Netlist buildFromSPICENetlist(String fileName, ConfigurationSourceProvider configurationSourceProvider) throws IOException {

    List<String> netlistLines = getPreProcessedLines(fileName, configurationSourceProvider);

    // Temporary Lists/Maps
    NetlistBuilder netlistBuilder = new NetlistBuilder();
    List<Driver> drivers = new ArrayList<>();
    Map<String, Double> paramsMap = new HashMap<>();
    Map<String, String> memristorsMap = new HashMap<>();
    Map<String, Map<String, String>> memristorsModelsMap = new HashMap<>();

    // For each line...
    for (int i = 0; i < netlistLines.size(); i++) {

      String line = netlistLines.get(i);
      System.out.println("line: " + line);

      if (line.startsWith(".PARAM") || line.startsWith(".param")) {

        String paramString = line.substring(6);
        paramString = paramString.replaceAll("\\s+", "");
        String[] paramSplit = paramString.split("=");
        paramsMap.put(paramSplit[0], fromString(paramSplit[1], 0));

      } else if (line.startsWith("R") || line.startsWith("r")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double resistance = fromString(tokens[3]);
        netlistBuilder.addNetlistResistor(id, resistance, nodeA, nodeB);

      } else if (line.startsWith("C") || line.startsWith("c")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double capactiance = fromString(tokens[3]);
        netlistBuilder.addNetlistCapacitor(id, capactiance, nodeA, nodeB);

      } else if (line.startsWith("L") || line.startsWith("l")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double inductance = fromString(tokens[3]);
        netlistBuilder.addNetlistInductor(id, inductance, nodeA, nodeB);

      } else if (line.startsWith("V") || line.startsWith("v")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        // DC
        int dcStartIndex = line.indexOf("DC");
        tokens = line.substring(dcStartIndex).split("\\s+");
        double dc = fromString(tokens[1], 0);
        netlistBuilder.addNetlistDCVoltage(id, dc, nodeA, nodeB);

        // Sin
        int sinStartIndex = line.indexOf("SIN");
        if (sinStartIndex >= 0) {
          String sineDef = line.substring(sinStartIndex + 4, line.indexOf(")")).trim();

          //        SIN(V0 VA FREQ TD THETA PHASE)
          tokens = sineDef.split("\\s+");
          String v0 = ifExists(tokens, 0);
          String amplitude = ifExists(tokens, 1);
          String freq = ifExists(tokens, 2);
          String phase = ifExists(tokens, 5);
          drivers.add(new Sine(id, fromString(v0, 0), fromString(phase, 0), fromString(amplitude, 0), fromString(freq, 0)));
        }

        // Pulse
        int pulseStartIndex = line.indexOf("PULSE");
        if (pulseStartIndex >= 0) {
          String pulseDef = line.substring(pulseStartIndex + 6, line.indexOf(")")).trim();
          System.out.println("pulseDef = " + pulseDef);

          // PULSE( {v1} {v2} {tdelay} {trise} {tfall} {width} {period} )
          tokens = pulseDef.split("\\s+");
          String v1AsString = ifExists(tokens, 0);
          String v2AsString = ifExists(tokens, 1);
          String widthAsString = ifExists(tokens, 5);
          String periodAsString = ifExists(tokens, 6);

          System.out.println("v1AsString = " + v1AsString);
          System.out.println("v2AsString = " + v2AsString);
          System.out.println("widthAsString = " + widthAsString);
          System.out.println("periodAsString = " + periodAsString);

          // conversion from SPICE to JSPICE driver

          double v1 = fromString(v1AsString, 0);
          double v2 = fromString(v2AsString, 0);
          double width = fromString(widthAsString, 0);
          double period = fromString(periodAsString, 0);

          double dcOffset = (v1 + v2) / 2;
          double amplitude = Math.abs(v1 - v2) / 2;
          double frequency = 1 / period;
          double dutyCycle = width / period;
          double phase = v2 > v1 ? 0 : period / 2;

          drivers.add(new Pulse(id, dcOffset, phase, amplitude, frequency, dutyCycle));
        }

      } else if (line.startsWith(".TRAN") || line.startsWith(".tran")) {

        // .tran
        String[] tokens = line.split("\\s+");
        String stepSize = tokens[1];
        String endTime = tokens[2];
        netlistBuilder.addTransientSimulationConfig(fromString(endTime), fromString(stepSize), drivers.toArray(new Driver[drivers.size()]));

      } else if (line.startsWith("YMEMRISTOR") || line.startsWith("ymemristor")) {

        // memristor
        String[] tokens = line.split("\\s+");
        String id = tokens[1];
        memristorsMap.put(id, line);

      } else if (line.startsWith(".MODEL") || line.startsWith(".model")) {

        // memristor
        String[] tokens = line.split("\\s+");

        String modelID = tokens[1];
        String modelLine = line.substring(line.indexOf("(") + 1, line.indexOf(")") - 1).trim();
        String[] modelTokens = modelLine.split("\\s+");

        Map<String, String> modelMap = new HashMap<>();
        for (String modelParam : modelTokens) {
          String[] keyValue = modelParam.split("=");
          modelMap.put(keyValue[0], keyValue[1]);
        }
        memristorsModelsMap.put(modelID, modelMap);
      } else if (line.startsWith("I") || line.startsWith("i")) {
        //        SIN(I0 IA FREQ TD THETA PHASE)
        throw new IllegalArgumentException("Not yet Implemented!!!  >I");
      } else if (line.startsWith("M") || line.startsWith("m")) {
        throw new IllegalArgumentException("Not yet Implemented!!!  >M");
      } else if (line.startsWith("X") || line.startsWith("x")) {
        throw new IllegalArgumentException("Not yet Implemented!!!  >X");
      } else {
        throw new IllegalArgumentException("Not yet Implemented!!! " + line);
      }
    }

    // finally loop through all memristors, find their matching model and add it to the netlist
    for (Entry<String, String> entry : memristorsMap.entrySet()) {

      String memristorID = entry.getKey();
      String memristorLine = entry.getValue();
      String[] tokens = memristorLine.split("\\s+");
      //      String ID = tokens[1];
      String nodeA = tokens[2];
      String nodeB = tokens[3];
      String modelID = tokens[4];

      Map<String, String> modelMap = memristorsModelsMap.get(modelID);
      //      System.out.println("modelLine " + modelLine);

      netlistBuilder.addNetlistMMSSMemristor(memristorID, paramsMap.get("Rinit"), fromString(modelMap.get("Ron")), fromString(modelMap.get("Roff")),
          fromString(modelMap.get("Tau")), fromString(modelMap.get("Von")), fromString(modelMap.get("Voff")), fromString(modelMap.get("Phi"), 1),
          fromString(modelMap.get("Sfa"), 0), fromString(modelMap.get("Sfb"), 0), fromString(modelMap.get("Sra"), 0), fromString(modelMap.get
              ("Srb"), 0), nodeA, nodeB);
    }

    return netlistBuilder.build();
  }

  private static String ifExists(String[] array, int index) {

    if (array.length > index) {
      return array[index];
    } else {
      return null;
    }

  }

  private static double fromString(String value) {

    // take care of units

    // remove electrical unit
    if (value.endsWith("ohm")) {
      value = value.replace("ohm", "");
    } else if (value.endsWith("OHM")) {
      value = value.replace("OHM", "");
    } else if (value.endsWith("s")) {
      value = value.replace("s", "");
    } else if (value.endsWith("S")) {
      value = value.replace("S", "");
    } else if (value.endsWith("f")) {
      value = value.replace("s", "");
    } else if (value.endsWith("F")) {
      value = value.replace("S", "");
    }

    //    F    E-15    femto
//    P    E-12    pico
//    N    E-9    nano
//    U    E-6    micro
//    M    E-3    milli
//    K    E+3    kilo
//    MEG	E+6	mega
//    G	E+9	giga
//    T	E+12	tera

    if (value.endsWith("F") || value.endsWith("f")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1_000_000_000_000_000L;
    } else if (value.endsWith("P") || value.endsWith("p")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1_000_000_000_000L;
    } else if (value.endsWith("N") || value.endsWith("n")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1_000_000_000;
    } else if (value.endsWith("U") || value.endsWith("u")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1_000_000;
    } else if (value.endsWith("M") || value.endsWith("m")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1_000;
    } else if (value.endsWith("K") || value.endsWith("k")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000;
    } else if (value.endsWith("MEG") || value.endsWith("meg")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000_000;
    } else if (value.endsWith("G") || value.endsWith("g")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000_000_000;
    } else if (value.endsWith("T") || value.endsWith("t")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000_000_000_000L;
    } else {
      return Double.parseDouble(value.trim());
    }
  }

  private static double fromString(String value, double defaultValue) {

    if (value == null) {
      return defaultValue;
    }

    return fromString(value);

  }
}
