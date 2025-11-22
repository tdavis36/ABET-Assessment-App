package com.abetappteam.abetapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abetappteam.abetapp.entity.Outcome;
import com.abetappteam.abetapp.dto.OutcomeDTO;
import com.abetappteam.abetapp.repository.OutcomeRepository;

@Service
public class OutcomeService extends BaseService<Outcome, Long, OutcomeRepository>{
    
    @Autowired
    public OutcomeService(OutcomeRepository repository){
        super(repository);
    }

    @Override
    protected String getEntityName(){
        return "Outcome";
    }

    @Transactional
    public Outcome create(OutcomeDTO dto){
        Outcome outcome = new Outcome();
        outcome.setDescription(dto.getDescription());
        outcome.setNumber(dto.getNumber());
        outcome.setEvaluation(dto.getEvaluation());
        outcome.setSemesterId(dto.getSemesterId());
        outcome.setActive(dto.getActive());
        outcome.setValue(dto.getValue());
        logger.info("Creating new student outcome: {}", outcome.getNumber());
        return repository.save(outcome);
    }

    @Transactional
    public Outcome update(Long id, OutcomeDTO dto){
        Outcome outcome = findById(id);

        outcome.setDescription(dto.getDescription());
        outcome.setNumber(dto.getNumber());
        outcome.setEvaluation(dto.getEvaluation());
        outcome.setSemesterId(dto.getSemesterId());
        outcome.setValue(dto.getValue());

        if(dto.getActive() != null){
            outcome.setActive(dto.getActive());
        }
        logger.info("Updating student outcome: {}", id);
        return repository.save(outcome);
    }

    //Activate Student Outcome
    @Transactional
    public Outcome activate(Long id){
        Outcome outcome = findById(id);
        outcome.setActive(true);
        logger.info("Activating Student Outcome: {}", id);
        return repository.save(outcome);
    }
    
    //Deactivate Student Outcome
    @Transactional
    public Outcome deactivate(Long id){
        Outcome outcome = findById(id);
        outcome.setActive(false);
        logger.info("Deactivaitng Student Outcome: {}", id);
        return repository.save(outcome);
    }

    //Return all active student outcomes
    @Transactional(readOnly = true)
    public List<Outcome> findAllActive() {
        return repository.findByActiveTrue();
    }

    //Return all inactive student outcomes
    @Transactional(readOnly = true)
    public List<Outcome> findAllInactive() {
        return repository.findByActiveFalse();
    }

    //Return all active student outcomes by semester id
    @Transactional(readOnly = true)
    public List<Outcome> findActiveOutcomesBySemester(Long semesterId) {
        return repository.findBySemesterIdAndActive(semesterId, true);
    }

    //Return all inactive student outcomes by semester id
    @Transactional(readOnly = true)
    public List<Outcome> findInactiveOutcomesBySemester(Long semesterId) {
        return repository.findBySemesterIdAndActive(semesterId, false);
    }

    //Return list of student outcomes by semester id and number
    //It's possible to make multiple Outcomes with the same number so it returns a list
    @Transactional(readOnly = true)
    public List<Outcome> findOutcomesBySemesterAndNumber(Long semesterId, Integer number){
        return repository.findBySemesterIdAndOutNum(semesterId, number);
    }
}
