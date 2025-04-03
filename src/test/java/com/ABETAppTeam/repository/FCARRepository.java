package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;

public interface FCARRepository {
    FCAR findById(String id);
    void save(FCAR fcar);
    void update(FCAR fcar);
    void delete(String id);
}
