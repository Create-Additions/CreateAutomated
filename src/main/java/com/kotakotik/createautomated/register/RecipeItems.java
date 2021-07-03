package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.content.blocks.NodeBlock;
import com.kotakotik.createautomated.content.blocks.oreextractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.recipes.ModMixingRecipes;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.DataIngredient;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class RecipeItems {
    public static class ExtractableResource {
        public final String name;
        public CreateRegistrate reg;

        public BlockEntry<NodeBlock> NODE;
        public ItemEntry<Item> ORE_PIECE;

        List<Consumer<RegistrateRecipeProvider>> recipeGen = new ArrayList<>();
        public WorldGen.FeatureToRegister oreGenFeature;

        public ExtractableResource(String name, CreateRegistrate reg) {
            this.name = name;
            this.reg = reg;

            ORE_PIECE = reg.item(name + "_ore_piece", Item::new).recipe((ctx, prov) -> recipeGen.forEach(r -> r.accept(prov))).tag(ModTags.Items.ORE_PIECES).model(($, $$) -> {
            }).register();
        }

        public ExtractableResource oreGen(int veinSize, int minHeight, int maxHeight, int frequency) {
            oreGenFeature = WorldGen.add(name + "_node", NODE::get, veinSize, minHeight, maxHeight, frequency, new TagMatchRuleTest(Tags.Blocks.DIRT));
            return this;
//            WorldGen.register(name + "_node", f);
//            this.oreGenFeature = f;
//            return this;
        }

        public ExtractableResource oreGen(int veinSize, int frequency) {
            return oreGen(veinSize, 40, 256, frequency);
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

        public ExtractableResource recipe(Consumer<RegistrateRecipeProvider> consumer) {
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
                recipe(provider -> {
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
                recipe(provider -> MIXING.add(name + "_gluing", b -> {
                    for (int i = 0; i < 8; i++) {
                        b.require(ORE_PIECE.get());
                    }
                    return b.require(Items.SLIME_BALL).output(output.get()).requiresHeat(HeatCondition.SUPERHEATED);
                }));
            }
        }
    }

    public static ExtractableResource LAPIS_EXTRACTABLE;

    public static void register(CreateRegistrate registrate) {
        LAPIS_EXTRACTABLE = new GlueableExtractableResource("lapis", registrate, true, () -> Items.LAPIS_LAZULI)
                .node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(10).build())
                .oreGen(10, 5);
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
