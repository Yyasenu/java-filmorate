package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new ValidationException("Фильм не найден");
        }

        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            film.getLikes().remove(userId);
            filmStorage.update(film);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }
}

