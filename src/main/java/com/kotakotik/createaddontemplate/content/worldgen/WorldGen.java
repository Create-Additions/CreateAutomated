package com.kotakotik.createaddontemplate.content.worldgen;

import com.kotakotik.createaddontemplate.CreateAutomated;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldGen {
    //    public static ConfiguredFeature<?, ?> LAPIS_NODE;
    public static List<ConfiguredFeature<?, ?>> NODES = new ArrayList<>();
    protected static HashMap<String, FeatureToRegister> toReg = new HashMap<>();

    public static class FeatureToRegister {
        public final NonNullSupplier<Block> block;
        public final int veinSize;
        public final int minHeight;
        public final int maxHeight;
        public final int frequency;
        public final RuleTest test;
        public ConfiguredFeature<?, ?> registered;

        public FeatureToRegister(NonNullSupplier<Block> block, int veinSize, int minHeight, int maxHeight, int frequency, RuleTest test) {
            this.block = block;
            this.veinSize = veinSize;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.frequency = frequency;
            this.test = test;
        }

        public ConfiguredFeature<?, ?> create() {
            ConfiguredFeature<?, ?> f = Feature.ORE.configure(new OreFeatureConfig(
                    test, block.get().getDefaultState(), veinSize))
                    .decorate(Placement.RANGE.configure(new TopSolidRangeConfig(minHeight, 0, maxHeight)))
                    .spreadHorizontally()
                    .repeat(frequency);

            this.registered = f;
            return f;
        }
    }

    public static void register() {
    }

    public static void gen(BiomeLoadingEvent e) {
        BiomeGenerationSettingsBuilder gen = e.getGeneration();
        NODES.forEach(n -> gen.feature(GenerationStage.Decoration.SURFACE_STRUCTURES, n));
    }

    public static void reg(FMLCommonSetupEvent e) {
        toReg.forEach((name, toReg) -> register(name, toReg.create()));
    }

    public static FeatureToRegister add(String name, NonNullSupplier<Block> block, int veinSize, int minHeight, int maxHeight, int frequency, RuleTest test) {
        FeatureToRegister f = new FeatureToRegister(block, veinSize, minHeight, maxHeight, frequency, test);
        toReg.put(name, f);
        return f;
    }

    public static <F extends IFeatureConfig> ConfiguredFeature<F, ?> register(String name, ConfiguredFeature<F, ?> feature) {
        NODES.add(feature);
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CreateAutomated.modid + ":" + name, feature);
    }
}
