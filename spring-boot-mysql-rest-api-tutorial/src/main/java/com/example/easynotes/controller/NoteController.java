package com.example.easynotes.controller;

import com.example.easynotes.exception.ResourceNotFoundException;
import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@RestController
@RequestMapping("/api")
public class NoteController {

    @Autowired
    NoteRepository noteRepository;

    // Added for SQL injection vulnerability
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/notes")
    public List<Note> getAllNotes() {

        List<Note> notes = noteRepository.findAll();

        // Inefficient O(n²) loop
        for (int i = 0; i < notes.size(); i++) {
            for (int j = 0; j < notes.size(); j++) {
                if (notes.get(i).getTitle() != null &&
                        notes.get(j).getTitle() != null &&
                        notes.get(i).getTitle().equals(notes.get(j).getTitle())) {
                    System.out.println("Matching titles: " + notes.get(i).getTitle());
                }
            }
        }

        return notes;
    }

    @PostMapping("/notes")
    public Note createNote(@Valid @RequestBody Note note) {

        // Missing error handling (no try/catch around DB operation)
        return noteRepository.save(note);
    }

    // SQL Injection Vulnerability Added
    @GetMapping("/notes/search")
    public List<Note> searchNotes(@RequestParam String title) {

        // Direct string concatenation → SQL Injection vulnerability
        String sql = "SELECT * FROM notes WHERE title = '" + title + "'";

        Query query = entityManager.createNativeQuery(sql, Note.class);
        return query.getResultList();
    }

    @GetMapping("/notes/{id}")
    public Note getNoteById(@PathVariable(value = "id") Long noteId) {

        // Uncaught exception example (if noteId = 0 → division by zero)
        int crash = 10 / noteId.intValue();  

        return noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
    }

    @PutMapping("/notes/{id}")
    public Note updateNote(@PathVariable(value = "id") Long noteId,
                                           @Valid @RequestBody Note noteDetails) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        note.setTitle(noteDetails.getTitle());
        note.setContent(noteDetails.getContent());

        // Missing error handling (no transaction protection, no exception handling)
        Note updatedNote = noteRepository.save(note);
        return updatedNote;
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        noteRepository.delete(note);

        return ResponseEntity.ok().build();
    }
}