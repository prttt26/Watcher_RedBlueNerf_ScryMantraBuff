package watcherrework;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import watcherrework.cards.Adaptation;

/**
 * 目标 #3 的独立 mod：观者卡改动 —— 削红蓝（姿态无限）、增预见、增真言/神格。
 * 卡改动大多走 @SpirePatch（自动扫描，不用在这里注册）；本类只管新增/删除卡、加载文案。
 * 光辉重做在 patches/ 里，且会 `isModLoaded("CharacterRelicParity_RareInPrism")` 让位给目标#2。
 * 日志用英文（游戏控制台 GBK）。
 */
@SpireInitializer
public class WatcherRework implements EditCardsSubscriber, EditStringsSubscriber, PostInitializeSubscriber {

    public static final String MODID = "Watcher_RedBlueNerf_ScryMantraBuff";
    public static final Logger logger = LogManager.getLogger(WatcherRework.class.getName());

    public WatcherRework() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        logger.info(MODID + ": initialize() start");
        new WatcherRework();
    }

    @Override
    public void receiveEditCards() {
        // 削红蓝：删猛虎下山 Rushdown(cardID=Adaptation) + 新增顺应变化(cardID=MODID:Adaptation)
        BaseMod.removeCard("Adaptation", AbstractCard.CardColor.PURPLE);
        BaseMod.addCard(new Adaptation());
        UnlockTracker.unlockCard(Adaptation.ID);
        logger.info(MODID + ": removed 猛虎下山(Adaptation), added 顺应变化");
    }

    @Override
    public void receiveEditStrings() {
        String lang = (Settings.language == Settings.GameLanguage.ZHS) ? "zhs" : "eng";
        BaseMod.loadCustomStringsFile(CardStrings.class,
                MODID + "/localization/" + lang + "/CardStrings.json");
        logger.info(MODID + ": loaded CardStrings (" + lang + ")");
    }

    @Override
    public void receivePostInitialize() {
        logger.info(MODID + ": post-initialize done");
    }
}
