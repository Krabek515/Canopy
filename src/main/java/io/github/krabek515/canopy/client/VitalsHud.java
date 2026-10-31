package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class VitalsHud {
    public static final IGuiOverlay OVERLAY = VitalsHud::render;

    private VitalsHud() {
    }

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null) {
            return;
        }
        Player player = minecraft.player;
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> draw(graphics, minecraft.font, stats));
    }

    private static void draw(GuiGraphics graphics, Font font, SurvivalStats stats) {
        Component warning = warning(stats);
        int panelBottom = warning == null ? 58 : 72;
        graphics.fill(4, 4, 156, panelBottom, 0x70110E0A);
        drawMeter(graphics, font, 8, 8, "hud.canopy.thirst", stats.thirst(), SurvivalStats.NEED_MAX, 0xFF3D7EC9, false);
        drawMeter(graphics, font, 8, 20, "hud.canopy.temperature", stats.temperature(), SurvivalStats.TEMP_MAX, temperatureColor(stats.temperature()), true);
        drawMeter(graphics, font, 8, 32, "hud.canopy.wetness", stats.wetness(), 1f, 0xFF4AA89A, false);
        drawMeter(graphics, font, 8, 44, "hud.canopy.stamina", stats.stamina(), SurvivalStats.NEED_MAX, 0xFFE0A04A, false);
        if (warning != null) {
            graphics.drawString(font, warning, 8, 58, 0xFFF0C7A0, true);
        }
    }

    private static void drawMeter(GuiGraphics graphics, Font font, int x, int y, String label, float value, float max, int color, boolean comfortMark) {
        graphics.drawString(font, Component.translatable(label), x, y, 0xFFF3EBDD, true);
        int barX = x + 58;
        int barY = y + 1;
        int barWidth = 80;
        int barHeight = 6;
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF1A1612);
        int fill = Math.round(barWidth * Mth.clamp(value / max, 0f, 1f));
        graphics.fill(barX, barY, barX + fill, barY + barHeight, color);
        if (comfortMark) {
            int start = barX + Math.round(barWidth * 0.43f);
            int end = barX + Math.round(barWidth * 0.57f);
            graphics.fill(start, barY + barHeight, end, barY + barHeight + 1, 0xFFD7E7C8);
        }
    }

    private static int temperatureColor(float temperature) {
        if (temperature < 36f) {
            return 0xFF6EB6FF;
        }
        if (temperature > 64f) {
            return 0xFFE0703A;
        }
        return 0xFF7DCE6A;
    }

    private static Component warning(SurvivalStats stats) {
        if (stats.thirst() <= 4f) {
            return Component.translatable("hud.canopy.dehydrated");
        }
        if (stats.temperature() < 36f) {
            return Component.translatable("hud.canopy.cold");
        }
        if (stats.temperature() > 64f) {
            return Component.translatable("hud.canopy.hot");
        }
        if (stats.wetness() > 0.85f) {
            return Component.translatable("hud.canopy.soaked");
        }
        if (stats.stamina() <= 0.5f) {
            return Component.translatable("hud.canopy.exhausted");
        }
        return null;
    }
}
