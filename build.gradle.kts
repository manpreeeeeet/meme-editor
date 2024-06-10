plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.manpreet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.javalin:javalin:6.1.3")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}