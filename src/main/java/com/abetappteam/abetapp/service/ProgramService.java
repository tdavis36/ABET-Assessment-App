package com.abetappteam.abetapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.repository.ProgramRepository;

import java.util.List;

@Service
public class ProgramService extends BaseService<Program, Long, ProgramRepository>{
    @Autowired
    public ProgramService(ProgramRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName(){
        return "Program";
    }

    //Create new program from DTO
    @Transactional
    public Program create(ProgramDTO dto){
        Program program = new Program();
        program.setName(dto.getName());
        program.setInstitution(dto.getInstitution());
        program.setActive(dto.getActive() != null ? dto.getActive() : true);

        logger.info("Creating new program: {}", dto.getName());
        return repository.save(program);
    }

    //Update existing program
    @Transactional
    public Program update(Long id, ProgramDTO dto){
        Program program = findById(id);

        program.setName(dto.getName());
        program.setInstitution(dto.getInstitution());
        if(dto.getActive() != null){
            program.setActive(dto.getActive());
        }

        logger.info("Updating program: {}", id);
        return repository.save(program);
    }

    //Search for programs by name
    @Transactional
    public List<Program> searchActiveByNameFragment(String searchTerm){
        return repository.findActiveProgramsByNameContaining(searchTerm);
    }

    //Find all active programs
    @Transactional(readOnly = true)
    public List<Program> findAllActive() {
        return repository.findByActiveTrue();
    }


    //Find all inactive programs
    @Transactional(readOnly = true)
    public List<Program> findAllInactive() {
        return repository.findByActiveFalse();
    }

    //Activate a program
    @Transactional
    public Program activate(Long id){
        Program program = findById(id);
        program.setActive(true);
        logger.info("Activating Program: {}", id);
        return repository.save(program);
    }

    //Deactivate a program
    @Transactional
    public Program deactivate(Long id){
        Program program = findById(id);
        program.setActive(false);
        logger.info("Deactivating Program: {}", id);
        return repository.save(program);
    }

   
}
