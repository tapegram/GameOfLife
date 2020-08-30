plugins {
    kotlin("jvm") version "1.3.72"
    application
}

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))
//    testImplementation("junit:junit:4.12")

    implementation("io.arrow-kt:arrow-core:0.10.3")
    implementation("io.arrow-kt:arrow-generic:0.10.3")
    implementation("io.arrow-kt:arrow-mtl:0.10.3")
    implementation("io.arrow-kt:arrow-fx:0.10.3")

    // Tests
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.3") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:4.1.3") // for kotest property test
}

application {
    mainClassName = "com.gameoflife.version1.App"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
