package org.knowm.jspice.netlist.spice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SPICESubckt {

  private String id;
  private List<String> nodes = new ArrayList<>();
  private List<String> lines = new ArrayList<>();

  public void setId(String id) {
    this.id = id;
  }

  public void addNode(String node) {
    this.nodes.add(node);
  }

  public void addLine(String line) {
    this.lines.add(line);
  }

  public String getId() {
    return id;
  }

  public List<String> getNodes() {
    return nodes;
  }

  public List<String> getLines() {
    return lines;
  }

  @Override
  public String toString() {
    return "SPICESubckt{" + "id='" + id + '\'' + ", nodes=" + Arrays.toString(nodes.toArray()) + ", lines=" + Arrays.toString(lines.toArray()) + '}';
  }

}