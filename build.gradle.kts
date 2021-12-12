import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("kapt") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.github.secretx33"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // API dependency
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("com.github.secretx33.sc-cfg:bukkit-kotlin:8318ce6")
    implementation("com.github.secretx33.sc-cfg:yaml:8318ce6")
    val toothpick_version = "3.1.0"
    implementation("com.github.stephanenicolas.toothpick:ktp:$toothpick_version")
    kapt("com.github.stephanenicolas.toothpick:toothpick-compiler:$toothpick_version")
    implementation("org.slf4j:slf4j-simple:1.7.32")
}

tasks.test {
    useJUnitPlatform()
}

// Disables the normal jar task
tasks.jar { enabled = false }

// And enables shadowJar task
artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + ".jar")
    val dependencyPackage = "${rootProject.group}.dependencies.${rootProject.name.toLowerCase()}"
    relocate("com.github.secretx33.sccfg", "${dependencyPackage}.sccfg")
    relocate("com.cryptomorin.xseries", "${dependencyPackage}.xseries")
    relocate("com.typesafe.config", "${dependencyPackage}.typesafe.config")
    relocate("io.leangen.geantyref", "${dependencyPackage}.geantyref")
    relocate("javassist", "${dependencyPackage}.javassist")
    relocate("javax.annotation", "${dependencyPackage}.javax.annotation")
    relocate("javax.inject", "${dependencyPackage}.javax.inject")
    relocate("org.reflections", "${dependencyPackage}.reflections")
    relocate("org.slf4j", "${dependencyPackage}.slf4j")
    relocate("org.spongepowered.configurate", "${dependencyPackage}.configurate")
    relocate("org.yaml.snakeyaml", "${dependencyPackage}.snakeyaml")
    relocate("kotlin", "${dependencyPackage}.kotlin")
    relocate("kotlinx", "${dependencyPackage}.kotlinx")
    relocate("org.jetbrains", "${dependencyPackage}.jetbrains")
    relocate("org.intellij", "${dependencyPackage}.jetbrains.intellij")
    relocate("toothpick", "${dependencyPackage}.toothpick")
    exclude("ScopeJVMKt.class")
    exclude("DebugProbesKt.bin")
    exclude("META-INF/**")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "1.8"
    }
}

tasks.processResources {
    outputs.upToDateWhen { false }
    val main_class = "${project.group}.${project.name.toLowerCase()}.${project.name}"
    expand("name" to project.name, "version" to project.version, "mainClass" to main_class)
}
