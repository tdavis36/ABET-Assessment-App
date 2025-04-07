package com.ABETAppTeam;

import com.ABETAppTeam.repository.IFCARRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}