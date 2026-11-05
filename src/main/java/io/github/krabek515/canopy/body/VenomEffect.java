package io.github.krabek515.canopy.body;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class VenomEffect extends MobEffect {
    public VenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x3E6B32);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            entity.hurt(CanopyDamage.source(entity.level(), CanopyDamage.VENOM), 1f);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0 && duration % 50 == 0;
    }
}
