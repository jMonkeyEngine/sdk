<#-- FreeMarker template (see http://freemarker.org/) -->
plugins {
    id 'java'
    id 'application'
}

group = 'com.mygame'
version = '1.0'

application {
    applicationName = '${name}'
    mainClass = 'com.mygame.Main'
    // Mac OS with LWJGL 3 needs to be started with this JVM argument
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
        applicationDefaultJvmArgs = ['-XstartOnFirstThread']
    }
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
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

dependencies {

  // Core JME
  implementation "org.jmonkeyengine:jme3-core:$jmeVer"
  implementation "org.jmonkeyengine:jme3-desktop:$jmeVer"
  <#if jmeVersion.versionInfo.major gt 3 || (jmeVersion.versionInfo.major == 3 && jmeVersion.versionInfo.minor gte 6 )>
  // Mac OS with LWJGL 3 doesn't allow AWT/Swing
  if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
    implementation "org.jmonkeyengine:jme3-awt-dialogs:$jmeVer"
  }
  </#if>
  <#if lwjglLibrary.isCoreJmeLibrary == true>
  implementation "${lwjglLibrary.groupId}:${lwjglLibrary.artifactId}:$jmeVer"
  <#else>
  implementation "${lwjglLibrary.groupId}:${lwjglLibrary.artifactId}:${lwjglLibrary.versionInfo.versionString}"
  </#if>

  // Suppress errors / warnings building in SDK
  implementation "org.jmonkeyengine:jme3-jogg:$jmeVer"
  implementation "org.jmonkeyengine:jme3-plugins:$jmeVer"
  <#if guiLibrary.label != "">
  
  // GUI Library
  <#if guiLibrary.isCoreJmeLibrary == true>
  implementation "${guiLibrary.groupId}:${guiLibrary.artifactId}:$jmeVer"
  <#else>
  implementation "${guiLibrary.groupId}:${guiLibrary.artifactId}:${guiLibrary.versionInfo.versionString}"
  </#if>
  </#if>
  <#if physicsLibrary.label != "">
  
  // Physics Library
  <#if physicsLibrary.isCoreJmeLibrary == true>
  implementation "${physicsLibrary.groupId}:${physicsLibrary.artifactId}:$jmeVer"
  <#else>
  implementation "${physicsLibrary.groupId}:${physicsLibrary.artifactId}:${physicsLibrary.versionInfo.versionString}"
  </#if>
  </#if>
  <#if networkingLibrary.label != "">
  
  // Networking Library
  <#if networkingLibrary.isCoreJmeLibrary == true>
  implementation "${networkingLibrary.groupId}:${networkingLibrary.artifactId}:$jmeVer"
  <#else>
  implementation "${networkingLibrary.groupId}:${networkingLibrary.artifactId}:${networkingLibrary.versionInfo.versionString}"
  </#if>
  </#if>

  // Additional Libraries
  <#list additionalLibraries as additionalLibrary>
  <#if additionalLibrary.isCoreJmeLibrary == true>
  implementation "${additionalLibrary.groupId}:${additionalLibrary.artifactId}:$jmeVer"
  <#else>
  implementation "${additionalLibrary.groupId}:${additionalLibrary.artifactId}:${additionalLibrary.versionInfo.versionString}"
  </#if>
  </#list>

  // Assets sub-project
  runtimeOnly project(':assets')
}

jar {
    manifest {
        attributes 'Main-Class': application.mainClass
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

wrapper {
    gradleVersion = '9.2.1'
}