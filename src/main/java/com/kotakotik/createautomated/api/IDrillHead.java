package com.kotakotik.createautomated.api;

import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.register.RecipeItems;
import com.kotakotik.createautomated.register.config.ModConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;

public interface IDrillHead extends IItemProvider, IForgeItem {
	HashMap<ResourceLocation, Item> cachedById = new HashMap<>();

	@Nullable
	static IDrillHead getFromId(ResourceLocation id) {
		Item item;
		if (cachedById.containsKey(id)) item = cachedById.get(id);
		else {
			item = ForgeRegistries.ITEMS.getValue(id);
			if (item.getRegistryName().equals(Items.AIR.getRegistryName())) item = RecipeItems.DRILL_HEAD.item.get();
			cachedById.put(id, item);
		}
		if (item instanceof IDrillHead) return (IDrillHead) item;
		return null;
	}

	int getDurability();

	default boolean takeDamage(OreExtractorTile tile, int amount) {
		if (ModConfig.SERVER.machines.extractor.unbreakableDrills.get()) return false;
		int unclampedOut = tile.durability - amount;
		int out = MathHelper.clamp(unclampedOut, 0, getDurability());
		tile.durability = out;
		return out == unclampedOut;
	}
}
