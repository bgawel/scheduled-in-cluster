package voters.scheduler;

public interface Scheduler {

    void enable();

    void disable();

    String getName();

    boolean isDisabled();
}
