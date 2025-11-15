package com.example.doggo.controller;

import com.example.doggo.dto.DoggoDto;
import com.example.doggo.model.Doggo;
import com.example.doggo.repository.DoggoRepository;
import com.example.doggo.service.RemoteDogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class DoggoController {

    private final RemoteDogService service;
    private final DoggoRepository repo;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public DoggoController(RemoteDogService service, DoggoRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping("/woof")
    public DoggoDto woof() {
        return service.fetchRandom();
    }

    @GetMapping("/doggos")
    public List<DoggoDto> all() {
        return repo.findAll().stream()
                .map(d -> new DoggoDto(d.getName(), d.getUrl(), d.getSizeBytes()))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DoggoDto uploadUrl(@RequestBody java.util.Map<String,String> body) {
        String url = body.get("url");
        if (url == null) throw new IllegalArgumentException("Missing 'url' in body");
        return service.uploadByUrl(url);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DoggoDto uploadFile(@RequestPart("file") MultipartFile file) throws Exception {
        Path dir = Paths.get(uploadDir);
        return service.uploadFile(file, dir);
    }
}