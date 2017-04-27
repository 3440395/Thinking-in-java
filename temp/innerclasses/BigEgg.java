//: innerclasses/BigEgg.java
package com.xk.sample.innerclasses; /* Added by Eclipse.py */
// An inner class cannot be overriden like a method.
import static com.xk.sample.net.mindview.util.Print.print;

class Egg {
  private Yolk y;
  protected class Yolk {
    public Yolk() { print("Egg.Yolk()"); }
  }
  public Egg() {
    print("New Egg()");
    y = new Yolk();
  }
}	

public class BigEgg extends Egg {
  public class Yolk {
    public Yolk() { print("BigEgg.Yolk()"); }
  }
  public static void main(String[] args) {
    new BigEgg();
  }
} /* Output:
New Egg()
Egg.Yolk()
*///:~
