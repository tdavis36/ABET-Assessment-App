package com.abetappteam.abetapp.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abetappteam.abetapp.entity.Measure;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.entity.CourseIndicator;
import com.abetappteam.abetapp.dto.MeasureDTO;
import com.abetappteam.abetapp.repository.CourseIndicatorRepository;
import com.abetappteam.abetapp.repository.CourseRepository;
import com.abetappteam.abetapp.repository.MeasureRepository;

@Service
public class MeasureService extends BaseService<Measure, Long, MeasureRepository>{
    
    private final CourseIndicatorRepository courseIndicatorRepository;
    private final CourseRepository courseRepository;
    
    @Autowired
    public MeasureService(MeasureRepository repository, CourseIndicatorRepository courseIndicatorRepository, CourseRepository courseRepository){
        super(repository);
        this.courseIndicatorRepository = courseIndicatorRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    protected String getEntityName(){
        return "Measure";
    }

    //Create new Measure
    @Transactional
    public Measure create(MeasureDTO dto){
        Measure measure = new Measure();
        measure.setId(dto.getId());
        measure.setCourseIndicatorId(dto.getCourseIndicatorId());
        measure.setDescription(dto.getDescription());
        measure.setObservation(dto.getObservation());
        measure.setFcar(dto.getFCar());
        measure.setRecommendedAction(dto.getRecommendedAction());
        measure.setStatus(dto.getStatus());
        measure.setStudentsMet(dto.getStudentsMet());
        measure.setStudentsExceeded(dto.getStudentsExceeded());
        measure.setStudentsBelow(dto.getStudentsBelow());
        measure.setActive(dto.getActive());

        logger.info("Creating new measure: {}", dto.getId());
        return repository.save(measure);
    }

    //Update Existing Measure
    @Transactional
    public Measure update(Long id, MeasureDTO dto){
        Measure measure = findById(id);

        measure.setId(dto.getId());
        measure.setCourseIndicatorId(dto.getCourseIndicatorId());
        measure.setDescription(dto.getDescription());
        measure.setObservation(dto.getObservation());
        measure.setFcar(dto.getFCar());
        measure.setRecommendedAction(dto.getRecommendedAction());
        measure.setStatus(dto.getStatus());
        measure.setStudentsMet(dto.getStudentsMet());
        measure.setStudentsExceeded(dto.getStudentsExceeded());
        measure.setStudentsBelow(dto.getStudentsBelow());
        if(dto.getActive() != null){
            measure.setActive(dto.getActive());
        }

        logger.info("Updating measure: {}", id);
        return repository.save(measure);
    }

    //Activate Measure
    @Transactional
    public Measure activate(Long id){
        Measure measure = findById(id);
        measure.setActive(true);
        logger.info("Activating Measure: {}", id);
        return repository.save(measure);
    }
    
    //Deactivate Measure
    @Transactional
    public Measure deactivate(Long id){
        Measure measure = findById(id);
        measure.setActive(false);
        logger.info("Deactivaitng Measure: {}", id);
        return repository.save(measure);
    }

    //Return all active measures
    @Transactional(readOnly = true)
    public List<Measure> findAllActive() {
        return repository.findByActiveTrue();
    }

    //Return all inactive measures
    @Transactional(readOnly = true)
    public List<Measure> findAllInactive() {
        return repository.findByActiveFalse();
    }

    //Return all active Measures by Course Id
    @Transactional(readOnly = true)
    public List<Measure> findAllActiveMeasuresByCourse(Long courseId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByCourseIdAndIsActive(courseId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every active Measure tied to courseId: {}", courseId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findActiveMeasuresByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all inactive measures by Course Id
    @Transactional(readOnly = true)
    public List<Measure> findAllInactiveMeasuresByCourse(Long courseId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByCourseIdAndIsActive(courseId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every inactive Measure tied to courseId: {}", courseId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findInactiveMeasuresByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all Measures by Course Id regardless of active status
    @Transactional(readOnly = true)
    public List<Measure> findAllMeasuresByCourse(Long courseId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByCourseIdAndIsActive(courseId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every Measure tied to courseId: {}", courseId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all active Measures by Indicator id
    @Transactional(readOnly = true)
    public List<Measure> findAllActiveMeasuresByIndicator(Long indicatorId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByIndicatorIdAndIsActive(indicatorId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every Measure tied to indicatorId: {}", indicatorId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findActiveMeasuresByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all inactive Measures by IndicatorId
    @Transactional(readOnly = true)
    public List<Measure> findAllInactiveMeasuresByIndicator(Long indicatorId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByIndicatorIdAndIsActive(indicatorId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every Measure tied to indicatorId: {}", indicatorId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findInactiveMeasuresByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all Measures by IndicatorId regardles of active status
    @Transactional(readOnly = true)
    public List<Measure> findAllMeasuresByIndicator(Long indicatorId){
        //Stores every course indicator found with the course id
        List<CourseIndicator> foundCI = courseIndicatorRepository.findByIndicatorIdAndIsActive(indicatorId, true);
        //Output
        List<Measure> output = new ArrayList<>();

        logger.debug("Fetching every Measure tied to indicatorId: {}", indicatorId);
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findByCourseIndicatorId(i.getId()));
        }

        return output;
    }

    //Return all active measures by Status and Semester Id
    @Transactional(readOnly = true)
    public List<Measure> findAllActiveMeasuresByStatusAndSemester(String status, Long semesterId){
        //Finds every active course in a semester
        List<Course> foundCourses = courseRepository.findBySemesterIdAndIsActive(semesterId, true);
        List<CourseIndicator> foundCI = new ArrayList<>();
        List<Measure> output = new ArrayList<>();

        //Finds every active CourseIndicator from every active course in the semester 
        for(Course i: foundCourses){
            foundCI.addAll(courseIndicatorRepository.findByCourseIdAndIsActive(i.getId(), true));
        }

        //Finds every active measure from every active CourseIndicator in the semester
        for(CourseIndicator i: foundCI){
            output.addAll(repository.findActiveMeasuresByCourseIndicatorIdAndStatus(i.getId(), status));
        }

        return output;
    }
}
