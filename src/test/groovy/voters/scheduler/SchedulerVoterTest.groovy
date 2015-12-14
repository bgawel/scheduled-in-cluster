package voters.scheduler

import spock.lang.Specification

class SchedulerVoterTest extends Specification {

    SchedulerVoter voter

    SchedulerVoterTask schedulerVoterTask
    Scheduler scheduler
    RegisteredSchedulerService registeredSchedulerService

    def "init scheduler voter"() {
        when:
        voter.init()

        then:
        1 * schedulerVoterTask.start(_)
    }

    def "enable scheduler if no master scheduler"() {
        given:
        scheduler.name >> 'scheduler'
        1 * registeredSchedulerService.ifNoMasterSchedulerRegisterThisScheduler('scheduler') >> true

        when:
        voter.voteForScheduler()

        then:
        1 * scheduler.enable()
    }

    def "disable scheduler if it isn't a master"() {
        given:
        scheduler.name >> 'scheduler'
        1 * registeredSchedulerService.ifNoMasterSchedulerRegisterThisScheduler('scheduler') >> false
        1 * registeredSchedulerService.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat('scheduler') >> false

        when:
        voter.voteForScheduler()

        then:
        1 * scheduler.disable()
    }

    def "enable scheduler if it is a master and disabled"() {
        given:
        scheduler.name >> 'scheduler'
        1 * registeredSchedulerService.ifNoMasterSchedulerRegisterThisScheduler('scheduler') >> false
        1 * registeredSchedulerService.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat('scheduler') >> true
        1 * scheduler.isDisabled() >> true

        when:
        voter.voteForScheduler()

        then:
        1 * scheduler.enable()
    }

    def "do nothing if it is a master and enabled"() {
        given:
        scheduler.name >> 'scheduler'
        1 * registeredSchedulerService.ifNoMasterSchedulerRegisterThisScheduler('scheduler') >> false
        1 * registeredSchedulerService.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat('scheduler') >> true
        1 * scheduler.isDisabled() >> false

        when:
        voter.voteForScheduler()

        then:
        0 * scheduler.enable()
        0 * scheduler.disable()
    }

    def setup() {
        schedulerVoterTask = Mock(SchedulerVoterTask)
        scheduler = Mock(Scheduler)
        registeredSchedulerService = Mock(RegisteredSchedulerService);
        voter = new SchedulerVoter(schedulerVoterTask, scheduler, registeredSchedulerService)
    }
}
