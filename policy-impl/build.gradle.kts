plugins {
    id("java")
    `maven-publish`
}

val mod_version: String by rootProject

group = rootProject.group

version = rootProject.version

dependencies { implementation("org.jetbrains:annotations:13.0") }

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
            url = uri(project.findProperty("localMvnRepo").toString())
        }
    }
}
