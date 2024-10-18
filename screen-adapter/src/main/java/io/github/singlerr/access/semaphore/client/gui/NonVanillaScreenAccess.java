/* (C) 2024 singlerr */
package io.github.singlerr.access.semaphore.client.gui;

public final class NonVanillaScreenAccess {

    private static NonVanillaScreen.Factory factory;

    public static NonVanillaScreen.Factory getFactory() {
        return factory;
    }

    public static void setFactory(NonVanillaScreen.Factory factory) {
        if (NonVanillaScreenAccess.factory != null) {
            throw new IllegalStateException("Cannot assign twice!");
        }

        NonVanillaScreenAccess.factory = factory;
    }
}
