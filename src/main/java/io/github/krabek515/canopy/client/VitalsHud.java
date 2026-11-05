package io.github.krabek515.canopy.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class VitalsHud {
    public static final IGuiOverlay OVERLAY = VitalsHud::render;
    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "textures/hud/icons.png");

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
        int panelBottom = warning == null ? 48 : 60;
        graphics.fill(4, 4, 100, panelBottom, 0x70110E0A);
        RenderSystem.enableBlend();
        drawMeter(graphics, 8, 8, 0, stats.thirst() / SurvivalStats.NEED_MAX, 0xFF3D7EC9, -1f, -1f);
        drawMeter(graphics, 8, 18, 1, stats.temperature() / SurvivalStats.TEMP_MAX, temperatureColor(stats.temperature()), 0.43f, 0.57f);
        drawMeter(graphics, 8, 28, 2, stats.wetness(), 0xFF4AA89A, -1f, -1f);
        drawMeter(graphics, 8, 38, 3, stats.stamina() / SurvivalStats.NEED_MAX, 0xFFE0A04A, -1f, -1f);
        RenderSystem.disableBlend();
        if (stats.wound() != SurvivalStats.WOUND_NONE) {
            graphics.blit(ICONS, 88, 36, 36, 0, 9, 9, 45, 9);
        }
        if (warning != null) {
            graphics.drawString(font, warning, 8, 50, 0xFFF0C7A0, true);
        }
    }

    /** Иконка слева, полоска справа. comfortLow/High — зелёная метка комфорта для температуры. */
    private static void drawMeter(GuiGraphics graphics, int x, int y, int icon, float fill01, int color, float comfortLow, float comfortHigh) {
        graphics.blit(ICONS, x, y, icon * 9, 0, 9, 9, 45, 9);
        int barX = x + 11;
        int barY = y + 2;
        int barWidth = 74;
        int barHeight = 5;
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF1A1612);
        int fill = Math.round(barWidth * Mth.clamp(fill01, 0f, 1f));
        graphics.fill(barX, barY, barX + fill, barY + barHeight, color);
        if (comfortLow >= 0f) {
            int start = barX + Math.round(barWidth * comfortLow);
            int end = barX + Math.round(barWidth * comfortHigh);
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
        if (stats.wound() == SurvivalStats.WOUND_FEVER) {
            return Component.translatable("hud.canopy.fever");
        }
        if (stats.wound() == SurvivalStats.WOUND_INFECTED) {
            return Component.translatable("hud.canopy.infected");
        }
        if (stats.wound() == SurvivalStats.WOUND_OPEN) {
            return Component.translatable(stats.woundDirty() ? "hud.canopy.wound_dirty" : "hud.canopy.wound");
        }
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
        if (stats.hand() >= SurvivalStats.HAND_INJURED) {
            return Component.translatable("hud.canopy.hand");
        }
        if (stats.stamina() <= 0.5f) {
            return Component.translatable("hud.canopy.exhausted");
        }
        return null;
    }
}
