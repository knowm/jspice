package org.knowm.jspice.netlist.spice;

public class SPICEUtils {



  private SPICEUtils(){

  }

   static String ifExists(String[] array, int index) {

    if (array.length > index) {
      return array[index];
    } else {
      return null;
    }

  }

   static double fromString(String value) {

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

   static double fromString(String value, double defaultValue) {

    if (value == null) {
      return defaultValue;
    }

    return fromString(value);

  }


}
