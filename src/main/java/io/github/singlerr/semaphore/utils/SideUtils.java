/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import lombok.experimental.UtilityClass;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@UtilityClass
public class SideUtils {

    public void validateSide(Class<?> cls, Side side) {
        SideOnly sideAnnotation = cls.getAnnotation(SideOnly.class);
        if (sideAnnotation == null) return;
        if (sideAnnotation.value() != side)
            throw new IllegalStateException("Required " + side + " but found: " + sideAnnotation.value());
    }
}
