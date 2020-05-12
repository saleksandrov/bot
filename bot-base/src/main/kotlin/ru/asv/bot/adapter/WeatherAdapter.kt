package ru.asv.bot.adapter

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.locks.ReentrantLock

class WeatherAdapter {

    private val log = LoggerFactory.getLogger(WeatherAdapter::class.java)

    companion object {
        private val cache = Cache()
    }

    fun getWeather(): WeatherInfo {
        return cache.getOrSet {
            WebClient.create("https://api.weather.yandex.ru")
                .get()
                .uri(
                    "/v1/forecast?lat={lat}&lon={lon}&lang=ru_RU&limit=1&hours=false&extra=false",
                    "55.542066",
                    "37.483933"
                )
                .header("X-Yandex-API-Key", "4983ebca-06b4-46c8-827a-b51649b71c36")
                .retrieve()
                .bodyToMono(WeatherInfo::class.java)
                .block()!!
        }
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

class Cache {

    private val log = LoggerFactory.getLogger(Cache::class.java)

    private val lock = ReentrantLock()
    private val cacheTimeMls = 30*60*1000

    private var lastUpdateTime: Long = 0
    private lateinit var weatherInfo: WeatherInfo

    fun getOrSet(calculate: () -> WeatherInfo): WeatherInfo {
        val currentTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli()
        if ((currentTime - lastUpdateTime) < cacheTimeMls) {
            log.info("Get Weather from cache")
            return weatherInfo
        }
        try {
            lock.lock()
            log.info("Request Weather from server")
            weatherInfo = calculate()
            lastUpdateTime = currentTime
            return weatherInfo
        } finally {
            lock.unlock()
        }
    }

}

fun main() {

    println(WeatherAdapter().getWeather())
    println(WeatherAdapter().getWeather())
    println(WeatherAdapter().getWeather())

}