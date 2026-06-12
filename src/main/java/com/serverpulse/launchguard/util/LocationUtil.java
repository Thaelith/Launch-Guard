package com.serverpulse.launchguard.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.Set;

public final class LocationUtil {

    public enum SafetyStatus {
        SAFE,
        UNSAFE,
        UNKNOWN
    }

    private static final Set<Material> UNSAFE_BLOCK = EnumSet.of(
            Material.LAVA,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.CACTUS,
            Material.SWEET_BERRY_BUSH,
            Material.COBWEB,
            Material.POWDER_SNOW
    );

    private static final Set<Material> UNSAFE_STAND = EnumSet.of(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.WATER,
            Material.LAVA,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.CACTUS,
            Material.MAGMA_BLOCK,
            Material.POWDER_SNOW,
            Material.COBWEB,
            Material.SWEET_BERRY_BUSH,
            Material.WITHER_ROSE,
            Material.POINTED_DRIPSTONE,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE
    );

    private LocationUtil() {
    }

    public static boolean isChunkLoadedForLocation(Location location) {
        if (location == null) return false;
        World world = location.getWorld();
        if (world == null) return false;
        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;
        return world.isChunkLoaded(cx, cz);
    }

    public static SafeResult checkSafety(Location location) {
        if (location == null) {
            return new SafeResult(SafetyStatus.UNSAFE, "Null location");
        }

        World world = location.getWorld();
        if (world == null) {
            return new SafeResult(SafetyStatus.UNSAFE, "World not loaded");
        }

        if (!isChunkLoadedForLocation(location)) {
            return new SafeResult(SafetyStatus.UNKNOWN, "Chunk not loaded");
        }

        Block block = location.getBlock();
        Material blockType = block.getType();
        if (UNSAFE_BLOCK.contains(blockType)) {
            return new SafeResult(SafetyStatus.UNSAFE, "Block at location is " + blockType.name());
        }

        Block headBlock = block.getRelative(BlockFace.UP);
        if (UNSAFE_BLOCK.contains(headBlock.getType())) {
            return new SafeResult(SafetyStatus.UNSAFE, "Block above location is " + headBlock.getType().name());
        }

        Block standBlock = block.getRelative(BlockFace.DOWN);
        if (UNSAFE_STAND.contains(standBlock.getType())) {
            return new SafeResult(SafetyStatus.UNSAFE, "Block below location is " + standBlock.getType().name());
        }

        return new SafeResult(SafetyStatus.SAFE, null);
    }

    public record SafeResult(SafetyStatus status, String reason) {
        public boolean isSafe() {
            return status == SafetyStatus.SAFE;
        }

        public boolean isUnknown() {
            return status == SafetyStatus.UNKNOWN;
        }

        public boolean isUnsafe() {
            return status == SafetyStatus.UNSAFE;
        }
    }
}
