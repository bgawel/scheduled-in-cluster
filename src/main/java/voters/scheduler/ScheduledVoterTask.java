package voters.scheduler;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

class ScheduledVoterTask extends ScheduledTaskRegistrar implements SchedulerVoterTask {

    private final int fixedDelayInSeconds;

    ScheduledVoterTask(final int fixedDelayInSeconds) {
        this.fixedDelayInSeconds = fixedDelayInSeconds;
    }

    @Override
    public void start(Runnable runnable) {
        addFixedDelayTask(runnable, fixedDelayInSeconds * 1000);
        scheduleTasks();
    }
}
