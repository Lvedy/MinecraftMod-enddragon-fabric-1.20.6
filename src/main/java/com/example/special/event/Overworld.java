package com.example.special.event;

import com.example.registry.ModItems;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

public class Overworld implements ServerTickEvents.StartTick{
    public int MAX_PLAYER;
    static public int TpTime = 400;  //维度传送冷却
    static public int TpTime2 = TpTime;
    static public int ChangeTime = 150;  //目标切换的初始时间
    static public int Time = 2400;        //后续目标切换的时间
    static public boolean backEnd = false;  //末地告急，主公速回
    static public boolean nether = false;  //地狱暴走
    static public int backEndTime = 600;    //回到末地所需时间
    static public int op = 1;    //追击逻辑选择(1.优先追血少  2.每过一段时间切换追击目标  3.定死追某一位)
    static public float takeHealth = 6;  //当为多少血时被抓起
    public int speedTime = 20;
    static public double speed = 1.3;  //速度调节
    static public int attack = 60;  //末影龙盘旋时长
    static public int takeTime = 100;  //末影龙抓人冷却及时
    static public int strafeTime = 50;  //末影龙龙息喷射时长
    public int strafeTime2 = 50;
    static public int holdingTime = 200; //末影龙盘旋时长
    public int takeTime2 = 300;
    static public float chargeDamage = 0.5f;//末影龙的撞击伤害
    static public MinecraftServer server;
    static public boolean pro = true;
    static public boolean pro1 = true;
    static public int pro1Level = 3;
    static public int pro1Time = 3600;
    static public boolean pro2 = true;
    static boolean start = false;
    static public String Force_Player = null;
    public ServerPlayerEntity ForcePlayer = null;
    static public ServerPlayerEntity Target_Player = null;
    ServerPlayerEntity Old_Target_Player;
    BlockPos oldBlockPos = new BlockPos(0,0,0);
    BlockPos blockPos;
    Random random = new Random();
    public static List<ServerPlayerEntity> AllPlayer;
    List<? extends EnderDragonEntity> enderDragonEntities;
    List<? extends EnderDragonEntity> enderDragonEntities2;
    List<? extends EnderDragonEntity> enderDragonEntities3;
    List<ServerPlayerEntity> OverWorldPlayer;
    List<ServerPlayerEntity> NetherPlayer;
    public static List<ServerPlayerEntity> EndPlayer;
    List<ServerPlayerEntity> list2 = null;
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(Text.of("末影龙"), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);

    public void setTpTime(int a){
        TpTime = a;
        TpTime2 = a;
    }
    public void setChangeTime(int a){
        ChangeTime = a;
    }
    public void setTime(int a){
        Time = a;
    }
    public void setbackEndTime(int a){
        backEndTime = a;
    }
    public void setOp(int a){
        op = a;
    }
    public void setTakeHealth(int a){
        takeHealth = a;
    }
    public void setSpeed(double a){
        speed = a;
    }
    public void setAttack(int a){
        attack = a;
    }
    public void setShort(int a){
        strafeTime2 = a;
    }
    public void setForcePlayer(String a){
        Force_Player = a;
    }
    public void setStart(boolean a){
        start = a;
    }
    public void setChargeDamage(float a){
        chargeDamage = a;
    }
    public void setPro(boolean a){
        pro = a;
    }
    public void setPro1(boolean a){
        pro1 = a;
    }
    public void setPro2(boolean a){
        pro2 = a;
    }
    public void resetOverworld(){
        setTpTime(400);
        setChangeTime(150);
        setTime(2400);
        setbackEndTime(600);
        setOp(1);
        setTakeHealth(6);
        setSpeed(1.3);
        setAttack(60);
        setShort(50);
        setForcePlayer(" ");
        setStart(false);
        setChargeDamage(0.5f);
    }

