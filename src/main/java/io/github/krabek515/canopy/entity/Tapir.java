package io.github.krabek515.canopy.entity;

import net.minecraft.sounds.SoundEvent;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Тапир: крупный, пугливый, ночной. Убегает от человека и кошки, топчет чащу. Самая большая добыча леса. */
public class Tapir extends Beast {
    public Tapir(EntityType<? extends Tapir> type, Level level) {
        super(type, level, true);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 10, 1.1, 1.4));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Jaguar.class, 14, 1.2, 1.5));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CanopySounds.TAPIR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CanopySounds.TAPIR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CanopySounds.TAPIR_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }
}
