package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModTags {
    public static class Blocks {
        public static final Tags.IOptionalNamedTag<Block> NODES = tag("nodes");

        protected static Tags.IOptionalNamedTag<Block> tag(String name, Supplier<Block>... defaults) {
            return BlockTags.createOptional(new ResourceLocation(CreateAutomated.modid, name), Arrays.stream(defaults).collect(Collectors.toSet()));
        }
    }

    public static class Items {
        public static final Tags.IOptionalNamedTag<Item> ORE_PIECES = tag("ore_pieces");
        public static final Tags.IOptionalNamedTag<Item> INGOT_PIECES = tag("ingot_pieces");
        public static final Tags.IOptionalNamedTag<Item> DRILL_HEADS = tag("drill_heads");

        protected static Tags.IOptionalNamedTag<Item> tag(String name, Supplier<Item>... defaults) {
            return ItemTags.createOptional(new ResourceLocation(CreateAutomated.modid, name), Arrays.stream(defaults).collect(Collectors.toSet()));
        }
    }
}
