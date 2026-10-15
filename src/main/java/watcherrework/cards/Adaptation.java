package watcherrework.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.CalmStance;
import watcherrework.WatcherRework;
import watcherrework.powers.AdaptationPower;

/**
 * 顺应变化（新增稀有能力，替换被删的猛虎下山 Rushdown/cardID=Adaptation）。
 * 2(1) 费；施加「进入姿态抽 1」power；打出时退出当前姿态、进入平静（因此打出即触发一次抽 1）。
 * 削红蓝：把「进入愤怒抽 2」的强引擎换成「进入任意姿态抽 1、且只算进入」的温和版。
 * 详见 目标#3 制作计划。
 */
public class Adaptation extends CustomCard {

    public static final String ID = WatcherRework.MODID + ":Adaptation";
    private static final String IMG = WatcherRework.MODID + "/images/cards/adaptation.png";
    private static final CardStrings CS = CardCrawlGame.languagePack.getCardStrings(ID);

    public Adaptation() {
        super(ID, CS.NAME, IMG, 2, CS.DESCRIPTION,
                CardType.POWER, CardColor.PURPLE, CardRarity.RARE, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 先挂 power，再进平静 → 进平静触发一次「进入姿态抽 1」
        addToBot(new ApplyPowerAction(p, p, new AdaptationPower(p)));
        addToBot(new ChangeStanceAction(CalmStance.STANCE_ID));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);          // 2 → 1 费
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Adaptation();
    }
}
