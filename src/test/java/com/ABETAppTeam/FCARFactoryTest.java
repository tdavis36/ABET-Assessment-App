package com.ABETAppTeam;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.repository.IFCARRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class FCARFactoryTest {

    /**
     * Tests the createFCAR method in the FCARFactory class.
     * <p>
     * The createFCAR method is responsible for creating a new FCAR object
     * and saving it to the repository.
     */
    @Test
    void testCreateFCAR_Success() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR testFCAR = new FCAR(0, "CS101", 2, "Fall", 2023);
        FCAR savedFCAR = new FCAR(1, "CS101", 2, "Fall", 2023);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(savedFCAR);

        // Replace static repository with mock (if permitted through testing setup)
        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR("CS101", 2, "Fall", 2023);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        assertEquals("CS101", result.getCourseCode());
        assertEquals(2, result.getInstructorId());
        assertEquals("Fall", result.getSemester());
        assertEquals(2023, result.getYear());
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testCreateFCAR_NullCourseId() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(null);

        // Replace static repository with mock (if permitted through testing setup)
        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR(null, 2, "Fall", 2023);

        // Assert
        assertNull(result);
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testCreateFCAR_InvalidYear() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(null);

        // Replace static repository with mock (if permitted through testing setup)
        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR("CS101", 2, "Spring", 1800);

        // Assert
        assertNull(result);
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testCreateFCAR_DuplicateEntry() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR duplicateFCAR = new FCAR(1, "CS101", 2, "Fall", 2023);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(duplicateFCAR);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR("CS101", 2, "Fall", 2023);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testCreateFCAR_InvalidCourseCode() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(null);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR("", 2, "Spring", 2023);

        // Assert
        assertNull(result);
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testCreateFCAR_WithNullSemester() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        Mockito.when(mockRepository.save(any(FCAR.class))).thenReturn(null);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        FCAR result = FCARFactory.createFCAR("CS101", 2, null, 2023);

        // Assert
        assertNull(result);
        Mockito.verify(mockRepository, Mockito.times(1)).save(any(FCAR.class));
    }

    @Test
    void testGetFCARsBySemester_Found() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        List<FCAR> allFCARs = List.of(
                new FCAR(1, "CS101", 2, "Fall", 2023),
                new FCAR(2, "CS102", 3, "Spring", 2023),
                new FCAR(3, "CS103", 4, "Fall", 2023)
        );
        Mockito.when(mockRepository.findAll()).thenReturn(allFCARs);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        List<FCAR> results = FCARFactory.getFCARsBySemester("Fall", 2023);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Fall", results.get(0).getSemester());
        assertEquals(2023, results.get(0).getYear());
        assertEquals("Fall", results.get(1).getSemester());
        assertEquals(2023, results.get(1).getYear());
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetFCARsBySemester_NotFound() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        List<FCAR> allFCARs = List.of(
                new FCAR(1, "CS101", 2, "Spring", 2023),
                new FCAR(2, "CS102", 3, "Spring", 2023)
        );
        Mockito.when(mockRepository.findAll()).thenReturn(allFCARs);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        List<FCAR> results = FCARFactory.getFCARsBySemester("Fall", 2023);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetFCARsBySemester_NullSemester() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        List<FCAR> allFCARs = List.of(
                new FCAR(1, "CS101", 2, "Spring", 2023),
                new FCAR(2, "CS102", 3, "Fall", 2023)
        );
        Mockito.when(mockRepository.findAll()).thenReturn(allFCARs);

        FCARFactory factory = new FCARFactory() {
            @Override
            protected IFCARRepository getRepository() {
                return mockRepository;
            }
        };

        // Act
        List<FCAR> results = FCARFactory.getFCARsBySemester(null, 2023);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        Mockito.verify(mockRepository, Mockito.times(1)).findAll();
    }
}