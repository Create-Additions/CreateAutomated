package com.kotakotik.createautomated.content.base;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.List;

public interface IOreExtractorBlock extends IWrenchable, IForgeBlock {
	default PushReaction pushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	boolean isTop();

	static Direction getDirectionToOther(boolean isTop) {
		return isTop ? Direction.DOWN : Direction.UP;
	}

	default BlockState checkForOther(BlockState state, Direction direction, BlockState updatingState, IWorld world, BlockPos pos, BlockPos updatingPos, boolean drop) {
		if (direction == getDirectionToOther(isTop())) {
			if (!(updatingState.getBlock() instanceof IOreExtractorBlock)) {
				if (drop) {
					return Blocks.AIR.defaultBlockState();
				}
				world.destroyBlock(pos, false, null);
			}
		}
		return state;
	}

	default List<String> nbtList() {
		return Arrays.asList(
				"DrillId",
				"Durability",
				"MaxDurability"
		);
	}

	@ZenRegister
	@ZenCodeType.Name("mods.createautomated.RequiredProgress")
	class ExtractorProgressBuilder {
		public int speed = 128;
		public int ticks = 0;

		@ZenCodeType.Method
		public ExtractorProgressBuilder atSpeedOf(int speed) {
			this.speed = speed;
			return this;
		}

		public static ExtractorProgressBuilder atSpeedOfS(int speed) {
			return new ExtractorProgressBuilder().atSpeedOf(speed);
		}

		@ZenCodeType.Method
		public ExtractorProgressBuilder takesTicks(int ticks) {
			this.ticks += ticks;
			return this;
		}

		@ZenCodeType.Method
		public ExtractorProgressBuilder takesSeconds(int seconds) {
			return takesTicks(seconds * 20);
		}

		@ZenCodeType.Method
		public ExtractorProgressBuilder takesMinutes(int minutes) {
			return takesSeconds(minutes * 60);
		}

		@ZenCodeType.Constructor
		public ExtractorProgressBuilder() {
		}

		@ZenCodeType.Method
		public int build() {
			return speed * ticks;
		}
	}
}
