package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.journal.Journal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JournalScreen extends Screen {
    private static final ResourceLocation PAPER = ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "textures/gui/journal.png");
    private static final int PAGE_W = 176;
    private static final int PAGE_H = 208;
    private static final int MARK_W = 72;
    private static final int CELL = 24;
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
        renderTabs(graphics, left, top);
        graphics.drawString(this.font, this.title, left + 22, top + 16, 0xFF3A2A1A, false);
        graphics.fill(left + 22, top + 28, left + PAGE_W - 22, top + 29, 0xFF8A6A42);

        boolean wet = this.minecraft != null && this.minecraft.player != null
                && this.minecraft.player.getCapability(CanopyCapabilities.SURVIVAL)
                .map(stats -> stats.wetness() > 0.55f)
                .orElse(false);
        Journal journal = this.minecraft.player.getCapability(CanopyCapabilities.JOURNAL).orElse(null);
        if (wet) {
            renderWet(graphics, left, top);
        } else if (this.tab == 0 || journal == null) {
            renderTextPage(graphics, left, top, journal);
        } else {
            renderTree(graphics, left, top, journal, mouseX, mouseY);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderTabs(GuiGraphics graphics, int left, int top) {
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
    }

    private void renderWet(GuiGraphics graphics, int left, int top) {
        int textLeft = left + 22;
        int y = top + 40;
        for (FormattedCharSequence line : this.font.split(Component.translatable("journal.canopy.wet"), PAGE_W - 48)) {
            graphics.drawString(this.font, line, textLeft, y, 0xFF5C6E78, false);
            y += 11;
        }
        graphics.fill(left + 12, top + 34, left + PAGE_W - 16, top + PAGE_H - 12, 0x66506870);
    }

    private void renderTextPage(GuiGraphics graphics, int left, int top, Journal journal) {
        Component body = journal == null
                ? Component.translatable("journal.canopy.empty")
                : JournalPages.page(this.minecraft.player, journal, 0);
        int textLeft = left + 22;
        int textWidth = PAGE_W - 48;
        int y = top + 40;
        for (int line = 0; line < 13; line++) {
            graphics.fill(textLeft, y + 9, textLeft + textWidth, y + 10, 0x228A6A42);
            y += 11;
        }
        y = top + 40;
        for (FormattedCharSequence line : this.font.split(body, textWidth)) {
            if (y > top + PAGE_H - 22) {
                break;
            }
            graphics.drawString(this.font, line, textLeft, y, 0xFF2C2116, false);
            y += 11;
        }
    }

    /** Древо узлов: рамки с иконками, линии-«корни», тултип с описанием и крафтом. */
    private void renderTree(GuiGraphics graphics, int left, int top, Journal journal, int mouseX, int mouseY) {
        List<JournalEntries.Node> nodes = JournalEntries.nodes(this.tab);
        for (JournalEntries.Node node : nodes) {
            if (node.parent() == null) {
                continue;
            }
            JournalEntries.Node parent = JournalEntries.byFact(node.parent());
            if (parent == null) {
                continue;
            }
            int x1 = left + parent.x() + CELL / 2;
            int y1 = top + parent.y() + CELL / 2;
            int x2 = left + node.x() + CELL / 2;
            int y2 = top + node.y() + CELL / 2;
            int midY = (y1 + y2) / 2;
            graphics.fill(Math.min(x1, x1), y1, Math.max(x1, x1) + 1, midY, 0xFF8A6A42);
            graphics.fill(Math.min(x1, x2), midY, Math.max(x1, x2) + 1, midY + 1, 0xFF8A6A42);
            graphics.fill(x2, midY, x2 + 1, y2, 0xFF8A6A42);
        }
        JournalEntries.Node hovered = null;
        for (JournalEntries.Node node : nodes) {
            int x = left + node.x();
            int y = top + node.y();
            boolean known = journal.knows(node.fact());
            boolean hover = mouseX >= x && mouseX <= x + CELL && mouseY >= y && mouseY <= y + CELL;
            int bg = known ? 0xFFE7D3AE : 0xFF9A8468;
            if (hover) {
                bg = known ? 0xFFF4E4C0 : 0xFFAE9678;
            }
            graphics.fill(x, y, x + CELL, y + CELL, bg);
            graphics.renderOutline(x, y, CELL, CELL, 0xFF6B4A2C);
            if (known) {
                graphics.renderItem(node.icon().get(), x + 4, y + 4);
            } else {
                Component q = Component.literal("?");
                graphics.drawString(this.font, q, x + (CELL - this.font.width(q)) / 2, y + 8, 0xFF4A3828, false);
            }
            if (hover) {
                hovered = node;
            }
        }
        if (hovered != null) {
            renderNodeTooltip(graphics, left, top, journal, hovered, mouseX, mouseY);
        } else {
            Component tip = Component.translatable("journal.canopy.hover_tip");
            graphics.drawString(this.font, tip, left + (PAGE_W - this.font.width(tip)) / 2, top + PAGE_H - 16, 0xFF6B4A2C, false);
        }
    }

    private void renderNodeTooltip(GuiGraphics graphics, int left, int top, Journal journal,
                                   JournalEntries.Node node, int mouseX, int mouseY) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(node.titleKey()));
        boolean known = journal.knows(node.fact());
        if (known) {
            lines.add(Component.translatable(node.descKey()));
            if (node.ingredients() != null) {
                net.minecraft.network.chat.MutableComponent craft = Component.empty();
                for (int i = 0; i < node.ingredients().length; i++) {
                    if (i > 0) {
                        craft.append(" + ");
                    }
                    craft.append(node.ingredients()[i].getHoverName());
                }
                craft.append(" → ").append(node.result().getHoverName());
                lines.add(craft);
            }
        } else {
            lines.add(Component.translatable(node.hintKey()));
        }
        int tx = Math.min(mouseX + 6, left + PAGE_W - 110);
        int ty = Math.min(mouseY + 6, top + PAGE_H - 12 * lines.size() - 10);
        graphics.renderComponentTooltip(this.font, lines, tx, ty);
    }

    private int pageLeft() {
        return (this.width - PAGE_W - 48) / 2;
    }

    private int pageTop() {
        return (this.height - PAGE_H) / 2;
    }
}
