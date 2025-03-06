package ru.practicum.shareit.gateway.item.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

@Service
@RequiredArgsConstructor
public class ItemClient {
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:9090/items";

    public ResponseEntity<Object> addItem(Long ownerId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);
        return restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long id, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);
        return restTemplate.exchange(serverUrl + "/" + id, HttpMethod.PATCH, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getItem(Long id, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl + "/" + id, HttpMethod.GET, requestEntity, Object.class);
    }

    public ResponseEntity<Object> getUserItems(Long ownerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", ownerId.toString());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl, HttpMethod.GET, requestEntity, Object.class);
    }

    public ResponseEntity<Object> searchItems(String text) {
        return restTemplate.getForEntity(serverUrl + "/search?text=" + text, Object.class);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto, headers);
        return restTemplate.exchange(serverUrl + "/" + itemId + "/comment", HttpMethod.POST, requestEntity, Object.class);
    }
}
