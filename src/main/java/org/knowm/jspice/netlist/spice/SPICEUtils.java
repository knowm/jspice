package org.knowm.jspice.netlist.spice;

import java.math.BigDecimal;

public class SPICEUtils {

  private SPICEUtils() {

  }

  static String ifExists(String[] array, int index) {

    if (array.length > index) {
      return array[index];
    } else {
      return null;
    }
  }

  static double doubleFromString(String value) {

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
      value = value.replace("f", "");
    } else if (value.endsWith("F")) {
      value = value.replace("F", "");
    }else if (value.endsWith("v")) {
      value = value.replace("v", "");
    } else if (value.endsWith("V")) {
      value = value.replace("V", "");
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
      return Double.parseDouble(value.trim().substring(0, value.length() - 3)) * 1_000_000;
    } else if (value.endsWith("G") || value.endsWith("g")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000_000_000;
    } else if (value.endsWith("T") || value.endsWith("t")) {
      return Double.parseDouble(value.trim().substring(0, value.length() - 1)) * 1_000_000_000_000L;
    } else {
      return Double.parseDouble(value.trim());
    }
  }

  static double doubleFromString(String value, double defaultValue) {

    if (value == null) {
      return defaultValue;
    }

    return doubleFromString(value);
  }

  static String doubleToString(double doubleValue) {

      return String.valueOf(doubleValue);
     
  }

  public static BigDecimal bigDecimalFromString(String value) {

    // take care of units

    // remove electrical unit
    if (value.endsWith("ohm")) {
      value = value.replace("ohm", "");
    } else if (value.endsWith("OHM")) {
      value = value.replace("OHM", "");
    } else if (value.endsWith("Hz")) {
      value = value.replace("Hz", "");
    } else if (value.endsWith("HZ")) {
      value = value.replace("HZ", "");
    } else if (value.endsWith("hz")) {
      value = value.replace("hz", "");
    } else if (value.endsWith("s")) {
      value = value.replace("s", "");
    } else if (value.endsWith("S")) {
      value = value.replace("S", "");
    } else if (value.endsWith("f")) {
      value = value.replace("f", "");
    } else if (value.endsWith("F")) {
      value = value.replace("F", "");
    } else if (value.endsWith("v")) {
      value = value.replace("v", "");
    } else if (value.endsWith("V")) {
      value = value.replace("V", "");
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
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).divide(new BigDecimal("1000000000000000"));
    } else if (value.endsWith("P") || value.endsWith("p")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).divide(new BigDecimal("1000000000000"));
    } else if (value.endsWith("N") || value.endsWith("n")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).divide(new BigDecimal("1000000000"));
    } else if (value.endsWith("U") || value.endsWith("u")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).divide(new BigDecimal("1000000"));
    } else if (value.endsWith("M") || value.endsWith("m")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).divide(new BigDecimal("1000"));
    } else if (value.endsWith("K") || value.endsWith("k")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).multiply(new BigDecimal("1000"));
    } else if (value.endsWith("MEG") || value.endsWith("meg")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 3)).multiply(new BigDecimal("1000000"));
    } else if (value.endsWith("G") || value.endsWith("g")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).multiply(new BigDecimal("1000000000"));
    } else if (value.endsWith("T") || value.endsWith("t")) {
      return new BigDecimal(value.trim().substring(0, value.length() - 1)).multiply(new BigDecimal("1000000000000"));
    } else {
      return new BigDecimal(value.replaceAll("_", "").trim());
    }
  }

  public static BigDecimal bigDecimalFromString(String value, String defaultValue) {

    if (value == null) {
      return new BigDecimal(defaultValue);
    }

    return bigDecimalFromString(value);
  }
}
