package ro.unibuc.prodeng.metrics;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import ro.unibuc.prodeng.repository.AppointmentRepository;
import ro.unibuc.prodeng.repository.TodoRepository;
import ro.unibuc.prodeng.repository.UserRepository;

@Component
public class ApplicationMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter usersCreatedCounter;
    private final Counter todosCreatedCounter;
    private final Timer userSearchDuration;
    private final DistributionSummary userSearchResults;
    private final Timer todoFetchDuration;
    private final Timer appointmentCreateDuration;
    private final Counter outsideWorkingHoursRejectedCounter;
    private final Counter duplicateSlotRejectedCounter;
    private final Counter validationErrorRejectedCounter;

    public ApplicationMetrics(
            MeterRegistry meterRegistry,
            UserRepository userRepository,
            TodoRepository todoRepository,
            AppointmentRepository appointmentRepository) {
        this.meterRegistry = meterRegistry;
        this.usersCreatedCounter = Counter.builder("prod.eng.users.created")
                .description("Number of users created through the API")
                .register(meterRegistry);
        this.todosCreatedCounter = Counter.builder("prod.eng.todos.created")
                .description("Number of todos created through the API")
                .register(meterRegistry);
        this.userSearchDuration = Timer.builder("prod.eng.user.search.duration")
                .description("Time spent searching users by name")
                .register(meterRegistry);
        this.userSearchResults = DistributionSummary.builder("prod.eng.user.search.results")
                .description("Number of results returned by user search requests")
                .baseUnit("results")
                .register(meterRegistry);
        this.todoFetchDuration = Timer.builder("prod.eng.todo.fetch.duration")
                .description("Time spent loading todos for an assignee")
                .register(meterRegistry);
        this.appointmentCreateDuration = Timer.builder("prod.eng.appointment.create.duration")
                .description("Time spent creating an appointment")
                .register(meterRegistry);
        this.outsideWorkingHoursRejectedCounter = meterRegistry.counter(
                "prod.eng.appointments.rejected",
                "reason", "outside_working_hours");
        this.duplicateSlotRejectedCounter = meterRegistry.counter(
                "prod.eng.appointments.rejected",
                "reason", "duplicate_slot");
        this.validationErrorRejectedCounter = meterRegistry.counter(
                "prod.eng.appointments.rejected",
                "reason", "validation_error");

        Gauge.builder("prod.eng.users.total", userRepository, UserRepository::count)
                .description("Current number of users stored in MongoDB")
                .register(meterRegistry);
        Gauge.builder("prod.eng.todos.total", todoRepository, TodoRepository::count)
                .description("Current number of todos stored in MongoDB")
                .register(meterRegistry);
        Gauge.builder("prod.eng.appointments.total", appointmentRepository, AppointmentRepository::count)
                .description("Current number of appointments stored in MongoDB")
                .register(meterRegistry);
    }

    public void recordUserCreated() {
        usersCreatedCounter.increment();
    }

    public void recordUserSearch(int resultCount, long durationNanos) {
        userSearchDuration.record(durationNanos, TimeUnit.NANOSECONDS);
        userSearchResults.record(resultCount);
    }

    public void recordTodoFetch(int resultCount, long durationNanos) {
        todoFetchDuration.record(durationNanos, TimeUnit.NANOSECONDS);
        meterRegistry.summary("prod.eng.todo.fetch.results", "operation", "list-by-assignee")
                .record(resultCount);
    }

    public void recordTodoCreated() {
        todosCreatedCounter.increment();
    }

    public void recordTodoStatusChange(boolean done) {
        meterRegistry.counter("prod.eng.todos.status.changes", "done", Boolean.toString(done)).increment();
    }

    public void recordAppointmentCreated(String serviceType, long durationNanos) {
        appointmentCreateDuration.record(durationNanos, TimeUnit.NANOSECONDS);
        meterRegistry.counter("prod.eng.appointments.created", "service_type", normalizeTagValue(serviceType))
                .increment();
    }

    public void recordAppointmentStatusUpdate(String currentStatus, String newStatus) {
        meterRegistry.counter(
                "prod.eng.appointments.status.updated",
                "from", normalizeTagValue(currentStatus),
                "to", normalizeTagValue(newStatus)).increment();
    }

    public void recordAppointmentRejected(String reason) {
        switch (normalizeTagValue(reason)) {
            case "outside_working_hours" -> outsideWorkingHoursRejectedCounter.increment();
            case "duplicate_slot" -> duplicateSlotRejectedCounter.increment();
            default -> validationErrorRejectedCounter.increment();
        }
    }

    private String normalizeTagValue(String value) {
        return value == null ? "unknown" : value.trim().toLowerCase().replace(' ', '_');
    }
}
