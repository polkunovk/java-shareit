package ru.practicum.shareit.gateway.booking.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;

@Service
@RequiredArgsConstructor
public class BookingClient {
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://shareit-server:9090/bookings";

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        HttpEntity<BookingDto> requestEntity = createHttpEntityWithContentType(bookingDto, userId);
        return restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<Object> approveBooking(Long ownerId, Long bookingId, boolean approved) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/" + bookingId)
                .queryParam("approved", approved)
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                createHttpEntity(ownerId),
                Object.class
        );
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/{bookingId}")
                .buildAndExpand(bookingId)
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(userId),
                Object.class
        );
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .queryParam("state", state)
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(userId),
                Object.class
        );
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/owner")
                .queryParam("state", state)
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                createHttpEntity(ownerId),
                Object.class
        );
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        return headers;
    }

    private HttpEntity<Void> createHttpEntity(Long userId) {
        return new HttpEntity<>(createHeaders(userId));
    }

    private HttpEntity<BookingDto> createHttpEntityWithContentType(BookingDto body, Long userId) {
        HttpHeaders headers = createHeaders(userId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}