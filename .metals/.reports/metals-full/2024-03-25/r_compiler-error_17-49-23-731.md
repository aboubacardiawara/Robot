file://<WORKSPACE>/src/characteristics/Parameters.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
offset: 6371
uri: file://<WORKSPACE>/src/characteristics/Parameters.java
text:
```scala

/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: characteristics/Parameters.java 2014-10-19 buixuan.
 * ******************************************************/
package characteristics;

public class Parameters {
        // ---------------------------//
        // ---HARD-CODED-PARAMETERS---//
        // ---------------------------//
        public static enum Direction {
                LEFT, RIGHT
        };

        public static final double EAST = 0, // clockwise trigonometric unit, according to screen pixel coordinate
                                             // reference
                        WEST = Math.PI,
                        SOUTH = 0.5 * Math.PI,
                        NORTH = -0.5 * Math.PI,
                        RIGHTTURNFULLANGLE = 0.5 * Math.PI, // value set according to screen pixel coordinate reference
                        LEFTTURNFULLANGLE = -0.5 * Math.PI;

        // -----------------------//
        // ---TEAM-A-PARAMETERS---//
        // -----------------------//
        public static final String teamAName = "KD Runners";
        public static final String teamAMainBotBrainClassName = "algorithms.hunters.HunterMain";; // class given by name;
                                                                                       // is
        // supposed to extends
        // robotsimulator.Brain
        public static final String teamAMainBotAvatar = "avatars/cyclope.png"; // path relative to location of ant
                                                                               // build.xml file
        public static final double teamAMainBotRadius = 50, // 1 unit = 1mm, body radius
                        teamAMainBotFrontalDetectionRange = 300, // 1 unit = 1mm, range of frontal sensor
                        teamAMainBotFrontalDetectionAngle = 0, // UNUSED AT THE MOMENT, frontal sensor detection angle
                                                               // is suppoed to be absolute
                        teamAMainBotSpeed = 1, // 1 unit = 1mm, distance performed at step movement
                        teamAMainBotStepTurnAngle = 0.01 * Math.PI, // trigonometric unit, angle performed at step turn
                                                                    // action
                        teamAMainBotHealth = 300, // FICTIONAL SIMOVIES
                        teamAMainBot1InitX = 200, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot1InitY = 800, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot1InitHeading = EAST, // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference
                        teamAMainBot2InitX = 200, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot2InitY = 1000, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot2InitHeading = EAST, // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference
                        teamAMainBot3InitX = 200, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot3InitY = 1200, // 1 unit = 1mm, coordinate of central point
                        teamAMainBot3InitHeading = EAST; // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference

        public static final String teamASecondaryBotBrainClassName = "algorithms.hunters.HunterSecondary"; // clas
                                                                                            // given by
                                                                                            // name; is
        // supposed to extends
        // robotsimulator.Brain
        public static final String teamASecondaryBotAvatar = "avatars/clumpsy.png"; // path relative to location of ant
                                                                                    // build.xml file
        public static final double teamASecondaryBotRadius = 50, // 1 unit = 1mm, body radius
                        teamASecondaryBotFrontalDetectionRange = 500, // 1 unit = 1mm, range of frontal sensor
                        teamASecondaryBotFrontalDetectionAngle = 0, // UNUSED AT THE MOMENT, frontal sensor detection
                                                                    // angle is suppoed to be absolute
                        teamASecondaryBotSpeed = 3, // 1 unit = 1mm, distance performed at step movement
                        teamASecondaryBotStepTurnAngle = 0.01 * Math.PI, // trigonometric unit, angle performed at step
                                                                         // turn action
                        teamASecondaryBotHealth = 100, // FICTIONAL SIMOVIES
                        teamASecondaryBot1InitX = 500, // 1 unit = 1mm, coordinate of central point
                        teamASecondaryBot1InitY = 800, // 1 unit = 1mm, coordinate of central point
                        teamASecondaryBot1InitHeading = EAST, // clockwise trigonometric unit, according to screen pixel
                                                              // coordinate reference
                        teamASecondaryBot2InitX = 500, // 1 unit = 1mm, coordinate of central point
                        teamASecondaryBot2InitY = 1200, // 1 unit = 1mm, coordinate of central point
                        teamASecondaryBot2InitHeading = EAST; // clockwise trigonometric unit, according to screen pixel
                                                              // coordinate reference

        // -----------------------//
        // ---TEAM-B-PARAMETERS---//
        // -----------------------//
        public static final String teamBName = "Fantom Danger";
        public static final String teamBMainBotBrainClassName = "algorithms.CampFire"; // class given by name;
                                                                                 @@                // is
        // supposed to extends
        // robotsimulator.Brain
        public static final String teamBMainBotAvatar = "avatars/hollowee.png"; // path relative to location of ant
                                                                                // build.xml file
        public static final double teamBMainBotRadius = 50, // 1 unit = 1mm, body radius
                        teamBMainBotFrontalDetectionRange = 300, // 1 unit = 1mm, range of frontal sensor
                        teamBMainBotFrontalDetectionAngle = 0, // UNUSED AT THE MOMENT, frontal sensor detection angle
                                                               // is suppoed to be absolute
                        teamBMainBotSpeed = 1, // 1 unit = 1mm, distance performed at step movement
                        teamBMainBotStepTurnAngle = 0.01 * Math.PI, // trigonometric unit, angle performed at step turn
                                                                    // action
                        teamBMainBotHealth = 300, // FICTIONAL SIMOVIES
                        teamBMainBot1InitX = 2800, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot1InitY = 800, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot1InitHeading = WEST, // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference
                        teamBMainBot2InitX = 2800, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot2InitY = 1000, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot2InitHeading = WEST, // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference
                        teamBMainBot3InitX = 2800, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot3InitY = 1200, // 1 unit = 1mm, coordinate of central point
                        teamBMainBot3InitHeading = WEST; // clockwise trigonometric unit, according to screen pixel
                                                         // coordinate reference

        public static final String teamBSecondaryBotBrainClassName = "algorithms.HighwayFugitive"; // class
                                                                                                           // given by
                                                                                                           // name; is
        // supposed to extends
        // robotsimulator.Brain
        public static final String teamBSecondaryBotAvatar = "avatars/fannyExplorer.png"; // path relative to location
                                                                                          // of ant build.xml file
        public static final double teamBSecondaryBotRadius = 50, // 1 unit = 1mm, body radius
                        teamBSecondaryBotFrontalDetectionRange = 500, // 1 unit = 1mm, range of frontal sensor
                        teamBSecondaryBotFrontalDetectionAngle = 0, // UNUSED AT THE MOMENT, frontal sensor detection
                                                                    // angle is suppoed to be absolute
                        teamBSecondaryBotSpeed = 3, // 1 unit = 1mm, distance performed at step movement
                        teamBSecondaryBotStepTurnAngle = 0.01 * Math.PI, // trigonometric unit, angle performed at step
                                                                         // turn action
                        teamBSecondaryBotHealth = 100, // FICTIONAL SIMOVIES
                        teamBSecondaryBot1InitX = 2500, // 1 unit = 1mm, coordinate of central point
                        teamBSecondaryBot1InitY = 800, // 1 unit = 1mm, coordinate of central point
                        teamBSecondaryBot1InitHeading = WEST, // clockwise trigonometric unit, according to screen pixel
                                                              // coordinate reference
                        teamBSecondaryBot2InitX = 2500, // 1 unit = 1mm, coordinate of central point
                        teamBSecondaryBot2InitY = 1200, // 1 unit = 1mm, coordinate of central point
                        teamBSecondaryBot2InitHeading = WEST; // clockwise trigonometric unit, according to screen pixel
                                                              // coordinate reference

        // -----------------------//
        // ---BULLET-PARAMETERS---//
        // -----------------------//
        public static final double bulletVelocity = 10,
                        bulletDamage = 10,
                        bulletRadius = 5,
                        bulletRange = 1000;
        public static final int bulletFiringLatency = 20;
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