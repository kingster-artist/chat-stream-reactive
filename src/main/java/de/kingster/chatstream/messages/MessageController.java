package de.kingster.chatstream.messages;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * access to chat messages
 * for example get messages, create a message and so on
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MessageController {

    public static final String[] texts = {
            "Das ist ein Test",
            "Wann ist heute Werkstatt?",
            "Ich mach heute Feierabend?",
            "Treffen wir uns im Pony?"
    };

    public static final String[] users = {"vall1", "beebee", "admin", "modbot", "Hans", "Venom", "chipchap86"};

    /**
     * produces = MediaType.TEXT_EVENT_STREAM_VALUE durch ServerSentEvent
     * Sendet jede Sekunde eine Chatnachricht an ein Frontend, welches mittels Stream verbunden ist
     * @return
     */
    @GetMapping("/api/v1/chat-messages/stream-reactive")
    public Flux<ServerSentEvent<String>> getChatMessageStreamReactive() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(sequence -> ServerSentEvent.<String>builder()
                //Eindeutigkeit sicherstellen
                .id(String.valueOf(sequence))

                //Kanal festlegen, auf den Hören wir im Frontend
                .event("chat-message-event")

                //Nachricht ist immer Text UTF8
                //Interpretation erfolgt am Frontend als JSON
                .data(serializeMessageAsJSON())
                .build());
    }

    /**
     * returns random chat message as JSON string
     * @return
     */
    private String serializeMessageAsJSON() {
        String text = getRandomStringFromArray(texts);
        String username = getRandomStringFromArray(users);
        String when = LocalDateTime.now().toString();
        return "{\"when\":\"" + when + "\",\"username\":\"" + username + "\",\"text\":\"" + text + "\"}";
    }

    /**
     * utility function to pick random element in string array
     * @param arr
     * @return
     */
    private static String getRandomStringFromArray(String[] arr) {
        int rnd = new Random().nextInt(arr.length);
        return arr[rnd];
    }

}
