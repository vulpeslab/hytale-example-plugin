plugins {
    java
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Add Hytale Server as compileOnly dependency (not bundled in final JAR)
    compileOnly(files("HytaleServer.jar"))
}

tasks.jar {
    // Set the archive name
    archiveBaseName.set("example-plugin")
    archiveVersion.set("1.0.0")

    // Handle duplicates (resources are already included by default)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
