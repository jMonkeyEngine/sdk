<#-- FreeMarker template (see http://freemarker.org/) -->
plugins {
    id 'java'
    id 'application'
}

group = 'com.rollingthemonkey'
version = '1.0'

application {
    applicationName = '${name}'
    mainClass = 'com.rollingthemonkey.RollingTheMonkey'
}

repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

project.ext {
  jmeVer = '${jmeVersion}'
}

project(":assets") {
    apply plugin: "java"

    buildDir = rootProject.file("build/assets")

    sourceSets {
        main {
            resources {
                srcDir '.'
            }
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(${javaVersion})
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(${javaVersion})
    }
}

dependencies {

    implementation "org.jmonkeyengine:jme3-core:$jmeVer"
    implementation "org.jmonkeyengine:jme3-desktop:$jmeVer"
    implementation "org.jmonkeyengine:jme3-lwjgl:$jmeVer"
    implementation "com.github.stephengold:Heart:${heartVersion}"
    implementation "com.github.stephengold:Minie:${minieVersion}"
    implementation project("assets")

}

jar {
    manifest {
        attributes 'Main-Class': application.mainClass
    }
}

wrapper {
    gradleVersion = '${gradleVersion}'
}
