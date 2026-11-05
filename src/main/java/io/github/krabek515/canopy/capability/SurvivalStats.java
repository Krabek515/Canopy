package io.github.krabek515.canopy.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public final class SurvivalStats {
    public static final float NEED_MAX = 20f;
    public static final float TEMP_MAX = 100f;
    public static final float COMFORT_LOW = 43f;
    public static final float COMFORT_HIGH = 57f;
    public static final float HAND_INJURED = 0.4f;
    public static final int WOUND_NONE = 0;
    public static final int WOUND_OPEN = 1;
    public static final int WOUND_INFECTED = 2;
    public static final int WOUND_FEVER = 3;

    private float thirst = NEED_MAX;
    private float temperature = 50f;
    private float wetness;
    private float stamina = NEED_MAX;
    private float hand;
    private int wound;
    private boolean woundDirty;
    private long woundClock;
    private long recoveringUntil;
    private long bleedingUntil;
    private long floodWarnUntil;
    private long floodWaveUntil;
    private long floodNext;
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

    public float hand() {
        return hand;
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

    public int wound() {
        return wound;
    }

    public boolean woundDirty() {
        return woundDirty;
    }

    public long woundClock() {
        return woundClock;
    }

    public boolean isBleeding(long gameTime) {
        return gameTime < bleedingUntil;
    }

    public long bleedingUntil() {
        return bleedingUntil;
    }

    public long floodWarnUntil() {
        return floodWarnUntil;
    }

    public long floodWaveUntil() {
        return floodWaveUntil;
    }

    public long floodNext() {
        return floodNext;
    }

    public void setBleedingUntil(long gameTime) {
        if (bleedingUntil != gameTime) {
            bleedingUntil = gameTime;
            dirty = true;
        }
    }

    public boolean startBleeding(long gameTime) {
        boolean fresh = !isBleeding(gameTime);
        long until = Math.max(bleedingUntil, gameTime + 1800L);
        if (until != bleedingUntil) {
            bleedingUntil = until;
            dirty = true;
        }
        return fresh;
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

    public void setHand(float value) {
        hand = mark(hand, Mth.clamp(value, 0f, 1f));
    }

    public boolean strainHand(float amount) {
        float before = hand;
        setHand(hand + amount);
        return before < HAND_INJURED && hand >= HAND_INJURED;
    }

    public void restHand(float amount) {
        if (hand > 0f) {
            setHand(hand - amount);
        }
    }

    public void setWound(int value) {
        int next = Mth.clamp(value, WOUND_NONE, WOUND_FEVER);
        if (wound != next) {
            wound = next;
            dirty = true;
        }
    }

    public void setWoundDirty(boolean value) {
        if (woundDirty != value) {
            woundDirty = value;
            dirty = true;
        }
    }

    public void assignWound(int stage, boolean dirtyWound, long now) {
        setWound(stage);
        setWoundDirty(stage != WOUND_NONE && dirtyWound);
        if (woundClock != now) {
            woundClock = now;
            dirty = true;
        }
    }

    public void setFloodWarnUntil(long gameTime) {
        if (floodWarnUntil != gameTime) {
            floodWarnUntil = gameTime;
            dirty = true;
        }
    }

    public void setFloodWaveUntil(long gameTime) {
        if (floodWaveUntil != gameTime) {
            floodWaveUntil = gameTime;
            dirty = true;
        }
    }

    public void setFloodNext(long gameTime) {
        floodNext = gameTime;
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
        hand = other.hand;
        wound = other.wound;
        woundDirty = other.woundDirty;
        woundClock = other.woundClock;
        recoveringUntil = other.recoveringUntil;
        bleedingUntil = other.bleedingUntil;
        floodWarnUntil = other.floodWarnUntil;
        floodWaveUntil = other.floodWaveUntil;
        floodNext = other.floodNext;
        dirty = true;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Thirst", thirst);
        tag.putFloat("Temperature", temperature);
        tag.putFloat("Wetness", wetness);
        tag.putFloat("Stamina", stamina);
        tag.putFloat("Hand", hand);
        tag.putInt("Wound", wound);
        tag.putBoolean("WoundDirty", woundDirty);
        tag.putLong("WoundClock", woundClock);
        tag.putLong("RecoveringUntil", recoveringUntil);
        tag.putLong("BleedingUntil", bleedingUntil);
        tag.putLong("FloodWarnUntil", floodWarnUntil);
        tag.putLong("FloodWaveUntil", floodWaveUntil);
        tag.putLong("FloodNext", floodNext);
        return tag;
    }

    public void load(CompoundTag tag) {
        thirst = tag.getFloat("Thirst");
        temperature = tag.contains("Temperature") ? tag.getFloat("Temperature") : 50f;
        wetness = tag.getFloat("Wetness");
        stamina = tag.contains("Stamina") ? tag.getFloat("Stamina") : NEED_MAX;
        hand = tag.getFloat("Hand");
        wound = Mth.clamp(tag.getInt("Wound"), WOUND_NONE, WOUND_FEVER);
        woundDirty = tag.getBoolean("WoundDirty");
        woundClock = tag.getLong("WoundClock");
        recoveringUntil = tag.getLong("RecoveringUntil");
        bleedingUntil = tag.getLong("BleedingUntil");
        floodWarnUntil = tag.getLong("FloodWarnUntil");
        floodWaveUntil = tag.getLong("FloodWaveUntil");
        floodNext = tag.getLong("FloodNext");
        dirty = true;
    }

    private float mark(float current, float next) {
        if (current != next) {
            dirty = true;
        }
        return next;
    }
}
