package dev.thefable.url_shortener.controller;

import dev.thefable.url_shortener.dto.UrlRequest;
import dev.thefable.url_shortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    @Autowired
    UrlShortenerService urlShortenerService;

    @PostMapping
    public ResponseEntity<?> shortenUrl(@RequestBody UrlRequest urlRequest, HttpServletRequest req) {
        String ip = req.getRemoteAddr();

        if(urlShortenerService.isRateLimitExceeded(ip)) {
            return ResponseEntity.status(429).body("Too many requests");
        }

        return ResponseEntity.ok(urlShortenerService.shortenUrl(urlRequest.getUrl()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String url = urlShortenerService.resolveAndTrackClick(shortCode);

        return ResponseEntity.status(302)
                .header("Location", url)
                .build();
    }
}
