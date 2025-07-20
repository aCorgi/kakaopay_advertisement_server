package com.task.kakaopayadvertisementserver.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName

class RepositoryContainerExtension : BeforeAllCallback {
    companion object {
        private const val RABBITMQ_DOCKER_IMAGE = "rabbitmq:3.12-management"

        @Container
        var rabbitMQContainer = RabbitMQContainer(DockerImageName.parse(RABBITMQ_DOCKER_IMAGE))

        private const val MYSQL_VERSION = "mysql:8.0.28"
        private const val DATABASE_NAME = "test"
        private const val USERNAME = "test"
        private const val PASSWORD = "password"
        private const val MYSQL_PORT = 3306

        @Container
        val mysqlContainer: MySQLContainer<*> =
            MySQLContainer(MYSQL_VERSION)
                .withCommand(
                    "--character-set-server=utf8mb4",
                    "--collation-server=utf8mb4_unicode_ci",
                    "--max_connections=1000",
                )
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .withExposedPorts(MYSQL_PORT)

        private const val REDIS_VERSION = "redis:7.0.7"
        private const val REDIS_PORT = 6379

        @Container
        val redisContainer: GenericContainer<*> =
            GenericContainer(REDIS_VERSION)
                .withExposedPorts(REDIS_PORT)
    }

    override fun beforeAll(context: ExtensionContext?) {
        val notRunningContainers =
            listOf(rabbitMQContainer, mysqlContainer, redisContainer)
                .filter { it.isRunning.not() }

        if (notRunningContainers.isEmpty()) {
            return
        }
        Startables.deepStart(notRunningContainers).join()

        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.host)
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.amqpPort.toString())
        System.setProperty("spring.rabbitmq.username", rabbitMQContainer.adminUsername)
        System.setProperty("spring.rabbitmq.password", rabbitMQContainer.adminPassword)

        System.setProperty(
            "spring.datasource.url",
            "jdbc:mysql://localhost:${
                mysqlContainer.getMappedPort(
                    MYSQL_PORT,
                )}/$DATABASE_NAME?useSSL=false&useUnicode=true&characterEncoding=UTF-8",
        )
        System.setProperty("spring.datasource.username", USERNAME)
        System.setProperty("spring.datasource.password", PASSWORD)

        System.setProperty("spring.data.redis.host", redisContainer.host)
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())
    }
}
