/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import net.minecraft.util.ResourceLocation;

public final class ResourceLocationBuilder {

    private String namespace;

    private final StringBuilder path;

    private ResourceLocationBuilder() {
        this.path = new StringBuilder();
    }

    public static ResourceLocationBuilder builder() {
        return new ResourceLocationBuilder();
    }

    public ResourceLocationBuilder namespace(final String namespace) {
        this.namespace = namespace;
        return this;
    }

    public ResourceLocationBuilder append(final String... path) {
        for (String p : path) {
            if (this.path.length() > 0) this.path.append("/");
            this.path.append(p);
        }

        return this;
    }

    public ResourceLocation build() {
        return new ResourceLocation(namespace, path.toString());
    }
}
