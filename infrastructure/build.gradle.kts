dependencies {
    implementation(libs.io.nats.spring)
    implementation(libs.io.nats)
    implementation(libs.apache.poi)
    implementation(libs.spring.boot.web)
    implementation(libs.apache.poi.ooxml)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.spring.boot.starter.amqp)    // Добавлена зависимость для RabbitMQ
    implementation(libs.spring.boot.starter.security) // Добавлена зависимость для Spring Security
    implementation(libs.jjwt.api)                    // Добавлена зависимость для JWT
    implementation(libs.jjwt.impl)                   // Добавлена зависимость для JWT
    implementation(libs.jjwt.jackson)                // Добавлена зависимость для JWT
    implementation(project(":application"))
    implementation(project(":domain"))
}

tasks.test {
    useJUnitPlatform()
}