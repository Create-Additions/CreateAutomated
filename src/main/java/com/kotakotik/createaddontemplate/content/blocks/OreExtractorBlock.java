package com.kotakotik.createaddontemplate.content.blocks;

import com.kotakotik.createaddontemplate.content.tiles.OreExtractorTile;
import com.kotakotik.createaddontemplate.register.ModTiles;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class OreExtractorBlock extends HorizontalKineticBlock implements ICogWheel, ITE<OreExtractorTile> {
    public OreExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face.equals(Direction.UP);
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
        return ModTiles.ORE_EXTRACTOR.create();
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.FAST;
    }

    @Override
    public Class<OreExtractorTile> getTileEntityClass() {
        return OreExtractorTile.class;
    }

    @Override
    public PushReaction getPushReaction(BlockState p_149656_1_) {
        return super.getPushReaction(p_149656_1_);
//        return PushReaction.BLOCK;
    }

    public static class ExtractorProgressBuilder {
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
            return takesMinutes(minutes * 60);
        }

        public int build() {
            return speed * ticks;
        }
    }
}
