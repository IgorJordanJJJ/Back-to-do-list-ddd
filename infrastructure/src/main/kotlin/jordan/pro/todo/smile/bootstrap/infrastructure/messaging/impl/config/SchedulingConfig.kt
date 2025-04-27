package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Конфигурация для включения запланированных задач
 */
@Configuration
@EnableScheduling
open class SchedulingConfig