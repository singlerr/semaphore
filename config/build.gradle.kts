plugins {
    id("java")
    id("io.freefair.lombok") version "8.2.2"
    `maven-publish`
}

val mod_version: String by rootProject

group = rootProject.group

version = rootProject.version

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${rootProject.group}"
            artifactId = "${project.name}"
            version = "${mod_version}"
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "singlerrs_repo"
            url = File(project.findProperty("localMvnRepo").toString()).toURI()
        }
    }
}
