package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.processing.picker.PickerItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;

public class ModItems {
	public static ItemGroup itemGroup = new ItemGroup(CreateAutomated.MODID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(AllItems.WRENCH.get());
		}
	};

	public static ItemEntry<PickerItem> PICKER;

	public static void register(CreateRegistrate registrate) {
		registrate.itemGroup(() -> itemGroup, "Create Automated");

		PICKER = registrate.item("picker", PickerItem::new).properties(p -> p.durability(32))
				.recipe((ctx, prov) -> {
					ShapedRecipeBuilder.shaped(ctx.get())
							.pattern("si ")
							.pattern("isi")
							.pattern(" is")
							.define('s', Tags.Items.STRING)
							.define('i', RecipeItems.IRON_BIT.itemTag)
							.unlockedBy("has_string", prov.hasItem(Tags.Items.STRING))
							.save(prov);
				})
				.register();
//        LAPIS_ORE_PIECE = registrate.item("lapis_ore_piece", Item::new).tag(ModTags.Items.ORE_PIECES).register();
//        IRON_INGOT_PIECE = registrate.item("iron_ingot_piece", Item::new).register();
	}
}
