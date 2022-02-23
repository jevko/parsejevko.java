import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class JevkoParser {
  public class Jevko {
    List<Subjevko> subjevkos;
    String suffix;

    Jevko() {
      this.subjevkos = new ArrayList<>();
      this.suffix = "";
    }

    @Override
    public String toString() {
      String ret = "";

      for (var i = 0; i < this.subjevkos.size(); ++i) {
        ret += this.subjevkos.get(i);
      }
      return ret + escape(this.suffix);
    }
  }
  
  public class Subjevko {
    String prefix;
    Jevko jevko;
  
    Subjevko(String prefix, Jevko jevko) {
      this.prefix = prefix;
      this.jevko = jevko;
    }

    @Override
    public String toString() {
      return escape(this.prefix) + opener + this.jevko + closer;
    }
  }

  static final char opener = '[';
  static final char closer = ']';
  static final char escaper = '`';

  public static String escape(String str) {
    String ret = "";
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (c == opener || c == closer || c == escaper) {
        ret += escaper;
      }
      ret += c;
    }
    return ret;
  }

  public Jevko parse(String str) throws Exception {
    Stack<Jevko> ancestors = new Stack<Jevko>();

    Jevko parent = new Jevko();
    String text = "";
    boolean isEscaped = false;
  
    var line = 1;
    var column = 1;

    for (int i = 0; i < str.length(); ++i) {
      char chr = str.charAt(i);
      if (isEscaped) {
        if (chr == escaper || chr == opener || chr == closer) {
          text += chr;
          isEscaped = false;
        } else {
          throw new Exception(String.format(
            "Invalid digraph (%c%c) at %d:%d!\n", escaper, chr, line, column
          ));
        }
      } else if (chr == escaper) {
        isEscaped = true;
      } else if (chr == opener) {
        Jevko jevko = new Jevko();
        Subjevko sub = new Subjevko(text, jevko);
        parent.subjevkos.add(sub);
        ancestors.add(parent);
        parent = jevko;
        text = "";
      } else if (chr == closer) {
        parent.suffix = text;
        text = "";
        if (ancestors.size() < 1) {
          throw new Exception(String.format(
            "Unexpected closer (%c) at %d:%d!\n", closer, line, column
          ));
        }
        parent = ancestors.pop();
      } else {
        text += chr;
      }
  
      if (chr == '\n') {
        line += 1;
        column = 1;
      } else {
        column += 1;
      }
    }
    if (isEscaped) {
      throw new Exception(String.format(
        "Unexpected end after escaper (%c)!\n", escaper
      ));
    }
    if (ancestors.size() > 0) {
      throw new Exception(String.format(
        "Unexpected end: missing %d closer(s) (%c)!\n", 
        ancestors.size(), 
        closer
      ));
    }
    parent.suffix = text;
    return parent;
  }
}
