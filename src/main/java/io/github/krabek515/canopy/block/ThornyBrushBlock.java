package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.body.CanopyDamage;
import io.github.krabek515.canopy.body.HandWork;
import io.github.krabek515.canopy.body.WoundCare;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ThornyBrushBlock extends TaroBlock {
    public ThornyBrushBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getType() == EntityType.PARROT || entity.getType() == EntityType.PANDA || entity.getType() == EntityType.BAT) {
            return;
        }
        if (entity instanceof Player player && HandWork.holdingMachete(player)) {
            return;
        }
        entity.makeStuckInBlock(state, new Vec3(0.55, 0.75, 0.55));
        if (level.isClientSide || !(entity instanceof LivingEntity)) {
            return;
        }
        if (Math.abs(entity.getX() - entity.xOld) < 0.003 && Math.abs(entity.getZ() - entity.zOld) < 0.003) {
            return;
        }
        entity.hurt(CanopyDamage.source(level, CanopyDamage.THORNS), 1f);
        if (entity instanceof ServerPlayer player) {
            WoundCare.open(player);
        }
    }
}
