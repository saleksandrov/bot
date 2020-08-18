package ru.asv.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import ru.asv.bot.config.YamlPropertySourceFactory

@PropertySource(value = ["classpath:bot-base-config.yaml", "file:config/bot-base-config.yaml"],
    factory = YamlPropertySourceFactory::class,
    ignoreResourceNotFound = true)
@SpringBootApplication
class BotApplication

fun main(args: Array<String>) {
    SpringApplication(BotApplication::class.java).apply {
        webApplicationType = WebApplicationType.REACTIVE
    }.run(*args)
}