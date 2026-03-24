package com.example.springbackend.controller;

import com.example.springbackend.model.Comment;
import com.example.springbackend.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {
    private final CommentRepository repo;

    public CommentController(CommentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Comment> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(@PathVariable String id) {
        Optional<Comment> c = repo.findById(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Comment create(@RequestBody Comment c) {
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable String id, @RequestBody Comment c) {
        return repo.findById(id).map(existing -> {
            c.setId(existing.getId());
            return ResponseEntity.ok(repo.save(c));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
