package ru.practicum.shareit.gateway.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://shareit-server:9090/users"; // URL `shareIt-server`

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return restTemplate.postForEntity(serverUrl, userDto, Object.class);
    }

    public ResponseEntity<Object> getUser(Long id) {
        return restTemplate.getForEntity(serverUrl + "/" + id, Object.class);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);

        return restTemplate.exchange(
                serverUrl + "/" + userId,
                HttpMethod.PATCH,
                requestEntity,
                Object.class
        );
    }


    public ResponseEntity<Void> deleteUser(Long id) {
        restTemplate.delete(serverUrl + "/" + id);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Object> getAllUsers() {
        return restTemplate.getForEntity(serverUrl, Object.class);
    }
}