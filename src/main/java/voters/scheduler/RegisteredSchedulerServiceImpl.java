package voters.scheduler;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Transactional(isolation = REPEATABLE_READ)
class RegisteredSchedulerServiceImpl implements RegisteredSchedulerService {

    private static final Logger log = getLogger(RegisteredSchedulerServiceImpl.class);

    private final RegisteredSchedulerRepository registeredSchedulerRepository;
    private final int noActiveHeartbeatSeconds;

    RegisteredSchedulerServiceImpl(final RegisteredSchedulerRepository registeredSchedulerRepository,
                                   final int noActiveHeartbeatSeconds) {
        this.registeredSchedulerRepository = registeredSchedulerRepository;
        this.noActiveHeartbeatSeconds = noActiveHeartbeatSeconds;
    }

    @Override
    public boolean ifNoMasterSchedulerRegisterThisScheduler(final String schedulerName) {
        RegisteredScheduler masterScheduler = registeredSchedulerRepository.findMasterScheduler();
        DateTime now = DateTime.now();
        if (masterScheduler == null || notThisSchedulerIsInactive(schedulerName, masterScheduler, now)) {
            if (masterScheduler == null) {
                masterScheduler = RegisteredScheduler.newInstanceOfMasterScheduler();
            }
            masterScheduler.setSchedulerName(schedulerName);
            log.info("Registering master scheduler {}", schedulerName);
            return saveOrUpdateMasterScheduler(masterScheduler, now);
        }
        return false;
    }

    @Override
    public boolean ifThisSchedulerIsRegisteredAsMasterIncreaseHeartbeat(final String schedulerName) {
        RegisteredScheduler masterScheduler = registeredSchedulerRepository.findMasterScheduler();
        if (masterScheduler.getSchedulerName().equals(schedulerName)) {
            log.debug("Increasing heartbeat for scheduler {}", schedulerName);
            return saveOrUpdateMasterScheduler(masterScheduler, DateTime.now());
        }
        return false;
    }

    private boolean saveOrUpdateMasterScheduler(final RegisteredScheduler masterScheduler, final DateTime now) {
        masterScheduler.setHeartbeat(now);
        try {
            registeredSchedulerRepository.saveAndFlush(masterScheduler);
        } catch (OptimisticLockingFailureException e) {
            log.warn("Cannot save or update scheduler; most probably another scheduler won", e);
            return false;
        }
        return true;
    }

    private boolean notThisSchedulerIsInactive(String schedulerName, RegisteredScheduler masterScheduler, DateTime now) {
        return !masterScheduler.getSchedulerName().equals(schedulerName) &&
                now.minusSeconds(noActiveHeartbeatSeconds).isAfter(masterScheduler.getHeartbeat());
    }
}
