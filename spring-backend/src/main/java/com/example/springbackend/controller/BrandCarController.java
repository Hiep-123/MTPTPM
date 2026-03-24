package com.example.springbackend.controller;

import com.example.springbackend.model.BrandCar;
import com.example.springbackend.repository.BrandCarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brandCar")
@CrossOrigin
public class BrandCarController {
    private final BrandCarRepository repo;

    public BrandCarController(BrandCarRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<BrandCar> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandCar> getById(@PathVariable String id) {
        Optional<BrandCar> b = repo.findById(id);
        return b.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public BrandCar create(@RequestBody BrandCar b) {
        return repo.save(b);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandCar> update(@PathVariable String id, @RequestBody BrandCar b) {
        return repo.findById(id).map(existing -> {
            b.setId(existing.getId());
            return ResponseEntity.ok(repo.save(b));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
