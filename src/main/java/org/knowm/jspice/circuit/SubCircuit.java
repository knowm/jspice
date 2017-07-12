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
package org.knowm.jspice.circuit;

import org.knowm.jspice.component.Component;
import org.knowm.jspice.netlist.NetList2;

/**
 * @author timmolter
 */
public class SubCircuit {

  private NetList2 subCircuitNetlist = new NetList2();

  public void addNetListComponent(Component component, String[] nodes) {

    subCircuitNetlist.addNetListComponent(component, nodes);
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB) {

    addNetListComponent(component, new String[]{nodeA, nodeB});
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB, String nodeC) {

    addNetListComponent(component, new String[]{nodeA, nodeB, nodeC});
  }

  public void addNetListComponent(Component component, String nodeA, String nodeB, String nodeC, String nodeD) {

    addNetListComponent(component, new String[]{nodeA, nodeB, nodeC, nodeD});
  }

  public NetList2 getNetlist() {

    return subCircuitNetlist;
  }
}
