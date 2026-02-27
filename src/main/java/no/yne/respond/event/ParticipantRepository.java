package no.yne.respond.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    List<Participant> findByEventId(UUID eventId);
}
