package com.example.doggo.repository;

import com.example.doggo.model.Doggo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoggoRepository extends JpaRepository<Doggo, Long> {
    Optional<Doggo> findByName(String name);
}