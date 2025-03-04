package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "items", schema = "shareit_schema")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(length = 512)
    private String description;
    @Column(nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<String> comments;
}