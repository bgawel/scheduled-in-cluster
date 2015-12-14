package voters.jobs;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

public class Job1 {

    private static final Logger log = getLogger(Job1.class);

    @Scheduled(cron = "0 0/1 * * * ?")
    public void execute() {
      log.info("Executing job1");
    }
}
