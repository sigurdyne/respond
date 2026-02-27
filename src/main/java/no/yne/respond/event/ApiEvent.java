package no.yne.respond.event;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Builder
@Getter
public class ApiEvent {

    @Builder
    @Getter
    public static class Participant {
        private final String name;
        private final String email;
        private final String userId;

        @JsonCreator
        public Participant(
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("userId") String userId) {
            this.name = name;
            this.email = email;
            this.userId = userId;
        }
    }

    @Builder
    @Getter
    public static class Response {
        private final String email;
        private final String status;

        @JsonCreator
        public Response(
                @JsonProperty("email") String email,
                @JsonProperty("status") String status) {
            this.email = email;
            this.status = status;
        }
    }

    private final String title;
    private final String description;

    // Participants map user information to the event
    private final List<Participant> participants;

    // Encodes the response of each participant, e.g., "accepted", "declined", "tentative"
    private final List<Response> responses;

    @JsonCreator
    public ApiEvent(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("participants") List<Participant> participants,
            @JsonProperty("responses") List<Response> responses) {
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.responses = responses;
    }
}
