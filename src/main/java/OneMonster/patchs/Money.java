package OneMonster.patchs;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Apotheosis;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import java.util.ArrayList;
import java.util.Random;
import basemod.abstracts.CustomRelic;

import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.GiantHead;
import com.megacrit.cardcrawl.monsters.beyond.Nemesis;
import com.megacrit.cardcrawl.monsters.beyond.Reptomancer;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;
import com.megacrit.cardcrawl.monsters.city.BookOfStabbing;
import com.megacrit.cardcrawl.monsters.city.GremlinLeader;
import com.megacrit.cardcrawl.monsters.city.SnakePlant;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.monsters.exordium.GremlinNob;
import com.megacrit.cardcrawl.monsters.exordium.Lagavulin;
import com.megacrit.cardcrawl.potions.AttackPotion;
import com.megacrit.cardcrawl.potions.FairyPotion;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.FlashPowerEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

import java.util.Iterator;


import static OneMonster.modcore.OneMonster.config;
import static OneMonster.ui.BreedsearchButton.savedPowers;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.getRandomMonster;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.relicsToRemoveOnStart;
import static loadout.screens.MonsterSelectScreen.MonsterButton.createMonster;

/**
 * 这个遗物会在开始的时候赋予房间所有怪物3点血量
 *
 * @author : Administrator
 * @date : 2020-08-06 16:27
 **/
@SuppressWarnings("unused")
public class Money extends CustomRelic   {
    private int count=1;
    public static int count2=0;
    public static final String ID =  "fanzhuan:Money";
    /**
     * 日志对象
     */
    private static final Logger log = LogManager.getLogger(Money.class);
    private boolean cardsReceived;

    /**
     * 构造函数
     */
    public Money() {
        //图片使用内置的 使用破碎王冠 的图标
        //使用内置图标就不需要导入了 想自定义可以抄其他的mod或者看教程
        super(ID, "crown.png", RelicTier.SPECIAL, LandingSound.CLINK);

    }
    
    /**
     * 在战斗开始时触发
     */
    @Override

    public void atBattleStart() {
        super.atBattleStart();
            roll_monster();
            this.counter+=1;

            }




    public static void roll_monster() {
        AbstractMonster m;
        Iterator var2 = AbstractDungeon.getCurrRoom().monsters.monsters.iterator();//获取所有怪物

        while (var2.hasNext()) {
            m = (AbstractMonster) var2.next();
            Random random = new Random();
            int randomNumber;


            int s = AbstractDungeon.floorNum;
            AbstractMonster SP = null;

            if(s<=17) {
                if(config.getInt("monster1")!=0) {
                    SP = createMonster(savedPowers.get(config.getInt("monster1")));
                }            }
            else if (s<=34) {
                if(config.getInt("monster2")!=0)
                    SP =createMonster(savedPowers.get(config.getInt("monster2")));
            }else if (s<=52) {
                if(config.getInt("monster3")!=0)
                    SP =createMonster(savedPowers.get(config.getInt("monster3")));
            }else{
                if(config.getInt("monster4")!=0) {
                    randomNumber = AbstractDungeon.monsterRng.random(5) + 63;
                    System.out.println(randomNumber);
                    SP = createMonster(savedPowers.get(config.getInt("monster4")));
                }
            }

            SP.drawX = m.drawX;
            SP.drawY = m.drawY;//蛇花的位置
            SP.maxHealth = m.maxHealth;
            SP.addBlock(m.currentBlock);


            SP.currentHealth = m.currentHealth;


            AbstractPower po;
            for (AbstractPower power : m.powers) {
                // 对每个元素进行处理
                power.owner=SP;
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(SP, SP, power));

            }
            if (m.maxHealth!=400) {
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(SP, false));
            }
            AbstractMonster finalSP = SP;
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    finalSP.rollMove();
                    finalSP.createIntent();
                    this.isDone = true;
                }
            });


            if (SP.id!="CorruptHeart") {
                SP.usePreBattleAction();
            }

            //AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(SP, false));
            if(AbstractDungeon.player.hasRelic("NeowsBlessing"))
            {
                if(AbstractDungeon.player.getRelic("NeowsBlessing").counter>0)
                {
                    SP.currentHealth=1;
                }

            }
            if(AbstractDungeon.player.hasRelic("PreservedInsect"))
            {
                if (AbstractDungeon.getCurrRoom().eliteTrigger) {



                    if (SP.currentHealth > (int)((float)SP.maxHealth * (1.0F - 0.25F))) {
                        SP.currentHealth = (int)((float)SP.maxHealth * (1.0F - 0.25F));
                        SP.healthBarUpdatedEvent();

                    }


                }

            } if(AbstractDungeon.player.hasRelic("Red Mask"))
            {
              AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(SP, AbstractDungeon.player, new WeakPower(SP, 1, false), 1, true));

            }
            if(AbstractDungeon.player.hasRelic("Bag of Marbles"))
            {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(SP, AbstractDungeon.player, new VulnerablePower(SP, 1, false), 1, true));

            }
            if (m.maxHealth!=400) { AbstractDungeon.actionManager.addToBottom(new CannotLoseAction());

            AbstractDungeon.actionManager.addToBottom(new HideHealthBarAction(m));
           AbstractDungeon.actionManager.addToBottom(new SuicideAction(m, false));


            AbstractDungeon.actionManager.addToBottom(new CanLoseAction());}

        }}
        //定义一个金钱变量为100
        @Override
    public void onVictory() {
            if (this.count2 > 0) {
                RewardItem rewardItem1 = new RewardItem(new PrayerWheel());
                RewardItem rewardItem2 = new RewardItem(new PotionBelt());
                RewardItem rewardItem3 = new RewardItem(new MoltenEgg2());

                AbstractDungeon.getCurrRoom().rewards.add(rewardItem1);
                AbstractDungeon.getCurrRoom().rewards.add(rewardItem2);
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(new FairyPotion()));
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(new SmokeBomb()));
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(new AttackPotion()));
                AbstractDungeon.getCurrRoom().rewards.add(rewardItem3);
                this.count2 = 0;
                AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
            }
            if (this.counter <= 4) {
                this.counter++;


            }
        }

    
    /**
     * 重写遗物的描述内容 可以不用管
     *
     * @return 字符串内容
     */
    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
}