package ru.asv.bot.http

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient


fun main() {

    val sslContext = SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build()
    val httpConnector = HttpClient.create().secure {
        it.sslContext(sslContext)
    }
    val url = System.getProperty("URL")
    val v = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpConnector))
        .baseUrl(url)
        .build().get().retrieve().bodyToMono(String::class.java).block()

    print(v)
}