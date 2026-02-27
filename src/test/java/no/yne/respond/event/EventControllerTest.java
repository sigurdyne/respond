package no.yne.respond.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class EventControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventResponseRepository eventResponseRepository;

    @BeforeEach
    void setUp() {
        eventResponseRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void createEvent_savesEventWithParticipantsAndResponses() throws Exception {
        String startTime = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        String requestBody = """
                {
                    "title": "Team standup",
                    "description": "Daily sync",
                    "startTime": "%s",
                    "participants": [
                        {"name": "Alice", "email": "alice@example.com"},
                        {"name": "Bob", "email": "bob@example.com"}
                    ],
                    "responses": [
                        {"email": "alice@example.com", "status": "accepted"},
                        {"email": "bob@example.com", "status": "tentative"}
                    ]
                }
                """.formatted(startTime);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team standup"))
                .andExpect(jsonPath("$.description").value("Daily sync"))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants.length()").value(2))
                .andExpect(jsonPath("$.participants[0].name").value("Alice"))
                .andExpect(jsonPath("$.responses").isArray())
                .andExpect(jsonPath("$.responses.length()").value(2))
                .andExpect(jsonPath("$.responses[0].status").value("accepted"));

        // Verify data was persisted
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Team standup");

        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).hasSize(2);

        List<EventResponse> responses = eventResponseRepository.findAll();
        assertThat(responses).hasSize(2);
    }

    @Test
    void fetchUpcomingEvents_returnsEventsOrderedByStartTime() throws Exception {
        // Create events with different start times
        Event pastEvent = new Event();
        pastEvent.setTitle("Past event");
        pastEvent.setStartTime(Instant.now().minus(1, ChronoUnit.DAYS));
        eventRepository.save(pastEvent);

        Event soonEvent = new Event();
        soonEvent.setTitle("Soon event");
        soonEvent.setStartTime(Instant.now().plus(1, ChronoUnit.HOURS));
        eventRepository.save(soonEvent);

        Event laterEvent = new Event();
        laterEvent.setTitle("Later event");
        laterEvent.setStartTime(Instant.now().plus(7, ChronoUnit.DAYS));
        eventRepository.save(laterEvent);

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Soon event"))
                .andExpect(jsonPath("$[1].title").value("Later event"));
    }
}
