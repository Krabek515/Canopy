package io.github.krabek515.canopy.body;

import net.minecraft.world.Difficulty;

public final class SurvivalTuning {
    private SurvivalTuning() {
    }

    public static float drain(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 0.45f;
            case EASY -> 0.75f;
            case HARD -> 1.4f;
            default -> 1f;
        };
    }

    public static boolean hurts(Difficulty difficulty) {
        return difficulty != Difficulty.PEACEFUL;
    }

    public static int hurtIntervalTicks(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 100;
            case HARD -> 60;
            default -> 80;
        };
    }

    public static float hurtAmount(Difficulty difficulty) {
        return difficulty == Difficulty.HARD ? 2f : 1f;
    }

    public static boolean carryToCamp(Difficulty difficulty) {
        return difficulty != Difficulty.HARD;
    }

    public static int mercyTicks(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 20 * 120;
            case EASY -> 20 * 75;
            case HARD -> 20 * 40;
            default -> 20 * 90;
        };
    }

    public static float wakeHealth(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 14f;
            case EASY -> 10f;
            case HARD -> 4f;
            default -> 8f;
        };
    }
}
