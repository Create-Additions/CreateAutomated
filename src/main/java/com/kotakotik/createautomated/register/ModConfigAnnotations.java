package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.foundation.config.ui.ConfigAnnotations;
import org.apache.commons.lang3.StringUtils;

public class ModConfigAnnotations {
	public static Hidden hidden(String description) {
		return new Hidden(description);
	}

	public static String hide(String description) {
		return hidden(description).asComment();
	}

	public static String hide() {
		return hide("Hidden without description");
	}

	public static String screenUnsupported(String whatIs) {
		return ModConfigAnnotations.hide("Create's config screen does not support " + whatIs);
	}

	protected static String name(String name) {
		return StringUtils.capitalize(CreateAutomated.MODID) + StringUtils.capitalize(name);
	}

	public static class Hidden implements ConfigAnnotations.ConfigAnnotation {
		public static final String NAME = name("Hidden");

		private final String description;

		protected Hidden(String description) {
			this.description = description;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public String getValue() {
			return description;
		}
	}
}
