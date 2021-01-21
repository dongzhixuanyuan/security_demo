package com.spring.security.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class SecurityDemoApplication

fun main(args: Array<String>) {
    runApplication<SecurityDemoApplication>(*args)
}
