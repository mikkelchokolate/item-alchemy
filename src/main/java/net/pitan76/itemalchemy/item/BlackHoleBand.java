package net.pitan76.itemalchemy.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;

import java.util.List;

public class BlackHoleBand extends Ring {

    private static final double RANGE = 7.0;

    public BlackHoleBand(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(InventoryTickEvent e) {
        super.inventoryTick(e);
        if (e.isClient()) return;
        if (!(e.entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) e.entity;
        Vector3d playerPos = EntityUtil.getPosM(player);

        List<ItemEntity> items = WorldUtil.getEntitiesByClass(
                e.world, ItemEntity.class, playerPos, RANGE
        );

        for (ItemEntity itemEntity : items) {
            Vector3d itemPos = EntityUtil.getPosM(itemEntity);
            double dx = playerPos.getX() - itemPos.getX();
            double dy = playerPos.getY() - itemPos.getY();
            double dz = playerPos.getZ() - itemPos.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                double speed = 0.45;
                double nx = dx / distance * speed;
                double ny = dy / distance * speed;
                double nz = dz / distance * speed;
                EntityUtil.setVelocity(itemEntity, nx, ny, nz);
                EntityUtil.setVelocityModified(itemEntity, true);
            } else {
                EntityUtil.setVelocity(itemEntity, 0, 0, 0);
                EntityUtil.setPos(itemEntity, playerPos.getX(), playerPos.getY(), playerPos.getZ());
                itemEntity.onPlayerCollision(player);
            }
        }
    }
}
