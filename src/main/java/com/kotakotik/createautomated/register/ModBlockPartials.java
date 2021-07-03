package com.kotakotik.createautomated.register;

import com.simibubi.create.AllBlockPartials;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ModBlockPartials {
    public static AllBlockPartials HALF_SHAFT_COGWHEEL = get("half_shaft_cogwheel");
    public static List<AllBlockPartials> all;

    //    protected static final Method BlockPartialsGet = ObfuscationReflectionHelper.findMethod(AllBlockPartials.class, "get", String.class);
    protected static Field BlockPartialsModelLocation;
    protected static Constructor<AllBlockPartials> BlockPartialsConstructor;
    protected static Field BlockPartialsBakedModel;

    public static Constructor<AllBlockPartials> createConstructor() {
        if (BlockPartialsConstructor != null) return BlockPartialsConstructor;
        Constructor<AllBlockPartials> BlockPartialsConstructor1;
        try {
            BlockPartialsConstructor1 = AllBlockPartials.class.getDeclaredConstructor();
            BlockPartialsConstructor1.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            BlockPartialsConstructor1 = null;
        }
        BlockPartialsConstructor = BlockPartialsConstructor1;
        return BlockPartialsConstructor;
    }

    public static Field createBlockPartialsModelLocationReflection() {
        if (BlockPartialsModelLocation != null) return BlockPartialsModelLocation;
        BlockPartialsModelLocation = ObfuscationReflectionHelper.findField(AllBlockPartials.class, "modelLocation");
        return BlockPartialsModelLocation;
    }

    public static Field createBlockPartialsBakedModelReflection() {
        if (BlockPartialsBakedModel != null) return BlockPartialsBakedModel;
        BlockPartialsBakedModel = ObfuscationReflectionHelper.findField(AllBlockPartials.class, "bakedModel");
        return BlockPartialsBakedModel;
    }

    protected static AllBlockPartials createBlockPartials() {
        try {
            return createConstructor().newInstance();
//            return (AllBlockPartials) BlockPartialsGet.invoke(null, "");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            System.out.println(e.getClass().getSimpleName());
        }
        return null;
    }

    protected static AllBlockPartials get(ResourceLocation loc) {
        AllBlockPartials p = createBlockPartials();
        try {
            createBlockPartialsModelLocationReflection().set(p, loc);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        getAllList().add(p);
        return p;
    }

    protected static List<AllBlockPartials> getAllList() {
        if (all != null) return all;
        all = new ArrayList<>();
        return all;
    }

    protected static ResourceLocation getModel(AllBlockPartials p) {
        try {
            return (ResourceLocation) createBlockPartialsModelLocationReflection().get(p);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static void setBakedModel(AllBlockPartials p, IBakedModel b) {
        try {
            createBlockPartialsBakedModelReflection().set(p, b);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static AllBlockPartials get(String name) {
        return get(new ResourceLocation("createautomated", "block/" + name));
    }

    // load class
    public static void register() {
    }

    public static void onModelBake(@Nonnull ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

        for (AllBlockPartials partials : getAllList()) {
            System.out.println(getModel(partials));
            setBakedModel(partials, modelRegistry.get(getModel(partials)));
        }
    }

    public static void onModelRegistry(ModelRegistryEvent event) {
        for (AllBlockPartials partials : getAllList())
            ModelLoader.addSpecialModel(Objects.requireNonNull(getModel(partials)));
    }
}