import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.remusrd"
version = "0.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.kittinunf.fuel:fuel:2.1.0")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.1.0")
    implementation("io.github.seik.kotlin-telegram-bot:telegram:0.3.8")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.2.1")
    implementation("io.github.microutils:kotlin-logging:1.6.26")
    implementation("ch.qos.logback:logback-core:1.3.0-alpha4")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}
