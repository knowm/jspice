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
package org.knowm.jspice.component.source;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.knowm.jspice.simulate.dcoperatingpoint.DCOperatingPointResult;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ArbitraryUtils {

  public static double getArbitraryValue(DCOperatingPointResult dcOperatingPointResult) {

    Map<String, Double> symbol2ValueMap = new HashMap<>();
    String[] symbolicReplacements = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    String jspiceExpression = "I(x)*I(x)".replaceAll("\\s", "");
    String exp4jExpression = jspiceExpression;
    String subFunction;
    int count = 0;
    while ((subFunction =

        getNextFunction(exp4jExpression)) != null)

    {

      exp4jExpression = exp4jExpression.replaceFirst(Pattern.quote(subFunction), symbolicReplacements[count]);
      symbol2ValueMap.put(symbolicReplacements[count], dcOperatingPointResult.getValue(subFunction));
      count++;
    }

    ExpressionBuilder expressionBuilder = new ExpressionBuilder(exp4jExpression);
    for (
        Map.Entry<String, Double> stringDoubleEntry : symbol2ValueMap.entrySet())

    {
      expressionBuilder.variable(stringDoubleEntry.getKey());
    }

    Expression expression = expressionBuilder.build();

    for (
        Map.Entry<String, Double> stringDoubleEntry : symbol2ValueMap.entrySet())

    {
      expression.setVariable(stringDoubleEntry.getKey(), stringDoubleEntry.getValue());
    }

    return expression.evaluate();
  }

  private static String getNextFunction(String remaining) {

    if (remaining.indexOf("(") < 0) {
      return null;
    }
    String next = remaining.substring(remaining.indexOf("(") - 1, remaining.indexOf(")") + 1);
    return next;
  }
}
