package io.github.krabek515.canopy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/** Тукан: голос полога. Перелетает между кронами, от человека уходит выше. Падает редко, мяса мало. */
public class Toucan extends Beast {
    public Toucan(EntityType<? extends Toucan> type, Level level) {
        super(type, level, false);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6);
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(0.5);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8));
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }

    public boolean perched() {
        return this.onGround() || this.level().getBlockState(this.blockPosition().below()).is(BlockTags.LEAVES);
    }

    @Override
    public void registerControllers(software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar controllers) {
        // в полёте машет крыльями ("run"), на ветке сидит или переступает
        controllers.add(new software.bernie.geckolib.core.animation.AnimationController<>(this, "move", 3, state -> {
            if (!this.perched()) {
                return state.setAndContinue(BeastAnimations.RUN);
            }
            return state.setAndContinue(state.isMoving() ? BeastAnimations.WALK : BeastAnimations.IDLE);
        }));
    }
}
