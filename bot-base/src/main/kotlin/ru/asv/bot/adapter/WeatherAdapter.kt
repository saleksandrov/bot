package ru.asv.bot.adapter

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

class WeatherAdapter {

    private val log = LoggerFactory.getLogger(WeatherAdapter::class.java)

    fun getWeather(): Mono<WeatherInfo> {
        return WebClient.create("https://api.weather.yandex.ru")
                .get()
                .uri(
                    "/v1/forecast?lat={lat}&lon={lon}&lang=ru_RU&limit=1&hours=false&extra=false",
                    "55.542066",
                    "37.483933"
                )
                .header("X-Yandex-API-Key", "4983ebca-06b4-46c8-827a-b51649b71c36")
                .retrieve()
                .bodyToMono(WeatherInfo::class.java).cache(Duration.ofMinutes(30))

    }

}

data class WeatherInfo(
    val fact: Fact
)

data class Fact(
    val temp: Float,
    val feels_like: Float,
    val wind_speed: Float
)
