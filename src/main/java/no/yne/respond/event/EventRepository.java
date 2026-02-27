package no.yne.respond.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findTop20ByStartTimeAfterOrderByStartTimeAsc(Instant after);
}
