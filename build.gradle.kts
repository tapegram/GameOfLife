plugins {
    kotlin("jvm") version "1.3.72"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.12")
}

application {
    mainClassName = "com.gameoflife.version1.App"
}