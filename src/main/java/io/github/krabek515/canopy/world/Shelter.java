package io.github.krabek515.canopy.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class Shelter {
    private Shelter() {
    }

    public static boolean covered(Player player) {
        return solidAbove(player.level(), player.blockPosition());
    }

    public static boolean solidAbove(Level level, BlockPos origin) {
        for (int dy = 1; dy <= 8; dy++) {
            BlockPos pos = origin.above(dy);
            BlockState state = level.getBlockState(pos);
            if (!state.getCollisionShape(level, pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean underLeaves(Player player) {
        Level level = player.level();
        BlockPos origin = player.blockPosition();
        for (int dy = 1; dy <= 8; dy++) {
            if (level.getBlockState(origin.above(dy)).is(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }
}
