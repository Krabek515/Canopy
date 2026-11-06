package io.github.krabek515.canopy.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import io.github.krabek515.canopy.body.CanopyEffects;
import net.minecraft.sounds.SoundEvent;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Snake extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Snake(EntityType<? extends Snake> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.24);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(8);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.05, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::closeEnough));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    private boolean closeEnough(LivingEntity entity) {
        return entity instanceof Player player
                && !player.isCreative()
                && !player.isSpectator()
                && this.distanceTo(player) < 7.0;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(CanopyEffects.VENOM.get(), 200, 0));
        }
        return hurt;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CanopySounds.SNAKE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return CanopySounds.SNAKE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CanopySounds.SNAKE_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        BeastAnimations.register(this, controllers, false);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
