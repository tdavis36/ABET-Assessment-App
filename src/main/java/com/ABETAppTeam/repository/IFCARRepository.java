package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;
import java.util.List;

public interface IFCARRepository {
    FCAR findById(int fcarId);
    List<FCAR> findAll();
    List<FCAR> findByCourseCode(String courseCode);
    List<FCAR> findByInstructorId(int instructorId);
    FCAR save(FCAR fcar);
    boolean update(FCAR fcar);
    boolean delete(int fcarId);
}
