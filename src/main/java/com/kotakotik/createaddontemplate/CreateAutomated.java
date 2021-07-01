package com.kotakotik.createaddontemplate;

import com.kotakotik.createaddontemplate.register.ModBlocks;
import com.kotakotik.createaddontemplate.register.ModEntities;
import com.kotakotik.createaddontemplate.register.ModItems;
import com.kotakotik.createaddontemplate.register.ModTiles;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: rename this class!
@Mod(CreateAutomated.modid)
public class CreateAutomated {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String modid = "createautomated";

    public static final NonNullLazyValue<CreateRegistrate> registrate = CreateRegistrate.lazy(modid);

    public CreateAutomated() {
        CreateRegistrate r = registrate.get();
        ModItems.register(r);
        ModBlocks.register(r);
        ModEntities.register(r);
        ModTiles.register(r);
    }
}
