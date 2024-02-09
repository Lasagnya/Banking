package com.project.banking.configuration;

import com.project.banking.util.ChargingOfPercents;
import com.project.banking.util.IsPercentsNeeded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.*;
import java.util.Optional;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig implements SchedulingConfigurer {
	private final IsPercentsNeeded isPercentsNeeded;
	private final ChargingOfPercents chargingOfPercents;

	@Autowired
	public SchedulerConfig(IsPercentsNeeded isPercentsNeeded, ChargingOfPercents chargingOfPercents) {
		this.isPercentsNeeded = isPercentsNeeded;
		this.chargingOfPercents = chargingOfPercents;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addFixedRateTask(isPercentsNeeded, Duration.ofSeconds(30));

		taskRegistrar.addTriggerTask(
				chargingOfPercents,
				triggerContext -> {
					Optional<Instant> lastActualExecution = Optional.ofNullable(triggerContext.lastActualExecution());
					LocalDateTime next = lastActualExecution.map(instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault())).orElseGet(LocalDateTime::now)
//							.plusSeconds(10);
							.plusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
					return next.toInstant(OffsetDateTime.now().getOffset());
				}
		);
	}
}