package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    //Find all active programs
    List<Program> findByActiveTrue();

    //FInd all inactive programs
    List<Program> findByActiveFalse();

    //Find programs by name containing case insensitive search term
    List<Program> findByNameContainingIgnoreCase(String nameFragment);

    //Find active programs with name containing search term
    @Query("SELECT p FROM Program p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Program> findActiveProgramsByNameContaining(@Param("searchTerm") String searchTerm);
}
