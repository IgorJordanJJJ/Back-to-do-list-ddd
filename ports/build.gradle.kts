dependencies {
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.springdoc.openapi.starter.webflux.ui)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)
    testImplementation(libs.spring.module.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))
}

tasks.test {
    useJUnitPlatform()
}

