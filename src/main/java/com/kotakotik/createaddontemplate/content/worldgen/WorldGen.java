package com.kotakotik.createaddontemplate.content.worldgen;

import com.kotakotik.createaddontemplate.CreateAutomated;
import com.kotakotik.createaddontemplate.register.ModBlocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class WorldGen {
    // TODO: tweak the spawn frequency
    // TODO: create a list and an easy way to register new node types
    public static ConfiguredFeature<?, ?> LAPIS_NODE;

    public static void register() {
    }

    public static void gen(BiomeLoadingEvent e) {
        BiomeGenerationSettingsBuilder gen = e.getGeneration();
        gen.feature(GenerationStage.Decoration.SURFACE_STRUCTURES, LAPIS_NODE);
    }

    public static void reg(FMLCommonSetupEvent e) {
        LAPIS_NODE = register("lapis_node", Feature.ORE.configure(new OreFeatureConfig(
                new TagMatchRuleTest(Tags.Blocks.DIRT), ModBlocks.LAPIS_NODE.getDefaultState(), 10))
                .decorate(Placement.RANGE.configure(new TopSolidRangeConfig(40, 0, 256)))
                .spreadHorizontally()
                .repeat(5));
    }

    public static <F extends IFeatureConfig> ConfiguredFeature<F, ?> register(String name, ConfiguredFeature<F, ?> feature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CreateAutomated.modid + ":" + name, feature);
    }
}
