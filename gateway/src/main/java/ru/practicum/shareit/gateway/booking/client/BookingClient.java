package ru.practicum.shareit.gateway.booking.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;

@Service
@RequiredArgsConstructor
public class BookingClient {
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://shareit-server:9090/bookings"; // URL `shareIt-server`

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookingDto> requestEntity = new HttpEntity<>(bookingDto, headers);
        return restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<Object> approveBooking(Long ownerId, Long bookingId, boolean approved) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                serverUrl + "/" + bookingId + "?approved=" + approved,
                HttpMethod.PATCH,
                requestEntity,
                Object.class
        );
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                serverUrl + "/" + bookingId,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                serverUrl + "?state=" + state,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                serverUrl + "/owner?state=" + state,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
    }
}