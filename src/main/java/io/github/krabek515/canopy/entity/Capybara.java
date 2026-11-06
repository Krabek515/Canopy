package io.github.krabek515.canopy.entity;

import net.minecraft.sounds.SoundEvent;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/** Капибара: мирная, держится у воды, плавает. Много мяса, поэтому её берегут кошки и кайман. */
public class Capybara extends Beast {
    public Capybara(EntityType<? extends Capybara> type, Level level) {
        super(type, level, true);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(16);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Jaguar.class, 12, 1.3, 1.6));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Caiman.class, 6, 1.3, 1.6));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.9, 40));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CanopySounds.CAPYBARA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CanopySounds.CAPYBARA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CanopySounds.CAPYBARA_DEATH.get();
    }
}
