plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"

    id("io.github.goooler.shadow") version "8.1.8"
}

group = "ng.bossi"
version = 1

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("com.akuleshov7:ktoml-core:0.5.1")

    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

    implementation("org.jetbrains.exposed:exposed-core:0.52.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.52.0")
    implementation("org.jetbrains.exposed:exposed-json:0.52.0")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation(platform("org.http4k:http4k-bom:5.26.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}

kotlin {
    jvmToolchain(20)
}

tasks {

    jar {
        manifest {
            attributes["Main-Class"] = "ng.bossi.api.MainKt"
            attributes["Implementation-Version"] = version
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets.main.get().output)
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}