package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllFilms_shouldReturnNonEmptyList() {
        ResponseEntity<List<Film>> response = restTemplate.exchange(
                "/films", HttpMethod.GET, null, new ParameterizedTypeReference<List<Film>>() {
                });

        List<Film> films = response.getBody();
        assertNotNull(films);
        assertFalse(films.isEmpty());

        Film firstFilm = films.get(0);
        assertNotNull(firstFilm.getId());
        assertNotNull(firstFilm.getName());
    }

    @Test
    void createFilmWithValidData_shouldReturnFilmWithExpectedFields() {
        Film film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .mpa(new RatingMpa(1, "G"))
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        Film createdFilm = response.getBody();
        assertEquals("nisi eiusmod", createdFilm.getName());
        assertEquals("adipisicing", createdFilm.getDescription());
        assertEquals(LocalDate.of(1967, 3, 25), createdFilm.getReleaseDate());
        assertEquals(100, createdFilm.getDuration());
    }

    @Test
    void createFilmWithEmptyName_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name(null)
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(-180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithTooLongDescription_shouldShowErrorMessage() {
        String description = "tratata".repeat(200);
        Film film = Film.builder().name("Avatar").description(description).
                releaseDate(LocalDate.now().minusYears(13)).duration(280).build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithMinusDuration_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(20))
                .duration(-180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateFilmWithEmptyName_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        Film film2 = Film.builder()
                .name(null)
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());

        System.out.println(response2.getBody());
        System.out.println("hello");
    }

    @Test
    void updateFilmWithTooLongDescription_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        restTemplate.postForLocation("/films", film);
        String description = "tratata".repeat(200);
        Film film2 = Film.builder()
                .name("Avatar")
                .description(description)
                .releaseDate(LocalDate.now().minusYears(13))
                .duration(180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }

    @Test
    void updateFilmWithMinusDuration_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(180)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        Film film2 = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(14))
                .duration(-180)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film2);
        ResponseEntity<Film> response2 = restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class);

        assertEquals("400 BAD_REQUEST", response2.getStatusCode().toString());
    }
}