package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.blocks.NodeBlock;
import com.kotakotik.createautomated.content.blocks.oreextractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.recipes.ModMixingRecipes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.DataIngredient;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RecipeItems {
    public static class ExtractableResource {
        public final String name;
        public CreateRegistrate reg;

        public BlockEntry<NodeBlock> NODE;
        public ItemEntry<Item> ORE_PIECE;

        List<BiConsumer<RegistrateRecipeProvider, ExtractableResource>> recipeGen = new ArrayList<>();
        public WorldGen.FeatureToRegister oreGenFeature;

        public ExtractableResource(String name, CreateRegistrate reg) {
            this.name = name;
            this.reg = reg;

            ORE_PIECE = reg.item(name + "_ore_piece", Item::new).recipe((ctx, prov) -> recipeGen.forEach(r -> r.accept(prov, this))).tag(ModTags.Items.ORE_PIECES).model(($, $$) -> {
            }).register();
        }

        public ExtractableResource oreGen(int veinSize, int minHeight, int maxHeight, int frequency, WorldGen.NodeDimension dim) {
            oreGenFeature = WorldGen.add(name + "_node", NODE::get, veinSize, minHeight, maxHeight, frequency, dim == WorldGen.NodeDimension.NETHER ? new TagMatchRuleTest(Tags.Blocks.NETHERRACK) : new TagMatchRuleTest(Tags.Blocks.DIRT), dim);
            return this;
//            WorldGen.register(name + "_node", f);
//            this.oreGenFeature = f;
//            return this;
        }

        public ExtractableResource oreGen(int veinSize, int frequency, WorldGen.NodeDimension dimension) {
            return oreGen(veinSize, 40, 256, frequency, dimension);
        }

        public ExtractableResource node(int minOre, int maxOre, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress) {
            NODE = reg.block(name + "_node", p -> new NodeBlock(p, ORE_PIECE, maxOre, minOre, progress.apply(new TopOreExtractorBlock.ExtractorProgressBuilder()))).blockstate(($, $$) -> {
            }).tag(ModTags.Blocks.NODES).item().model(($, $$) -> {
            }).build().register();
            return this;
        }

        public ExtractableResource node(int ore, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress) {
            return node(ore, ore, progress);
        }

        public ExtractableResource recipe(BiConsumer<RegistrateRecipeProvider, ExtractableResource> consumer) {
            recipeGen.add(consumer);
            return this;
        }
    }

    public static class IngotExtractableResource extends ExtractableResource {
        public final boolean autoCreateRecipes;
        public ItemEntry<Item> INGOT_PIECE;

        public IngotExtractableResource(String name, CreateRegistrate reg, boolean autoCreateRecipes, @Nullable NonNullSupplier<Item> ingot) {
            super(name, reg);
            this.autoCreateRecipes = autoCreateRecipes;

            INGOT_PIECE = reg.item(name + "_ingot_piece", Item::new)
                    .tag(ModTags.Items.INGOT_PIECES)
                    .model(($, $$) -> {
                    }).register();

            if (autoCreateRecipes) {
                recipe((provider, $) -> {
                    provider.smeltingAndBlasting(DataIngredient.items(ORE_PIECE.get()), INGOT_PIECE, 0);
                    MIXING.add(name + "_ingot_from_pieces", b -> {
                        for (int i = 0; i < 9; i++) {
                            b.require(INGOT_PIECE.get());
                        }
                        return b.output(ingot.get()).requiresHeat(HeatCondition.SUPERHEATED);
                    });
                });
            }
        }
    }

    public static class GlueableExtractableResource extends ExtractableResource {
        public GlueableExtractableResource(String name, CreateRegistrate reg, boolean autoCreateRecipes, NonNullSupplier<Item> output) {
            super(name, reg);

            if (autoCreateRecipes) {
                recipe((provider, $) -> MIXING.add(name + "_gluing", b -> {
                    for (int i = 0; i < 8; i++) {
                        b.require(ORE_PIECE.get());
                    }
                    return b.require(Items.SLIME_BALL).output(output.get()).requiresHeat(HeatCondition.SUPERHEATED);
                }));
            }
        }
    }

    public static ExtractableResource LAPIS_EXTRACTABLE;
    public static ExtractableResource IRON_EXTRACTABLE;
    public static ExtractableResource ZINC_EXTRACTABLE;
    public static ExtractableResource GOLD_EXTRACTABLE;
    public static ExtractableResource COPPER_EXTRACTABLE;
    public static ExtractableResource CINDER_FLOUR_EXTRACTABLE;

    public static void register(CreateRegistrate registrate) {
        LAPIS_EXTRACTABLE = new GlueableExtractableResource("lapis", registrate, true, () -> Items.LAPIS_LAZULI)
                .node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(30).build())
                .oreGen(10, 4, WorldGen.NodeDimension.OVERWORLD);

        IRON_EXTRACTABLE = new IngotExtractableResource("iron", registrate, true, () -> Items.IRON_INGOT)
                .node(0, 2, (b) -> b.atSpeedOf(128).takesMinutes(3).build())
                .oreGen(4, 1, WorldGen.NodeDimension.OVERWORLD);

        ZINC_EXTRACTABLE = new IngotExtractableResource("zinc", registrate, true, AllItems.ZINC_INGOT)
                .node(1, 2, (b) -> b.atSpeedOf(128).takesSeconds(40).build())
                .oreGen(9, 2, WorldGen.NodeDimension.OVERWORLD);

        GOLD_EXTRACTABLE = new IngotExtractableResource("gold", registrate, true, () -> Items.GOLD_INGOT)
                .node(0, 2, (b) -> b.atSpeedOf(128).takesMinutes(2).build())
                .oreGen(6, 1, WorldGen.NodeDimension.OVERWORLD);

        COPPER_EXTRACTABLE = new IngotExtractableResource("copper", registrate, true, AllItems.COPPER_INGOT)
                .node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(40).build())
                .oreGen(16, 2, WorldGen.NodeDimension.OVERWORLD);

        CINDER_FLOUR_EXTRACTABLE = new ExtractableResource("cinder_flour", registrate)
                .node(1, 3, b -> b.atSpeedOf(128).takesSeconds(15).build())
                .oreGen(16, 0, 256, 10, WorldGen.NodeDimension.NETHER)
                .recipe((prov, r) -> {
                    MIXING.add("cinder_flour_from_ore_pieces", b -> {
                        for (int i = 0; i < 9; i++) {
                            b.require(r.ORE_PIECE.get());
                        }
                        return b.requiresHeat(HeatCondition.NONE).output(AllItems.CINDER_FLOUR.get());
                    });
                    MIXING.add("netherrack_from_cinder_flour", b -> {
                        for (int i = 0; i < 5; i++) {
                            b.require(AllItems.CINDER_FLOUR.get());
                        }
                        return b.requiresHeat(HeatCondition.NONE).output(Blocks.NETHERRACK);
                    });
                });
    }

    public static ModMixingRecipes MIXING;

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        MIXING = new ModMixingRecipes(gen);
        gen.addProvider(new IDataProvider() {
            public String getName() {
                return "CreateAutomated Processing Recipes";
            }

            public void act(DirectoryCache dc) throws IOException {
                MIXING.act(dc);
            }
        });
    }
}
