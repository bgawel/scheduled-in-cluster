package voters.scheduler

import spock.lang.Specification

import static com.jayway.awaitility.Awaitility.await
import static com.jayway.awaitility.Awaitility.fieldIn
import static java.util.concurrent.TimeUnit.SECONDS
import static org.hamcrest.Matchers.equalTo

class ScheduledVoterTaskTest extends Specification {

    ScheduledVoterTask task

    def "start a voter task"() {
        given:
        def status = false

        when:
        task.start({ status = true } as Runnable)

        then:
        await().atMost(2, SECONDS).until(fieldIn(status).ofType(boolean.class), equalTo(true))
    }

    def setup() {
        task = new ScheduledVoterTask(1)
    }
}
