package com.abetappteam.abetapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abetappteam.abetapp.entity.Outcome;

public interface OutcomeRepository extends JpaRepository<Outcome, Long>{
    //Find all active outcomes
    List<Outcome> findByActiveTrue();

    //Find all inactive outcomes
    List<Outcome> findByActiveFalse();

    //Check if any Outcomes exist within semester
    Boolean existsBySemesterId(Long semesterId);

    //Check if Outcome exists within semester by number
    Boolean existsBySemesterIdAndNumber(Long semesterId, Integer number);

    //Find by Semester id and Outcome Number
    //Although it shouldn't be done, it's technically possible to have two Outcomes in a semester with the same nunber
    //Therefore, this returns a List and not an Optional
    @Query("SELECT o FROM Outcome o WHERE o.semesterId = :semesterId AND o.number = :number")
    List<Outcome> findBySemesterIdAndOutNum(@Param("semesterId") Long semesterId, @Param("number") Integer number);

    //Find Active Outcomes by Semester Id
    @Query("SELECT o FROM Outcome o WHERE o.active = :active AND o.semesterId = :semesterId")
    List<Outcome> findBySemesterIdAndActive(@Param("semesterId") Long semesterId, @Param("active") Boolean active);
}
