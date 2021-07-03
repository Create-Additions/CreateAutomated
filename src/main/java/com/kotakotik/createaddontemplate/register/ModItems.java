package com.kotakotik.createaddontemplate.register;

import com.kotakotik.createaddontemplate.CreateAutomated;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItems {
    public static ItemGroup itemGroup = new ItemGroup(CreateAutomated.modid) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(AllItems.WRENCH.get());
        }
    };

    public static ItemEntry<Item> LAPIS_ORE_PIECE;
    public static ItemEntry<Item> IRON_INGOT_PIECE;

    public static void register(CreateRegistrate registrate) {
        registrate.itemGroup(() -> itemGroup, "Create Automated");
        RecipeItems.register(registrate);

//        LAPIS_ORE_PIECE = registrate.item("lapis_ore_piece", Item::new).tag(ModTags.Items.ORE_PIECES).register();
//        IRON_INGOT_PIECE = registrate.item("iron_ingot_piece", Item::new).register();
    }
}
