plugins {
    id 'java'
    id 'application'
}

group 'com.monkeyzone'
version '1.0'

mainClassName = "com.monkeyzone.MonkeyZone"

repositories {
    mavenCentral()
}

project.ext {
  jmeVer = '3.7.0-stable'
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
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {

  implementation "org.jmonkeyengine:jme3-core:$jmeVer"
  implementation "org.jmonkeyengine:jme3-desktop:$jmeVer"
  implementation "org.jmonkeyengine:jme3-lwjgl:$jmeVer"
  implementation "org.jmonkeyengine:jme3-lwjgl:$jmeVer"
  implementation "com.github.stephengold:Heart:9.0.0"
  implementation "com.github.stephengold:Minie:8.0.0"
  implementation project("assets")

}

jar {
    manifest {
        attributes 'Main-Class': "$mainClassName"
    }
}

wrapper {
    gradleVersion = '8.6'
}
