package com.example.registry;

import com.example.special.event.Overworld;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import java.util.List;

public class ModEvent {
    static Overworld overWorld = new Overworld();

    public static void register() {
        // 注册事件监听器
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            List<EnderDragonEntity> list = world.getEntitiesByClass(EnderDragonEntity.class,player.getBoundingBox(),entity -> (entity instanceof EnderDragonEntity));
            if(!list.isEmpty() && player.getHealth()<= Overworld.takeHealth && player == Overworld.Target_Player)
                return TypedActionResult.fail(itemStack);
            return TypedActionResult.pass(itemStack);
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            List<EnderDragonEntity> list = world.getEntitiesByClass(EnderDragonEntity.class,player.getBoundingBox(),entity1 -> (entity1 instanceof EnderDragonEntity));
            if(!list.isEmpty() && player.getHealth()<= Overworld.takeHealth && player == Overworld.Target_Player) {
                    return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->{
            handler.player.sendMessageToClient(Text.literal("感谢下载本模组并游玩  当前版本1.1.6EX\n关注卡慕喵，关注卡慕谢谢喵").formatted(Formatting.GOLD), false);
            if(server.getPlayerManager().getPlayerList().isEmpty()){
                overWorld.resetOverworld();
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("ender")
                .then(argument("维度传送冷却", IntegerArgumentType.integer())
                    .executes(context -> {
                        overWorld.setTpTime(IntegerArgumentType.getInteger(context, "维度传送冷却"));
                        context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                        return 1;
                })
                .then(argument("首次切换目标前等待时间(不是隔一段时间切换追击玩家不生效)", IntegerArgumentType.integer())
                .executes(context -> {
                    overWorld.setChangeTime(IntegerArgumentType.getInteger(context, "首次切换目标前等待时间(不是隔一段时间切换追击玩家不生效)"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                .then(argument("后继切换目标等待时间(不是隔一段时间切换追击玩家不生效)", IntegerArgumentType.integer())
                .executes(context -> {
                    overWorld.setTime(IntegerArgumentType.getInteger(context, "后继切换目标等待时间(不是隔一段时间切换追击玩家不生效)"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                .then(argument("返回末地的时间", IntegerArgumentType.integer())
                .executes(context -> {
                    overWorld.setbackEndTime(IntegerArgumentType.getInteger(context, "返回末地的时间"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                .then(argument("追击逻辑选择(1:优先追血少  2:隔一段时间切换追击目标  3:定死追一位)", IntegerArgumentType.integer())
                .executes(context -> {
                    overWorld.setOp(IntegerArgumentType.getInteger(context, "追击逻辑选择(1:优先追血少  2:隔一段时间切换追击目标  3:定死追一位)"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                .then(argument("末影龙抓取玩家所需血量", IntegerArgumentType.integer())
                .executes(context -> {
                    overWorld.setTpTime(IntegerArgumentType.getInteger(context, "维度传送冷却"));
                    overWorld.setChangeTime(IntegerArgumentType.getInteger(context, "首次切换目标前等待时间(不是隔一段时间切换追击玩家不生效)"));
                    overWorld.setTime(IntegerArgumentType.getInteger(context, "后继切换目标等待时间(不是隔一段时间切换追击玩家不生效)"));
                    overWorld.setbackEndTime(IntegerArgumentType.getInteger(context, "返回末地的时间"));
                    overWorld.setOp(IntegerArgumentType.getInteger(context, "追击逻辑选择(1:优先追血少  2:隔一段时间切换追击目标  3:定死追一位)"));
                    overWorld.setTakeHealth(IntegerArgumentType.getInteger(context, "末影龙抓取玩家所需血量"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                ))))))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("speed")
                .then(argument("末影龙速度", DoubleArgumentType.doubleArg())
                .executes(context -> {
                    overWorld.setSpeed(DoubleArgumentType.getDouble(context,"末影龙速度"));
                    context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                    return 1;
                })
                )));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("attack")
                .then(argument("末影龙撞击频率", IntegerArgumentType.integer())
                        .executes(context -> {
                            overWorld.setAttack(IntegerArgumentType.getInteger(context,"末影龙撞击频率"));
                            context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("short")
                .then(argument("末影龙射击频率", IntegerArgumentType.integer())
                        .executes(context -> {
                            overWorld.setShort(IntegerArgumentType.getInteger(context,"末影龙射击频率"));
                            context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("force")
                .then(argument("龙骑士", StringArgumentType.string())
                        .executes(context -> {
                            overWorld.setForcePlayer(StringArgumentType.getString(context,"龙骑士"));
                            context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("start")
                .then(argument("bool", BoolArgumentType.bool())
                        .executes(context -> {
                            overWorld.setStart(BoolArgumentType.getBool(context,"bool"));
                            context.getSource().sendFeedback(() -> Text.literal("开始"), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("charge")
                .then(argument("末影龙撞击伤害", FloatArgumentType.floatArg())
                        .executes(context -> {
                            overWorld.setChargeDamage(FloatArgumentType.getFloat(context,"末影龙撞击伤害"));
                            context.getSource().sendFeedback(() -> Text.literal("修改成功"), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("set1")
                .then(argument("开局撞击误差", BoolArgumentType.bool())
                        .executes(context -> {
                            overWorld.setPro(BoolArgumentType.getBool(context,"开局撞击误差"));
                            context.getSource().sendFeedback(() -> Text.literal(" "), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("set2")
                .then(argument("开局抗性提升", BoolArgumentType.bool())
                .then(argument("等级", IntegerArgumentType.integer())
                .then(argument("持续时间", IntegerArgumentType.integer())
                        .executes(context -> {
                            overWorld.setPro1(BoolArgumentType.getBool(context,"开局抗性提升"));
                            Overworld.pro1Level = IntegerArgumentType.getInteger(context,"等级");
                            Overworld.pro1Time = IntegerArgumentType.getInteger(context,"持续时间");
                            context.getSource().sendFeedback(() -> Text.literal(" "), false);
                            return 1;
                        })
                )))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("set3")
                .then(argument("末影龙丢弃击飞效果", BoolArgumentType.bool())
                        .executes(context -> {
                            overWorld.setPro2(BoolArgumentType.getBool(context,"末影龙丢弃击飞效果"));
                            context.getSource().sendFeedback(() -> Text.literal(" "), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("ok")
                .then(argument("ok", BoolArgumentType.bool())
                        .executes(context -> {
                            if(Overworld.pro1)
                                for(ServerPlayerEntity player:Overworld.AllPlayer){
                                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,Overworld.pro1Time,Overworld.pro1Level));
                                }
                            context.getSource().sendFeedback(() -> Text.literal(" "), false);
                            return 1;
                        })
                )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("show")
                        .executes(context -> {
                            String string = " ";
                            if(Overworld.op == 1)
                                string = "优先追血少";
                            if(Overworld.op == 2)
                                string = "每隔一段时间换人追";
                            if(Overworld.op == 3)
                                string = "定死追某一玩家";
                            String str = getString(string);
                            context.getSource().sendFeedback(() -> Text.literal(str).formatted(Formatting.YELLOW), false);
                            return 1;
                        })
                ));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("reforce")
                .executes(context -> {
                    Overworld.Force_Player = "null";
                    overWorld.ForcePlayer = null;
                    context.getSource().sendFeedback(() -> Text.literal(" "), false);
                    return 1;
                })
        ));
    }

    private static @NotNull String getString(String string) {
        String str;
        str="当前设置:\n末影龙维度传送冷却:"+ Overworld.TpTime2+"\n每隔一段时间切换追击目标模式下,切换时间:"+Overworld.Time +
                "\n末影龙回到末地的时间:" + Overworld.backEndTime + "\n当前追击模式:" + string +
                "\n末影龙抓起玩家所需血量:" + Overworld.takeHealth+"\n末影龙速度:" + Overworld.speed+
                "\n末影龙撞击频率:" + Overworld.attack+"\n龙息频率:" + Overworld.strafeTime+
                "\n龙骑士:" + Overworld.Force_Player+"\n末影龙撞击伤害:" + Overworld.chargeDamage+
                "\n设置_开局末影龙撞击误差:" + Overworld.pro+"\n设置_开局抗性提升:" + Overworld.pro1 + "   等级:" + Overworld.pro1Level + "   持续时间:"+Overworld.pro1Time+
                "\n设置_末影龙抓取后丢弃击飞:" + Overworld.pro2;
        return str;
    }

    public static void main_registerEvent(){
        register();
        ServerTickEvents.START_SERVER_TICK.register(overWorld);
    }
}
