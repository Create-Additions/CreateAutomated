package com.kotakotik.createautomated.compat.kubejs;

import com.kotakotik.createautomated.CreateAutomated;

import static dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent.EVENT;

public class CAKubeJS {
	public CAKubeJS() {
		EVENT.register(event -> {
			event.register(CreateAutomated.asResource("extracting"), ExtractingJS::new);
			event.register(CreateAutomated.asResource("picking"), PickingJS::new);
		});
	}
}
