package org.knowm.jspice.netlist;

import org.knowm.jspice.component.element.memristor.Memristor;

public class NetlistMemristor extends NetlistComponent {

  public NetlistMemristor(Memristor memristor, String... nodes) {

    super(memristor, nodes);
  }

}
