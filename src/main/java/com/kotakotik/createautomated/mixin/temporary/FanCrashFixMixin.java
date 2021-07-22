package com.kotakotik.createautomated.mixin.temporary;

import com.simibubi.create.content.contraptions.components.fan.AirCurrentSound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AirCurrentSound.class)
@Pseudo
public abstract class FanCrashFixMixin extends TickableSound {
	protected FanCrashFixMixin(SoundEvent p_i46532_1_, SoundCategory p_i46532_2_) {
		super(p_i46532_1_, p_i46532_2_);
	}

	@Inject(method = "stop", at = @At("HEAD"), cancellable = true, remap = false)
	public void stop(CallbackInfo ci) {
		super.stop();
		ci.cancel();
	}
}
