package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;

    @JsonIgnore
    final private Set<Integer> friendIds = new HashSet<>();

    public void addFriend(Integer id) {
        friendIds.add(id);
    }

    public void deleteFriend(Integer id) {
        friendIds.remove(id);
    }
}