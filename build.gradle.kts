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

    implementation("io.arrow-kt:arrow-core:0.10.3")
    implementation("io.arrow-kt:arrow-generic:0.10.3")
    implementation("io.arrow-kt:arrow-mtl:0.10.3")
    implementation("io.arrow-kt:arrow-fx:0.10.3")

    testImplementation ("io.kotlintest:kotlintest-runner-junit5:3.4.2") {
        exclude(group="io.arrow-kt")
    }
}

application {
    mainClassName = "com.gameoflife.version1.App"
}