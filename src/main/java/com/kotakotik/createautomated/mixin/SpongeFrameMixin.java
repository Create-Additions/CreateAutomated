package com.kotakotik.createautomated.mixin;

import com.kotakotik.createautomated.content.processing.spongeFrame.SpongeFrameBlock;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InWorldProcessing.Type.class)
class SpongeFrameMixin {
	@Inject(method = "byBlock", at = @At(value = "HEAD"), cancellable = true, remap = false)
	private static void mixin(IBlockReader block, BlockPos pos, CallbackInfoReturnable<InWorldProcessing.Type> cir) {
		Block b = block.getBlockState(pos).getBlock();
		if (b instanceof SpongeFrameBlock && ((SpongeFrameBlock) b).isWet) {
			cir.setReturnValue(InWorldProcessing.Type.SPLASHING);
		}
	}
}