    @Override
    public void onStartTick(MinecraftServer server) {
        server.getOverworld().getChunkManager().setChunkForced(new ChunkPos(0,0),true);
        Overworld.server = server;
        TpTime--;
        AllPlayer = server.getPlayerManager().getPlayerList();
        OverWorldPlayer = server.getOverworld().getPlayers();
        NetherPlayer = Objects.requireNonNull(server.getWorld(World.NETHER)).getPlayers();
        EndPlayer = Objects.requireNonNull(server.getWorld(World.END)).getPlayers();
        MAX_PLAYER = AllPlayer.size();
        Old_Target_Player = Target_Player;

        for(ServerPlayerEntity player:AllPlayer) {
            if (Force_Player != null) {
                if (Objects.equals(player.getName().getString(), Force_Player)) {
                    player.changeGameMode(GameMode.SPECTATOR);
                    ForcePlayer = player;
                }
            }
            if (Target_Player != null && player == Target_Player) {
                int i = player.getInventory().getSlotWithStack(ModItems.ENDER_EGG.getDefaultStack());
                if (i == -1 && player.getOffHandStack().getItem() != ModItems.ENDER_EGG) {
                    for (int j = 0; j <= 35; j++) {
                        if (player.getInventory().getStack(j).isEmpty()) {
                            player.getInventory().setStack(j, ModItems.ENDER_EGG.getDefaultStack());
                            break;
                        }
                        if (j == 35)
                            player.getInventory().setStack(9, ModItems.ENDER_EGG.getDefaultStack());
                    }
                }
            } else {
                int j = player.getInventory().getSlotWithStack(ModItems.ENDER_EGG.getDefaultStack());
                if (j != -1)
                    player.getInventory().setStack(j, Items.AIR.getDefaultStack());
                if (player.getOffHandStack().getItem() == ModItems.ENDER_EGG)
                    player.setStackInHand(Hand.OFF_HAND,Items.AIR.getDefaultStack());
            }
            if (player.isDead()) {
                player.changeGameMode(GameMode.SPECTATOR);
            }
            if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 30));
            }
            if(GetSize(AllPlayer) != 1 && start && op==2) {
                if (ChangeTime == 100) {
                    sendTitle(player, "5", Formatting.YELLOW);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 5, 1);
                }
                if (ChangeTime == 80) {
                    sendTitle(player, "4", Formatting.YELLOW);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 5, 1);
                }
                if (ChangeTime == 60) {
                    sendTitle(player, "3", Formatting.GOLD);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 5, 1);
                }
                if (ChangeTime == 40) {
                    sendTitle(player, "2", Formatting.RED);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 5, 1);
                }
                if (ChangeTime == 20) {
                    sendTitle(player, "1", Formatting.RED);
                    player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 5, 1);
                }
            }
        }

        enderDragonEntities = server.getOverworld().getAliveEnderDragons();
        enderDragonEntities2 = Objects.requireNonNull(server.getWorld(World.NETHER)).getAliveEnderDragons();
        enderDragonEntities3 = Objects.requireNonNull(server.getWorld(World.END)).getAliveEnderDragons();

        if(!backEnd && start)
            SelectTargetPlayer(op);
        else
            Target_Player = null;

        if((Target_Player != null) && (Old_Target_Player != Target_Player) && (Target_Player.interactionManager.getGameMode() == GameMode.SURVIVAL)){
            for(ServerPlayerEntity player:AllPlayer){
                player.sendMessage(Text.literal("当前追击的目标是:").formatted(Formatting.AQUA));
                player.sendMessage(Target_Player.getName().copy().formatted(Formatting.AQUA));
            }
        }
        if(TpTime <= 0){
            TpTime = -19;
        }
        //主世界末影龙控制
        for(EnderDragonEntity dragon:enderDragonEntities){
            EnderDragonTakePlayer(server,dragon);
            strafePlayer(Target_Player,dragon);
            BossBarShow(dragon);
            DragonHoldingControl(dragon);
            server.getOverworld().getChunkManager().setChunkForced(dragon.getChunkPos(),true);  //保证末影龙加载
            EnderDragonForced(server,dragon);
            EnderDragonPathFix(dragon);
            if(dragon.getWorld().getRegistryKey() == World.END)
                dragon.setFightOrigin(oldBlockPos);
            else {
                if (Target_Player == null) {
                    dragon.setFightOrigin(oldBlockPos);
                } else {
                    blockPos = Target_Player.getBlockPos().withY(0);
                    dragon.setFightOrigin(blockPos);
                }
            }
            //维度传送
            EnderDragonTeleport(dragon,server);
            EnderDragonSpeed(speed,dragon);
            }
        //地狱末影龙控制
        for(EnderDragonEntity dragon:enderDragonEntities2){
            EnderDragonTakePlayer(server,dragon);
            strafePlayer(Target_Player,dragon);
            BossBarShow(dragon);
            DragonHoldingControl(dragon);
            Objects.requireNonNull(server.getWorld(World.NETHER)).getChunkManager().setChunkForced(dragon.getChunkPos(),true);
            EnderDragonForced(server,dragon);
            //维度传送
            EnderDragonTeleport(dragon,server);
            EnderDragonPathFix(dragon);
            if(Target_Player==null){
                dragon.setFightOrigin(oldBlockPos);
            }
            else {
                blockPos = Target_Player.getBlockPos();
                dragon.setFightOrigin(blockPos);
            }
            EnderDragonSpeed(speed,dragon);
        }
        //末地末影龙控制
        for(EnderDragonEntity dragon:enderDragonEntities3){
            EnderDragonTakePlayer(server,dragon);
            EnderDragonSpeed(speed,dragon);
            BossBarShow(dragon);
        }
    }

    public void DragonHoldingControl(EnderDragonEntity dragon){
        PhaseType<? extends Phase> phaseType = dragon.getPhaseManager().getCurrent().getType();
/*        for(ServerPlayerEntity player:AllPlayer){
            if(phaseType == PhaseType.CHARGING_PLAYER)
                player.sendMessage(Text.literal("撞击").formatted(Formatting.DARK_GREEN));
            if(phaseType == PhaseType.LANDING_APPROACH)
                player.sendMessage(Text.literal("登录迫近").formatted(Formatting.DARK_GREEN));
            if(phaseType == PhaseType.LANDING)
                player.sendMessage(Text.literal("登录").formatted(Formatting.DARK_GREEN));
            if(phaseType == PhaseType.HOLDING_PATTERN)
                player.sendMessage(Text.literal("盘旋").formatted(Formatting.DARK_GREEN));
        }*/
        if(dragon.getWorld().getRegistryKey() != World.END){
           if(phaseType != PhaseType.LANDING && phaseType!= PhaseType.LANDING_APPROACH && phaseType != PhaseType.CHARGING_PLAYER)
                holdingTime--;
           if(holdingTime<=0) {
                replayDragonPhase(dragon);
                holdingTime = attack;
           }
        }
        if(Target_Player != null) {
            int a;
            if(nether || Target_Player.getY()<=45)
                a = 0;
            else
                a = random.nextInt(12) + 4;
            if(!pro)
                a = 0;
            if (dragon.getPhaseManager().getCurrent().getType() == PhaseType.LANDING && (Target_Player.getY() - dragon.getY() <= 5 || Target_Player.getY() - dragon.getY() >= -5)) {
                Vec3d vec3d = dragon.getPos();
                Vec3d vec3d2 = dragon.getRotationVector();
                Vec3d vec3d3 = vec3d2.normalize();
                List<ServerPlayerEntity> list;
                for (int i = 0; i < 50; ++i) {
                    Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
                    list = dragon.getWorld().getEntitiesByClass(ServerPlayerEntity.class, Box.of(vec3d4, 8, 8, 4),(entity->entity instanceof ServerPlayerEntity));
                    if(!list.isEmpty()){
                        dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                        dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(Target_Player.getX()+a, Target_Player.getY()+a, Target_Player.getZ()+a));
                    }
                }
            }
            if (dragon.getPhaseManager().getCurrent().isSittingOrHovering()){
                dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(Target_Player.getX(), Target_Player.getY(), Target_Player.getZ()));
            }
        }
    }

    public void replayDragonPhase(EnderDragonEntity dragon){
        PhaseType<? extends Phase> phaseType = dragon.getPhaseManager().getCurrent().getType();
        if(phaseType != PhaseType.LANDING_APPROACH && phaseType != PhaseType.LANDING && phaseType != PhaseType.CHARGING_PLAYER) {
            if(dragon.getWorld().getRegistryKey() == World.NETHER)
                dragon.getPhaseManager().setPhase(PhaseType.LANDING);
            else {
                if (Target_Player != null && Target_Player.getPos().squaredDistanceTo(dragon.getPos()) <= 2500 && pro) {
                    Vec3d vec3d = dragon.getPos();
                    Vec3d vec3d2 = Target_Player.getPos().subtract(vec3d);
                    Vec3d vec3d3 = vec3d2.normalize();
                    dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                    dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(Target_Player.getX()-10*vec3d3.x, Target_Player.getY()+20, Target_Player.getZ()-10*vec3d3.z));
                } else
                    dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            }
        }
        if(nether)
            dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        if(Target_Player != null && Target_Player.getY()<=45){
            dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
        else{
            Vec3d vec3d = dragon.getPhaseManager().getCurrent().getPathTarget();
            if(vec3d != null && Target_Player != null && vec3d.squaredDistanceTo(Target_Player.getPos())>=90000){
                dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            }
        }
    }

    public void EnderDragonSpeed(double sp,EnderDragonEntity dragon){
            speedTime--;
            if(speedTime<=0) {
                if(!dragon.getPhaseManager().getCurrent().isSittingOrHovering())
                    dragon.setVelocity(dragon.getVelocity().multiply(sp));
                if(dragon.getPhaseManager().getCurrent().getType() == PhaseType.LANDING) {
                    dragon.setVelocity(dragon.getVelocity().multiply(1, 2, 1));
                }
                speedTime = 20;
            }
    }

    public void EnderDragonForced(MinecraftServer server, EnderDragonEntity dragon){
        ChunkPos chunkPos = dragon.getChunkPos();
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(chunkPos, true);
        ChunkPos pos = new ChunkPos(chunkPos.x+1,chunkPos.z);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x+1,chunkPos.z+1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x,chunkPos.z+1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x-1,chunkPos.z+1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x-1,chunkPos.z);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x-1,chunkPos.z-1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x,chunkPos.z-1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
        pos = new ChunkPos(chunkPos.x+1,chunkPos.z-1);
        Objects.requireNonNull(server.getWorld(dragon.getWorld().getRegistryKey())).getChunkManager().setChunkForced(pos, true);
    }

    public void strafePlayer(ServerPlayerEntity player,EnderDragonEntity dragon){
        if(player != null && dragon.getY() <= 150) {
            strafeTime--;
            if (strafeTime <= 0) {
                Vec3d vec3d3 = dragon.getRotationVec(1.0f);
                double l = dragon.head.getX() - vec3d3.x;
                double m = dragon.head.getBodyY(0.5) + 0.5;
                double n = dragon.head.getZ() - vec3d3.z;
                double o = player.getX() - l;
                double p = player.getBodyY(0.5) - m;
                double q = player.getZ() - n;
                DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(dragon.getWorld(), dragon, o, p, q);
                dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0f, 0.0f);
                dragon.getWorld().spawnEntity(dragonFireballEntity);
                strafeTime = strafeTime2;
            }
        }
    }

     public void BossBarShow(EnderDragonEntity dragon){
         this.bossBar.setPercent(dragon.getHealth() / dragon.getMaxHealth());
         for (ServerPlayerEntity player : AllPlayer) {
             bossBar.removePlayer(player);
         }
         for(ServerPlayerEntity player: dragon.getWorld().getEntitiesByClass(ServerPlayerEntity.class, Box.of(dragon.getPos(), 200, 400, 200), LivingEntity::isAlive)){
             if(!dragon.isDead() && player.getWorld().getRegistryKey() != World.END)
                 bossBar.addPlayer(player);
         }
     }

    public void EnderDragonTeleport(EnderDragonEntity dragon,MinecraftServer server){
        if(backEnd){
            backEndTime--;
            if(backEndTime<=0){
                Set<PositionFlag> set = Set.of();
                dragon.teleport(server.getWorld(World.END),0,100,0,set,0,0);
            }
            return;
        }
        if(Target_Player != null) {
            if (((Target_Player.getWorld().getRegistryKey() == World.OVERWORLD) || (isTouchingPortal(dragon))) && dragon.getWorld().getRegistryKey().equals(World.NETHER) && (TpTime<=0)) {
                Set<PositionFlag> set = Set.of();
                ChunkPos chunkPos = new ChunkPos((int)dragon.getX()/2,(int)dragon.getZ()/2);
                server.getOverworld().getChunkManager().setChunkForced(chunkPos,true);
                dragon.teleport(server.getWorld(World.OVERWORLD),dragon.getX()*8,dragon.getY(),dragon.getZ()*8,set,0,0);
                TpTime = TpTime2;
                replayDragonPhase(dragon);
                for(ServerPlayerEntity player:AllPlayer){
                    player.sendMessage(Text.of("末影龙已被传送至主世界").copy().formatted(Formatting.RED));
                }
            }
            if (((Target_Player.getWorld().getRegistryKey() == World.NETHER) || (isTouchingPortal(dragon))) && dragon.getWorld().getRegistryKey().equals(World.OVERWORLD) && (TpTime<=0)) {
                Set<PositionFlag> set = Set.of();
                dragon.teleport(server.getWorld(World.NETHER),dragon.getX()/8,dragon.getY(),dragon.getZ()/8,set,0,0);
                ChunkPos chunkPos = new ChunkPos((int)dragon.getX()/128,(int)dragon.getZ()/128);
                Objects.requireNonNull(server.getWorld(World.NETHER)).getChunkManager().setChunkForced(chunkPos,true);
                nether = true;  //末地告急，主公速回
                TpTime = TpTime2;
                replayDragonPhase(dragon);
                for(ServerPlayerEntity player:AllPlayer){
                    player.sendMessage(Text.of("末影龙已被传送至地狱").copy().formatted(Formatting.RED));
                }
            }
            if((dragon.getWorld().getRegistryKey() == Target_Player.getWorld().getRegistryKey()) && (Old_Target_Player != Target_Player || TpTime == TpTime2 - 40) && (Target_Player.interactionManager.getGameMode() == GameMode.SURVIVAL) && (Target_Player.distanceTo(dragon)>200)){
                dragon.teleport(Target_Player.getX()+100,Target_Player.getY(),Target_Player.getZ());
                BlockPos pos = new BlockPos((int)Target_Player.getX()+100,(int)Target_Player.getY(),(int)Target_Player.getZ());
                ChunkPos chunkPos = new ChunkPos(pos);
                Objects.requireNonNull(server.getWorld(World.NETHER)).getChunkManager().setChunkForced(chunkPos,true);
            }
        }
    }

    public boolean isTouchingPortal(EnderDragonEntity dragon) {
        Box box2 = dragon.getBoundingBox().expand(3);
        for (BlockPos blockPos : BlockPos.iterate(MathHelper.floor(box2.minX), MathHelper.floor(box2.minY), MathHelper.floor(box2.minZ), MathHelper.floor(box2.maxX), MathHelper.floor(box2.maxY), MathHelper.floor(box2.maxZ))) {
            if (dragon.getWorld().getBlockState(blockPos).getBlock() == Blocks.NETHER_PORTAL) {
                return true;
            }
        }
        return false;
    }

    public void SelectTargetPlayer(int a) {
        if (!ListEmpty(AllPlayer)) {
            if (a == 1) {
                if (ListEmpty(EndPlayer)) {
                    if (ListEmpty(NetherPlayer)) {
                        Optional<ServerPlayerEntity> minElementOptional = OverWorldPlayer.stream()
                                .min((o1, o2) -> Float.compare(o1.getHealth(), o2.getHealth()));
                        minElementOptional.ifPresent(serverPlayerEntity -> Target_Player = serverPlayerEntity); //将target_player设置为血量最低的玩家
                    } else {
                        Optional<ServerPlayerEntity> minElementOptional = NetherPlayer.stream()
                                .min((o1, o2) -> Float.compare(o1.getHealth(), o2.getHealth()));
                        minElementOptional.ifPresent(serverPlayerEntity -> Target_Player = serverPlayerEntity);
                    }
                } else {
                    for (ServerPlayerEntity player : EndPlayer) {
                        if (player.interactionManager.getGameMode() == GameMode.SURVIVAL)
                            backEnd = true;
                    }
                }
            }
            if (a == 2) {
                ChangeTime--;
                if ((Target_Player != null) && (Target_Player.interactionManager.getGameMode() != GameMode.SURVIVAL)) {
                    ChangeTime = 0;
                }
                if (ChangeTime <= 0) {
                    if (ListEmpty(EndPlayer)) {
                        if (ListEmpty(NetherPlayer)) {
                            for (; true; ) {
                                int i = random.nextInt(OverWorldPlayer.size());
                                if (OverWorldPlayer.get(i).interactionManager.getGameMode() != GameMode.SURVIVAL)
                                    continue;
                                if (Target_Player == null) {
                                    Target_Player = OverWorldPlayer.get(i);
                                    break;
                                } else if (Target_Player == OverWorldPlayer.get(i)) {
                                    if (GetSize(OverWorldPlayer) == 1)
                                        break;
                                    else
                                        continue;
                                }
                                Target_Player = OverWorldPlayer.get(i);
                                break;
                            }
                        } else {
                            for (; true; ) {
                                int i = random.nextInt(NetherPlayer.size());
                                if (NetherPlayer.get(i).interactionManager.getGameMode() != GameMode.SURVIVAL)
                                    continue;
                                if (Target_Player == null) {
                                    Target_Player = NetherPlayer.get(i);
                                    break;
                                } else if (Target_Player == NetherPlayer.get(i)) {
                                    if (GetSize(NetherPlayer) == 1)
                                        break;
                                    else
                                        continue;
                                }
                                Target_Player = NetherPlayer.get(i);
                                break;
                            }
                        }
                        ChangeTime = Time;
                    } else {
                        for (ServerPlayerEntity player : EndPlayer) {
                            if (player.interactionManager.getGameMode() == GameMode.SURVIVAL)
                                backEnd = true;
                        }
                    }
                }
            }
            if (a == 3) {
                if (ListEmpty(EndPlayer)) {
                    if (ListEmpty(NetherPlayer)) {
                        int i = random.nextInt(MAX_PLAYER);
                        if ((Target_Player == null) || (Target_Player.interactionManager.getGameMode() != GameMode.SURVIVAL)) {
                            Target_Player = AllPlayer.get(i);
                        }
                    } else {
                        if (Target_Player != null && Target_Player.getWorld().getRegistryKey() != World.NETHER) {
                            int i = random.nextInt(NetherPlayer.size());
                            Target_Player = NetherPlayer.get(i);
                        }
                    }
                } else {
                    for (ServerPlayerEntity player : EndPlayer) {
                        if (player.interactionManager.getGameMode() == GameMode.SURVIVAL)
                            backEnd = true;
                    }
                }
            }
        }
        else
            Target_Player = null;
    }

    public boolean ListEmpty(List<ServerPlayerEntity> list){
        for(ServerPlayerEntity player:list){
            if(player.interactionManager.getGameMode() == GameMode.SURVIVAL)
                return false;
        }
        return true;
    }

    public int GetSize(List<ServerPlayerEntity> list){
        int i = 0;
        for(ServerPlayerEntity player:list){
            if(player.interactionManager.getGameMode() == GameMode.SURVIVAL)
                i++;
        }
        return i;
    }

    public void EnderDragonPathFix(EnderDragonEntity dragon){
        if(dragon.isTouchingWater() || dragon.isInLava()){    //确保末影龙不会被水流困住
            Box box = dragon.getBoundingBox().expand(2.0).expand(0,-2,0);
            for (BlockPos blockPos : BlockPos.iterate(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ))) {
                BlockState blockState = dragon.getWorld().getBlockState(blockPos);
                Block block = blockState.getBlock();
/*                if((block != Blocks.OBSIDIAN)&&(block != Blocks.BEDROCK)&&(block != Blocks.END_STONE)&&(block != Blocks.NETHERRACK)&&(block != Blocks.END_PORTAL_FRAME)&&(block != Blocks.END_PORTAL))
                    dragon.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());*/
                if(block==Blocks.WATER)
                    dragon.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }
        }
    }

    public void EnderDragonTakePlayer(MinecraftServer server,EnderDragonEntity dragon){
        takeTime--;
        Box box = dragon.getBoundingBox();
        List<ServerPlayerEntity> list = dragon.getWorld().getEntitiesByClass(ServerPlayerEntity.class,box,entity -> (entity == Target_Player));
        if(ForcePlayer != null){
            Set<PositionFlag> set = Set.of();
            if(dragon.getWorld().getRegistryKey() != ForcePlayer.getWorld().getRegistryKey())
                ForcePlayer.teleport(server.getWorld(dragon.getWorld().getRegistryKey()),dragon.getX(),dragon.getY(),dragon.getZ(),set,0,0);
            ForcePlayer.teleport(dragon.getX(),dragon.getY() + 50,dragon.getZ());
        }
        for (ServerPlayerEntity player : list) {
            if(player.getHealth() <= takeHealth && takeTime<=0){
                takeTime2--;
                player.teleport(dragon.getX(),dragon.getY() - 1,dragon.getZ());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,70,2));
                takeTime = -1;
                if(takeTime2<=0){
                    if(pro2) {
                        player.damage(dragon.getDamageSources().mobAttack(dragon),0.5f);
                    }
                    takeTime2 = 320;
                    takeTime=100;
                }
            }
            if(dragon.getAttacker() != null) {
                list2 = list;
                takeTime = 100;
            }
        }
        if (list2 != null && takeTime>0) {
            for(ServerPlayerEntity player:list2){
                if(player.getVelocity().y>1.0){
                    player.setVelocity(player.getVelocity().multiply(0.3));
                }
            }
        }
    }

    public void sendTitle(ServerPlayerEntity player,String string,Formatting formatting){
        Function<Text, Packet<?>> constructor = TitleS2CPacket::new;
        TitleFadeS2CPacket titleFadeS2CPacket = new TitleFadeS2CPacket(10, 20, 10);
        ServerCommandSource source = null;
        try {
            player.networkHandler.sendPacket(titleFadeS2CPacket);
            player.networkHandler.sendPacket(constructor.apply(Texts.parse(null, Text.literal(string).formatted(formatting), player, 0)));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
