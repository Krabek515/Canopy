package io.github.krabek515.canopy.entity;

import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** Общий контроллер зверей: покой, шаг, бег. Имена анимаций одинаковые у всех geo-файлов мода. */
public final class BeastAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.beast.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.beast.walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.beast.run");

    private BeastAnimations() {
    }

    public static <T extends LivingEntity & GeoAnimatable> void register(T beast, AnimatableManager.ControllerRegistrar controllers, boolean canRun) {
        controllers.add(new AnimationController<>(beast, "move", 4, state -> {
            if (!state.isMoving()) {
                return state.setAndContinue(IDLE);
            }
            if (canRun && (beast.isSprinting() || beast.getDeltaMovement().horizontalDistanceSqr() > 0.03)) {
                return state.setAndContinue(RUN);
            }
            return state.setAndContinue(WALK);
        }).setAnimationSpeedHandler(e -> 0.8 + Math.min(1.6, e.getDeltaMovement().horizontalDistance() * 6.0)));
    }
}
