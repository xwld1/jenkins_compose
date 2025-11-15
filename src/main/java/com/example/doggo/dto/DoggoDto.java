package com.example.doggo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoggoDto {
    private String name;
    private String url;
    private Long sizeBytes;
}