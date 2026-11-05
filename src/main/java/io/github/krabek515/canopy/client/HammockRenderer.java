package io.github.krabek515.canopy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.block.HammockBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

/**
 * Рисует гамак: плетёное полотно, провисшее между двумя концами, и узкие стропы у опор.
 * Модель строится от головного блока и накрывает оба — второй блок не рисует ничего.
 * Раскладка как у ванильной кровати: поворот вокруг центра по facing.
 */
public class HammockRenderer implements BlockEntityRenderer<HammockBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "hammock"), "main");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "textures/entity/hammock.png");

    private final ModelPart cloth;

    public HammockRenderer(BlockEntityRendererProvider.Context context) {
        this.cloth = context.bakeLayer(LAYER).getChild("cloth");
    }

    /** Полотно идёт от дальнего конца ножной части до края головной: z от -16 до 0 в пространстве головного блока. */
    public static LayerDefinition mesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition cloth = root.addOrReplaceChild("cloth", CubeListBuilder.create(), PartPose.ZERO);
        // сегменты провисания: края выше, середина ниже; полотно шириной 12 по центру
        float[][] seg = {
                {-16.0f, -12.8f, 13.2f}, {-12.8f, -9.6f, 12.3f}, {-9.6f, -6.4f, 11.7f},
                {-6.4f, -3.2f, 12.3f}, {-3.2f, 0.0f, 13.2f}
        };
        for (float[] s : seg) {
            cloth.addOrReplaceChild("seg" + (int) -s[0],
                    CubeListBuilder.create().texOffs(0, 0).addBox(2f, s[2], s[0], 12f, 0.8f, s[1] - s[0]),
                    PartPose.ZERO);
        }
        // стропы к опорам на обоих концах
        cloth.addOrReplaceChild("rope_foot", CubeListBuilder.create().texOffs(0, 32).addBox(7.5f, 13.5f, -15.5f, 1f, 2.5f, 1f), PartPose.ZERO);
        cloth.addOrReplaceChild("rope_head", CubeListBuilder.create().texOffs(8, 32).addBox(7.5f, 13.5f, -0.5f, 1f, 2.5f, 1f), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(HammockBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
            return;
        }
        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
        this.cloth.render(poseStack, consumer, light, overlay);
        poseStack.popPose();
    }
}
