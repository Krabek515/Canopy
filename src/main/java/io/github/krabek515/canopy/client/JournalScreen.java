package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class JournalScreen extends Screen {
    private static final ResourceLocation PAPER = ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "textures/gui/journal.png");
    private static final int PAGE_W = 176;
    private static final int PAGE_H = 208;
    private static final int MARK_W = 72;
    private static final String[] TABS = {
            "journal.canopy.tab.now",
            "journal.canopy.tab.plants",
            "journal.canopy.tab.beasts",
            "journal.canopy.tab.places",
            "journal.canopy.tab.craft"
    };

    private int tab;

    public JournalScreen() {
        super(Component.translatable("journal.canopy.heading"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int left = pageLeft();
        int top = pageTop();
        for (int i = 0; i < TABS.length; i++) {
            int x = left + PAGE_W - 8;
            int y = top + 36 + i * 30;
            if (mouseX >= x && mouseX <= x + MARK_W && mouseY >= y && mouseY <= y + 24) {
                this.tab = i;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        int left = pageLeft();
        int top = pageTop();
        graphics.blit(PAPER, left, top, 0, 0, PAGE_W, PAGE_H, 256, 256);
        for (int i = 0; i < TABS.length; i++) {
            int x = left + PAGE_W - 8;
            int y = top + 36 + i * 30;
            int mark = i == this.tab ? 0xFFE7D3AE : 0xFFC4A67A;
            graphics.fill(x, y, x + MARK_W, y + 24, mark);
            graphics.fill(x, y, x + MARK_W, y + 1, 0xFF6B4A2C);
            graphics.fill(x, y + 23, x + MARK_W, y + 24, 0xFF6B4A2C);
            Component label = Component.translatable(TABS[i]);
            int textX = x + (MARK_W - this.font.width(label)) / 2;
            graphics.drawString(this.font, label, textX, y + 8, 0xFF2C2116, false);
        }
        graphics.drawString(this.font, this.title, left + 22, top + 16, 0xFF3A2A1A, false);
        graphics.fill(left + 22, top + 28, left + PAGE_W - 22, top + 29, 0xFF8A6A42);
        boolean wet = this.minecraft != null && this.minecraft.player != null && this.minecraft.player.getCapability(CanopyCapabilities.SURVIVAL)
                .map(stats -> stats.wetness() > 0.55f)
                .orElse(false);
        Component body = wet
                ? Component.translatable("journal.canopy.wet")
                : this.minecraft.player.getCapability(CanopyCapabilities.JOURNAL)
                .map(journal -> JournalPages.page(this.minecraft.player, journal, this.tab))
                .orElse(Component.translatable("journal.canopy.empty"));
        int textLeft = left + 22;
        int textWidth = PAGE_W - 48;
        int y = top + 40;
        for (int line = 0; line < 13; line++) {
            graphics.fill(textLeft, y + 9, textLeft + textWidth, y + 10, 0x228A6A42);
            y += 11;
        }
        List<FormattedCharSequence> lines = this.font.split(body, textWidth);
        y = top + 40;
        for (FormattedCharSequence line : lines) {
            if (y > top + PAGE_H - 22) {
                break;
            }
            graphics.drawString(this.font, line, textLeft, y, wet ? 0xFF5C6E78 : 0xFF2C2116, false);
            y += 11;
        }
        if (wet) {
            graphics.fill(left + 12, top + 34, left + PAGE_W - 16, top + PAGE_H - 12, 0x66506870);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private int pageLeft() {
        return (this.width - PAGE_W - 48) / 2;
    }

    private int pageTop() {
        return (this.height - PAGE_H) / 2;
    }
}
