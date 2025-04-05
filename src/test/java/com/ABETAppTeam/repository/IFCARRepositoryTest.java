package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IFCARRepositoryTest {

    /**
     * Unit tests for the save method in the IFCARRepository interface.
     * The save method is responsible for persisting an FCAR object
     * into the data store and returning the saved instance.
     */

    @Test
    void testSave_SuccessfulSave() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR inputFcar = new FCAR(0, "CS101", 123, "Spring", 2023);

        when(mockRepository.save(any(FCAR.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        FCAR resultFcar = mockRepository.save(inputFcar);

        // Assert
        assertNotNull(resultFcar, "Saved FCAR should not be null");
        assertEquals(0, resultFcar.getFcarId(), "FCAR ID should match the input");
        assertEquals("CS101", resultFcar.getCourseCode(), "Course ID should be correctly saved");
        verify(mockRepository, times(1)).save(inputFcar);
    }

    @Test
    void testSave_NullInput() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);

        when(mockRepository.save(null)).thenReturn(null);

        // Act
        FCAR resultFcar = mockRepository.save(null);

        // Assert
        assertEquals(null, resultFcar, "Saving null input should return null");
        verify(mockRepository, times(1)).save(null);
    }
}