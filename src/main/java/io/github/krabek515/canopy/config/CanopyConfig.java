package io.github.krabek515.canopy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CanopyConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue THIRST_MINUTES;
    public static final ForgeConfigSpec.BooleanValue BLACKOUT;
    public static final ForgeConfigSpec.DoubleValue WOUND_MINUTES;
    public static final ForgeConfigSpec.DoubleValue DOWNPOUR_MINUTES;
    public static final ForgeConfigSpec.DoubleValue EVENT_COOLDOWN_MINUTES;
    public static final ForgeConfigSpec.IntValue FLOOD_WARNING_SECONDS;
    public static final ForgeConfigSpec.IntValue DRY_SEASON_DAYS;
    public static final ForgeConfigSpec.IntValue WET_SEASON_DAYS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Настройка тела. Сложность мира умножает эти числа, но не выключает механики.").push("body");
        THIRST_MINUTES = builder
                .comment("Сколько спокойных минут на сложности «Нормально» уходит, чтобы жажда упала с полной до нуля.")
                .defineInRange("thirstMinutesToEmpty", 12.0, 1.0, 120.0);
        BLACKOUT = builder
                .comment("Смертельный урон превращается в обморок. На «Сложно» игрока не уносят к точке возрождения.")
                .define("blackout", true);
        WOUND_MINUTES = builder
                .comment("Сколько минут грязная открытая рана идёт до воспаления, и сколько ещё до лихорадки. Одинаково на всех сложностях.")
                .defineInRange("woundMinutesUntended", 10.0, 1.0, 60.0);
        DOWNPOUR_MINUTES = builder
                .comment("Сколько минут длится скриптованный ливень.")
                .defineInRange("downpourMinutes", 3.0, 1.0, 20.0);
        EVENT_COOLDOWN_MINUTES = builder
                .comment("Сколько минут после ливня или налёта следующее такое событие не начинается. Первый раз в сессии короче.")
                .defineInRange("eventCooldownMinutes", 40.0, 1.0, 180.0);
        FLOOD_WARNING_SECONDS = builder
                .comment("Сколько секунд гудит паводок, прежде чем вода сбивает тех, кто остался на дне русла.")
                .defineInRange("floodWarningSeconds", 45, 15, 90);
        DRY_SEASON_DAYS = builder
                .comment("Сколько игровых дней подряд длится сухой сезон.")
                .defineInRange("drySeasonDays", 6, 1, 60);
        WET_SEASON_DAYS = builder
                .comment("Сколько игровых дней подряд длится мокрый сезон: ливни и паводки вдвое чаще, дождь почти не прекращается.")
                .defineInRange("wetSeasonDays", 3, 1, 60);
        builder.pop();
        SPEC = builder.build();
    }

    private CanopyConfig() {
    }
}
