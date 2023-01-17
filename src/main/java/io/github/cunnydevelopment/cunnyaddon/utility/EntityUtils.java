package io.github.cunnydevelopment.cunnyaddon.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EntityUtils {
    private static final List<EntityType<?>> collidable = List.of(EntityType.ITEM, EntityType.TRIDENT, EntityType.ARROW, EntityType.AREA_EFFECT_CLOUD);
    private static final List<Direction> horizontals = List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST);

    public static List<Direction> getHorizontals() {
        return horizontals;
    }

    public static float getActualHealth() {
        assert mc.player != null;
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static TypeOfEntity getType(Entity entity) {
        switch (Registries.ENTITY_TYPE.getId(entity.getType()).getPath()) {
            case "creeper":
                if (((CreeperEntity) entity).isIgnited()) return TypeOfEntity.EXPLOSION;
            case "tnt":
                return TypeOfEntity.EXPLOSION_INANIMATE;
            case "end_crystal":
                return TypeOfEntity.EXPLOSION;
            case "llama":
                if (((LlamaEntity) entity).isAngry()) return TypeOfEntity.HOSTILE;
                return TypeOfEntity.RIDEABLE;
            case "wolf":
                assert mc.player != null;
                if (((WolfEntity) entity).getOwnerUuid() != null) {
                    if (((WolfEntity) entity).getOwnerUuid() == mc.player.getUuid()) return TypeOfEntity.PET;
                }
                if (((WolfEntity) entity).getAngryAt() == mc.player.getUuid())
                    return TypeOfEntity.HOSTILE;
                return TypeOfEntity.NEUTRAL;
            case "cat":
                if (((CatEntity) entity).getOwnerUuid() != null) return TypeOfEntity.PET;
                return TypeOfEntity.PASSIVE;
            case "enderman":
                if (((EndermanEntity) entity).isProvoked() && ((EndermanEntity) entity).getAngryAt() == Objects.requireNonNull(mc.player).getUuid())
                    return TypeOfEntity.HOSTILE;
                return TypeOfEntity.NEUTRAL;
            case "pig":
                if (((PigEntity) entity).isSaddled()) return TypeOfEntity.RIDEABLE;
                return TypeOfEntity.PASSIVE;
            case "mule":
            case "donkey":
            case "skeleton_horse":
            case "zombie_horse":
            case "horse":
                if (((AbstractHorseEntity) entity).getOwnerUuid() != null) return TypeOfEntity.PET;
                return TypeOfEntity.RIDEABLE;
            case "minecart":
            case "boat":
                return TypeOfEntity.RIDEABLE;
            default:
                if (!entity.isAlive() && entity.isLiving() && ((LivingEntity) entity).getHealth() <= 0)
                    return TypeOfEntity.UNKNOWN;
                if (entity.isPlayer()) {
                    if (entity.getUuid() == Objects.requireNonNull(mc.player).getUuid()) return TypeOfEntity.SELF;
                    return TypeOfEntity.PLAYER;
                }

                if (!entity.isAttackable()) {
                    return TypeOfEntity.INANIMATE;
                }

                if (entity.getType().getSpawnGroup().isPeaceful()) {
                    return TypeOfEntity.PASSIVE;
                } else {
                    return TypeOfEntity.HOSTILE;
                }
        }
    }

    public static boolean canPlaceIn(Entity entity) {
        return collidable.contains(entity.getType()) || entity.isRemoved() || entity.isSpectator();
    }

    public enum TypeOfEntity {
        RIDEABLE,
        NEUTRAL,
        PASSIVE,
        HOSTILE,
        INANIMATE,
        EXPLOSION_INANIMATE,
        EXPLOSION,
        UNKNOWN,
        PLAYER,
        PET,
        SELF
    }
}
