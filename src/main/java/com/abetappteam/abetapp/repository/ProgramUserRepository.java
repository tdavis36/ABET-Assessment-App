package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.ProgramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramUserRepository extends JpaRepository<ProgramUser, Long>{
    //Find by Program
        //Return all users in a program
    List<ProgramUser> findByProgramId(Long programId);

        //Return all active or inactive users in a program
    List<ProgramUser> findByProgramIdAndIsActive(Long programId, Boolean isActive);

        //Return all admins or insturctors in a program regardless of active status
    List<ProgramUser> findByProgramIdAndIsAdmin(Long programId, Boolean isAdmin);

        //Return all admins or instructors in a program that are either active or inactive
    List<ProgramUser> findByProgramIdAndIsAdminAndIsActive(Long programid, Boolean isAdmin, Boolean isActive);

    //Find by User
        //Return all programs a user is in
    List<ProgramUser> findByUserId(Long userId);

        //Return all active or inactive programs a user is in
    List<ProgramUser> findByUserIdAndIsActive(Long userId, Boolean isActive);

        //Return all programs a user is an admin or an instructor in
    List<ProgramUser> findByUserIdAndIsAdmin(Long userId, Boolean isAdmin);

        //Return all active or inactive programs that a user is either an admin or instructor in
    List<ProgramUser> findByUserIdAndIsAdminAndIsActive(Long userId, Boolean isAdmin, Boolean isActive);

    //Find specific relationship
    Optional<ProgramUser> findByProgramIdAndUserId(Long programId, Long userId);

    Optional<ProgramUser> findByProgramIdAndUserIdAndIsActive(Long programId, Long userId, Boolean isActive);

    //Find Active
    List<ProgramUser> findByIsActive(Boolean isActive);

    //Find Admins
    List<ProgramUser> findByIsAdmin(Boolean isAdmin);

    //Check existence
    boolean existsByProgramIdAndUserId(Long programId, Long userId);

    boolean existsByProgramIdAndUserIdAndIsActive(Long programId, Long userId, Boolean isActive);

    boolean existsByProgramIdAndUserIdAndIsAdmin(Long programId, Long userId, Boolean isAdmin);

    //Count Queries
    Long countByProgramId(Long programId);

    Long countByProgramIdAndIsActive(Long programId, Boolean isActive);

    Long countByUserId(Long userId);

    Long countByUserIdAndIsActive(Long userId, Boolean isActive);

    //Delete Operations
    void deleteByProgramId(Long programId);

    void deleteByUserId(Long userId);

    void deleteByProgramIdAndUserId(Long programId, Long userId);

    //Check if a program has any active users (instructors and admins)
    @Query("SELECT COUNT(pu) > 0 FROM ProgramUser pu WHERE pu.programId = :programId AND pu.isActive = true")
    boolean isUsedByActiveUsers(@Param("programId") Long programId);

    //Check if a program has any active instructors
    @Query("SELECT COUNT(pu) > 0 FROM ProgramUser pu WHERE pu.programId = :programId AND pu.isActive = true AND pu.isAdmin = false")
    boolean isUsedByActiveInstructors(@Param("programId") Long programId);

    //Check if a program has any active admins
    @Query("SELECT COUNT(pu) > 0 FROM ProgramUser pu WHERE pu.programId = :programId AND pu.isActive = true AND pu.isAdmin = true")
    boolean isUsedByActiveAdmins(@Param("programId") Long programId);

    //Check if a User is a part of any active programs
    @Query("SELECT COUNT(pu) > 0 FROM ProgramUser pu WHERE pu.userId = :userId AND pu.isActive = true")
    boolean isUsedByActivePrograms(@Param("userId") Long userId);
}
