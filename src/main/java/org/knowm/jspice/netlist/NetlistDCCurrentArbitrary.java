package org.knowm.jspice.netlist;

import org.knowm.jspice.component.source.DCCurrentArbitrary;

public class NetlistDCCurrentArbitrary extends NetlistComponent {

  public NetlistDCCurrentArbitrary(DCCurrentArbitrary dcCurrentArbitrary, String... nodes) {

    super(dcCurrentArbitrary, nodes);
  }

}
