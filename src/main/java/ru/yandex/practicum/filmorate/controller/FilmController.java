package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма с id: {}", id);
        Film film = filmService.getFilmById(id);
        if (film == null) {
            throw new RuntimeException("Фильм с id = " + id + " не найден");
        }
        return film;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        log.info("Добавлен фильм: {}", createdFilm.getName());
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Обновлён фильм: {}", updatedFilm.getName());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        log.info("Пользователь {} убрал лайк у фильма {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
