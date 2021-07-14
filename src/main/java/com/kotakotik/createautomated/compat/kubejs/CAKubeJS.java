package com.kotakotik.createautomated.compat.kubejs;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.Create;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

import static dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent.EVENT;

public class CAKubeJS {
	public CAKubeJS() {
		EVENT.register(event -> {
			event.register(CreateAutomated.asResource("extracting"), ExtractingJS::new);
		});
	}
}
