package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsersService extends BaseService<Users, Long, UsersRepository> {

    @Autowired
    public UsersService(UsersRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Users";
    }

    //Create new user from Data Transfer Object
    @Transactional
    public Users create(UsersDTO dto) {
        if(repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ConflictException("User with email address '" + dto.getEmail() + "' already exists");
        }
        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTitle(dto.getTitle());
        user.setActive(dto.getActive() != null ? dto.getActive() : true);

        logger.info("Creating new user : {}", dto.getFullName());
        return repository.save(user);
    }
 
    //Update existing user
    @Transactional
    public Users update(Long id, UsersDTO dto) {
        Users user = findById(id);

        //Check for duplicate email address, excluding current user
        repository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existing -> {
            if(!existing.getId().equals(id)) {
                throw new ConflictException("User with email address '" + dto.getEmail() + "' already exists");
            }
        });

        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTitle(dto.getTitle());
        
        if(dto.getActive() != null){
            user.setActive(dto.getActive());
        }
        logger.info("Updating user: {}", id);
        return repository.save(user);
    }

    //Find user by email address
    @Transactional(readOnly = true)
    public Users findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email address: " + email));
    }

    //Find user by first name
    @Transactional(readOnly = true)
    public List<Users> searchByFirstName(String searchTerm) {
        return repository.findByFirstNameContainingIgnoreCase(searchTerm);
    }

    //Find user by last name
    @Transactional(readOnly = true)
    public List<Users> searchByLastName(String searchTerm) {
        return repository.findByLastNameContainingIgnoreCase(searchTerm);
    }

    //Find all active users
    @Transactional(readOnly = true)
    public List<Users> findAllActive() {
        return repository.findByActiveTrue();
    }

    //Find all inactive users
    @Transactional(readOnly = true)
    public List<Users> findAllInactive() {
        return repository.findByActiveFalse();
    }

    //Activate User Account
    @Transactional
    public Users activate(Long id){
        Users user = findById(id);
        user.setActive(true);
        logger.info("Activating user: {}", id);
        return repository.save(user);
    }

    //Deactivate User Account
    @Transactional
    public Users deactivate(Long id){
        Users users = findById(id);
        users.setActive(false);
        logger.info("Deactivaing user: {}", id);
        return repository.save(users);
    }
}
