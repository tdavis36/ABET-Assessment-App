package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.entity.CourseIndicator;
import com.abetappteam.abetapp.entity.CourseInstructor;
import com.abetappteam.abetapp.exception.BusinessException;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.CourseIndicatorRepository;
import com.abetappteam.abetapp.repository.CourseInstructorRepository;
import com.abetappteam.abetapp.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Course entity
 */
@Service
public class CourseService extends BaseService<Course, Long, CourseRepository> {

    @Autowired
    public CourseService(CourseRepository repository) {
        super(repository);
    }

    @Autowired
    private CourseInstructorRepository courseInstructorRepository;

    @Autowired
    private CourseIndicatorRepository courseIndicatorRepository;


    @Override
    protected String getEntityName() {
        return "Course";
    }

    @Transactional
    public Course createCourse(String courseCode, String courseName, String courseDescription, Long semesterId) {
        // Check for duplicate course code in the same semester
        if (repository.existsByCourseCodeAndSemesterId(courseCode, semesterId)) {
            throw new ConflictException("Course with code '" + courseCode + "' already exists in this semester");
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCourseDescription(courseDescription);
        course.setSemesterId(semesterId);
        course.setIsActive(true);

        logger.info("Creating new course: {} - {}", courseCode, courseName);
        return repository.save(course);
    }

    @Transactional
    public Course createCourse(CourseDTO dto) {
        return createCourse(dto.getCourseCode(), dto.getCourseName(),
                dto.getCourseDescription(), dto.getSemesterId());
    }

    @Transactional
    public Course updateCourse(Long courseId, String courseCode, String courseName, String courseDescription) {
        Course course = findById(courseId);

        // Check for duplicate course code if it's being changed
        if (courseCode != null && !courseCode.equals(course.getCourseCode())) {
            if (repository.existsByCourseCodeAndSemesterId(courseCode, course.getSemesterId())) {
                throw new ConflictException("Course with code '" + courseCode + "' already exists in this semester");
            }
            course.setCourseCode(courseCode);
        }

        if (courseName != null) {
            course.setCourseName(courseName);
        }
        if (courseDescription != null) {
            course.setCourseDescription(courseDescription);
        }

        logger.info("Updating course: {}", courseId);
        return repository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseDTO dto) {
        return updateCourse(courseId, dto.getCourseCode(), dto.getCourseName(), dto.getCourseDescription());
    }

    @Transactional
    public void removeCourse(Long courseId) {
        Course course = findById(courseId);

        if (hasMeasuresInReview(courseId)) {
            throw new BusinessException("Cannot delete course with measures submitted for review");
        }

        logger.info("Removing course: {} - {}", course.getCourseCode(), course.getCourseName());
        repository.delete(course);
    }

    @Transactional
    public void deactivateCourse(Long courseId) {
        Course course = findById(courseId);
        course.setIsActive(false);
        logger.info("Deactivating course: {} - {}", course.getCourseCode(), course.getCourseName());
        repository.save(course);
    }

    @Transactional
    public void activateCourse(Long courseId) {
        Course course = findById(courseId);
        course.setIsActive(true);
        logger.info("Activating course: {} - {}", course.getCourseCode(), course.getCourseName());
        repository.save(course);
    }

    @Transactional(readOnly = true)
    public Page<Course> getCoursesBySemester(Long semesterId, Pageable pageable) {
        logger.debug("Fetching courses for semester ID: {}", semesterId);
        return repository.findBySemesterId(semesterId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesBySemester(Long semesterId) {
        logger.debug("Fetching courses for semester ID: {}", semesterId);
        return repository.findBySemesterId(semesterId);
    }

    @Transactional(readOnly = true)
    public Page<Course> getActiveCoursesBySemester(Long semesterId, Pageable pageable) {
        logger.debug("Fetching active courses for semester ID: {}", semesterId);
        return repository.findBySemesterIdAndIsActive(semesterId, true, pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> getActiveCoursesBySemester(Long semesterId) {
        logger.debug("Fetching active courses for semester ID: {}", semesterId);
        return repository.findBySemesterIdAndIsActive(semesterId, true);
    }

    @Transactional(readOnly = true)
    public List<Course> getActiveCoursesByProgramUserId(Long programUserId) {
        logger.debug("Fetching active courses for program user ID: {}", programUserId);
        return repository.findActiveCoursesByProgramUserId(programUserId);
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
    public Course findByCourseCode(String courseCode) {
        return repository.findByCourseCodeIgnoreCase(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + courseCode));
    }

    @Transactional(readOnly = true)
    public boolean existsByCourseCode(String courseCode) {
        return repository.existsByCourseCodeIgnoreCase(courseCode);
    }

    @Transactional(readOnly = true)
    public List<Course> searchByNameOrCourseCode(String searchTerm) {
        logger.debug("Searching courses with term: {}", searchTerm);
        return repository.searchByNameOrCourseCode(searchTerm);
    }

    @Transactional(readOnly = true)
    public Page<Course> searchByNameOrCourseCode(String searchTerm, Pageable pageable) {
        logger.debug("Searching courses with term: {}", searchTerm);
        return repository.searchByNameOrCourseCode(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public long countBySemester(Long semesterId) {
        return repository.countBySemesterId(semesterId);
    }

    @Transactional(readOnly = true)
    public long countActiveBySemester(Long semesterId) {
        return repository.countBySemesterIdAndIsActive(semesterId, true);
    }

    @Transactional
    public void assignInstructor(Long courseId, Long programUserId) {
        Course course = findById(courseId); // validates course exists

        if (courseInstructorRepository.existsByCourseIdAndProgramUserId(courseId, programUserId)) {
            throw new ConflictException("Instructor already assigned to this course");
        }

        CourseInstructor assignment = new CourseInstructor(programUserId, courseId);
        courseInstructorRepository.save(assignment);
        logger.info("Assigned instructor {} to course {}", programUserId, courseId);
    }

    @Transactional
    public void removeInstructor(Long courseId, Long programUserId) {
        CourseInstructor assignment = courseInstructorRepository
                .findByCourseIdAndProgramUserId(courseId, programUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor assignment not found"));

        assignment.setIsActive(false);
        courseInstructorRepository.save(assignment);
        logger.info("Removed instructor {} from course {}", programUserId, courseId);
    }

    @Transactional(readOnly = true)
    public List<Long> getInstructorIds(Long courseId) {
        return courseInstructorRepository.findByCourseIdAndIsActive(courseId, true)
                .stream()
                .map(CourseInstructor::getProgramUserId)
                .collect(Collectors.toList());
    }

    // Indicator management
    @Transactional
    public void assignIndicator(Long courseId, Long indicatorId) {
        Course course = findById(courseId);

        if (courseIndicatorRepository.existsByCourseIdAndIndicatorId(courseId, indicatorId)) {
            throw new ConflictException("Indicator already assigned to this course");
        }

        CourseIndicator assignment = new CourseIndicator(courseId, indicatorId);
        courseIndicatorRepository.save(assignment);
        logger.info("Assigned indicator {} to course {}", indicatorId, courseId);
    }

    @Transactional
    public void removeIndicator(Long courseId, Long indicatorId) {
        CourseIndicator assignment = courseIndicatorRepository
                .findByCourseIdAndIndicatorId(courseId, indicatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Indicator assignment not found"));

        assignment.setIsActive(false);
        courseIndicatorRepository.save(assignment);
        logger.info("Removed indicator {} from course {}", indicatorId, courseId);
    }

    @Transactional(readOnly = true)
    public List<Long> getIndicatorIds(Long courseId) {
        return courseIndicatorRepository.findByCourseIdAndIsActive(courseId, true)
                .stream()
                .map(CourseIndicator::getIndicatorId)
                .collect(Collectors.toList());
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