package io.github.krabek515.canopy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public final class SurvivalStats {
    public static final float NEED_MAX = 20f;
    public static final float TEMP_MAX = 100f;
    public static final float COMFORT_LOW = 43f;
    public static final float COMFORT_HIGH = 57f;

    private float thirst = NEED_MAX;
    private float temperature = 50f;
    private float wetness;
    private float stamina = NEED_MAX;
    private long recoveringUntil;
    private boolean nearHeat;
    private boolean dirty = true;

    public float thirst() {
        return thirst;
    }

    public float temperature() {
        return temperature;
    }

    public float wetness() {
        return wetness;
    }

    public float stamina() {
        return stamina;
    }

    public long recoveringUntil() {
        return recoveringUntil;
    }

    public boolean nearHeat() {
        return nearHeat;
    }

    public boolean isRecovering(long gameTime) {
        return gameTime < recoveringUntil;
    }

    public void setThirst(float value) {
        thirst = mark(thirst, Mth.clamp(value, 0f, NEED_MAX));
    }

    public void setTemperature(float value) {
        temperature = mark(temperature, Mth.clamp(value, 0f, TEMP_MAX));
    }

    public void setWetness(float value) {
        wetness = mark(wetness, Mth.clamp(value, 0f, 1f));
    }

    public void setStamina(float value) {
        stamina = mark(stamina, Mth.clamp(value, 0f, NEED_MAX));
    }

    public void setRecoveringUntil(long gameTime) {
        if (recoveringUntil != gameTime) {
            recoveringUntil = gameTime;
            dirty = true;
        }
    }

    public void setNearHeat(boolean nearHeat) {
        this.nearHeat = nearHeat;
    }

    public void drink(float amount) {
        setThirst(thirst + amount);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void copyFrom(SurvivalStats other) {
        thirst = other.thirst;
        temperature = other.temperature;
        wetness = other.wetness;
        stamina = other.stamina;
        recoveringUntil = other.recoveringUntil;
        dirty = true;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Thirst", thirst);
        tag.putFloat("Temperature", temperature);
        tag.putFloat("Wetness", wetness);
        tag.putFloat("Stamina", stamina);
        tag.putLong("RecoveringUntil", recoveringUntil);
        return tag;
    }

    public void load(CompoundTag tag) {
        thirst = tag.getFloat("Thirst");
        temperature = tag.contains("Temperature") ? tag.getFloat("Temperature") : 50f;
        wetness = tag.getFloat("Wetness");
        stamina = tag.contains("Stamina") ? tag.getFloat("Stamina") : NEED_MAX;
        recoveringUntil = tag.getLong("RecoveringUntil");
        dirty = true;
    }

    private float mark(float current, float next) {
        if (current != next) {
            dirty = true;
        }
        return next;
    }
}
