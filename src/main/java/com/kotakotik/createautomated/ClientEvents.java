package com.kotakotik.createautomated;


import com.kotakotik.createautomated.register.ModConfigAnnotations;
import com.simibubi.create.foundation.config.ui.ConfigHelper;
import com.simibubi.create.foundation.config.ui.ConfigScreenList;
import com.simibubi.create.foundation.config.ui.SubMenuConfigScreen;
import com.simibubi.create.foundation.config.ui.entries.ValueEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	public static Field valueField = ObfuscationReflectionHelper.findField(ValueEntry.class, "value");
	public static Field specField = ObfuscationReflectionHelper.findField(ValueEntry.class, "spec");

	public static Consumer<ConfigScreenList> readAnnotations(GuiScreenEvent.InitGuiEvent.Post event, SubMenuConfigScreen screen, ConfigScreenList list, ConfigScreenList.Entry entry, Map<String, String> annotations) {
		Consumer<ConfigScreenList> toRet = (c) -> {
		};
		if (annotations.containsKey(ModConfigAnnotations.Hidden.NAME)) {
			toRet = toRet.andThen(l -> {
				String name;
				try {
					name = String.join(".", ((ForgeConfigSpec.ConfigValue<?>) valueField.get(entry)).getPath());
				} catch (IllegalAccessException exception) {
					exception.printStackTrace();
					name = "COULD NOT FIND NAME " + exception.getClass().getSimpleName();
				}
				CreateAutomated.LOGGER.info("Config value " + name + " hidden!" + "\n" +
						annotations.get(ModConfigAnnotations.Hidden.NAME));
				list.children().remove(entry);
			});
		}
		return toRet;
	}

	@SubscribeEvent
	public static void guiInitEvent(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof SubMenuConfigScreen) {
			SubMenuConfigScreen screen = (SubMenuConfigScreen) event.getGui();
			screen.children().stream().filter(c -> c instanceof ConfigScreenList).findFirst().ifPresent(c -> {
				ConfigScreenList list = (ConfigScreenList) c;
				Consumer<ConfigScreenList> after = (s) -> {
				};
				for (ConfigScreenList.Entry child : list.children()) {
					if (child instanceof ValueEntry) {
						try {
							ForgeConfigSpec.ValueSpec v = (ForgeConfigSpec.ValueSpec) specField.get(child);
							after = after.andThen(
									readAnnotations(event, screen, list, child, ConfigHelper.readMetadataFromComment(new ArrayList<>(Arrays.asList(v.getComment().split("\n"))))
											.getSecond())
							);
						} catch (IllegalAccessException e) {
							CreateAutomated.LOGGER.warn("Could not access annotations for value entry, see error below:");
							e.printStackTrace();
						}
					}
//						try {
//							annotations = (Map<String, String>) annotationsField.get(child);
//						} catch (IllegalAccessException illegalAccessException) {
//							CreateAutomated.LOGGER.warn("Could not access annotations for value entry, see error below:");
//							illegalAccessException.printStackTrace();
//						}

					;
				}
				after.accept(list);
			});
		}
	}
}
