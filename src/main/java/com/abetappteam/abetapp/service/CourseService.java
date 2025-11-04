package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.exception.BusinessException;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Course entity
 */
@Service
public class CourseService extends BaseService<Course, Long, CourseRepository> {

    @Autowired
    public CourseService(CourseRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Course";
    }

    @Transactional
    public Course createCourse(String name, String courseId, Long semesterId, Long programId, Long instructorId,
                               String section, String description) {
        // Check for duplicate course with same course ID and section in same semester
        if (section != null && !section.trim().isEmpty()) {
            if (repository.existsByCourseIdAndSectionAndSemesterId(courseId, section, semesterId)) {
                throw new ConflictException("Course with ID '" + courseId + "' and section '" + section
                        + "' already exists in this semester");
            }
        } else {
            // Check for duplicate course ID in the same semester
            if (repository.findByCourseIdIgnoreCaseAndSemesterId(courseId, semesterId).isPresent()) {
                throw new ConflictException("Course with ID '" + courseId + "' already exists in this semester");
            }
        }

        Course course = new Course();
        course.setName(name);
        course.setCourseId(courseId);
        course.setSemesterId(semesterId);
        course.setProgramId(programId);
        course.setInstructorId(instructorId);
        course.setSection(section);
        course.setDescription(description);

        logger.info("Creating new course: {} - {} - Section: {}", courseId, name, section);
        return repository.save(course);
    }

    @Transactional
    public Course createCourse(CourseDTO dto) {
        return createCourse(dto.getName(), dto.getCourseId(), dto.getSemesterId(),
                dto.getProgramId(), dto.getInstructorId(), dto.getSection(),
                dto.getDescription());
    }

    @Transactional
    public Course updateCourse(Long courseId, String name, String courseIdStr, Long instructorId, String section,
                               String description) {
        Course course = findById(courseId);

        // Check for duplicate course ID
        if (courseIdStr != null && !courseIdStr.equals(course.getCourseId())) {
            repository.findByCourseIdIgnoreCase(courseIdStr).ifPresent(existing -> {
                if (!existing.getId().equals(courseId)) {
                    throw new ConflictException("Course with ID '" + courseIdStr + "' already exists");
                }
            });
            course.setCourseId(courseIdStr);
        }

        // Check for duplicate section
        if (section != null && !section.equals(course.getSection())) {
            Optional<Course> existingSections = repository.findByCourseIdIgnoreCase(course.getCourseId());
            boolean sectionExists = existingSections.stream()
                    .anyMatch(c -> section.equals(c.getSection()) &&
                            !c.getId().equals(courseId) &&
                            c.getSemesterId().equals(course.getSemesterId()));
            if (sectionExists) {
                throw new ConflictException(
                        "Section '" + section + "' already exists for this course in this semester");
            }
            course.setSection(section);
        }

        if (name != null) {
            course.setName(name);
        }
        if (instructorId != null) {
            course.setInstructorId(instructorId);
        }
        if (description != null) {
            course.setDescription(description);
        }

        logger.info("Updating course: {}", courseId);
        return repository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseDTO dto) {
        return updateCourse(courseId, dto.getName(), dto.getCourseId(), dto.getInstructorId(),
                dto.getSection(), dto.getDescription());
    }

