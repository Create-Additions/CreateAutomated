package com.kotakotik.createautomated.content.configUI;

import com.kotakotik.createautomated.CALocalization;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.gui.TextStencilElement;
import com.simibubi.create.foundation.gui.widgets.BoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber
public class CAConfigOptionRenderer {
	/**
	 * The offset to render the button, for addon compatibility.
	 * <p>
	 * You should probably +add to this instead of =setting it directly, again, for compatibility.
	 */
	public static int renderOffset = 60;

	public static Field widgetListField = ObfuscationReflectionHelper.findField(AbstractSimiScreen.class, "widgets");

	@SubscribeEvent
	public static void render(GuiScreenEvent.InitGuiEvent.Post event) throws IllegalAccessException {
		Screen screen = event.getGui();
		if (screen.getClass().equals(BaseConfigScreen.class)) {
			BaseConfigScreen configScreen = (BaseConfigScreen) screen;
			TextStencilElement commonText = new TextStencilElement(Minecraft.getInstance().font, CALocalization.CONFIG_TITLE.getComponent()).centered(true, true);
			BoxWidget caWidget;
			caWidget = new BoxWidget(configScreen.width / 2 - 100, configScreen.height / 2 + renderOffset, 200, 16).showingElement(commonText);

			caWidget.withCallback(() -> ScreenOpener.open(CAConfigScreen.forCA(configScreen)));
			commonText.withElementRenderer(BoxWidget.gradientFactory.apply(caWidget));
			// addWidget doesnt work for some reason
			List<Widget> widgets = (List<Widget>) widgetListField.get(configScreen);
			widgets.add(caWidget);
		}
	}
}
