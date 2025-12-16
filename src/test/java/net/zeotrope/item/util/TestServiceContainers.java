package net.zeotrope.item.util;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class TestServiceContainers {
    @Container
    @ServiceConnection
    public static final PostgreSQLContainer postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer(DockerImageName.parse("postgres:17.6"))
            .withDatabaseName("items_db")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.defaultWaitStrategy());

    @Container
    @ServiceConnection
    public static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:8.2.3-alpine"))
            .withExposedPorts(6379);
}
