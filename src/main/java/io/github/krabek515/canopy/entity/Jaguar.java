package io.github.krabek515.canopy.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import io.github.krabek515.canopy.body.HandWork;
import io.github.krabek515.canopy.item.WornClothing;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class Jaguar extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int leapCooldown;

    public Jaguar(EntityType<? extends Jaguar> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.33);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(40);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.2);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.leapCooldown > 0) {
            this.leapCooldown--;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LeapGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.65));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HuntGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Agouti.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public boolean concealed() {
        BlockPos pos = this.blockPosition();
        BlockState here = this.level().getBlockState(pos);
        BlockState below = this.level().getBlockState(pos.below());
        if (here.is(BlockTags.LEAVES) || here.is(HandWork.FOLIAGE) || here.is(HandWork.THICKET) || here.getBlock() instanceof BushBlock) {
            return true;
        }
        return below.is(BlockTags.LOGS) || below.is(BlockTags.LEAVES);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.OCELOT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.OCELOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.OCELOT_DEATH;
    }

    private static final class HuntGoal extends Goal {
        private final Jaguar jaguar;
        private int wait;

        private HuntGoal(Jaguar jaguar) {
            this.jaguar = jaguar;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (--this.wait > 0) {
                return false;
            }
            this.wait = 10;
            Player player = this.jaguar.level().getNearestPlayer(this.jaguar, 48);
            LivingEntity target = this.jaguar.getTarget();
            if (player == null || player.isCreative() || player.isSpectator() || !player.isAlive()) {
                if (target instanceof Player) {
                    this.jaguar.setTarget(null);
                }
                return false;
            }
            double range = Scent.jaguarRange(player);
            double distance = this.jaguar.distanceTo(player);
            if (distance > range) {
                if (target == player) {
                    this.jaguar.setTarget(null);
                }
                return false;
            }
            boolean commit = this.jaguar.concealed()
                    || this.jaguar.level().isRaining()
                    || distance < 6.0
                    || WornClothing.noise(player) >= 0.3f;
            if (!commit) {
                return false;
            }
            this.jaguar.setTarget(player);
            return false;
        }
    }

    private static final class LeapGoal extends Goal {
        private final Jaguar jaguar;

        private LeapGoal(Jaguar jaguar) {
            this.jaguar = jaguar;
            this.setFlags(EnumSet.of(Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.jaguar.getTarget();
            if (target == null || !this.jaguar.onGround() || this.jaguar.leapCooldown > 0) {
                return false;
            }
            double distance = this.jaguar.distanceTo(target);
            return distance > 3.5 && distance < 9.0;
        }

        @Override
        public void start() {
            LivingEntity target = this.jaguar.getTarget();
            if (target == null) {
                return;
            }
            Vec3 leap = target.position().subtract(this.jaguar.position()).normalize();
            this.jaguar.setDeltaMovement(leap.x * 0.72, 0.38, leap.z * 0.72);
            this.jaguar.leapCooldown = 50;
            this.jaguar.playSound(SoundEvents.GRASS_STEP, 0.45f, 1.4f);
            this.jaguar.playSound(SoundEvents.OCELOT_AMBIENT, 0.7f, 0.6f);
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
