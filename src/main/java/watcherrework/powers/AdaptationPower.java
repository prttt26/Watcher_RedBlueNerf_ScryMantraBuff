package watcherrework.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import watcherrework.WatcherRework;

/**
 * 顺应变化 power：每当进入一个姿态时抽 1 张牌。
 * **只对「进入具体姿态」生效**（姿态真变了且进入的不是中性）——化体/化智为空、神格结束这类
 * 「退出到中性」不触发。模板为 MentalFortressPower（切换姿态得格挡），改成排除进入 Neutral。
 * 图标暂借 mantra region 占位（真图标后换），避免 flash 访问 region48 NPE。
 */
public class AdaptationPower extends AbstractPower {

    public static final String POWER_ID = WatcherRework.MODID + ":Adaptation";

    public AdaptationPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = AbstractPower.PowerType.BUFF;
        this.name = (Settings.language == Settings.GameLanguage.ZHS) ? "顺应变化" : "Adaptation";
        loadRegion("mantra");            // 占位图标
        updateDescription();
    }

    @Override
    public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        if (!oldStance.ID.equals(newStance.ID)
                && !NeutralStance.STANCE_ID.equals(newStance.ID)) {   // 进入了具体姿态（非中性）
            flash();
            addToBot(new DrawCardAction(this.owner, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = (Settings.language == Settings.GameLanguage.ZHS)
                ? "每当你进入一个姿态时，抽 1 张牌。（进入中性不触发）"
                : "Whenever you enter a Stance, draw 1 card. (Entering Neutral does not count.)";
    }
}
