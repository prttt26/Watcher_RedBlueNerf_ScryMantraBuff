package watcherrework.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.purple.Tantrum;

/**
 * 目标 #3 · 削红蓝 · 发泄 Tantrum：**只提费 1(0)→2(1)，其余不动**——
 * 保留 3 伤 3 次 + 进入愤怒 + 洗回牌库（`shuffleBackIntoDrawPile`）。
 * 升级才平费、成为愤怒无限引擎（和初始卡暴怒一致），洗回保住了无限效率。
 * （早先误去掉「进入愤怒」，已按作者修正改回。）
 */
public class TantrumPatch {

    @SpirePatch(clz = Tantrum.class, method = SpirePatch.CONSTRUCTOR)
    public static class Ctor {
        @SpirePostfixPatch
        public static void postfix(Tantrum c) {
            c.cost = c.costForTurn = 2;
        }
    }

    @SpirePatch(clz = Tantrum.class, method = "upgrade")
    public static class Upgrade {
        @SpirePostfixPatch
        public static void postfix(Tantrum c) {
            c.cost = c.costForTurn = 1;
            c.upgradedCost = true;
        }
    }
}
