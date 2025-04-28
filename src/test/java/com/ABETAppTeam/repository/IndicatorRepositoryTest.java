package com.ABETAppTeam.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.util.TestDatabaseSetup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@ExtendWith(MockitoExtension.class)
public class IndicatorRepositoryTest {
    private static HikariDataSource h2Hikari;
    private static MockedStatic<DataSourceFactory> dsfMock;
    private IndicatorRepository repo;

    @BeforeAll
    static void initDatabase() {
        // Initialize in-memory H2 with migrations
        DataSource rawDs = TestDatabaseSetup.getDataSource();
        HikariConfig cfg = new HikariConfig();
        cfg.setDataSource(rawDs);
        h2Hikari = new HikariDataSource(cfg);

        // Mock DataSourceFactory to return H2
        dsfMock = Mockito.mockStatic(DataSourceFactory.class);
        dsfMock.when(DataSourceFactory::getDataSource).thenReturn(h2Hikari);
    }

    @AfterAll
    static void tearDown() {
        dsfMock.close();
        h2Hikari.close();
    }

    @BeforeEach
    void setUp() {
        repo = new IndicatorRepository();
    }

    /**
     * Helper to insert a dummy outcome and return its generated ID
     */
    private int insertOutcome() throws SQLException {
        try (Connection conn = h2Hikari.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Outcome (outcome_num, outcome_desc) VALUES (?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "O-" + UUID.randomUUID());
            ps.setString(2, "desc");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Test
    void save_and_findById_roundTrips() throws SQLException {
        int outId = insertOutcome();
        Indicator ind = new Indicator();
        ind.setOutcomeId(outId);
        ind.setNumber(1);
        ind.setDescription("Test Desc");

        Indicator saved = repo.save(ind);
        assertNotNull(saved);
        assertTrue(saved.getIndicatorId() > 0);

        Indicator fetched = repo.findById(saved.getIndicatorId());
        assertNotNull(fetched);
        assertEquals(outId, fetched.getOutcomeId());
        assertEquals(1, fetched.getNumber());
        assertEquals("Test Desc", fetched.getDescription());
    }

    @Test
    void findAll_reflectsInserts() throws SQLException {
        // Should be empty to start
        assertTrue(repo.findAll().isEmpty());

        int outId = insertOutcome();
        Indicator ind1 = new Indicator();
        ind1.setOutcomeId(outId);
        ind1.setNumber(1);
        ind1.setDescription("A");
        repo.save(ind1);

        Indicator ind2 = new Indicator();
        ind2.setOutcomeId(outId);
        ind2.setNumber(2);
        ind2.setDescription("B");
        repo.save(ind2);

        List<Indicator> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void findByOutcomeId_returnsOnlyThatOutcome() throws SQLException {
        int out1 = insertOutcome();
        int out2 = insertOutcome();

        Indicator a = new Indicator(); a.setOutcomeId(out1); a.setNumber(1); a.setDescription("X"); repo.save(a);
        Indicator b = new Indicator(); b.setOutcomeId(out1); b.setNumber(2); b.setDescription("Y"); repo.save(b);
        Indicator c = new Indicator(); c.setOutcomeId(out2); c.setNumber(1); c.setDescription("Z"); repo.save(c);

        List<Indicator> list1 = repo.findByOutcomeId(out1);
        assertEquals(2, list1.size());
        list1.forEach(ind -> assertEquals(out1, ind.getOutcomeId()));

        List<Indicator> list2 = repo.findByOutcomeId(out2);
        assertEquals(1, list2.size());
        assertEquals(out2, list2.get(0).getOutcomeId());
    }

    @Test
    void update_modifiesFields() throws SQLException {
        int outId = insertOutcome();
        Indicator ind = new Indicator();
        ind.setOutcomeId(outId);
        ind.setNumber(1);
        ind.setDescription("Orig");
        repo.save(ind);

        ind.setNumber(5);
        ind.setDescription("Updated");
        assertTrue(repo.update(ind));

        Indicator fetched = repo.findById(ind.getIndicatorId());
        assertEquals(5, fetched.getNumber());
        assertEquals("Updated", fetched.getDescription());
    }

    @Test
    void delete_removesIndicator() throws SQLException {
        int outId = insertOutcome();
        Indicator ind = new Indicator();
        ind.setOutcomeId(outId);
        ind.setNumber(9);
        ind.setDescription("Del");
        repo.save(ind);

        int id = ind.getIndicatorId();
        assertTrue(repo.delete(id));
        assertNull(repo.findById(id));
    }
}
