plugins {
    id("idea")
    id("java")
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.10"
    id("io.freefair.lombok") version "8.3"
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "6.11.0"
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

val minecraft_version: String by project
val minecraft_version_range: String by project
val forge_version: String by project
val forge_version_range: String by project
val loader_version_range: String by project
val mapping_channel: String by project
val mapping_version: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_version: String by project
val mod_authors: String by project
val mod_description: String by project
val mod_group_id: String by project

val voicechat_version: String by project
val voicechat_api_version: String by project

group = mod_group_id

version = "${minecraft_version}-${mod_version}"

base { archivesName.set(mod_id) }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

kotlin { jvmToolchain(8) }

// Minecraft configuration:
loom {
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--mixin", "${mod_id}.mixins.json")
            //      arg("--username", "Dev")
        }

        "server" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--mixin", "${mod_id}.mixins.json")
        }
    }

    runs {
        named("client") {
            vmArgs(
                "-Ddevauth.enabled=true",
                "-Ddevauth.configDir=./.devauth",
                "-Ddevauth.account=alt"
            )
            runDir = "run-client"
        }
        named("server") { runDir = "run-server" }
    }

    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("${mod_id}.mixins.json")
        //        accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    }

    mixin { defaultRefmapName.set("${mod_id}.refmap.json") }
}

sourceSets.main { output.setResourcesDir(file("$buildDir/classes/java/main")) }

val shadowImpl: Configuration by
    configurations.creating { configurations.implementation.get().extendsFrom(this) }

repositories {
    mavenCentral()
    maven("https://maven.maxhenkel.de/repository/public")
    maven("https://repo.essential.gg/repository/maven-public")
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content { includeGroup("maven.modrinth") }
    }
    maven("https://repo.spongepowered.org/maven/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("de.oceanlabs.mcp:mcp_${mapping_channel}:${mapping_version}")
    forge("net.minecraftforge:forge:${minecraft_version}-${forge_version}")

    implementation("de.maxhenkel.voicechat:voicechat-api:${voicechat_api_version}")
    modImplementation(
        "maven.modrinth:simple-voice-chat:forge-${minecraft_version}-${voicechat_version}"
    )
    modRuntimeOnly(
        "maven.modrinth:simple-voice-chat:forge-${minecraft_version}-${voicechat_version}"
    )
    shadowImpl("gg.essential:elementa-${minecraft_version}-forge:642")
    shadowImpl("com.github.psambit9791:jdsp:2.0.0")

    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.0")

    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

tasks.withType<Jar> {
    archiveBaseName.set(mod_id)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "${mod_id}.mixins.json"
    }
    exclude("META-INF/versions/*/")
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties =
        mapOf(
            "minecraft_version" to minecraft_version,
            "minecraft_version_range" to minecraft_version_range,
            "forge_version" to forge_version,
            "forge_version_range" to forge_version_range,
            "loader_version_range" to loader_version_range,
            "mod_id" to mod_id,
            "mod_name" to mod_name,
            "mod_license" to mod_license,
            "mod_version" to mod_version,
            "mod_authors" to mod_authors,
            "mod_description" to mod_description,
        )

    inputs.properties(replaceProperties)

    filesMatching(arrayListOf("mcmod.info", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

val remapJar by
    tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        archiveClassifier.set("")
        from(tasks.shadowJar)
        input.set(tasks.shadowJar.get().archiveFile)
    }

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)

    //    relocate("gg.essential.elementa", "${mod_group_id}.gg.essential.elementa")
    //    relocate("gg.essential.universal", "${mod_group_id}.gg.essential.universal")
    //    relocate("kotlin", "${mod_group_id}.kotlin")
    //    relocate("org.jetbrains", "${mod_group_id}.org.jetbrains")

    dependencies { exclude("META-INF/versions/**") }

    doLast { configurations.forEach { println("Copying jars into mod: ${it.files}") } }
}

spotless {
    java {
        target("src/*/java/**/*.java", "*/src/*/java/**/*.java")
        palantirJavaFormat()
        licenseHeader("/* (C) \$YEAR singlerr */")
    }
    kotlinGradle {
        target("*.gradle.kts", "*/**.gradle.kts")

        ktfmt().kotlinlangStyle()
    }

    kotlin {
        target("src/*/kotlin/**/*.kt", "*/src/*/kotlin/**/*.kt")

        ktfmt().kotlinlangStyle()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = mod_group_id
            artifactId = mod_id
            version = "${minecraft_version}-${mod_version}"
            from(components["java"])
        }
    }

    repositories { mavenLocal() }
}