    @Transactional
    public void removeCourse(Long courseId) {
        Course course = findById(courseId);

        if (hasMeasuresInReview(courseId)) {
            throw new BusinessException("Cannot delete course with measures submitted for review");
        }

        logger.info("Removing course: {} - {} - Section: {}", course.getCourseId(), course.getName(),
                course.getSection());
        repository.delete(course);
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseSections(String courseId) {
        logger.debug("Fetching all sections for course ID: {}", courseId);
        return repository.findByCourseIdIgnoreCase(courseId);
    }

    /**
     * Get specific course section
     */
    @Transactional(readOnly = true)
    public Course getCourseSection(String courseId, String section) {
        logger.debug("Fetching course section: {} - {}", courseId, section);
        return repository.findByCourseIdIgnoreCaseAndSection(courseId, section)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course section not found: " + courseId + " - " + section));
    }

    @Transactional(readOnly = true)
    public Page<Course> getCoursesBySemester(Long semesterId, Pageable pageable) {
        logger.debug("Fetching courses for semester ID: {}", semesterId);
        return repository.findBySemesterId(semesterId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> getCoursesByProgram(Long programId, Pageable pageable) {
        logger.debug("Fetching courses for program ID: {}", programId);
        return repository.findByProgramId(programId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> getCoursesByInstructor(Long instructorId, Pageable pageable) {
        logger.debug("Fetching courses for instructor ID: {}", instructorId);
        return repository.findByInstructorId(instructorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> getCoursesBySemesterAndProgram(Long semesterId, Long programId, Pageable pageable) {
        logger.debug("Fetching courses for semester {} and program {}", semesterId, programId);
        return repository.findBySemesterIdAndProgramId(semesterId, programId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesWithSections() {
        logger.debug("Fetching courses with sections");
        return repository.findCoursesWithSections();
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesWithoutSections() {
        logger.debug("Fetching courses without sections");
        return repository.findCoursesWithoutSections();
    }

    @Transactional(readOnly = true)
    public MeasureCompletenessResponse calculateMeasureCompleteness(Long courseId) {
        Course course = findById(courseId);

        int totalMeasures = getTotalMeasuresForCourse(courseId);
        int completedMeasures = getCompletedMeasuresForCourse(courseId);
        int inProgressMeasures = getInProgressMeasuresForCourse(courseId);
        int submittedMeasures = getSubmittedMeasuresForCourse(courseId);

        double completionPercentage = totalMeasures > 0 ? (double) completedMeasures / totalMeasures * 100 : 0;

        MeasureCompletenessResponse response = new MeasureCompletenessResponse();
        response.setCourseId(courseId);
        response.setTotalMeasures(totalMeasures);
        response.setCompletedMeasures(completedMeasures);
        response.setInProgressMeasures(inProgressMeasures);
        response.setSubmittedMeasures(submittedMeasures);
        response.setCompletionPercentage(completionPercentage);

        return response;
    }

    @Transactional(readOnly = true)
    public Course findByCourseId(String courseId) {
        return repository.findByCourseIdIgnoreCase(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    @Transactional(readOnly = true)
    public boolean existsByCourseId(String courseId) {
        return repository.existsByCourseIdIgnoreCase(courseId);
    }

    @Transactional(readOnly = true)
    public List<Course> searchByNameOrCourseIdOrSection(String searchTerm) {
        logger.debug("Searching courses with term: {}", searchTerm);
        return repository.searchByNameOrCourseIdOrSection(searchTerm);
    }

    @Transactional(readOnly = true)
    public Page<Course> searchByNameOrCourseIdOrSection(String searchTerm, Pageable pageable) {
        logger.debug("Searching courses with term: {}", searchTerm);
        return repository.searchByNameOrCourseIdOrSection(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> searchByNameOrCourseId(String searchTerm) {
        logger.debug("Searching courses with term: {}", searchTerm);
        return repository.searchByNameOrCourseId(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByProgramAndSemester(Long programId, Long semesterId) {
        logger.debug("Fetching courses for program {} and semester {}", programId, semesterId);
        return repository.findByProgramIdAndSemesterId(programId, semesterId);
    }

    @Transactional(readOnly = true)
    public long countByProgram(Long programId) {
        return repository.countByProgramId(programId);
    }

    @Transactional
    public Course assignInstructor(Long courseId, Long instructorId) {
        Course course = findById(courseId);
        course.setInstructorId(instructorId);
        logger.info("Assigning instructor {} to course {}", instructorId, courseId);
        return repository.save(course);
    }

    @Transactional
    public Course removeInstructor(Long courseId) {
        Course course = findById(courseId);
        course.setInstructorId(null);
        logger.info("Removing instructor from course {}", courseId);
        return repository.save(course);
    }

    /**
     * Check if section exists for a course
     */
    @Transactional(readOnly = true)
    public boolean sectionExists(String courseId, String section, Long semesterId) {
        return repository.existsByCourseIdAndSectionAndSemesterId(courseId, section, semesterId);
    }

    // Helper methods for business logic
    private boolean hasMeasuresInReview(Long courseId) {
        return repository.countMeasuresInReviewByCourseId(courseId) > 0;
    }

    private int getTotalMeasuresForCourse(Long courseId) {
        return repository.countTotalMeasuresByCourseId(courseId);
    }

    private int getCompletedMeasuresForCourse(Long courseId) {
        return repository.countCompletedMeasuresByCourseId(courseId);
    }

    private int getInProgressMeasuresForCourse(Long courseId) {
        return repository.countInProgressMeasuresByCourseId(courseId);
    }

    private int getSubmittedMeasuresForCourse(Long courseId) {
        return repository.countSubmittedMeasuresByCourseId(courseId);
    }

    /**
     * Response DTO for measure completeness
     */
    public static class MeasureCompletenessResponse {
        private Long courseId;
        private int totalMeasures;
        private int completedMeasures;
        private int inProgressMeasures;
        private int submittedMeasures;
        private double completionPercentage;

        // Getters and setters
        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public int getTotalMeasures() {
            return totalMeasures;
        }

        public void setTotalMeasures(int totalMeasures) {
            this.totalMeasures = totalMeasures;
        }

        public int getCompletedMeasures() {
            return completedMeasures;
        }

        public void setCompletedMeasures(int completedMeasures) {
            this.completedMeasures = completedMeasures;
        }

        public int getInProgressMeasures() {
            return inProgressMeasures;
        }

        public void setInProgressMeasures(int inProgressMeasures) {
            this.inProgressMeasures = inProgressMeasures;
        }

        public int getSubmittedMeasures() {
            return submittedMeasures;
        }

        public void setSubmittedMeasures(int submittedMeasures) {
            this.submittedMeasures = submittedMeasures;
        }

        public double getCompletionPercentage() {
            return completionPercentage;
        }

        public void setCompletionPercentage(double completionPercentage) {
            this.completionPercentage = completionPercentage;
        }
    }
}