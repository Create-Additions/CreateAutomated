package com.kotakotik.createautomated.content.configUI;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.register.config.ModCommonConfig;
import com.kotakotik.createautomated.register.config.ModServerConfig;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nonnull;

public class CAConfigScreen extends BaseConfigScreen {
	public static CAConfigScreen forCA(Screen parent) {
		return (CAConfigScreen) new CAConfigScreen(parent, CreateAutomated.MODID)
				.withTitles("Client Settings", "World Generation Settings", "Gameplay Settings")
				.withSpecs(null, ModCommonConfig.SPEC, ModServerConfig.SPEC);
	}

	public CAConfigScreen(Screen parent, @Nonnull String modID) {
		super(parent, modID);
	}
}
