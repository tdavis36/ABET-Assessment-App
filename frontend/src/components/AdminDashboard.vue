<template>
  <section class="combined-dashboard">
    <header class="dashboard-header">
      <h1>Administrator Dashboard</h1>
    </header>
 
    <ProgramInstructorsPage :programId="programId" />

    <hr class="divider" />

    <InstructorViewPage :programId="programId" />
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
  padding: 2rem;
  font-family:
    system-ui,
    -apple-system,
    "Segoe UI",
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
  color: #222;
  background-color: #fff;
}

.divider {
  border: none;
  border-top: 2px solid #ccc;
  margin: 1.5rem 0;
}
</style>
