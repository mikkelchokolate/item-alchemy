package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarColorArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarStepArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;

public class KleinStar extends CompatItem {

    public static final String EMC_KEY = "stored_emc";

    public enum Tier {
        EIN(50_000L),
        ZWEI(200_000L),
        DREI(800_000L),
        VIER(3_200_000L),
        SPHERE(12_800_000L),
        OMEGA(51_200_000L);

        private final long maxEmc;

        Tier(long maxEmc) {
            this.maxEmc = maxEmc;
        }

        public long getMaxEmc() {
            return maxEmc;
        }
    }

    private final Tier tier;

    public KleinStar(Tier tier, CompatibleItemSettings settings) {
        super(settings);
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    public long getMaxEmc() {
        return tier.getMaxEmc();
    }

    public static long getStoredEmc(ItemStack stack) {
        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);
        if (!NbtUtil.has(nbt, EMC_KEY)) {
            return 0;
        }
        return NbtUtil.getLong(nbt, EMC_KEY);
    }

    public static void setStoredEmc(ItemStack stack, long emc) {
        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);
        NbtUtil.set(nbt, EMC_KEY, emc);
        CustomDataUtil.set(stack, ItemAlchemy.MOD_ID, nbt);
    }

    /**
     * Adds EMC to the Klein Star. Returns the amount actually added.
     */
    public static long addEmc(ItemStack stack, long amount) {
        if (!(ItemStackUtil.getItem(stack) instanceof KleinStar)) return 0;
        KleinStar star = (KleinStar) ItemStackUtil.getItem(stack);

        long stored = getStoredEmc(stack);
        long max = star.getMaxEmc();
        long space = max - stored;
        long toAdd = Math.min(amount, space);

        if (toAdd > 0) {
            setStoredEmc(stack, stored + toAdd);
        }
        return toAdd;
    }

    /**
     * Extracts EMC from the Klein Star. Returns the amount actually extracted.
     */
    public static long extractEmc(ItemStack stack, long amount) {
        long stored = getStoredEmc(stack);
        long toExtract = Math.min(amount, stored);

        if (toExtract > 0) {
            setStoredEmc(stack, stored - toExtract);
        }
        return toExtract;
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        super.appendTooltip(e);
        long stored = getStoredEmc(e.getStack());
        long max = getMaxEmc();
        e.addTooltip(TextUtil.literal("EMC: " + formatNumber(stored) + " / " + formatNumber(max)));
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemBarStepArgs args) {
        long stored = getStoredEmc(args.getStack());
        long max = getMaxEmc();
        if (max == 0) return 0;
        return (int) (13L * stored / max);
    }

    @Override
    public int getItemBarColor(ItemBarColorArgs args) {
        // Cyan/teal color for EMC bar
        return 0x00CCFF;
    }

    private static String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        }
        return String.valueOf(number);
    }
}
