/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import lombok.Getter;

public class ModDependencyBuilder {

    private final StringBuilder builder;

    private ModDependencyBuilder() {
        this.builder = new StringBuilder();
    }

    public static ModDependencyBuilder builder() {
        return new ModDependencyBuilder();
    }

    public ModDependencyBuilder dependsOn(DependencyType type, String modId) {
        builder.append(type.value);
        builder.append(":");
        builder.append(modId);
        builder.append(";");
        return this;
    }

    public ModDependencyBuilder dependsOn(DependencyType type, String modId, DependencyVersion version) {
        builder.append(type.value);
        builder.append(":");
        builder.append(modId);
        builder.append("@");
        builder.append(version.toVersionString());
        builder.append(";");
        return this;
    }

    public String build() {
        return builder.toString();
    }

    @Getter
    public enum DependencyType {
        BEFORE("before"),
        REQUIRED_BEFORE("require-before"),
        AFTER("after"),
        REQUIRED_AFTER("required-after");

        private final String value;

        DependencyType(String value) {
            this.value = value;
        }
    }

    public static class DependencyVersion {

        private final VersionRange from;

        private final VersionRange to;

        public DependencyVersion(VersionRange from, VersionRange to) {
            this.from = from;
            this.to = to;
        }

        public static DependencyVersion create(VersionRange from, VersionRange to) {
            return new DependencyVersion(from, to);
        }

        public String toVersionString() {
            return from.toVersionString(true) + "," + to.toVersionString(false);
        }

        public static class VersionRange {

            private final double version;

            private final boolean inclusive;

            public VersionRange(double version, boolean inclusive) {
                this.version = version;
                this.inclusive = inclusive;
            }

            public String toVersionString(boolean start) {
                if (start) {
                    return inclusive ? "[" + version : "(" + version;
                } else {
                    return inclusive ? version + "]" : version + ")";
                }
            }

            public static VersionRange inclusive(double version) {
                return new VersionRange(version, true);
            }

            public static VersionRange exclusive(double version) {
                return new VersionRange(version, false);
            }
        }
    }
}
