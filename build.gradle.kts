plugins {
    java
    idea
}

val pluginName: String = rootProject.name
val pluginVersion: String by project
val pluginApi: String by project
val pluginDescription: String by project
val pluginGroup: String by project
val pluginArtifact: String by project
val pluginMain: String by project

group = pluginGroup
version = pluginVersion

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    // PaperMc
    maven("https://repo.papermc.io/repository/maven-public/")
    // VaultAPI
    maven("https://jitpack.io/")
}

dependencies {
    implementation(libs.papermc)
    implementation(libs.jetbrains.annotations)
    // VaultAPI
    implementation(libs.vaultapi)
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.1")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("resources")
        }
    }
    test {
        java {
            srcDir("test")
        }
    }
}
// this is for easier local testing
tasks.register<Copy>("prepareServer") {
    dependsOn("build")
    from(tasks.jar.get().archiveFile.get().asFile.path)
    rename(tasks.jar.get().archiveFile.get().asFile.name, "$pluginName.jar")
    into("G:\\Minecraft Servers\\paper\\plugins")
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }
    compileTestJava { options.encoding = "UTF-8" }
    javadoc { options.encoding = "UTF-8" }
    // plugin.yml placeholders
    processResources {
        outputs.upToDateWhen { false }
        filesMatching("**/plugin.yml") {
            expand(
                mapOf(
                    "version" to pluginVersion,
                    "api" to pluginApi,
                    "name" to pluginName,
                    "artifact" to pluginArtifact,
                    "main" to pluginMain,
                    "description" to pluginDescription,
                    "group" to pluginGroup
                )
            )
        }
    }
}