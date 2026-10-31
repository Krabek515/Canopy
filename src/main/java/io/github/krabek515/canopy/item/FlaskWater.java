package io.github.krabek515.canopy.item;

public enum FlaskWater {
    EMPTY(0f),
    MURKY(0.5f),
    CLEAN(1f);

    private final float modelValue;

    FlaskWater(float modelValue) {
        this.modelValue = modelValue;
    }

    public float modelValue() {
        return modelValue;
    }

    public static FlaskWater byName(String name) {
        if (name == null || name.isEmpty()) {
            return EMPTY;
        }
        try {
            return valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return EMPTY;
        }
    }
}
