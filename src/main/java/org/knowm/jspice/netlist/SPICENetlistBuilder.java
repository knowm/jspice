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
          String sineDef = line.substring(sinStartIndex + 4, line.indexOf(")"));

          tokens = sineDef.split("\\s+");
          String v0 = ifExists(tokens, 0);
          String amplitude = ifExists(tokens, 1);
          String freq = ifExists(tokens, 2);
          String phase = ifExists(tokens, 5);
          drivers.add(new Sine(id, fromString(v0, 0), fromString(phase, 0), fromString(amplitude, 0), fromString(freq, 0)));
        }

        //        SIN(I0 IA FREQ TD THETA PHASE)
        //        SIN(V0 VA FREQ TD THETA PHASE)

      } else if (line.startsWith(".TRAN") || line.startsWith(".tran")) {

        // .tran
        String[] tokens = line.split("\\s+");
        String stepSize = tokens[1];
        String endTime = tokens[2];
        netlistBuilder.addTransientSimulationConfig(fromString(endTime, 1), fromString(stepSize, .1), drivers.toArray(new Driver[drivers.size()]));

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
          fromString(modelMap.get("Tau")), fromString(modelMap.get("Von")), fromString(modelMap.get("Voff")), 1, 0, 0, 0, 0, nodeA, nodeB);
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
    if (value.endsWith("M")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) + 1_000_000;
    } else {

      return Double.parseDouble(value.trim());
    }
  }

  private static double fromString(String value, double defaultValue) {

    if (value == null) {
      return defaultValue;
    }

    // take care of units
    if (value.endsWith("M")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) / 1000;
    } else {

      return Double.parseDouble(value.trim());
    }
  }
}
