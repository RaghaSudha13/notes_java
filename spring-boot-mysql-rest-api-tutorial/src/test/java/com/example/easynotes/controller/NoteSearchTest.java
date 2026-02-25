package com.example.easynotes.controller;

import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();

        Note note1 = new Note();
        note1.setTitle("Spring Boot Guide");
        note1.setContent("A guide to Spring Boot");

        Note note2 = new Note();
        note2.setTitle("Docker Tutorial");
        note2.setContent("Learn Docker basics");

        Note note3 = new Note();
        note3.setTitle("Spring Security");
        note3.setContent("Securing Spring apps");

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);
    }

    @Test
    void searchByTitle_returnsMatchingNotes() throws Exception {
        mockMvc.perform(get("/api/notes/search")
                        .param("query", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", everyItem(containsStringIgnoringCase("Spring"))));
    }

    @Test
    void searchByTitle_caseInsensitive() throws Exception {
        mockMvc.perform(get("/api/notes/search")
                        .param("query", "docker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Docker Tutorial"));
    }

    @Test
    void searchByTitle_noResults() throws Exception {
        mockMvc.perform(get("/api/notes/search")
                        .param("query", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchById_numericQuery() throws Exception {
        Note saved = noteRepository.findAll().get(0);
        Long id = saved.getId();

        mockMvc.perform(get("/api/notes/search")
                        .param("query", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());
    }

    @Test
    void search_missingQueryParam_returns400() throws Exception {
        mockMvc.perform(get("/api/notes/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_blankQueryParam_returns400() throws Exception {
        mockMvc.perform(get("/api/notes/search")
                        .param("query", "   "))
                .andExpect(status().isBadRequest());
    }
}
