package com.ABETAppTeam.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.util.TestDatabaseSetup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@ExtendWith(MockitoExtension.class)
public class OutcomeRepositoryTest {

    private static HikariDataSource h2Hikari;
    private static MockedStatic<DataSourceFactory> dsfMock;
    private OutcomeRepository repo;

    @BeforeAll
    static void initDatabase() {
        // 1) Use TestDatabaseSetup to spin up an H2 mem-DB with migrations applied
        DataSource rawDs = TestDatabaseSetup.getDataSource();

        // 2) Wrap it in a HikariDataSource so that OutcomeRepository gets the right type
        HikariConfig cfg = new HikariConfig();
        cfg.setDataSource(rawDs);
        h2Hikari = new HikariDataSource(cfg);

        // 3) Mock DataSourceFactory.getDataSource() → our H2 pool
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
        // Every test gets a fresh repo (which will use the mocked H2 pool)
        repo = new OutcomeRepository();
    }

    // --- CSV loader ---
    private static class CSVRepo extends OutcomeRepository {
        private final String csv;
        CSVRepo(String csv) { this.csv = csv; }
        @Override
        protected InputStream getResourceAsStream(String resourceName) {
            return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    void loadFromCSV_parsesQuotesAndCommas() {
        String csv =
                "outcome_num,outcome_desc,indA,indB\n" +
                        "1,\"Test, outcome\",\"First indicator\",\"Second, indicator\"\n";

        List<Outcome> list = new CSVRepo(csv).loadFromCSV();
        assertEquals(1, list.size());

        Outcome o = list.get(0);
        assertEquals("1",      o.getOutcomeNum());
        assertEquals("Test, outcome", o.getDescription());

        List<Indicator> inds = o.getIndicators();
        assertEquals(2, inds.size());
        assertEquals("First indicator",      inds.get(0).getDescription());
        assertEquals("Second, indicator",    inds.get(1).getDescription());
    }

    // --- CRUD tests ---
    @Test
    void save_and_findById_roundTrips() {
        Outcome o = new Outcome();
        o.setOutcomeNum("O-1");
        o.setDescription("Desc1");

        Outcome saved = repo.save(o);
        assertTrue(saved.getId() > 0);

        Outcome fetched = repo.findById(saved.getId());
        assertNotNull(fetched);
        assertEquals("O-1", fetched.getOutcomeNum());
        assertEquals("Desc1", fetched.getDescription());
    }

    @Test
    void findAll_reflectsInserts() {
        // DB starts empty (only schema).
        assertTrue(repo.findAll().isEmpty());

        Outcome o = new Outcome();
        o.setOutcomeNum("ALL-1");
        o.setDescription("DescAll");
        repo.save(o);

        List<Outcome> all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals("ALL-1", all.get(0).getOutcomeNum());
    }

    @Test
    void update_modifiesExistingRow() {
        Outcome o = new Outcome();
        o.setOutcomeNum("UP-1");
        o.setDescription("Before");
        repo.save(o);

        o.setOutcomeNum("UP-1a");
        o.setDescription("After");
        assertTrue(repo.update(o));

        Outcome updated = repo.findById(o.getId());
        assertEquals("UP-1a", updated.getOutcomeNum());
        assertEquals("After", updated.getDescription());
    }

    @Test
    void delete_removesRow() {
        Outcome o = new Outcome();
        o.setOutcomeNum("DEL-1");
        o.setDescription("ToBeDeleted");
        repo.save(o);

        int id = o.getId();
        assertTrue(repo.delete(id));
        assertNull(repo.findById(id));
    }

    // --- course-outcome mappings ---
    @Test
    void findByCourseId_and_findAllCourseOutcomes_returnCorrectMappings() throws SQLException {
        // 1) create two outcomes
        Outcome o1 = new Outcome(); o1.setOutcomeNum("C1"); o1.setDescription("D1"); repo.save(o1);
        Outcome o2 = new Outcome(); o2.setOutcomeNum("C2"); o2.setDescription("D2"); repo.save(o2);

        // 2) seed Course_Outcome table manually
        try (Connection conn = h2Hikari.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Course_Outcome(course_code, outcome_id) VALUES(?,?)")) {
            ps.setString(1, "CS101"); ps.setInt(2, o1.getId()); ps.addBatch();
            ps.setString(1, "CS101"); ps.setInt(2, o2.getId()); ps.addBatch();
            ps.setString(1, "CS102"); ps.setInt(2, o1.getId()); ps.addBatch();
            ps.executeBatch();
        }

        List<Integer> cs101 = repo.findByCourseId("CS101");
        assertEquals(2, cs101.size());
        assertTrue(cs101.contains(o1.getId()));
        assertTrue(cs101.contains(o2.getId()));

        Map<String,List<Integer>> allMap = repo.findAllCourseOutcomes();
        assertTrue(allMap.containsKey("CS101"));
        assertTrue(allMap.containsKey("CS102"));
        assertEquals(2, allMap.get("CS101").size());
        assertEquals(1, allMap.get("CS102").size());
    }
}
