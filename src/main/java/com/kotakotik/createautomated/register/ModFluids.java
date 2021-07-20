package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.FluidEntry;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class ModFluids {
	public static FluidEntry<ForgeFlowingFluid.Flowing> MOLTEN_DIAMOND;

	protected static ResourceLocation fluidRes(String name, String type) {
		return CreateAutomated.asResource("fluid/"+name+"_"+type);
	}

	protected static ResourceLocation still(String name) {
		return fluidRes(name, "still");
	}

	protected static ResourceLocation flow(String name) {
		return fluidRes(name, "flow");
	}

	public static void register(CreateRegistrate registrate) {
		MOLTEN_DIAMOND = registrate.fluid("molten_diamond", still("molten_diamond"), flow("molten_diamond"), NoColorFluidAttributes::new)
				.attributes(b -> b.viscosity(500)
						.density(1400))
				.properties(p -> p.levelDecreasePerBlock(2)
						.tickRate(25)
						.slopeFindDistance(3)
						.explosionResistance(100f))
				.removeTag(FluidTags.WATER)
				.tag(FluidTags.LAVA)
				.bucket()
				.recipe((ctx, prov) -> {
					RecipeItems.MIXING.add("molten_diamond", b -> {
						for (int i = 0; i < 3; i++) {
							b.require(RecipeItems.DIAMOND_BIT.itemTag);
						}
						return b.requiresHeat(HeatCondition.SUPERHEATED).output(MOLTEN_DIAMOND.get(), 150);
					});
					RecipeItems.MIXING.add("molten_diamond_from_ingot", b -> b.require(Tags.Items.GEMS_DIAMOND).requiresHeat(HeatCondition.SUPERHEATED).output(MOLTEN_DIAMOND.get(), 1000));
				})
				.properties(p -> p.stacksTo(1))
				.build()
				.register();
	}

	public static class NoColorFluidAttributes extends FluidAttributes {

		protected NoColorFluidAttributes(FluidAttributes.Builder builder, Fluid fluid) {
			super(builder, fluid);
		}

		@Override
		public int getColor(IBlockDisplayReader world, BlockPos pos) {
			return 0x00ffffff;
		}
	}
}
