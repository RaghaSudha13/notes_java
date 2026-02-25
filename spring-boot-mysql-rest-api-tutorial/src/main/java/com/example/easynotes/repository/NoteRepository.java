package com.example.easynotes.repository;

import com.example.easynotes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Note> searchByTitle(@Param("query") String query);

    @Query("SELECT n FROM Note n WHERE n.id = :id OR LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Note> searchByIdOrTitle(@Param("id") Long id, @Param("query") String query);
}
