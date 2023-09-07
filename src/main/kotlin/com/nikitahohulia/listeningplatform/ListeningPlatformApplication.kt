package com.nikitahohulia.listeningplatform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ListeningPlatformApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<ListeningPlatformApplication>(*args)
}
