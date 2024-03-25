file://<WORKSPACE>/src/algorithms/Mule.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
uri: file://<WORKSPACE>/src/algorithms/Mule.java
text:
```scala
/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/Mule.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;
import characteristics.Parameters;
import characteristics.IFrontSensorResult;

public class Mule extends Brain {
  //---PARAMETERS---//
  private static final double HEADINGPRECISION = 0.001;

  //---VARIABLES---//
  private boolean turnLeftTask;
  private double endTaskDirection;

  //---CONSTRUCTORS---//
  public Mule() { super(); }

  //---ABSTRACT-METHODS-IMPLEMENTATION---//
  public void activate() {
    turnLeftTask=false;
    move();
    sendLogMessage("Moving a head. Waza!");
  }
  public void step() {
    if (turnLeftTask) {
      if (isHeading(endTaskDirection)) {
	turnLeftTask=false;
	move();
        sendLogMessage("Moving a head. Waza!");
      } else {
	stepTurn(Parameters.Direction.LEFT);
        sendLogMessage("Iceberg at 12 o'clock. Heading 9!");
      }
      return;
    }
    if (detectFront().getObjectType()==IFrontSensorResult.Types.OpponentMainBot) {
      fire(getHeading());
      return;
    }
    if (!(detectFront().getObjectType()==IFrontSensorResult.Types.WALL || detectFront().getObjectType()==IFrontSensorResult.Types.Wreck)) {
      if (Math.random()<0.98) move(); //And what to do when blind blocked?
      else fire(getHeading());
      sendLogMessage("Moving a head. Waza!");
    } else {
      turnLeftTask=true;
      endTaskDirection=getHeading()+Parameters.LEFTTURNFULLANGLE;
      stepTurn(Parameters.Direction.LEFT);
      sendLogMessage("Iceberg at 12 o'clock. Heading 9!");
    }
  }
  private boolean isHeading(double dir){
    return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
  }
}

```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:933)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:168)
	scala.meta.internal.pc.MetalsDriver.run(MetalsDriver.scala:45)
	scala.meta.internal.pc.PcCollector.<init>(PcCollector.scala:44)
	scala.meta.internal.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:61)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:61)
	scala.meta.internal.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:61)
	scala.meta.internal.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:90)
	scala.meta.internal.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:109)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator