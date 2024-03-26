file://<WORKSPACE>/src/supportGUI/FileLoader.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.1/scala3-library_3-3.3.1.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar [exists ]
Options:



action parameters:
uri: file://<WORKSPACE>/src/supportGUI/FileLoader.java
text:
```scala
/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: supportGUI/FileLoader.java 2014-10-28 buixuan.
 * ******************************************************/
package suppor;

import characteristics.IBrain;
import characteristics.Parameters;

public class FileLoader {
  // ---CONSTRUCTORS---//
  public FileLoader() {
  }

  // ---GET/SETTERS---//
  public IBrain getTeamAMainBotBrain() {
    return HardCodedParameters.instantiate(Parameters.teamAMainBotBrainClassName, IBrain.class);
  }

  public IBrain getTeamASecondaryBotBrain() {
    return HardCodedParameters.instantiate(Parameters.teamASecondaryBotBrainClassName, IBrain.class);
  }

  public IBrain getTeamBMainBotBrain() {
    return HardCodedParameters.instantiate(Parameters.teamBMainBotBrainClassName, IBrain.class);
  }

  public IBrain getTeamBSecondaryBotBrain() {
    return HardCodedParameters.instantiate(Parameters.teamBSecondaryBotBrainClassName, IBrain.class);
  }

  public String getTeamAMainBotAvatarFileName() {
    return Parameters.teamAMainBotAvatar;
  }

  public String getTeamASecondaryBotAvatarFileName() {
    return Parameters.teamASecondaryBotAvatar;
  }

  public String getTeamBMainBotAvatarFileName() {
    return Parameters.teamBMainBotAvatar;
  }

  public String getTeamBSecondaryBotAvatarFileName() {
    return Parameters.teamBSecondaryBotAvatar;
  }

  public String getTeamAName() {
    return Parameters.teamAName;
  }

  public String getTeamBName() {
    return Parameters.teamBName;
  }

  public double getTeamAMainBotSpeed() {
    return Parameters.teamAMainBotSpeed;
  }

  public double getTeamASecondaryBotSpeed() {
    return Parameters.teamASecondaryBotSpeed;
  }

  public double getTeamBMainBotSpeed() {
    return Parameters.teamBMainBotSpeed;
  }

  public double getTeamBSecondaryBotSpeed() {
    return Parameters.teamBSecondaryBotSpeed;
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