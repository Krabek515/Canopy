package io.github.krabek515.canopy.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Кайман: лежит в воде у берега и бьёт того, кто зашёл в реку или встал у самой кромки.
 * На сушу далеко не идёт. Укус открывает грязную рану, как у ягуара.
 */
public class Caiman extends Beast {
    public Caiman(EntityType<? extends Caiman> type, Level level) {
        super(type, level, false);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 0.8, 60));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.5, 200));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::ambushable));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Capybara.class, 20, true, false, this::ambushable));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    private boolean ambushable(LivingEntity target) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        boolean wet = target.isInWater() || this.isInWater();
        return wet && this.distanceTo(target) < 7.0;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TURTLE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TURTLE_DEATH;
    }
}
