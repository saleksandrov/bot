package ru.asv.bot.adapter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

interface DataAdapter<T> {

    fun getData(): Mono<T>
}

@Component
class WeatherAdapter(
    @Value("\${bot-base.weatherToken}") private val weatherToken: String
): DataAdapter<WeatherInfo> {

    private val log = LoggerFactory.getLogger(WeatherAdapter::class.java)

    override fun getData(): Mono<WeatherInfo> {
        return WebClient.create("https://api.weather.yandex.ru")
            .get()
            .uri(
                "/v1/informers?lat={lat}&lon={lon}&lang=ru_RU&limit=1&hours=false&extra=false",
                "55.542066",
                "37.483933"
            )
            .header("X-Yandex-API-Key", weatherToken)
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
