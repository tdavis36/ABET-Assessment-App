package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.entity.ProgramUser;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.ProgramUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.repository.ProgramRepository;

import java.util.List;

@Service
public class ProgramService extends BaseService<Program, Long, ProgramRepository>{
    private final ProgramUserRepository programUserRepository;

    @Autowired
    public ProgramService(ProgramRepository repository, ProgramUserRepository programUserRepository) {
        super(repository);
        this.programUserRepository = programUserRepository;
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

    /**
     * Get all active programs for a user
     */
    @Transactional(readOnly = true)
    public List<ProgramUser> getActiveProgramsForUser(Long userId) {
        logger.debug("Fetching active programs for user ID: {}", userId);
        return programUserRepository.findByUserIdAndIsActive(userId, true);
    }

    /**
     * Get all programs for a user (including inactive)
     */
    @Transactional(readOnly = true)
    public List<ProgramUser> getAllProgramsForUser(Long userId) {
        logger.debug("Fetching all programs for user ID: {}", userId);
        return programUserRepository.findByUserId(userId);
    }

    /**
     * Check if user is admin in a specific program
     */
    @Transactional(readOnly = true)
    public boolean isAdminInProgram(Long userId, Long programId) {
        logger.debug("Checking if user {} is admin in program {}", userId, programId);
        return programUserRepository.existsByProgramIdAndUserIdAndIsAdmin(programId, userId, true);
    }

    /**
     * Get the user's role in a specific program
     * @return "ADMIN", "INSTRUCTOR", or null if not in program
     */
    @Transactional(readOnly = true)
    public String getRoleInProgram(Long userId, Long programId) {
        logger.debug("Getting role for user {} in program {}", userId, programId);
        var programUser = programUserRepository.findByProgramIdAndUserIdAndIsActive(programId, userId, true);

        return programUser.map(user -> user.getAdminStatus() ? "ADMIN" : "INSTRUCTOR").orElse(null);

    }

    /**
     * Get user's relationship with a program
     */
    @Transactional(readOnly = true)
    public ProgramUser getProgramUser(Long userId, Long programId) {
        logger.debug("Fetching ProgramUser for user {} and program {}", userId, programId);
        return programUserRepository.findByProgramIdAndUserIdAndIsActive(programId, userId, true)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User is not associated with this program"
                ));
    }

    /**
     * Get user's default/first program
     * Useful for initial login
     */
    @Transactional(readOnly = true)
    public ProgramUser getDefaultProgramForUser(Long userId) {
        logger.debug("Getting default program for user ID: {}", userId);
        List<ProgramUser> programs = getActiveProgramsForUser(userId);

        if (programs.isEmpty()) {
            throw new ResourceNotFoundException("User is not associated with any programs");
        }

        // Return first admin program if exists, otherwise first program
        return programs.stream()
                .filter(ProgramUser::getAdminStatus)
                .findFirst()
                .orElse(programs.getFirst());
    }

    /**
     * Check if user has access to a program (admin or instructor)
     */
    @Transactional(readOnly = true)
    public boolean hasAccessToProgram(Long userId, Long programId) {
        logger.debug("Checking access for user {} to program {}", userId, programId);
        return programUserRepository.existsByProgramIdAndUserIdAndIsActive(programId, userId, true);
    }

    /**
     * Get all users in a program
     */
    @Transactional(readOnly = true)
    public List<ProgramUser> getUsersInProgram(Long programId) {
        logger.debug("Fetching all users in program ID: {}", programId);
        return programUserRepository.findByProgramIdAndIsActive(programId, true);
    }

    /**
     * Get all admins in a program
     */
    @Transactional(readOnly = true)
    public List<ProgramUser> getAdminsInProgram(Long programId) {
        logger.debug("Fetching admins in program ID: {}", programId);
        return programUserRepository.findByProgramIdAndIsAdminAndIsActive(programId, true, true);
    }

    /**
     * Get all instructors (non-admin) in a program
     */
    @Transactional(readOnly = true)
    public List<ProgramUser> getInstructorsInProgram(Long programId) {
        logger.debug("Fetching instructors in program ID: {}", programId);
        return programUserRepository.findByProgramIdAndIsAdminAndIsActive(programId, false, true);
    }

    /**
     * Add user to program
     */
    @Transactional
    public ProgramUser addUserToProgram(Long userId, Long programId, boolean isAdmin) {
        logger.info("Adding user {} to program {} as {}", userId, programId, isAdmin ? "admin" : "instructor");

        // Check if relationship already exists
        if (programUserRepository.existsByProgramIdAndUserId(programId, userId)) {
            throw new ConflictException("User is already associated with this program");
        }

        // Verify program exists
        findById(programId);

        ProgramUser programUser = new ProgramUser(isAdmin, programId, userId);
        return programUserRepository.save(programUser);
    }

    /**
     * Update user's admin status in a program
     */
    @Transactional
    public ProgramUser updateUserRole(Long userId, Long programId, boolean isAdmin) {
        logger.info("Updating user {} role in program {} to {}", userId, programId, isAdmin ? "admin" : "instructor");

        ProgramUser programUser = getProgramUser(userId, programId);
        programUser.setAdminStatus(isAdmin);
        return programUserRepository.save(programUser);
    }

    /**
     * Remove user from program (soft delete by setting inactive)
     */
    @Transactional
    public void removeUserFromProgram(Long userId, Long programId) {
        logger.info("Removing user {} from program {}", userId, programId);

        ProgramUser programUser = getProgramUser(userId, programId);
        programUser.setIsActive(false);
        programUserRepository.save(programUser);
    }

    /**
     * Permanently delete user from program
     */
    @Transactional
    public void deleteUserFromProgram(Long userId, Long programId) {
        logger.warn("Permanently deleting user {} from program {}", userId, programId);
        programUserRepository.deleteByProgramIdAndUserId(programId, userId);
    }
}
