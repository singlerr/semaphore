plugins {
    id("java")
    id("gg.essential.loom") version "1.5.polyfrost.1"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.diffplug.spotless") version "6.11.0" apply false
    id("io.freefair.lombok") version "8.2.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
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

val elementa_version: String by project
val uc_version: String by project

val modId = "$mod_id-elementa"
val modName = "$mod_name-elementa"

group = "${mod_group_id}.elementa"

version = rootProject.version

loom {
    forge { pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter()) }

    runConfigs.configureEach { ideConfigGenerated(false) }
}

kotlin { jvmToolchain(8) }

repositories {
    mavenCentral()
    maven { url = uri("https://repo.essential.gg/repository/maven-public") }
    flatDir { dir("libs") }
}

val shadowImpl: Configuration by
    configurations.creating { configurations.modImplementation.get().extendsFrom(this) }

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("de.oceanlabs.mcp:mcp_${mapping_channel}:${mapping_version}")
    forge("net.minecraftforge:forge:${minecraft_version}-${forge_version}")

    shadowImpl("gg.essential:elementa:665")
    shadowImpl("gg.essential:universalcraft-${minecraft_version}-forge:$uc_version")

    implementation(project(":policy-impl"))
    implementation(project(":")) { isTransitive = false }
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

tasks.withType<Jar> {
    archiveBaseName.set(modId)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "${modId}.mixins.json"
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
