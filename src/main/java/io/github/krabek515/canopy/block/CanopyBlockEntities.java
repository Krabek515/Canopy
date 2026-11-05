package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Canopy.MOD_ID);
    public static final RegistryObject<BlockEntityType<HammockBlockEntity>> HAMMOCK = BLOCK_ENTITIES.register("hammock",
            () -> BlockEntityType.Builder.of(HammockBlockEntity::new, CanopyBlocks.HAMMOCK.get()).build(null));

    private CanopyBlockEntities() {
    }
}
