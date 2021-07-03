package com.kotakotik.createautomated.register;

import com.simibubi.create.AllBlockPartials;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class ModBlockPartials { // TODO: cant figure out how to get an instance of block partials.. :/
    public static AllBlockPartials HALF_SHAFT_COGWHEEL = get("half_shaft_cogwheel");

    //    protected static final Method BlockPartialsGet = ObfuscationReflectionHelper.findMethod(AllBlockPartials.class, "get", String.class);
    protected static final Field BlockPartialsModelLocation = ObfuscationReflectionHelper.findField(AllBlockPartials.class, "modelLocation");
    protected static Constructor<AllBlockPartials> BlockPartialsConstructor;

    protected static Constructor<AllBlockPartials> createConstructor() {
        if (BlockPartialsConstructor != null) return null;
        Constructor<AllBlockPartials> BlockPartialsConstructor1;
        try {
            BlockPartialsConstructor1 = AllBlockPartials.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            BlockPartialsConstructor1 = null;
        }
        BlockPartialsConstructor = BlockPartialsConstructor1;
        return BlockPartialsConstructor;
    }

    protected static AllBlockPartials createBlockPartials() {
        try {
            return createConstructor().newInstance();
//            return (AllBlockPartials) BlockPartialsGet.invoke(null, "");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static AllBlockPartials get(ResourceLocation loc) {
        AllBlockPartials p = createBlockPartials();
        try {
            BlockPartialsModelLocation.set(p, loc);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static AllBlockPartials get(String name) {
        return get(new ResourceLocation("createautomated", "block/" + name));
    }
}
