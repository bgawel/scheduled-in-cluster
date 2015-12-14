package voters.scheduler;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

import static org.joda.time.DateTimeZone.UTC;

@Entity
class RegisteredScheduler implements Serializable {

    private static final long serialVersionUID = -6666637843591286669L;

    static final String MASTER_SCHEDULER_TYPE = "M";

    @Id
    @GeneratedValue
    private Integer id;
    @Length(max = 32)
    private String schedulerName;
    @Length(max = 1)
    private String schedulerType;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime heartbeat = DateTime.now(UTC);

    RegisteredScheduler(final String schedulerType) {
        this.schedulerType = schedulerType;
    }

    public RegisteredScheduler(Integer id, String schedulerName, String schedulerType, DateTime heartbeat) {
        this.id = id;
        this.schedulerName = schedulerName;
        this.schedulerType = schedulerType;
        this.heartbeat = heartbeat;
    }

    public RegisteredScheduler() {
    }

    static RegisteredScheduler newInstanceOfMasterScheduler() {
        return new RegisteredScheduler(MASTER_SCHEDULER_TYPE);
    }

    public Integer getId() {
        return this.id;
    }

    public String getSchedulerName() {
        return this.schedulerName;
    }

    public String getSchedulerType() {
        return this.schedulerType;
    }

    public DateTime getHeartbeat() {
        return this.heartbeat;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public void setSchedulerType(String schedulerType) {
        this.schedulerType = schedulerType;
    }

    public void setHeartbeat(DateTime heartbeat) {
        this.heartbeat = heartbeat;
    }
}
