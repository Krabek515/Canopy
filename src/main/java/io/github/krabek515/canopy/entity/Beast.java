package io.github.krabek515.canopy.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Общая основа зверей Canopy: без опыта, с анимациями GeckoLib. Существо, не монстр, иначе фильтр спавна его сотрёт. */
public abstract class Beast extends AgeableMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final boolean runs;

    protected Beast(EntityType<? extends Beast> type, Level level, boolean runs) {
        super(type, level);
        this.xpReward = 0;
        this.runs = runs;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        net.minecraft.world.entity.Entity baby = this.getType().create(level);
        return baby instanceof AgeableMob ageable ? ageable : null;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.55f : 1.0f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        BeastAnimations.register(this, controllers, runs);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
