package io.github.krabek515.canopy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CanopyConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue THIRST_MINUTES;
    public static final ForgeConfigSpec.BooleanValue BLACKOUT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Настройка тела. Сложность мира умножает эти числа, но не выключает механики.").push("body");
        THIRST_MINUTES = builder
                .comment("Сколько спокойных минут на сложности «Нормально» уходит, чтобы жажда упала с полной до нуля.")
                .defineInRange("thirstMinutesToEmpty", 12.0, 1.0, 120.0);
        BLACKOUT = builder
                .comment("Смертельный урон превращается в обморок. На «Сложно» игрока не уносят к точке возрождения.")
                .define("blackout", true);
        builder.pop();
        SPEC = builder.build();
    }

    private CanopyConfig() {
    }
}
