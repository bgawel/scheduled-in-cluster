package voters.scheduler

import org.joda.time.DateTime
import org.springframework.dao.OptimisticLockingFailureException
import spock.lang.Specification

class RegisteredSchedulerServiceImplTest extends Specification {

    RegisteredSchedulerService service

    RegisteredSchedulerRepository registeredSchedulerRepository

    def "if there is no master scheduler, register a new scheduler"() {
        given:
        registeredSchedulerRepository.findMasterScheduler() >> null

        when:
        def registered = service.ifNoMasterSchedulerRegisterThisScheduler('scheduler')

        then:
        registered
        1 * registeredSchedulerRepository.saveAndFlush(_) >> { RegisteredScheduler scheduler ->
            with(scheduler) {
                assert schedulerName == 'scheduler'
                assert schedulerType == 'M'
                assert heartbeat != null
                assert id == null
            }
        }
    }

    def "if there is inactive master scheduler, register a new scheduler"() {
        given:
        def previousHeartbeat = DateTime.now().minusSeconds(61)
        def oldScheduler = new RegisteredScheduler(1, 'old scheduler', 'M', previousHeartbeat)
        registeredSchedulerRepository.findMasterScheduler() >> oldScheduler

        when:
        def registered = service.ifNoMasterSchedulerRegisterThisScheduler('scheduler')

        then:
        registered
        1 * registeredSchedulerRepository.saveAndFlush(_) >> { RegisteredScheduler scheduler ->
            with(scheduler) {
                assert schedulerName == 'scheduler'
                assert schedulerType == 'M'
                assert heartbeat != null && heartbeat.isAfter(previousHeartbeat)
                assert id == oldScheduler.id
            }
        }
    }

    def "if there is inactive master scheduler, do not register a new scheduler if master = this"() {
        given:
        def previousHeartbeat = DateTime.now().minusSeconds(61)
        registeredSchedulerRepository.findMasterScheduler() >>
                new RegisteredScheduler(1, 'scheduler', 'M', previousHeartbeat)

        when:
        def registered = service.ifNoMasterSchedulerRegisterThisScheduler('scheduler')

        then:
        !registered
        0 * registeredSchedulerRepository.saveAndFlush(_)
    }

    def "if there is active master scheduler, do not register a new scheduler"() {
        given:
        registeredSchedulerRepository.findMasterScheduler() >>
                new RegisteredScheduler(1, 'old scheduler', 'M', DateTime.now().minusSeconds(1))

        when:
        def registered = service.ifNoMasterSchedulerRegisterThisScheduler('scheduler')

        then:
        !registered
        0 * registeredSchedulerRepository.saveAndFlush(_)
    }

    def "handle locking exception if a new scheduler cannot be registered"() {
        given:
        def oldScheduler = new RegisteredScheduler(1, 'old scheduler', 'M', DateTime.now().minusSeconds(61))
        registeredSchedulerRepository.findMasterScheduler() >> oldScheduler
        1 * registeredSchedulerRepository.saveAndFlush(_) >> { throw new OptimisticLockingFailureException("error") }

        when:
        def registered = service.ifNoMasterSchedulerRegisterThisScheduler('scheduler')

        then:
        !registered
    }

    def "if a scheduler is registered as master, increase heartbeat"() {
        given:
        def previousHeartbeat = DateTime.now().minusSeconds(21)
        registeredSchedulerRepository.findMasterScheduler() >>
                new RegisteredScheduler(1, 'scheduler', 'M', previousHeartbeat)

        when:
        def increased = service.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat('scheduler')

        then:
        increased
        1 * registeredSchedulerRepository.saveAndFlush(_) >> { RegisteredScheduler scheduler ->
            scheduler.heartbeat.isAfter(previousHeartbeat)
        }
    }

    def "if a scheduler is not registered as master, do not increase heartbeat"() {
        given:
        def previousHeartbeat = DateTime.now().minusSeconds(21)
        def scheduler = new RegisteredScheduler()
        scheduler.id = 1
        scheduler.schedulerName = 'other scheduler'
        scheduler.schedulerType = 'M'
        scheduler.heartbeat = previousHeartbeat
        registeredSchedulerRepository.findMasterScheduler() >> scheduler

        when:
        def increased = service.ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat('scheduler')

        then:
        !increased
        0 * registeredSchedulerRepository.saveAndFlush(_)
    }

    def setup() {
        registeredSchedulerRepository = Mock(RegisteredSchedulerRepository)
        service = new RegisteredSchedulerServiceImpl(registeredSchedulerRepository, 60)
    }
}
