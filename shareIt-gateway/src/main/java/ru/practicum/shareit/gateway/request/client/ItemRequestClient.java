package ru.practicum.shareit.gateway.request.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestClient {
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:9090/requests";

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto itemRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(itemRequestDto, headers);
        return restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl, HttpMethod.GET, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl + "/all", HttpMethod.GET, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl + "/" + requestId, HttpMethod.GET, requestEntity, Object.class);
    }
}
