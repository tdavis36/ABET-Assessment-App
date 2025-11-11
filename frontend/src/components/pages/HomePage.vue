<template>
  <main class="homepage">
    
    <header class="dashboard-header">
      <h1>Administrator Dashboard</h1>
    </header>

    <!-- Admin Dashboard -->
    <ProgramInstructorsPage :programId="1" />

    <hr class="section-divider" />

    <!-- Instructor Dashboard -->
    <InstructorViewPage :programId="1" />

    <section class="title-section">
      <h1>ABET Assessment App — Homepage</h1>
      <p>
        This page defines the foundational terms used within the ABET Assessment App. These concepts
        form the basis for how programs measure, evaluate, and document student achievement and
        accreditation readiness.
      </p>
    </section>

    <section class="content-section">
      <h2>Fundamental Assessment Concepts</h2>

      <div class="two-column">
        <div class="column">
          <h3>Program Educational Objectives</h3>
          <p>
            Broad statements describing what graduates are expected to achieve a few years after
            graduation.
          </p>

          <h3>Student Outcome</h3>
          <p>
            The knowledge, skills, and behaviors students are expected to attain by graduation.
            Institutions demonstrate achievement through performance indicators.
          </p>
        </div>

        <div class="column">
          <h3>Assessment</h3>
          <p>Processes used to identify, collect, and prepare data for evaluation.</p>

          <h3>Evaluation</h3>
          <p>
            Processes used to interpret assessment data to determine how well outcomes are met and
            what program changes are needed.
          </p>
        </div>
      </div>

      <div class="center-block">
        <h3>Performance Indicator</h3>
        <p>
          Specific criteria defined by programs to determine whether a student outcome has been met.
        </p>

        <div class="example-outcome">
          <p class="italic">
            Example Outcome: “Students should be able to conduct an experiment and interpret data.”
          </p>
          <p>- Follow the design of an experiment plan</p>
          <p>- Compare experimental results to theoretical models</p>
          <p>- Acquire data on appropriate variables</p>
        </div>
      </div>
    </section>

    <section class="content-section alt">
      <h2>Reports and Measurement</h2>
      <div class="two-column">
        <div class="column">
          <p>
            <strong>FCAR —</strong> Faculty Course Assessment Report; summarizes assessments across
            a six-year accreditation cycle.
          </p>
          <p>
            <strong>Measure —</strong> Work and data collected for a specific performance indicator
            instance.
          </p>
          <p>
            <strong>Achievement Threshold —</strong> The minimum level of performance considered
            satisfactory.
          </p>
        </div>
        <div class="column">
          <p>
            <strong>Concern —</strong> A potential issue identified by ABET that may become a
            problem if unaddressed.
          </p>
          <p><strong>Weakness —</strong> A problem that must be corrected within two years.</p>
          <p>
            <strong>Deficiency —</strong> A critical failure requiring immediate corrective action
            to maintain accreditation.
          </p>
        </div>
      </div>
    </section>

    <section class="content-section">
      <h2>Core Educational Principles</h2>
      <div class="two-column">
        <div class="column">
          <p>
            <strong>Respectful Environment —</strong> “A respectful environment supports, values,
            and treats all members fairly and with dignity.”
          </p>
          <p>
            <strong>Basic Science —</strong> Fundamental sciences such as chemistry, physics, life,
            earth, and space sciences.
          </p>
          <p>
            <strong>College-Level Mathematics —</strong> Mathematics at or above introductory
            calculus.
          </p>
          <p>
            <strong>Engineering Design —</strong> Designing systems or processes to meet needs under
            constraints using science and mathematics.
          </p>
        </div>
        <div class="column">
          <p>
            <strong>Complex Engineering Problems —</strong> Problems with no obvious solution,
            multiple disciplines, or significant consequences.
          </p>
          <p>
            <strong>Engineering Science —</strong> Application of basic science and math to
            understand and solve engineering problems.
          </p>
          <p><strong>Team —</strong> “More than one person working toward a common goal.”</p>
        </div>
      </div>
    </section>

    <footer class="footer">
      <hr />
      <p>© 2025 ABET Assessment App</p>
      <p>Definitions adapted from ABET documentation.</p>
    </footer>
  </main>
</template>

<script>
import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
import InstructorViewPage from "@/components/pages/InstructorViewPage.vue";

export default {
  name: "HomePage",
  components: {
    ProgramInstructorsPage,
    InstructorViewPage,
  },
};
</script>

<style scoped>
.homepage {
  font-family:
    system-ui,
    -apple-system,
    'Segoe UI',
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
  color: #222;
  background-color: #fff;
  margin: 0 auto;
  padding: 2rem;
  max-width: 1200px;
}


.dashboard-header {
  text-align: center;
  margin-bottom: 1.5rem;
}
.dashboard-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #000000;
  margin: 0;
}


.section-divider {
  border: none;
  border-top: 2px dashed rgba(178, 34, 34, 0.35); 
  margin: 2.5rem auto;   /* space between dashbaords*/
  width: 80%;
}
</style>
