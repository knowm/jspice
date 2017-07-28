package org.knowm.jspice.netlist.spice;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Specialized class allowing peeking for a Scanner
 */
 class PeekableScanner implements Closeable {

  private Scanner scanner;
  private String nextLine;

  public PeekableScanner(InputStream source) {

//    System.out.println("source = " + source);
    scanner = new Scanner(source);
    nextLine = (scanner.hasNext() ? scanner.nextLine().trim() : null);
  }

  public boolean hasNext() {
    return (nextLine != null);
  }

  public String nextLine() {
    String current = nextLine;
    nextLine = (scanner.hasNext() ? scanner.nextLine().trim() : null);
    return current;
  }

  public String peek() {
    return nextLine;
  }

  @Override
  public void close() throws IOException {
    scanner.close();
  }
}
