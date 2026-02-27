package no.yne.respond.event;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void createEvent_savesEventWithParticipantsAndResponses() throws Exception {
        String requestBody = """
                {
                    "title": "Team standup",
                    "description": "Daily sync",
                    "participants": [
                        {"name": "Alice", "email": "alice@example.com"},
                        {"name": "Bob", "email": "bob@example.com"}
                    ],
                    "responses": [
                        {"email": "alice@example.com", "status": "accepted"},
                        {"email": "bob@example.com", "status": "tentative"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team standup"))
                .andExpect(jsonPath("$.description").value("Daily sync"))
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
}
