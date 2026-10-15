package watcherrework.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.EmptyMind;
import com.megacrit.cardcrawl.cards.purple.MentalFortress;
import com.megacrit.cardcrawl.cards.purple.Sanctity;
import com.megacrit.cardcrawl.cards.purple.Vigilance;
import com.megacrit.cardcrawl.cards.purple.WheelKick;
import com.megacrit.cardcrawl.cards.purple.Worship;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.stances.NeutralStance;

/**
 * 目标 #3 · 增预见 / 增真言 / 削姿态防御 的一批基础卡 patch（一个文件放多张）：
 * - 警惕 Vigilance：8(11) 格挡 + 进平静；**升级才额外预见 3**。
 * - 化智为空 EmptyMind：**重排为「先退出姿态→预见 !M!→抽 !M!」**（兼容灵光一现的预见得真言时机）。
 * - 心灵堡垒 MentalFortress：切换姿态格挡 **4(6)→3(5)**（削姿态防御引擎）。
 * - 敬拜 Worship：原「5 真言、升级保留」+ **3(5) 格挡 + 预见 3(5)**。
 * - 圣洁 Sanctity：纯原效果 + **获得真言 1(2)**。
 * - 回环踢 WheelKick：稀有度降为 COMMON。
 */
public class ScryBuffPatches {

    private static boolean zh() { return Settings.language == Settings.GameLanguage.ZHS; }
    private static CardStrings cs(String id) { return CardCrawlGame.languagePack.getCardStrings(id); }

    // ---------- 警惕 Vigilance：升级才预见 3 ----------
    @SpirePatch(clz = Vigilance.class, method = "upgrade")
    public static class VigilanceUpg {
        @SpirePostfixPatch
        public static void postfix(Vigilance c) {
            c.baseBlock = c.block = 11;             // 覆盖原 upgradeBlock(4) 的 12 → 11
            c.upgradedBlock = true;
            c.rawDescription = cs("Vigilance").DESCRIPTION + (zh() ? " 预见 3。" : " Scry 3.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = Vigilance.class, method = "use")
    public static class VigilanceUse {
        @SpirePostfixPatch
        public static void postfix(Vigilance c, AbstractPlayer p, AbstractMonster m) {
            if (c.upgraded) {
                AbstractDungeon.actionManager.addToBottom(new ScryAction(3));
            }
        }
    }

    // ---------- 化智为空 EmptyMind：重排 先退出→预见→抽 ----------
    @SpirePatch(clz = EmptyMind.class, method = SpirePatch.CONSTRUCTOR)
    public static class EmptyMindCtor {
        @SpirePostfixPatch
        public static void postfix(EmptyMind c) {
            c.rawDescription = cs("EmptyMind").DESCRIPTION + (zh() ? " 预见 !M!。" : " Scry !M!.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = EmptyMind.class, method = "upgrade")
    public static class EmptyMindUpg {
        @SpirePostfixPatch
        public static void postfix(EmptyMind c) {
            CardStrings s = cs("EmptyMind");
            String base = (s.UPGRADE_DESCRIPTION != null && !s.UPGRADE_DESCRIPTION.isEmpty()) ? s.UPGRADE_DESCRIPTION : s.DESCRIPTION;
            c.rawDescription = base + (zh() ? " 预见 !M!。" : " Scry !M!.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = EmptyMind.class, method = "use")
    public static class EmptyMindUse {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(EmptyMind c, AbstractPlayer p, AbstractMonster m) {
            // 顺序：先退出姿态到中性 → 再预见 → 再抽牌
            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction(NeutralStance.STANCE_ID));
            AbstractDungeon.actionManager.addToBottom(new ScryAction(c.magicNumber));
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, c.magicNumber));
            return SpireReturn.Return(null);
        }
    }

    // ---------- 心灵堡垒 MentalFortress：格挡 4(6) → 3(5) ----------
    @SpirePatch(clz = MentalFortress.class, method = SpirePatch.CONSTRUCTOR)
    public static class MentalFortressCtor {
        @SpirePostfixPatch
        public static void postfix(MentalFortress c) {
            c.baseMagicNumber = c.magicNumber = 3;
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = MentalFortress.class, method = "upgrade")
    public static class MentalFortressUpg {
        @SpirePostfixPatch
        public static void postfix(MentalFortress c) {
            c.baseMagicNumber = c.magicNumber = 5;   // 覆盖原 upgradeMagicNumber(2) 的 6
            c.upgradedMagicNumber = true;
            c.initializeDescription();
        }
    }

    // ---------- 敬拜 Worship：+3(5) 格挡 + 预见 3(5) ----------
    @SpirePatch(clz = Worship.class, method = SpirePatch.CONSTRUCTOR)
    public static class WorshipCtor {
        @SpirePostfixPatch
        public static void postfix(Worship c) {
            c.rawDescription = cs("Worship").DESCRIPTION + (zh() ? " 获得 3 点格挡。预见 3。" : " Gain 3 Block. Scry 3.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = Worship.class, method = "upgrade")
    public static class WorshipUpg {
        @SpirePostfixPatch
        public static void postfix(Worship c) {
            CardStrings s = cs("Worship");
            String base = (s.UPGRADE_DESCRIPTION != null && !s.UPGRADE_DESCRIPTION.isEmpty()) ? s.UPGRADE_DESCRIPTION : s.DESCRIPTION;
            c.rawDescription = base + (zh() ? " 获得 5 点格挡。预见 5。" : " Gain 5 Block. Scry 5.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = Worship.class, method = "use")
    public static class WorshipUse {
        @SpirePostfixPatch
        public static void postfix(Worship c, AbstractPlayer p, AbstractMonster m) {
            int n = c.upgraded ? 5 : 3;
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, n));
            AbstractDungeon.actionManager.addToBottom(new ScryAction(n));
        }
    }

    // ---------- 圣洁 Sanctity：+ 获得真言 1(2) ----------
    @SpirePatch(clz = Sanctity.class, method = SpirePatch.CONSTRUCTOR)
    public static class SanctityCtor {
        @SpirePostfixPatch
        public static void postfix(Sanctity c) {
            c.rawDescription = cs("Sanctity").DESCRIPTION + (zh() ? " 获得 1 点真言。" : " Gain 1 Mantra.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = Sanctity.class, method = "upgrade")
    public static class SanctityUpg {
        @SpirePostfixPatch
        public static void postfix(Sanctity c) {
            CardStrings s = cs("Sanctity");
            String base = (s.UPGRADE_DESCRIPTION != null && !s.UPGRADE_DESCRIPTION.isEmpty()) ? s.UPGRADE_DESCRIPTION : s.DESCRIPTION;
            c.rawDescription = base + (zh() ? " 获得 2 点真言。" : " Gain 2 Mantra.");
            c.initializeDescription();
        }
    }
    @SpirePatch(clz = Sanctity.class, method = "use")
    public static class SanctityUse {
        @SpirePostfixPatch
        public static void postfix(Sanctity c, AbstractPlayer p, AbstractMonster m) {
            int mantra = c.upgraded ? 2 : 1;
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new MantraPower(p, mantra), mantra));
        }
    }

    // ---------- 回环踢 WheelKick：降为普通卡 ----------
    @SpirePatch(clz = WheelKick.class, method = SpirePatch.CONSTRUCTOR)
    public static class WheelKickCtor {
        @SpirePostfixPatch
        public static void postfix(WheelKick c) {
            c.rarity = AbstractCard.CardRarity.COMMON;
        }
    }
}
