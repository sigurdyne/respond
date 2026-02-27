package no.yne.respond.event;


import lombok.Builder;

import java.util.List;

@Builder
public class ApiEvent {

    @Builder
    public static class Participant {
        private final String name;
        private final String email;
        private final String user;
    }

    private final String title;
    private final String description;

    // Participants map user information to the event
    private final List<Participant> participants;

    // Encodes the response of each participant, e.g., "accepted", "declined", "tentative"
    private final List<Responses> responses;
}
