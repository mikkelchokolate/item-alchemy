package net.pitan76.itemalchemy.item;

import net.minecraft.entity.player.PlayerEntity;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffect;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffectInstance;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.CompatibleArmorItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.StatusEffectUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.entity.LivingEntityUtil;

public class DarkMatterArmor extends CompatibleArmorItem {

    private static final int EFFECT_INTERVAL = 60;
    private static final int EFFECT_DURATION = 100;

    private static final CompatStatusEffect NIGHT_VISION = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "night_vision"));
    private static final CompatStatusEffect FIRE_RESISTANCE = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "fire_resistance"));
    private static final CompatStatusEffect SPEED = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "speed"));

    public DarkMatterArmor(ArmorEquipmentType type, CompatibleItemSettings settings) {
        super(AlchemicalArmorMaterials.DARK_MATTER, type, settings);
    }

    @Override
    public void inventoryTick(InventoryTickEvent e, Options options) {
        if (e.isClient()) return;
        if (!(e.entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) e.entity;

        if (!isWornByPlayer(player)) return;

        if (type == ArmorEquipmentType.FEET) {
            EntityUtil.setFallDistance(player, 0);
            return;
        }

        if (WorldUtil.getTime(e.world) % EFFECT_INTERVAL != 0) return;

        if (type == ArmorEquipmentType.HEAD) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(NIGHT_VISION, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.CHEST) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(FIRE_RESISTANCE, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.LEGS) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(SPEED, EFFECT_DURATION, 0, true, false));
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isWornByPlayer(PlayerEntity player) {
        return ItemStackUtil.getItem(LivingEntityUtil.getEquippedStack(player, type.getSlot())) == this;
    }
}
