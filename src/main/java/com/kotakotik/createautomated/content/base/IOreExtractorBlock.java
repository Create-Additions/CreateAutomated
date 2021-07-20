package com.kotakotik.createautomated.content.base;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.Arrays;
import java.util.List;

public interface IOreExtractorBlock extends IWrenchable {
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

	class ExtractorProgressBuilder {
		public int speed;
		public int ticks;

		public ExtractorProgressBuilder atSpeedOf(int speed) {
			this.speed = speed;
			return this;
		}

		public static ExtractorProgressBuilder atSpeedOfS(int speed) {
			return new ExtractorProgressBuilder().atSpeedOf(speed);
		}

		public ExtractorProgressBuilder takesTicks(int ticks) {
			this.ticks = ticks;
			return this;
		}

		public ExtractorProgressBuilder takesSeconds(int seconds) {
			return takesTicks(seconds * 20);
		}

		public ExtractorProgressBuilder takesMinutes(int minutes) {
			return takesSeconds(minutes * 60);
		}

		public int build() {
			return speed * ticks;
		}
	}
}
