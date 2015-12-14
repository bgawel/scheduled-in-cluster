package voters.scheduler;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;

import static org.slf4j.LoggerFactory.getLogger;

public class SchedulerVoter {

    private static final Logger log = getLogger(SchedulerVoter.class);

    private final SchedulerVoterTask schedulerVoterTask;
    private final Scheduler scheduler;
    private final RegisteredSchedulerService registeredSchedulerService;

    public SchedulerVoter(final SchedulerVoterTask schedulerVoterTask,
                          final Scheduler scheduler,
                          final RegisteredSchedulerService registeredSchedulerService) {
        this.schedulerVoterTask = schedulerVoterTask;
        this.scheduler = scheduler;
        this.registeredSchedulerService = registeredSchedulerService;
    }

    @PostConstruct
    void init() {
        schedulerVoterTask.start(this::voteForScheduler);
    }

    private void voteForScheduler() {
        log.trace("Voting for scheduler {}", scheduler.getName());
        if (registeredSchedulerService.ifNoMasterSchedulerRegisterThisScheduler(scheduler.getName())) {
            scheduler.enable();
        } else if (registeredSchedulerService.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat(scheduler.getName())) {
            if (scheduler.isDisabled()) {
                scheduler.enable();
            }
        } else {
            scheduler.disable();
        }
    }
}
