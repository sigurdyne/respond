package no.yne.respond.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EventResponseRepository eventResponseRepository;

    public EventController(EventRepository eventRepository,
                           ParticipantRepository participantRepository,
                           EventResponseRepository eventResponseRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.eventResponseRepository = eventResponseRepository;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiEvent createEvent(@RequestBody ApiEvent request) {

        // Save the event
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        eventRepository.save(event);

        // Save participants
        List<ApiEvent.Participant> savedParticipants = new ArrayList<>();
        try {
            if (request.getParticipants() != null) {
                for (ApiEvent.Participant p : request.getParticipants()) {
                    Participant participant = new Participant();
                    participant.setEvent(event);
                    participant.setName(p.getName());
                    participant.setEmail(p.getEmail());
                    if (p.getUserId() != null) {
                        participant.setUserId(UUID.fromString(p.getUserId()));
                    }
                    participantRepository.save(participant);

                    savedParticipants.add(ApiEvent.Participant.builder()
                            .name(participant.getName())
                            .email(participant.getEmail())
                            .userId(p.getUserId())
                            .build());
                }
            }
        } catch (Exception e ){
            log.error("Failed to save participants", e);
        }

        // Save responses
        List<ApiEvent.Response> savedResponses = new ArrayList<>();
        if (request.getResponses() != null) {
            for (ApiEvent.Response r : request.getResponses()) {
                EventResponse response = new EventResponse();
                response.setEvent(event);
                response.setEmail(r.getEmail());
                response.setStatus(r.getStatus());
                eventResponseRepository.save(response);

                savedResponses.add(ApiEvent.Response.builder()
                        .email(response.getEmail())
                        .status(response.getStatus())
                        .build());
            }
        }

        return ApiEvent.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .participants(savedParticipants)
                .responses(savedResponses)
                .build();
    }
}
