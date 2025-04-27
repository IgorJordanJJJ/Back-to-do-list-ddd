import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
}

repositories {
    mavenCentral() // Для загрузки зависимостей из Maven Central
}

subprojects {

    apply(plugin = "kotlin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    group = "digitalnomads.pro.veles.kyzia"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(rootProject.libs.coroutines.reactor)
        implementation(rootProject.libs.jackson.kotlin)
        implementation(rootProject.libs.reactor.kotlin.extensions)
        implementation(rootProject.libs.kotlin.reflect)
        implementation(rootProject.libs.kotlin.stdlib)
        implementation(rootProject.libs.kotlin.stdlib.jdk8)
        testImplementation(rootProject.libs.spring.boot.test)
        testImplementation(rootProject.libs.reactor.test)
        testImplementation(rootProject.libs.mockito.kotlin)
        testImplementation(rootProject.libs.mockk)
        testImplementation(rootProject.libs.kotlin.test.junit5)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
        implementation(rootProject.libs.spring.boot.starter.data.jpa)
    }

    // Конфигурация Ktlint
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true) // Для отладки
        verbose.set(true) // Подробные сообщения
        android.set(false) // Если это не Android проект
        outputToConsole.set(true) // Вывод результатов в консоль
        ignoreFailures.set(true) // <-- Игнорировать ошибки Ktlint
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN) // Текстовый вывод
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE) // XML-отчет
        }
    }
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
        outputs.cacheIf { false }
    }
}
