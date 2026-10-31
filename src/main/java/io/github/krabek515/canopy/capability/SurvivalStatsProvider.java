package io.github.krabek515.canopy.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class SurvivalStatsProvider implements ICapabilitySerializable<CompoundTag> {
    private final SurvivalStats stats = new SurvivalStats();
    private final LazyOptional<SurvivalStats> optional = LazyOptional.of(() -> stats);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CanopyCapabilities.SURVIVAL) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return stats.save();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        stats.load(tag);
    }
}
