package voters.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface RegisteredSchedulerRepository extends JpaRepository<RegisteredScheduler, Integer> {

    @Query("select s from RegisteredScheduler s where schedulerType='" + RegisteredScheduler.MASTER_SCHEDULER_TYPE + "'")
    RegisteredScheduler findMasterScheduler();
}
