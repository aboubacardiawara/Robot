file://<WORKSPACE>/src/algorithms/BootingBerzerk.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
uri: file://<WORKSPACE>/src/algorithms/BootingBerzerk.java
text:
```scala
/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BootingBerzerk.java 2014-11-03 buixuan.
 * ******************************************************/
package algorithms;

import java.util.ArrayList;

import robotsimulator.Brain;
import characteristics.Parameters;
import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;

public class BootingBerzerk extends Brain {
  // ---PARAMETERS---//
  private static final double HEADINGPRECISION = 0.001;

  // ---VARIABLES---//
  private boolean turnTask, turnRight, moveTask, berzerk, back;
  private double endTaskDirection, lastSeenDirection;
  private int endTaskCounter, berzerkInerty;
  private boolean firstMove, berzerkTurning;

  // ---CONSTRUCTORS---//
  public BootingBerzerk() {
    super();
  }

  // ---ABSTRACT-METHODS-IMPLEMENTATION---//
  public void activate() {
    turnTask = true;
    moveTask = false;
    firstMove = true;
    berzerk = false;
    berzerkInerty = 0;
    berzerkTurning = false;
    back = false;
    endTaskDirection = (Math.random() - 0.5) * 0.5 * Math.PI;
    turnRight = (endTaskDirection > 0);
    endTaskDirection += getHeading();
    lastSeenDirection = Math.random() * Math.PI * 2;
    if (turnRight)
      stepTurn(Parameters.Direction.RIGHT);
    else
      stepTurn(Parameters.Direction.LEFT);
    sendLogMessage("Turning point. Waza!");
  }

  public void step() {
    /*
     * if (Math.random()<0.01 && !berzerk) {
     * fire(Math.random()*Math.PI*2);
     * return;
     * }
     */
    ArrayList<IRadarResult> radarResults = detectRadar();
    if (berzerk) {
      if (berzerkTurning) {
        endTaskCounter--;
        if (isHeading(endTaskDirection)) {
          berzerkTurning = false;
          move();
          sendLogMessage("Moving a head. Waza!");
        } else {
          if (turnRight)
            stepTurn(Parameters.Direction.RIGHT);
          else
            stepTurn(Parameters.Direction.LEFT);
        }
        return;
      }
      
       * if (endTaskCounter<0) {
       * turnTask=true;
       * moveTask=false;
       * berzerk=false;
       * endTaskDirection=(Math.random()-0.5)*2*Math.PI;
       * turnRight=(endTaskDirection>0);
       * endTaskDirection+=getHeading();
       * if (turnRight) stepTurn(Parameters.Direction.RIGHT);
       * else stepTurn(Parameters.Direction.LEFT);
       * sendLogMessage("Turning point. Waza!");
       * } else {
       * endTaskCounter--;
       * if (Math.random()<0.1) {
       * for (IRadarResult r : radarResults) {
       * if (r.getObjectType()==IRadarResult.Types.OpponentMainBot) {
       * fire(r.getObjectDirection());
       * lastSeenDirection=r.getObjectDirection();
       * return;
       * }
       * }
       * fire(lastSeenDirection);
       * return;
       * } else {
       * if (back) moveBack(); else move();
       * }
       * }
       */
      if (berzerkInerty > 50) {
        turnTask = true;
        moveTask = false;
        berzerk = false;
        endTaskDirection = (Math.random() - 0.5) * 2 * Math.PI;
        turnRight = (endTaskDirection > 0);
        endTaskDirection += getHeading();
        if (turnRight)
          stepTurn(Parameters.Direction.RIGHT);
        else
          stepTurn(Parameters.Direction.LEFT);
        sendLogMessage("Turning point. Waza!");
        return;
      }
      if (endTaskCounter < 0) {
        for (IRadarResult r : radarResults) {
          if (r.getObjectType() == IRadarResult.Types.OpponentMainBot) {
            fire(r.getObjectDirection());
            lastSeenDirection = r.getObjectDirection();
            berzerkInerty = 0;
            return;
          }
        }
        fire(lastSeenDirection);
        berzerkInerty++;
        endTaskCounter = 21;
        return;
      } else {
        endTaskCounter--;
        for (IRadarResult r : radarResults) {
          if (r.getObjectType() == IRadarResult.Types.OpponentMainBot) {
            lastSeenDirection = r.getObjectDirection();
            berzerkInerty = 0;
            move();
            return;
          }
        }
        berzerkInerty++;
        move();
      }
      return;
    }
    if (radarResults.size() != 0) {
      for (IRadarResult r : radarResults) {
        if (r.getObjectType() == IRadarResult.Types.OpponentMainBot) {
          berzerk = true;
          back = (Math.cos(getHeading() - r.getObjectDirection()) > 0);
          endTaskCounter = 21;
          fire(r.getObjectDirection());
          lastSeenDirection = r.getObjectDirection();
          berzerkTurning = true;
          endTaskDirection = lastSeenDirection;
          double ref = endTaskDirection - getHeading();
          if (ref < 0)
            ref += Math.PI * 2;
          turnRight = (ref > 0 && ref < Math.PI);
          return;
        }
      }
      for (IRadarResult r : radarResults) {
        if (r.getObjectType() == IRadarResult.Types.OpponentSecondaryBot) {
          fire(r.getObjectDirection());
          return;
        }
      }
    }
    if (turnTask) {
      if (isHeading(endTaskDirection)) {
        if (firstMove) {
          firstMove = false;
          turnTask = false;
          moveTask = true;
          endTaskCounter = 400;
          move();
          sendLogMessage("Moving a head. Waza!");
          return;
        }
        turnTask = false;
        moveTask = true;
        endTaskCounter = 100;
        move();
        sendLogMessage("Moving a head. Waza!");
      } else {
        if (turnRight)
          stepTurn(Parameters.Direction.RIGHT);
        else
          stepTurn(Parameters.Direction.LEFT);
      }
      return;
    }
    if (moveTask) {
      /*
       * if (detectFront()!=NOTHING) {
       * turnTask=true;
       * moveTask=false;
       * endTaskDirection=(Math.random()-0.5)*Math.PI;
       * turnRight=(endTaskDirection>0);
       * endTaskDirection+=getHeading();
       * if (turnRight) stepTurn(Parameters.Direction.RIGHT);
       * else stepTurn(Parameters.Direction.LEFT);
       * sendLogMessage("Turning point. Waza!");
       * }
       */
      if (endTaskCounter < 0) {
        turnTask = true;
        moveTask = false;
        endTaskDirection = (Math.random() - 0.5) * 2 * Math.PI;
        turnRight = (endTaskDirection > 0);
        endTaskDirection += getHeading();
        if (turnRight)
          stepTurn(Parameters.Direction.RIGHT);
        else
          stepTurn(Parameters.Direction.LEFT);
        sendLogMessage("Turning point. Waza!");
      } else {
        endTaskCounter--;
        move();
      }
      return;
    }
    return;
  }

  private boolean isHeading(double dir) {
    return Math.abs(Math.sin(getHeading() - dir)) < Parameters.teamAMainBotStepTurnAngle;
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