package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{

    //List all active Users
    List<Users> findByActiveTrue();

    //List all inactive Users
    List<Users> findByActiveFalse();

    //Checks if user exists by email address (case insensitive)
    boolean existsByEmailIgnoreCase(String email);

    //Find user by email (case insensitive)
    Optional<Users> findByEmailIgnoreCase(String email);
    
    //Checks if users exists by first name (case insensitive)
    boolean existsByFirstNameIgnoreCase();

    //Checks if users exists by last name (cases insensitive)
    boolean existsByLastNameIgnoreCase();

    //List users by first name containing (case insensitive search)
    List<Users> findByFirstNameContainingIgnoreCase(String firstNameFragment);

    //List users by last name containing (case insensitive search)
    List<Users> findByLastNameContainingIgnoreCase(String lastNameFragment);
}
