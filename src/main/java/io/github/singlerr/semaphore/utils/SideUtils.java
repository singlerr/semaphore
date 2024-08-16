package io.github.singlerr.semaphore.utils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class SideUtils {

    private SideUtils(){}

    public static void validateSide(Class<?> cls, Side side){
        SideOnly sideAnnotation = cls.getAnnotation(SideOnly.class);
        if(sideAnnotation == null)
            return;
        if(sideAnnotation.value() != side)
            throw new IllegalStateException("Required " + side + " but found: " + sideAnnotation.value());
    }
}
