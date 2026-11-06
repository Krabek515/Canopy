package io.github.krabek515.canopy.entity;

import net.minecraft.sounds.SoundEvent;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * Горилла держит участок. Пока человек не лезет ближе четырёх блоков и не бьёт, она только смотрит
 * и ворчит. Подошёл вплотную или ударил — отшвыривает, потом остывает.
 */
public class Gorilla extends Beast {
    private int warn;
    private int anger;

    public Gorilla(EntityType<? extends Gorilla> type, Level level) {
        super(type, level, true);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
        this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(2.5);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::intruder));
    }

    private boolean intruder(LivingEntity target) {
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator()) {
            return false;
        }
        return this.anger > 0 || this.distanceTo(player) < 4.0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.anger > 0) {
            this.anger--;
            if (this.anger == 0) {
                this.setTarget(null);
            }
        }
        Player near = this.level().getNearestPlayer(this, 9);
        if (near != null && !near.isCreative() && !near.isSpectator() && this.getTarget() == null) {
            if (++this.warn % 60 == 0) {
                this.playSound(CanopySounds.GORILLA_AMBIENT.get(), 0.5f, 1.4f);
            }
        } else {
            this.warn = 0;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            this.anger = 400;
        }
        return hurt;
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            this.anger = Math.max(this.anger, 200);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CanopySounds.GORILLA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CanopySounds.GORILLA_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }
}
