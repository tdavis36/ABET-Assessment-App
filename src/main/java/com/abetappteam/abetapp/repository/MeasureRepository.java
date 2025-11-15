package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Measure;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MeasureRepository extends JpaRepository<Measure, Long>{
    //Find all active Measures
    List<Measure> findByActiveTrue();

    //Find all inactive Measures
    List<Measure> findByActiveFalse();

    //Find Measures by CourseIndicatorId
    List<Measure> findByCourseIndicatorId(Long courseIndicatorId);

    //Find Measure by Status
    List<Measure> findByStatus(String status);

    //Find active Measures by CourseIndicatorId
    @Query("SELECT m FROM Measure m WHERE m.active = true AND m.courseIndicatorId = :courseIndicatorId")
    List<Measure> findActiveMeasuresByCourseIndicatorId(@Param("courseIndicatorId") Long courseIndicatorId);

    //Find active Measures by Status
    @Query("SELECT m FROM Measure m WHERE m.active = true AND  m.status = :status")
    List<Measure> findActiveMeasuresByCourseIndicatorIdAndStatus(@Param("status") String status);
}
