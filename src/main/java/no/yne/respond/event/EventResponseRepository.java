package no.yne.respond.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventResponseRepository extends JpaRepository<EventResponse, UUID> {

    List<EventResponse> findByEventId(UUID eventId);
}
