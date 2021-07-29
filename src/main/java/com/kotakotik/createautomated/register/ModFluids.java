package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.fluids.FluidAttributes;

public class ModFluids {
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
