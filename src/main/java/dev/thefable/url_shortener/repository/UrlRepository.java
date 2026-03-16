package dev.thefable.url_shortener.repository;

import dev.thefable.url_shortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    public Optional<Url> findByShortUrl(String shortUrl);
}
