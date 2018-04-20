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
package org.knowm.jspice.netlist.spice;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.knowm.configuration.provider.ConfigurationSourceProvider;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistBuilder;
import org.knowm.jspice.simulate.transientanalysis.driver.Driver;
import org.knowm.jspice.simulate.transientanalysis.driver.Pulse;
import org.knowm.jspice.simulate.transientanalysis.driver.Sine;

public class SPICENetlistBuilder {

  public static Netlist buildFromSPICENetlist(String fileName, ConfigurationSourceProvider configurationSourceProvider) throws IOException {

    //    System.out.println("...............Preprocessing the Netlist.................");
    List<String> netlistLines = getPreProcessedLines(fileName, configurationSourceProvider);

    Map<String, SPICESubckt> subcircuitMap = new HashMap<>();
    Map<String, String> printItemMap = new HashMap<>();

    // For each line...
    for (int i = 0; i < netlistLines.size(); i++) {

      String line = netlistLines.get(i);

      // check if there is an .INCLUDE directive
      //    System.out.println("...............Looking for .Includes.................");

      if (line.startsWith(".INCLUDE") || line.startsWith(".include")) {

        String[] paramSplit = line.split("\\s+");
        String includeFileName = paramSplit[1].replaceAll("\"", "");
        List<String> subCircuitNetlistLines = getPreProcessedLines(includeFileName, configurationSourceProvider);

        String currentSubcircuit = null;
        for (int j = 0; j < subCircuitNetlistLines.size(); j++) {

          String subCircuitNetlistLine = subCircuitNetlistLines.get(j);

          //          System.out.println("subCircuitNetlistLine: " + subCircuitNetlistLine);

          if (subCircuitNetlistLine.startsWith(".SUBCKT") || subCircuitNetlistLine.startsWith(".subckt")) {

            SPICESubckt spiceSubckt = new SPICESubckt();

            String subcktParamString = subCircuitNetlistLine.substring(7);
            String[] subcktParamSplit = subcktParamString.split("\\s+");

            String id = subcktParamSplit[1];
            currentSubcircuit = id;
            spiceSubckt.setId(id);
            for (int k = 2; k < subcktParamSplit.length; k++) {
              spiceSubckt.addNode(subcktParamSplit[k]);
            }
            subcircuitMap.put(id, spiceSubckt);

          } else if (subCircuitNetlistLine.startsWith(".ENDS") || subCircuitNetlistLine.startsWith(".ends")) {

            currentSubcircuit = null;
          } else if (currentSubcircuit != null) {

            // add a sub component
            SPICESubckt spiceSubckt = subcircuitMap.get(currentSubcircuit);
            spiceSubckt.addLine(subCircuitNetlistLine);
          }
        }
      }
    }
    //    System.out.println("subcircuitMap = " + Arrays.toString(subcircuitMap.entrySet().toArray()));

    // replace subckts with mapped components to List

    //    System.out.println("...............Adding subckts to netlist.................");

    List<String> linesWithSubckts = new ArrayList<>();

    // For each line...
    for (int i = 0; i < netlistLines.size(); i++) {

      String line = netlistLines.get(i);
      //      System.out.println("line: " + line);

      if (line.startsWith("X") || line.startsWith("x")) {

        String[] tokens = line.split("\\s+");
        String id = tokens[0].substring(1);
        String subcktID = tokens[tokens.length - 1];

        SPICESubckt spiceSubckt = subcircuitMap.get(subcktID);
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        for (int j = 0; j < spiceSubckt.getNodes().size(); j++) {
          nodeIndexMap.put(spiceSubckt.getNodes().get(j), j);
        }
        for (String subcktLine : spiceSubckt.getLines()) {

          String newSubcktLine = subcktLine;
          String[] subcktTokens = newSubcktLine.split("\\s+");
          String componentID = subcktTokens[1];
          newSubcktLine = newSubcktLine.replace(componentID, componentID + "_" + id); // append the id
          for (Entry<String, Integer> stringIntegerEntry : nodeIndexMap.entrySet()) {

            newSubcktLine = newSubcktLine.replaceAll(stringIntegerEntry.getKey(), tokens[stringIntegerEntry.getValue() + 1]);
          }

          linesWithSubckts.add(newSubcktLine);

        }
      } else {
        linesWithSubckts.add(line);
      }
    }
    //    System.out.println("...............Parsing the Netlist.................");

    // Temporary Lists/Maps
    NetlistBuilder netlistBuilder = new NetlistBuilder();
    List<Driver> drivers = new ArrayList<>();
    Map<String, Double> paramsMap = new HashMap<>();
    Map<String, String> memristorsMap = new HashMap<>();
    Map<String, Map<String, String>> memristorsModelsMap = new HashMap<>();

    // For each line...
    for (int i = 0; i < linesWithSubckts.size(); i++) {

      String line = linesWithSubckts.get(i);

      if (line.startsWith("*") && i == 0) {
        // first line of the netilst is comment which is title of output raw file
        String sourceFile = line;

        System.out.println("...............Source of netList.... " + sourceFile);
        netlistBuilder.setSourceFile(sourceFile);
      } else if (line.startsWith(".PRINT") || line.startsWith(".print")) {

        //    System.out.println("...............Processing .PRINT statement");

        String[] printTokens = line.split("\\s+");

        for (String printItem : printTokens) {
          if (printItem.startsWith("Format") || printItem.startsWith("format")) {
            String[] keyValue = printItem.split("=");
            printItemMap.put(keyValue[0], keyValue[1]);

            String resFormat = keyValue[1];
            //            System.out.println("resFormat: " + resFormat);

            netlistBuilder.setResultsFormat(resFormat);
          }
          if (printItem.startsWith("File") || printItem.startsWith("file")) {
            String[] keyValue = printItem.split("=");
            printItemMap.put(keyValue[0], keyValue[1]);

            String resFilename = keyValue[1];

            //            System.out.println("resFilename: " + resFilename);
            netlistBuilder.setResultsFile(resFilename);
          }
        }
      }

      //      System.out.println("line: " + line);
      else if (line.startsWith(".PARAM") || line.startsWith(".param")) {

        String paramString = line.substring(6);
        paramString = paramString.replaceAll("\\s+", "");
        String[] paramSplit = paramString.split("=");
        paramsMap.put(paramSplit[0], SPICEUtils.doubleFromString(paramSplit[1], 0));

      } else if (line.startsWith("R") || line.startsWith("r")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double resistance = SPICEUtils.doubleFromString(tokens[3]);
        netlistBuilder.addNetlistResistor(id, resistance, nodeA, nodeB);

      } else if (line.startsWith("C") || line.startsWith("c")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double capactiance = SPICEUtils.doubleFromString(tokens[3]);
        netlistBuilder.addNetlistCapacitor(id, capactiance, nodeA, nodeB);

      } else if (line.startsWith("L") || line.startsWith("l")) {

        // voltage source
        String[] tokens = line.split("\\s+");
        String id = tokens[0];
        String nodeA = tokens[1];
        String nodeB = tokens[2];

        double inductance = SPICEUtils.doubleFromString(tokens[3]);
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
        double dc = SPICEUtils.doubleFromString(tokens[1], 0);
        netlistBuilder.addNetlistDCVoltage(id, dc, nodeA, nodeB);

        // Sin
        int sinStartIndex = line.indexOf("SIN");
        if (sinStartIndex >= 0) {
          String sineDef = line.substring(sinStartIndex + 4, line.indexOf(")")).trim();

          //        SIN(V0 VA FREQ TD THETA PHASE)
          tokens = sineDef.split("\\s+");
          String v0 = SPICEUtils.ifExists(tokens, 0);
          String amplitude = SPICEUtils.ifExists(tokens, 1);
          String freq = SPICEUtils.ifExists(tokens, 2);
          String phase = SPICEUtils.ifExists(tokens, 5);
          drivers.add(new Sine(id, SPICEUtils.doubleFromString(v0, 0), phase, SPICEUtils.doubleFromString(amplitude, 0), freq));
        }

        // Pulse
        int pulseStartIndex = line.indexOf("PULSE");
        if (pulseStartIndex >= 0) {
          String pulseDef = line.substring(pulseStartIndex + 6, line.indexOf(")")).trim();
          //          System.out.println("pulseDef = " + pulseDef);

          // PULSE( {v1} {v2} {tdelay} {trise} {tfall} {width} {period} )
          tokens = pulseDef.split("\\s+");
          String v1AsString = SPICEUtils.ifExists(tokens, 0);
          String v2AsString = SPICEUtils.ifExists(tokens, 1);
          String widthAsString = SPICEUtils.ifExists(tokens, 5);
          String periodAsString = SPICEUtils.ifExists(tokens, 6);

          //          System.out.println("v1AsString = " + v1AsString);
          //          System.out.println("v2AsString = " + v2AsString);
          //          System.out.println("widthAsString = " + widthAsString);
          //          System.out.println("periodAsString = " + periodAsString);

          // conversion from SPICE to JSPICE driver

          double v1 = SPICEUtils.doubleFromString(v1AsString, 0);
          double v2 = SPICEUtils.doubleFromString(v2AsString, 0);
          BigDecimal width = SPICEUtils.bigDecimalFromString(widthAsString, "0");
          BigDecimal period = SPICEUtils.bigDecimalFromString(periodAsString, "0");
          //          System.out.println("period = " + period);

          double dcOffset = (v1 + v2) / 2;
          double amplitude = Math.abs(v1 - v2) / 2;

          BigDecimal frequency = BigDecimal.ONE.divide(period, MathContext.DECIMAL128);
          BigDecimal dutyCycle = v2 > v1 ? width.divide(period, MathContext.DECIMAL128)
              : (period.subtract(width)).divide(period, MathContext.DECIMAL128);
          BigDecimal phase = v2 > v1 ? BigDecimal.ZERO : period.multiply(dutyCycle);
          //          BigDecimal phase = BigDecimal.ZERO;

          drivers.add(new Pulse(id, dcOffset, phase.toString(), amplitude, frequency.toString(), dutyCycle.toString()));
        }

      } else if (line.startsWith(".TRAN") || line.startsWith(".tran")) {

        // .tran
        String[] tokens = line.split("\\s+");
        String stepSize = tokens[1];
        String endTime = tokens[2];
        netlistBuilder.addTransientSimulationConfig(endTime, stepSize, drivers.toArray(new Driver[drivers.size()]));

      } else if (line.startsWith("YMEMRISTOR") || line.startsWith("ymemristor")) {

        // memristor
        String[] tokens = line.split("\\s+");
        String id = tokens[1];
        memristorsMap.put(id, line);

      } else if (line.startsWith(".MODEL") || line.startsWith(".model")) {

        // memristor
        String[] tokens = line.split("\\s+");

        String modelID = tokens[1];
        String modelLine = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
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
      } else if (line.startsWith(".INCLUDE") || line.startsWith(".include")) {
        // valid, but skip as it's handeled above
      } else if (line.startsWith("*")) {
        // valid, but skip as it's handeled above
      } else if (line.startsWith(".PRINT") || line.startsWith(".print")) {
        // valid, but skip as it's handeled above
      } else if (line.startsWith(".END") || line.startsWith(".end")) {
        // valid, but skip as it's handeled above
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
      //        System.out.println("modelLine " + modelLine);

      netlistBuilder.addNetlistMMSSMemristor(memristorID, SPICEUtils.doubleFromString(modelMap.get("Rinit")),
          SPICEUtils.doubleFromString(modelMap.get("Ron")), SPICEUtils.doubleFromString(modelMap.get("Roff")),
          SPICEUtils.doubleFromString(modelMap.get("Tau")), SPICEUtils.doubleFromString(modelMap.get("Von")),
          SPICEUtils.doubleFromString(modelMap.get("Voff")), SPICEUtils.doubleFromString(modelMap.get("Phi"), 1),
          SPICEUtils.doubleFromString(modelMap.get("Sfa"), 0), SPICEUtils.doubleFromString(modelMap.get("Sfb"), 0),
          SPICEUtils.doubleFromString(modelMap.get("Sra"), 0), SPICEUtils.doubleFromString(modelMap.get("Srb"), 0), nodeA, nodeB);
    }

    return netlistBuilder.build();
  }

  public static List<String> getPreProcessedLines(String fileName, ConfigurationSourceProvider configurationSourceProvider) throws IOException {

    //    System.out.println("fileName = " + fileName);

    List<String> netlistLines = new ArrayList<>();

    // create netlist from traditional SPICE netlist file.
    try (PeekableScanner scanner = new PeekableScanner(configurationSourceProvider.open(fileName))) {

      String multilineModelDef = null;

      while (scanner.hasNext()) {

        //        System.out.println("multilineModelDef = " + multilineModelDef);

        String nextLine = scanner.nextLine().trim();
        //        System.out.println("nextLine = " + nextLine);

        if (nextLine.startsWith(".STEP") || nextLine.startsWith(".step")) {
          continue;
        }
        if (nextLine.length() < 1) {
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
}
