package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.kinetic.picker.PickerItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItems {
	public static ItemGroup itemGroup = new ItemGroup(CreateAutomated.modid) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(AllItems.WRENCH.get());
		}
	};

	public static ItemEntry<PickerItem> PICKER;

	public static void register(CreateRegistrate registrate) {
		registrate.itemGroup(() -> itemGroup, "Create Automated");

		PICKER = registrate.item("picker", PickerItem::new).properties(p -> p.maxDamage(32)).register();
//        LAPIS_ORE_PIECE = registrate.item("lapis_ore_piece", Item::new).tag(ModTags.Items.ORE_PIECES).register();
//        IRON_INGOT_PIECE = registrate.item("iron_ingot_piece", Item::new).register();
	}
}
