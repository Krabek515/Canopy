package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Один рендерер на всех зверей: модель, анимация и текстура берутся по имени
 * из geo/entity, animations/entity и textures/entity. Кость head поворачивается за взглядом.
 */
public class BeastRenderer<T extends LivingEntity & GeoEntity> extends GeoEntityRenderer<T> {
    public BeastRenderer(EntityRendererProvider.Context context, String name, float shadow) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, name), true));
        this.shadowRadius = shadow;
    }
}
