package watcherrework.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.Brilliance;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;

/**
 * 目标 #3 · 光辉 Brilliance 重做（和目标 #2 完全相同的效果：获得 3(4) 真言，伤害 = 本场真言总数）。
 * **跨 mod 让位**：若目标 #2 的 mod 也在，这里全部不干预，让目标 #2 的光辉生效——避免叠加。
 * 单装本 mod 时正常施加。见 金卡自举化规格.md §5。
 */
public class BrilliancePatch {

    private static final String PRIORITY_MOD = "CharacterRelicParity_RareInPrism";  // 让位对象（它优先）

    private static String desc(int mantra) {
        if (Settings.language == Settings.GameLanguage.ZHS) {
            return "获得 " + mantra + " 点真言。造成 !D! 点伤害，数值等同于你本场获得过的真言数量。";
        }
        return "Gain " + mantra + " Mantra. Deal !D! damage equal to the Mantra you have gained this combat.";
    }

    @SpirePatch(clz = Brilliance.class, method = SpirePatch.CONSTRUCTOR)
    public static class Ctor {
        @SpirePostfixPatch
        public static void postfix(Brilliance c) {
            if (Loader.isModLoaded(PRIORITY_MOD)) return;      // 让位
            c.baseDamage = 3;
            c.baseMagicNumber = c.magicNumber = 3;
            c.rawDescription = desc(3);
            c.initializeDescription();
        }
    }

    @SpirePatch(clz = Brilliance.class, method = "upgrade")
    public static class Upgrade {
        @SpirePostfixPatch
        public static void postfix(Brilliance c) {
            if (Loader.isModLoaded(PRIORITY_MOD)) return;      // 让位
            c.baseDamage = 4;
            c.upgradedDamage = true;
            c.baseMagicNumber = c.magicNumber = 4;
            c.upgradedMagicNumber = true;
            c.rawDescription = desc(4);
            c.initializeDescription();
        }
    }

    @SpirePatch(clz = Brilliance.class, method = "use")
    public static class Use {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(Brilliance c, AbstractPlayer p, AbstractMonster m) {
            if (Loader.isModLoaded(PRIORITY_MOD)) return SpireReturn.Continue();  // 让位（让目标#2 的 use 生效）
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(p, p, new MantraPower(p, c.magicNumber), c.magicNumber));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                    new DamageInfo(p, c.damage, c.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.FIRE));
            return SpireReturn.Return(null);
        }
    }
}
