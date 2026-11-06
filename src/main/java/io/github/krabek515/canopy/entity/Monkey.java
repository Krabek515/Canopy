package io.github.krabek515.canopy.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class Monkey extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean fleeing;
    private int fleeTicks;
    private long wave;
    @Nullable
    private UUID raidPlayer;

    public Monkey(EntityType<? extends Monkey> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);
    }

    public void beginRaid(long wave, UUID player) {
        this.wave = wave;
        this.raidPlayer = player;
        this.fleeing = false;
        this.setPersistenceRequired();
    }

    public long wave() {
        return this.wave;
    }

    public void flee() {
        this.fleeing = true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FleeGoal());
        this.goalSelector.addGoal(2, new RaidGoal());
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !this.fleeing) {
            return;
        }
        this.fleeTicks++;
        if (this.fleeTicks > 400) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            this.fleeing = true;
        }
        return hurt;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CanopySounds.MONKEY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CanopySounds.MONKEY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CanopySounds.MONKEY_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Fleeing", this.fleeing);
        tag.putLong("Wave", this.wave);
        if (this.raidPlayer != null) {
            tag.putUUID("RaidPlayer", this.raidPlayer);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.fleeing = tag.getBoolean("Fleeing");
        this.wave = tag.getLong("Wave");
        this.raidPlayer = tag.hasUUID("RaidPlayer") ? tag.getUUID("RaidPlayer") : null;
    }

    @Nullable
    private ServerPlayer raidTarget() {
        if (this.raidPlayer == null || !(this.level() instanceof ServerLevel level)) {
            return null;
        }
        Player player = level.getPlayerByUUID(this.raidPlayer);
        return player instanceof ServerPlayer server && server.isAlive() ? server : null;
    }

    private final class RaidGoal extends Goal {
        private RaidGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return Monkey.this.raidPlayer != null && !Monkey.this.fleeing;
        }

        @Override
        public void tick() {
            ServerPlayer player = Monkey.this.raidTarget();
            if (player == null) {
                Monkey.this.discard();
                return;
            }
            Monkey.this.getNavigation().moveTo(player, 1.25);
            Monkey.this.getLookControl().setLookAt(player, 30f, 30f);
            if (Monkey.this.distanceTo(player) < 2.4) {
                MonkeyRaid.snatch(Monkey.this, player);
            }
        }
    }

    private final class FleeGoal extends Goal {
        private FleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Monkey.this.fleeing;
        }

        @Override
        public void tick() {
            ServerPlayer player = Monkey.this.raidTarget();
            if (player == null) {
                Monkey.this.discard();
                return;
            }
            Vec3 away = Monkey.this.position().subtract(player.position());
            if (away.lengthSqr() < 0.01) {
                away = new Vec3(1, 0, 0);
            }
            Vec3 target = Monkey.this.position().add(away.normalize().scale(8));
            Monkey.this.getNavigation().moveTo(target.x, target.y, target.z, 1.45);
            if (Monkey.this.fleeTicks > 40 && Monkey.this.distanceTo(player) > 20) {
                Monkey.this.discard();
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        BeastAnimations.register(this, controllers, true);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
