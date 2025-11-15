package com.example.doggo.service;

import com.example.doggo.dto.DoggoDto;
import com.example.doggo.model.Doggo;
import com.example.doggo.repository.DoggoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Service
public class RemoteDogService {

    private final RestTemplate rest = new RestTemplate();
    private final DoggoRepository repo;

    @Value("${randomdog.api:https://random.dog/woof.json}")
    private String randomDogApi;

    public RemoteDogService(DoggoRepository repo) {
        this.repo = repo;
    }

    public DoggoDto fetchRandom() {
        Map resp = rest.getForObject(randomDogApi, Map.class);
        if (resp == null) throw new RuntimeException("Empty response from random.dog");

        Object urlObj = resp.get("url");
        Object sizeObj = resp.get("fileSizeBytes");

        String url = urlObj != null ? urlObj.toString() : null;
        Long size = sizeObj != null ? Long.valueOf(sizeObj.toString()) : null;

        String name = extractName(url);

        // save if not exists
        repo.findByName(name).orElseGet(() -> {
            Doggo d = Doggo.builder()
                    .name(name)
                    .url(url)
                    .sizeBytes(size)
                    .createdAt(Instant.now())
                    .build();
            return repo.save(d);
        });

        return new DoggoDto(name, url, size);
    }

    public DoggoDto uploadByUrl(String url) {
        String name = extractName(url);
        Long size = headRequestSize(url);
        Doggo d = repo.findByName(name).orElseGet(() -> {
            Doggo x = Doggo.builder()
                    .name(name).url(url).sizeBytes(size).createdAt(Instant.now()).build();
            return repo.save(x);
        });
        return new DoggoDto(d.getName(), d.getUrl(), d.getSizeBytes());
    }

    public DoggoDto uploadFile(MultipartFile file, java.nio.file.Path uploadDir) throws Exception {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (original == null || original.isBlank()) original = "upload-" + System.currentTimeMillis();
        java.nio.file.Path target = uploadDir.resolve(original);
        java.nio.file.Files.createDirectories(uploadDir);
        file.transferTo(target);

        String url = "file://" + target.toAbsolutePath().toString();
        Doggo d = repo.findByName(original).orElseGet(() -> {
            Doggo x = Doggo.builder()
                    .name(original).url(url).sizeBytes(file.getSize()).createdAt(Instant.now()).build();
            return repo.save(x);
        });
        return new DoggoDto(d.getName(), d.getUrl(), d.getSizeBytes());
    }

    private Long headRequestSize(String url) {
        try {
            ResponseEntity<String> resp = rest.exchange(URI.create(url), HttpMethod.HEAD, null, String.class);
            List<String> cl = resp.getHeaders().get("Content-Length");
            if (cl != null && !cl.isEmpty()) return Long.valueOf(cl.get(0));
        } catch (Exception ignored) {}
        return null;
    }

    private String extractName(String url) {
        if (url == null) return "unknown";
        int idx = url.lastIndexOf('/');
        return idx >= 0 ? url.substring(idx + 1) : url;
    }
}