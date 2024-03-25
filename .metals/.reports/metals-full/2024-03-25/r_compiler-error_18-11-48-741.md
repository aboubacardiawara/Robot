file://<WORKSPACE>/src/algorithms/hunters/brains/core/MainBotBaseBrain.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
offset: 557
uri: file://<WORKSPACE>/src/algorithms/hunters/brains/core/MainBotBaseBrain.java
text:
```scala
package algorithms.hunters.brains.core;

import java.util.ArrayList;
import java.util.stream.Collectors;

import characteristics.IRadarResult;
import characteristics.Parameters;

public abstract class MainBotBaseBrain extends BaseBrain {

    @Override
    public void move() {
        super.move();
        robotX += Parameters.teamAMainBotSpeed * Math.cos(getHeading());
        robotY += Parameters.teamAMainBotSpeed * Math.sin(getHeading());
    }

    @Override
    protected Robots identifyRobot() {
        ArrayList<IRadarResult> mainRobotsAround = @@detectRadar();

        if (thereIsRobotUpAndDown(mainRobotsAround)) {
            return Robots.MRMIDDLE;
        } else if (thereIsRobotUp(mainRobotsAround)) {
            return Robots.MRBOTTOM;
        }
        return Robots.MRUP;

    }

    private boolean thereIsRobotUp(ArrayList<IRadarResult> mainRobotsAround) {
        return mainRobotsAround
                .stream()
                .filter(radarResult -> radarResult.getObjectType() == IRadarResult.Types.TeamMainBot)
                .filter(radarResult -> radarResult.getObjectDirection() == Parameters.NORTH)
                .collect(Collectors.toCollection(ArrayList::new))
                .size() == 1;
    }

    private boolean thereIsRobotUpAndDown(ArrayList<IRadarResult> mainRobotsAround) {

        return mainRobotsAround
                .stream()
                .filter(radarResult -> radarResult.getObjectType() == IRadarResult.Types.TeamMainBot)
                .filter(radarResult -> radarResult.getObjectDirection() == Parameters.NORTH
 radarResult.getObjectDirection() == Parameters.SOUTH)
                .collect(Collectors.toCollection(ArrayList::new))
                .size() == 2;
    }

    @Override
    protected double initialX() {
        if (leftSide) {
            if (currentRobot == Robots.MRUP) {
                return Parameters.teamAMainBot1InitX;
            } else if (currentRobot == Robots.MRMIDDLE) {
                return Parameters.teamAMainBot2InitX;
            } else {
                return Parameters.teamAMainBot3InitX;
            }
        } else {
            if (currentRobot == Robots.MRUP) {
                return Parameters.teamBMainBot1InitX;
            } else if (currentRobot == Robots.MRMIDDLE) {
                return Parameters.teamBMainBot2InitX;
            } else {
                return Parameters.teamBMainBot3InitX;
            }
        }
    }

    @Override
    protected double initialY() {
        if (leftSide) {
            if (currentRobot == Robots.MRUP) {
                return Parameters.teamAMainBot1InitY;
            } else if (currentRobot == Robots.MRMIDDLE) {
                return Parameters.teamAMainBot2InitY;
            } else {
                return Parameters.teamAMainBot3InitY;
            }
        } else {
            if (currentRobot == Robots.MRUP) {
                return Parameters.teamBMainBot1InitY;
            } else if (currentRobot == Robots.MRMIDDLE) {
                return Parameters.teamBMainBot2InitY;
            } else {
                return Parameters.teamBMainBot3InitY;
            }
        }
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