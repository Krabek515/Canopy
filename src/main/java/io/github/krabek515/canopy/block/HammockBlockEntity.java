package io.github.krabek515.canopy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Гамак — лежак ванильной кровати по механике, но свой тип сущности, чтобы рисовать ткань, а не матрас. */
public class HammockBlockEntity extends BedBlockEntity {
    public HammockBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, DyeColor.GREEN);
    }

    @Override
    public BlockEntityType<?> getType() {
        return CanopyBlockEntities.HAMMOCK.get();
    }
}
