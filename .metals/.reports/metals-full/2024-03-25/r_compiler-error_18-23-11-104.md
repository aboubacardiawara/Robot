file://<WORKSPACE>/src/algorithms/CampBot.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
offset: 1235
uri: file://<WORKSPACE>/src/algorithms/CampBot.java
text:
```scala
/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/CampBot.java 2014-11-04 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;
import characteristics.IFrontSensorResult;
import characteristics.Parameters;

public class CampBot extends Brain {
  private boolean turnTask,turnRight,finished,taskOne;
  private double endTaskDirection;
  private int endTaskCounter;
  private static IFrontSensorResult.Types WALL=IFrontSensorResult.Types.WALL;
  private static IFrontSensorResult.Types TEAMMAIN=IFrontSensorResult.Types.TeamMainBot;

  public CampBot() { super(); }

  public void activate() {
    turnTask=true;
    finished=false;
    taskOne=true;
    endTaskDirection=getHeading()+0.4*Math.PI;
    stepTurn(Parameters.Direction.RIGHT);
    sendLogMessage("Moving and healthy.");
  }
  public void step() {
    if (getHealth()<=0) { sendLogMessage("I'm dead.");return; }
    if (finished) { sendLogMessage("Camping point. Task complete.");return; }
    if (turnTask) {
      if (isHeadin@@g(endTaskDirection)) {
	turnTask=false;
        if (taskOne) endTaskCounter=200; else endTaskCounter=100;
	move();
      } else {
        stepTurn(Parameters.Direction.RIGHT);
      }
      return;
    }
    if (endTaskCounter>0) {
      endTaskCounter--;
      move();
      return;
    } else {
      taskOne=false;
      finished=(detectFront().getObjectType()==WALL||detectFront().getObjectType()==TEAMMAIN);
      if (finished) return;
      turnTask=true;
      endTaskDirection=getHeading()+0.5*Math.PI;
      stepTurn(Parameters.Direction.RIGHT);
      return;
    }
  }
  private boolean isHeading(double dir){
    return Math.abs(Math.sin(getHeading()-dir))<Parameters.teamBSecondaryBotStepTurnAngle;
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
	scala.meta.internal.pc.HoverProvider$.hover(HoverProvider.scala:34)
	scala.meta.internal.pc.ScalaPresentationCompiler.hover$$anonfun$1(ScalaPresentationCompiler.scala:352)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator