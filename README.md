# Watcher_RedBlueNerf_ScryMantraBuff

杀戮尖塔 1 观者调整 mod：削弱红蓝姿态循环，增强预见与真言路线。

作者：prttt26。当前版本：0.1.0。源码、资源和设计记录分别位于 src/ 和 docs/；设计记录包含历史状态和待办，实际行为以源码为准。

## 构建

需要 JDK 8、Maven 3.9，以及本机安装的 Slay the Spire、ModTheSpire、BaseMod 和 StSLib。
游戏及依赖 JAR 不随仓库分发。

在项目根目录执行（按本机 Steam 路径调整参数）：

~~~powershell
mvn package "-Dsts.home=C:/Program Files (x86)/Steam/steamapps/common/SlayTheSpire" "-Dworkshop=C:/Program Files (x86)/Steam/steamapps/workshop/content/646570"
~~~

pom.xml 默认使用工坊目录 1605060445（ModTheSpire）、1605833019（BaseMod）、1609158507（StSLib）。
也可通过 -Dmts.dir、-Dbasemod.dir、-Dstslib.dir 单独指定依赖所在目录。

构建生成 target/Watcher_RedBlueNerf_ScryMantraBuff.jar，并自动复制到游戏的 mods/ 目录，因此构建时需要该目录的写入权限。
通过 ModTheSpire 启用本 mod、BaseMod 和 StSLib 后启动游戏。

## 相关项目

另一个独立 mod：[CharacterRelicParity_RareInPrism](https://github.com/prttt26/CharacterRelicParity_RareInPrism)。两个 mod 可同时加载；观者 mod 检测到遗物 mod 时会让出光辉补丁，避免重复修改。