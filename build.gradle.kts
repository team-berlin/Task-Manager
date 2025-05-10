plugins {
    kotlin("jvm") version "2.1.10"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "com.berlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:4.0.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("com.opencsv:opencsv:5.7.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    implementation ("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.0")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "**.model.**",
                    "**.di.**",
                    "**.exception.**",
                    "**.io.**",
                )
            }
        }

        total {
            verify {
                rule {
                    minBound(80)
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

tasks.named("check") {
    dependsOn(tasks.koverVerify)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(22)
}