package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitMQConfig {

    companion object {
        const val EXCHANGE_NAME = "todo-service.events"
        const val TASK_QUEUE = "todo-service.task.events"
        const val TODO_LIST_QUEUE = "todo-service.todolist.events"
        const val REMINDER_QUEUE = "todo-service.reminder.events"
    }

    @Bean
    open fun topicExchange(): TopicExchange {
        return TopicExchange(EXCHANGE_NAME)
    }

    @Bean
    open fun taskQueue(): Queue {
        return QueueBuilder.durable(TASK_QUEUE).build()
    }

    @Bean
    open fun todoListQueue(): Queue {
        return QueueBuilder.durable(TODO_LIST_QUEUE).build()
    }

    @Bean
    open fun reminderQueue(): Queue {
        return QueueBuilder.durable(REMINDER_QUEUE).build()
    }

    @Bean
    open fun taskBinding(taskQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(taskQueue)
            .to(topicExchange)
            .with("event.task.*")
    }

    @Bean
    open fun todoListBinding(todoListQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(todoListQueue)
            .to(topicExchange)
            .with("event.todolist.*")
    }

    @Bean
    open fun reminderBinding(reminderQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(reminderQueue)
            .to(topicExchange)
            .with("event.reminder.*")
    }

    @Bean
    open fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = Jackson2JsonMessageConverter()
        return rabbitTemplate
    }
}