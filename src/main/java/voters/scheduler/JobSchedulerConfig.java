package voters.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!h2Server")
class JobSchedulerConfig {

    @Bean
    SchedulerVoterTask schedulerVoterTask(@Value("${scheduler.voter.fixedDelay:60}") final int fixedDelayInSeconds) {
        return new ScheduledVoterTask(fixedDelayInSeconds);
    }

    @Bean
    Scheduler scheduler(@Value("${scheduler.name}") final String schedulerName) {
        return new AnnotationBasedScheduler(schedulerName);
    }

    @Bean
    RegisteredSchedulerService registeredSchedulerService(
            final RegisteredSchedulerRepository registeredSchedulerRepository,
            @Value("${scheduler.active.noActiveHeartbeatSeconds:90}") final int noActiveHeartbeatSeconds) {
        return new RegisteredSchedulerServiceImpl(registeredSchedulerRepository, noActiveHeartbeatSeconds);
    }

    @Bean
    SchedulerVoter schedulerVoter(final SchedulerVoterTask schedulerVoterTask,
                                  final Scheduler scheduler,
                                  final RegisteredSchedulerService registeredSchedulerService) {
        return new SchedulerVoter(schedulerVoterTask, scheduler, registeredSchedulerService);
    }
}
