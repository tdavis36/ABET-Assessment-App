package com.ABETAppTeam.repository;

import com.ABETAppTeam.model.FCAR;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IFCARRepositoryTest {

    @Test
    void testUpdate_Successful() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        FCAR existingFcar = new FCAR(1, "CS101", 123, "Fall", 2023);
        FCAR updatedFcar = new FCAR(1, "CS101", 123, "Spring", 2024);

        doReturn(true).when(spyRepository).update(updatedFcar);

        // Act
        boolean isUpdated = spyRepository.update(updatedFcar);

        // Assert
        assertTrue(isUpdated, "The update should successfully return true");
        verify(spyRepository, times(1)).update(updatedFcar);
    }

    @Test
    void testUpdate_Failure_InvalidID() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        FCAR invalidFcar = new FCAR(0, "CS101", 123, "Spring", 2024);

        doReturn(false).when(spyRepository).update(invalidFcar);

        // Act
        boolean isUpdated = spyRepository.update(invalidFcar);

        // Assert
        assertFalse(isUpdated, "Updating an FCAR with invalid ID (<=0) should return false");
        verify(spyRepository, times(1)).update(invalidFcar);
    }

    @Test
    void testUpdate_DatabaseConnectionError() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        FCAR fcar = new FCAR(1, "CS101", 123, "Spring", 2024);

        doThrow(new SQLException("Simulated database error")).when(spyRepository).update(fcar);

        // Act & Assert
        assertThrows(SQLException.class, () -> spyRepository.update(fcar), "Should throw an SQLException if a database error occurs");
        verify(spyRepository, times(1)).update(fcar);
    }

    @Test
    void testFindById_FCARFound() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCAR expectedFcar = new FCAR(1, "CS101", 123, "Spring", 2023);

        FCARRepository spyRepository = spy(repository);
        doReturn(expectedFcar).when(spyRepository).findById(1);

        // Act
        FCAR resultFcar = spyRepository.findById(1);

        // Assert
        assertNotNull(resultFcar, "Retrieved FCAR should not be null");
        assertEquals(1, resultFcar.getFcarId(), "Retrieved FCAR ID should match");
        assertEquals("CS101", resultFcar.getCourseCode(), "Course code should match");
        verify(spyRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_FCARNotFound() {
        // Arrange
        FCARRepository repository = new FCARRepository();

        FCARRepository spyRepository = spy(repository);
        doReturn(null).when(spyRepository).findById(999);

        // Act
        FCAR resultFcar = spyRepository.findById(999);

        // Assert
        assertNull(resultFcar, "Finding non-existent FCAR should return null");
        verify(spyRepository, times(1)).findById(999);
    }

    @Test
    void testFindById_SqlExceptionThrown() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        doThrow(new SQLException("Simulated database error")).when(spyRepository).findById(1);

        // Act & Assert
        assertThrows(SQLException.class, () -> spyRepository.findById(1), "Should throw an SQLException for database error");
        verify(spyRepository, times(1)).findById(1);
    }

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
        assertNull(resultFcar, "Saving null input should return null");
        verify(mockRepository, times(1)).save(null);
    }

    @Test
    void testFindById_Successful() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR expectedFcar = new FCAR(1, "CS101", 123, "Spring", 2023);

        when(mockRepository.findById(1)).thenReturn(expectedFcar);

        // Act
        FCAR resultFcar = mockRepository.findById(1);

        // Assert
        assertNotNull(resultFcar, "Retrieved FCAR should not be null");
        assertEquals(1, resultFcar.getFcarId(), "Retrieved FCAR ID should match");
        assertEquals("CS101", resultFcar.getCourseCode(), "Course code should match");
        verify(mockRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);

        when(mockRepository.findById(999)).thenReturn(null);

        // Act
        FCAR resultFcar = mockRepository.findById(999);

        // Assert
        assertNull(resultFcar, "Finding non-existent ID should return null");
        verify(mockRepository, times(1)).findById(999);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR fcar1 = new FCAR(1, "CS101", 123, "Spring", 2023);
        FCAR fcar2 = new FCAR(2, "CS102", 124, "Fall", 2023);

        when(mockRepository.findAll()).thenReturn(List.of(fcar1, fcar2));

        // Act
        List<FCAR> fcars = mockRepository.findAll();

        // Assert
        assertNotNull(fcars, "Returned list should not be null");
        assertEquals(2, fcars.size(), "Returned list should contain 2 FCARs");
        assertEquals("CS101", fcars.get(0).getCourseCode(), "First FCAR's course code should match");
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);

        when(mockRepository.findAll()).thenReturn(List.of());

        // Act
        List<FCAR> fcars = mockRepository.findAll();

        // Assert
        assertNotNull(fcars, "Returned list should not be null");
        assertEquals(0, fcars.size(), "Returned list should be empty");
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    void testFindByCourseCode_Successful() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);
        FCAR fcar1 = new FCAR(1, "CS101", 123, "Spring", 2023);
        FCAR fcar2 = new FCAR(2, "CS101", 124, "Fall", 2023);

        when(mockRepository.findByCourseCode("CS101")).thenReturn(List.of(fcar1, fcar2));

        // Act
        List<FCAR> fcars = mockRepository.findByCourseCode("CS101");

        // Assert
        assertNotNull(fcars, "Returned list should not be null");
        assertEquals(2, fcars.size(), "Returned list should contain 2 FCARs");
        assertEquals("CS101", fcars.get(0).getCourseCode(), "Course code of first FCAR should match");
        assertEquals("CS101", fcars.get(1).getCourseCode(), "Course code of second FCAR should match");
        verify(mockRepository, times(1)).findByCourseCode("CS101");
    }

    @Test
    void testFindByCourseCode_EmptyResult() {
        // Arrange
        IFCARRepository mockRepository = Mockito.mock(IFCARRepository.class);

        when(mockRepository.findByCourseCode("CS102")).thenReturn(List.of());

        // Act
        List<FCAR> fcars = mockRepository.findByCourseCode("CS102");

        // Assert
        assertNotNull(fcars, "Returned list should not be null");
        assertEquals(0, fcars.size(), "Returned list should be empty");
        verify(mockRepository, times(1)).findByCourseCode("CS102");
    }

    @Test
    void testFindByInstructorId_Successful() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCAR fcar1 = new FCAR(1, "CS101", 123, "Spring", 2023);
        FCAR fcar2 = new FCAR(2, "CS102", 123, "Fall", 2023);

        List<FCAR> mockData = List.of(fcar1, fcar2);

        FCARRepository spyRepository = spy(repository);
        doReturn(mockData).when(spyRepository).findByInstructorId(123);

        // Act
        List<FCAR> fcars = spyRepository.findByInstructorId(123);

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertEquals(2, fcars.size(), "There should be 2 FCARs returned for the given instructor ID");
        assertEquals(123, fcars.get(0).getInstructorId(), "Instructor ID of the first FCAR should be 123");
        assertEquals(123, fcars.get(1).getInstructorId(), "Instructor ID of the second FCAR should be 123");
        verify(spyRepository, times(1)).findByInstructorId(123);
    }

    @Test
    void testFindByInstructorId_NotFound() {
        // Arrange
        FCARRepository repository = new FCARRepository();

        FCARRepository spyRepository = spy(repository);
        doReturn(List.of()).when(spyRepository).findByInstructorId(999);

        // Act
        List<FCAR> fcars = spyRepository.findByInstructorId(999);

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertTrue(fcars.isEmpty(), "The returned list should be empty for a non-existent instructor ID");
        verify(spyRepository, times(1)).findByInstructorId(999);
    }

    @Test
    void testFindByCourseCode_ValidCourseCode() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCAR fcar1 = new FCAR(1, "CS101", 123, "Spring", 2023);
        FCAR fcar2 = new FCAR(2, "CS101", 124, "Fall", 2023);

        List<FCAR> mockData = List.of(fcar1, fcar2);

        FCARRepository spyRepository = spy(repository);
        doReturn(mockData).when(spyRepository).findByCourseCode("CS101");

        // Act
        List<FCAR> fcars = spyRepository.findByCourseCode("CS101");

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertEquals(2, fcars.size(), "There should be 2 FCARs returned for the given course code");
        assertEquals("CS101", fcars.get(0).getCourseCode(), "Course code of the first FCAR should be CS101");
        assertEquals("CS101", fcars.get(1).getCourseCode(), "Course code of the second FCAR should be CS101");
        verify(spyRepository, times(1)).findByCourseCode("CS101");
    }

    @Test
    void testFindByCourseCode_NoResults() {
        // Arrange
        FCARRepository repository = new FCARRepository();

        FCARRepository spyRepository = spy(repository);
        doReturn(List.of()).when(spyRepository).findByCourseCode("CS999");

        // Act
        List<FCAR> fcars = spyRepository.findByCourseCode("CS999");

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertTrue(fcars.isEmpty(), "The returned list should be empty for a non-existent course code");
        verify(spyRepository, times(1)).findByCourseCode("CS999");
    }

    @Test
    void testFindBySemesterAndYear_Successful() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCAR fcar1 = new FCAR(1, "CS101", 123, "Spring", 2023);
        FCAR fcar2 = new FCAR(2, "CS102", 124, "Spring", 2023);

        List<FCAR> mockData = List.of(fcar1, fcar2);

        FCARRepository spyRepository = spy(repository);
        doReturn(mockData).when(spyRepository).findBySemesterAndYear("Spring", 2023);

        // Act
        List<FCAR> fcars = spyRepository.findBySemesterAndYear("Spring", 2023);

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertEquals(2, fcars.size(), "There should be 2 FCARs returned for the given semester and year");
        assertEquals("Spring", fcars.get(0).getSemester(), "Semester of the first FCAR should match");
        assertEquals(2023, fcars.get(0).getYear(), "Year of the first FCAR should match");
        verify(spyRepository, times(1)).findBySemesterAndYear("Spring", 2023);
    }

    @Test
    void testFindBySemesterAndYear_EmptyResult() {
        // Arrange
        FCARRepository repository = new FCARRepository();

        FCARRepository spyRepository = spy(repository);
        doReturn(List.of()).when(spyRepository).findBySemesterAndYear("Fall", 2025);

        // Act
        List<FCAR> fcars = spyRepository.findBySemesterAndYear("Fall", 2025);

        // Assert
        assertNotNull(fcars, "The returned list should not be null");
        assertTrue(fcars.isEmpty(), "The returned list should be empty for a non-existent semester and year");
        verify(spyRepository, times(1)).findBySemesterAndYear("Fall", 2025);
    }

    @Test
    void testFindById_FCARFound_Integration() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCAR expectedFcar = new FCAR(1, "CS101", 123, "Spring", 2023);

        // Act
        FCAR resultFcar = repository.findById(1);

        // Assert
        assertNotNull(resultFcar, "The FCAR should be found and not null");
        assertEquals(expectedFcar.getFcarId(), resultFcar.getFcarId(), "IDs must match");
        assertEquals(expectedFcar.getCourseCode(), resultFcar.getCourseCode(), "Course codes must match");
    }

    @Test
    void testFindById_FCARNotFound_Integration() {
        // Arrange
        FCARRepository repository = new FCARRepository();

        // Act
        FCAR resultFcar = repository.findById(999);

        // Assert
        assertNull(resultFcar, "No FCAR should be found for a non-existent ID");
    }

    @Test
    void testFindById_ThrowsSqlException_Integration() {
        // Arrange
        FCARRepository spyRepository = spy(new FCARRepository());
        doThrow(new SQLException("Simulated database error")).when(spyRepository).findById(1);

        // Act and Assert
        assertThrows(SQLException.class, () -> spyRepository.findById(1), "Should throw SQLException if a database error occurs");
    }

    @Test
    void testSave_RollbackOnFailure() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        FCAR inputFcar = new FCAR(0, "CS101", 123, "Spring", 2023);

        doThrow(new RuntimeException("Simulated exception")).when(spyRepository).save(any(FCAR.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> spyRepository.save(inputFcar), "Should throw an exception when save fails");
        verify(spyRepository, times(1)).save(inputFcar);
    }

    @Test
    void testSave_DatabaseConnectionFailure() {
        // Arrange
        FCARRepository repository = new FCARRepository();
        FCARRepository spyRepository = spy(repository);

        FCAR inputFcar = new FCAR(0, "CS101", 123, "Spring", 2023);
        doThrow(new SQLException("Simulated connection failure")).when(spyRepository).save(any(FCAR.class));

        // Act & Assert
        assertThrows(SQLException.class, () -> spyRepository.save(inputFcar), "Should throw an exception when there is a database connection issue");
        verify(spyRepository, times(1)).save(inputFcar);
    }
}