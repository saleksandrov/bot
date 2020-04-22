package ru.asv.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import ru.asv.bmd.base.config.YamlPropertySourceFactory

@PropertySource(value = ["classpath:bot-base-config.yaml"],
    factory = YamlPropertySourceFactory::class,
    ignoreResourceNotFound = true)
@SpringBootApplication
class BaseApplication

fun main(args: Array<String>) {
    SpringApplication(BaseApplication::class.java).apply {
        webApplicationType = WebApplicationType.REACTIVE
    }.run(*args)
}