package com.example.doggo.controller;

import com.example.doggo.dto.DogRecordDto;
import com.example.doggo.model.DogRecord;
import com.example.doggo.repository.DogRecordRepository;
import com.example.doggo.service.DogApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dog")
public class DogController {

    private final DogApiService service;
    private final DogRecordRepository repo;

    public DogController(DogApiService service, DogRecordRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    /**
     * GET /dog/random
     * HTML-страница с картинкой и кнопкой "Следующая собачка"
     */
    @GetMapping(value = "/random", produces = "text/html")
    public String random() {
        DogRecord r = service.fetchRandomDog();
        return """
            <html>
                <head>
                    <title>Random Dog</title>
                    <style>
                        body { font-family: sans-serif; text-align: center; margin-top: 50px; }
                        img { max-width: 600px; margin-top: 20px; border: 2px solid #ccc; border-radius: 10px; }
                        button { margin-top: 20px; padding: 10px 20px; font-size: 16px; cursor: pointer; }
                    </style>
                </head>
                <body>
                    <h1>Random Dog</h1>
                    <p><strong>ID:</strong> %d</p>
                    <p><strong>Size:</strong> %d bytes</p>
                    <img src="%s" alt="Random Dog">
                    <br>
                    <button onclick="window.location.reload()">Следующая собачка 🐶</button>
                </body>
            </html>
            """.formatted(r.getId(), r.getSizeBytes(), r.getUrl());
    }

    /**
     * GET /dog/history
     * Возвращает всю историю запросов в виде JSON
     */
    @GetMapping("/history")
    public List<DogRecordDto> history() {
        return repo.findAll().stream()
                .map(r -> new DogRecordDto(r.getId(), r.getUrl(), r.getSizeBytes(), r.getCreatedAt().toString()))
                .collect(Collectors.toList());
    }

    /**
     * GET /dog/history/{id}
     * Возвращает конкретную запись по ID в виде JSON
     */
    @GetMapping("/history/{id}")
    public DogRecordDto getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(r -> new DogRecordDto(r.getId(), r.getUrl(), r.getSizeBytes(), r.getCreatedAt().toString()))
                .orElseThrow(() -> new RuntimeException("Record not found: " + id));
    }
}