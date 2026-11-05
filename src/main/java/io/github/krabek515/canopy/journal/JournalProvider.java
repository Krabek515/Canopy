package io.github.krabek515.canopy.journal;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class JournalProvider implements ICapabilitySerializable<CompoundTag> {
    private final Journal journal = new Journal();
    private final LazyOptional<Journal> optional = LazyOptional.of(() -> this.journal);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CanopyCapabilities.JOURNAL) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.journal.save();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.journal.load(tag);
    }
}
