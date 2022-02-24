package org.jevko.java;

public class Subjevko {
  String prefix;
  Jevko jevko;

  Subjevko(String prefix, Jevko jevko) {
    this.prefix = prefix;
    this.jevko = jevko;
  }

  @Override
  public String toString() {
    return Jevko.escape(this.prefix) + Jevko.opener + this.jevko + Jevko.closer;
  }
}