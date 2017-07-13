package org.knowm.jspice.netlist;

import org.knowm.jspice.component.source.DCVoltageArbitrary;

public class NetlistDCVoltageArbitrary extends NetlistComponent {

  public NetlistDCVoltageArbitrary(DCVoltageArbitrary dcVoltageArbitrary, String... nodes) {

    super(dcVoltageArbitrary, nodes);
  }

}
