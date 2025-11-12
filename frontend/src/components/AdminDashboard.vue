<template>
  <section class="combined-dashboard">
    <header class="dashboard-header">
      <h1>Administrator Dashboard</h1>
    </header>

    <ProgramInstructorsPage :programId="programId" />

    <hr class="divider" />

    <header class="dashboard-header">
      <h1>Instructor Dashboard</h1>
    </header>

    <InstructorViewPage :programId="programId" />


    <footer class="footer">
      <hr />
      <p>© 2025 ABET Assessment App</p>
      <p>Definitions adapted from ABET documentation.</p>
    </footer>
  </section>
</template>

<script>
import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
import InstructorViewPage from "@/components/pages/InstructorViewPage.vue";

export default {
  name: "AdminDashboard",
  components: {
    ProgramInstructorsPage,
    InstructorViewPage,
  },
  props: {
    programId: {
      type: Number,
      default: 1, // change to ID
    },
  },
};
</script>

<style scoped>
.combined-dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  font-family:
    system-ui,
    -apple-system,
    "Segoe UI",
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
  color: var(--color-text-primary);
  background-color: var(--color-bg-primary);
}

.divider {
  border: none;
  border-top: 2px solid var(--color-border-light);
}

.footer {
  text-align: center;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  margin-top: 3rem;
}

.footer hr {
  border: none;
  border-top: 1px solid var(--color-border-light);
  margin-bottom: 1rem;
  width: 100%;
}
</style>
