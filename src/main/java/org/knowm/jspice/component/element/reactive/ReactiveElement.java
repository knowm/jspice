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
package org.knowm.jspice.component.element.reactive;

import java.util.Map;

import org.knowm.jspice.component.Component;

/**
 * @author timmolter
 */
public abstract class ReactiveElement extends Component {

  public ReactiveElement(String id) {

    super(id);
  }

  public abstract void stampSolutionVector(double[] RHS, Map<String, Integer> nodeID2ColumnIdxMap, String[] nodes);

  private Double initialCondition = null;

  public Double getInitialCondition() {

    return initialCondition;
  }

  public void setInitialCondition(double initialCondition) {

    this.initialCondition = initialCondition;
  }
}
