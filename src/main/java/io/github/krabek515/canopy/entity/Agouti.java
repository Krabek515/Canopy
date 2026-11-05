package io.github.krabek515.canopy.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Agouti extends Rabbit implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Agouti(EntityType<? extends Agouti> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.7));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 12, 1.3, 1.55));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Jaguar.class, 16, 1.35, 1.7));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.05));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData spawned = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.setVariant(Variant.BROWN);
        return spawned;
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
