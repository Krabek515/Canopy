package io.github.krabek515.canopy.block;

import net.minecraft.util.StringRepresentable;

public enum DeckKind implements StringRepresentable {
    BOG,
    STAKES,
    SNAKE;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(java.util.Locale.ROOT);
    }
}
