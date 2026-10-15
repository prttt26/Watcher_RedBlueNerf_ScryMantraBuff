package watcherrework.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.cards.purple.Weave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/**
 * 目标 #3 · 增预见 · 迂回 Weave：**升级不加伤**（保持 4 伤），改为额外「抽 1 张 + 放回 1 张到抽牌堆顶」，
 * 把预见切实变成「抽到堆顶卡」。`triggerOnScry`（预见时从弃牌堆回手）保留不动。
 * 抽 1 放 1 照搬战吼 Warcry：`DrawCardAction(1)` + `PutOnDeckAction(p,p,1,false)`。
 */
public class WeavePatch {

    @SpirePatch(clz = Weave.class, method = "upgrade")
    public static class Upgrade {
        @SpirePostfixPatch
        public static void postfix(Weave c) {
            c.baseDamage = 4;                       // 覆盖原 upgradeDamage(2) 的 6 → 升级不加伤
            c.upgradedDamage = false;
            c.rawDescription = CardCrawlGame.languagePack.getCardStrings("Weave").DESCRIPTION
                    + (Settings.language == Settings.GameLanguage.ZHS
                        ? " 抽 1 张牌，将 1 张手牌置于抽牌堆顶。"
                        : " Draw 1 card. Put a card from your hand on top of your draw pile.");
            c.initializeDescription();
        }
    }

    @SpirePatch(clz = Weave.class, method = "use")
    public static class Use {
        @SpirePostfixPatch
        public static void postfix(Weave c, AbstractPlayer p, AbstractMonster m) {
            if (c.upgraded) {
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
                AbstractDungeon.actionManager.addToBottom(new PutOnDeckAction(p, p, 1, false));
            }
        }
    }
}
