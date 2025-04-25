package com.ABETAppTeam.repository.impl;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.repository.IFCARRepository;

import java.util.*;

public class InMemoryFCARRepository implements IFCARRepository {

    private final Map<Integer, FCAR> store = new HashMap<>();
    private int currentId = 1;

    @Override
    public FCAR findById(int fcarId) {
        return store.get(fcarId);
    }

    @Override
    public List<FCAR> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> list = new ArrayList<>();
        for (FCAR f : store.values()) {
            if (f.getCourseId().equals(courseCode)) {
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> list = new ArrayList<>();
        for (FCAR f : store.values()) {
            // professorId is a string in FCAR, so parse it, or store it as an int in your real code
            if (String.valueOf(instructorId).equals(f.getProfessorId())) {
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public FCAR save(FCAR fcar) {
        // If no ID => create new
        if (fcar.getFcarId() == 0) {
            fcar.setFcarId(currentId++);
        }
        store.put(fcar.getFcarId(), fcar);
        return fcar;
    }

    @Override
    public boolean update(FCAR fcar) {
        int id = fcar.getFcarId();
        if (!store.containsKey(id)) {
            return false;
        }
        store.put(id, fcar);
        return true;
    }

    @Override
    public boolean delete(int fcarId) {
        return store.remove(fcarId) != null;
    }

    @Override
    public List<FCAR> findBySemesterAndYear(String semester, int year) {
        return List.of();
    }
}
