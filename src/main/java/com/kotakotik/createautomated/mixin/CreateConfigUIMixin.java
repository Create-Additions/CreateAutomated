package com.kotakotik.createautomated.mixin;

import com.kotakotik.createautomated.CALocalization;
import com.kotakotik.createautomated.content.configUI.CAConfigScreen;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.TextStencilElement;
import com.simibubi.create.foundation.gui.widgets.BoxWidget;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseConfigScreen.class)
public abstract class CreateConfigUIMixin extends AbstractSimiScreen {
	@Shadow
	protected abstract void linkTo(Screen screen);

	@Inject(method = "init", at = @At("RETURN"))
	public void mixin(CallbackInfo ci) {
		if (getClass().equals(BaseConfigScreen.class)) {
			TextStencilElement commonText = new TextStencilElement(minecraft.font, CALocalization.CONFIG_TITLE.getComponent()).centered(true, true);
			BoxWidget caWidget;
			widgets.add(caWidget = new BoxWidget(width / 2 - 100, height / 2 + 60, 200, 16).showingElement(commonText));

			caWidget.withCallback(() -> linkTo(CAConfigScreen.forCA(this)));
			commonText.withElementRenderer(BoxWidget.gradientFactory.apply(caWidget));
		}
	}
}
