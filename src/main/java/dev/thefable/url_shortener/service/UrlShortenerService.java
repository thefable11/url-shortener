package dev.thefable.url_shortener.service;

import dev.thefable.url_shortener.model.Url;
import dev.thefable.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class IpInfo {
    LocalTime localTime;
    long reqCount;

    public IpInfo(LocalTime localTime, long reqCount) {
        this.localTime = localTime;
        this.reqCount = reqCount;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public long getReqCount() {
        return reqCount;
    }

    public void setReqCount(long reqCount) {
        this.reqCount = reqCount;
    }
}

@Service
public class UrlShortenerService {

    @Autowired
    private UrlRepository urlRepository;

    private Map<String, IpInfo> ipInfoMap = new ConcurrentHashMap<>();


    public Boolean isRateLimitExceeded(String ip) {
        if(!ipInfoMap.containsKey(ip)) {
            ipInfoMap.put(ip, new IpInfo(LocalTime.now(), 1));
            return Boolean.FALSE;
        } else {
            LocalTime prevTime = ipInfoMap.get(ip).getLocalTime();
            LocalTime localTime = LocalTime.now();

            Duration dur = Duration.between(prevTime, localTime);

            if(dur.toMinutes() < 1 && ipInfoMap.get(ip).getReqCount() < 5) {
                ipInfoMap.put(ip, new IpInfo(prevTime, ipInfoMap.get(ip).getReqCount() + 1));
                return Boolean.FALSE;
            } else if(dur.toMinutes() >= 1) {
                ipInfoMap.put(ip, new IpInfo(localTime, 1));
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }

        }
    }

    public String shortenUrl(String url) {
        Url urlEntity = new Url(url);
        urlRepository.saveAndFlush(urlEntity);

        long id = urlEntity.getId();
        String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        long base = 62;
        int MIN_LENGTH = 4;
        StringBuilder shortUrlPart = new StringBuilder();

        if (id == 0) {
            shortUrlPart.append(charSet.charAt(0));
        } else {
            while (id > 0) {
                shortUrlPart.append(charSet.charAt((int)(id % base)));
                id /= base;
            }
        }

        shortUrlPart.reverse();

        while(shortUrlPart.length() < MIN_LENGTH) {
            shortUrlPart.insert(0, 'a');
        }

        String shortCode = shortUrlPart.toString();



        urlEntity.setShortUrl(shortCode);
        urlRepository.save(urlEntity);

        return shortCode;
    }

    public String resolveAndTrackClick(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(()->new RuntimeException("Short URL not found"));

        url.setClickCount(url.getClickCount()+1);
        urlRepository.save(url);

        return url.getUrl();
    }
}
