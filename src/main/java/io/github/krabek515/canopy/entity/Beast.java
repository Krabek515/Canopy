package io.github.krabek515.canopy.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Общая основа зверей Canopy: без опыта, с анимациями GeckoLib. Существо, не монстр, иначе фильтр спавна его сотрёт. */
public abstract class Beast extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final boolean runs;

    protected Beast(EntityType<? extends Beast> type, Level level, boolean runs) {
        super(type, level);
        this.xpReward = 0;
        this.runs = runs;
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
