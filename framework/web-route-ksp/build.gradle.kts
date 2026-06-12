plugins {
    kotlin("jvm")
    id("com.cramsan.kotlin-jvm-lib")
}

dependencies {
    "implementation"(project(":framework:annotations"))
    "implementation"("com.google.devtools.ksp:symbol-processing-api:2.3.7")
}
