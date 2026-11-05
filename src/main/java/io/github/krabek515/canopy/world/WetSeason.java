package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Мокрый сезон: длинный цикл по игровым дням, не отдельное измерение. Сухие дни идут подряд, потом мокрые.
 * В мокрые дни ливень и паводок приходят чаще, а обычный дождь почти не прекращается.
 */
public final class WetSeason {
    /** Снимок на текущий тик: читают Downpour и FlashFlood, пишет только tick(). */
    static boolean active;
    private static boolean announced;

    private WetSeason() {
    }

    public static boolean isWet(long dayTime) {
        long day = dayTime / 24000L;
        long dry = CanopyConfig.DRY_SEASON_DAYS.get();
        long wet = CanopyConfig.WET_SEASON_DAYS.get();
        return day % (dry + wet) >= dry;
    }

    /** Сколько дней до смены сезона. */
    public static int daysLeft(long dayTime) {
        long day = dayTime / 24000L;
        long dry = CanopyConfig.DRY_SEASON_DAYS.get();
        long wet = CanopyConfig.WET_SEASON_DAYS.get();
        long phase = day % (dry + wet);
        return (int) (phase < dry ? dry - phase : dry + wet - phase);
    }

    static void tick(ServerLevel level) {
        boolean wet = isWet(level.getDayTime());
        if (wet != active) {
            active = wet;
            announced = false;
        }
        if (!announced) {
            announced = true;
            for (ServerPlayer player : level.players()) {
                player.displayClientMessage(Component.translatable(wet ? "message.canopy.wet_season" : "message.canopy.dry_season"), true);
                if (wet) {
                    JournalLearning.note(player, Journal.WET_SEASON);
                }
            }
        }
        // в мокрый сезон дождь почти не прекращается, в сухой — редкий
        if (wet && !level.isRaining() && !Downpour.active(level) && level.random.nextInt(30) == 0) {
            level.setWeatherParameters(0, 20 * 60 * (4 + level.random.nextInt(6)), true, false);
        }
    }
}
