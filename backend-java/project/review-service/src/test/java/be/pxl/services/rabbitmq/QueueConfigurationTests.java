package be.pxl.services.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@SpringJUnitConfig(QueueConfiguration.class)
public class QueueConfigurationTests {
    @MockBean
    private ConnectionFactory connectionFactory;

    private final QueueConfiguration queueConfiguration = new QueueConfiguration();

    @Test
    void messageConverter_ShouldReturnJackson2JsonMessageConverter() {
        // Act
        Jackson2JsonMessageConverter converter = queueConfiguration.messageConverter();

        // Assert
        assertNotNull(converter);
    }

    @Test
    void rabbitTemplate_ShouldReturnConfiguredTemplate() {
        // Arrange
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);

        // Act
        RabbitTemplate template = queueConfiguration.rabbitTemplate(mockConnectionFactory);

        // Assert
        assertNotNull(template);
        assertTrue(template.getMessageConverter() instanceof Jackson2JsonMessageConverter);
    }

    @Test
    void reviewQueue_ShouldReturnNonDurableQueue() {
        // Act
        Queue queue = queueConfiguration.reviewQueue();

        // Assert
        assertNotNull(queue);
        assertEquals("ReviewQueue", queue.getName());
        assertFalse(queue.isDurable());
    }

    @Test
    void notificationQueue_ShouldReturnNonDurableQueue() {
        // Act
        Queue queue = queueConfiguration.notificationQueue();

        // Assert
        assertNotNull(queue);
        assertEquals("NotificationQueue", queue.getName());
        assertFalse(queue.isDurable());
    }
}
