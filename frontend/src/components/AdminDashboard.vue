<template>
  <section class="combined-dashboard">
 
    <ProgramInstructorsPage :programId="programId" />

    <hr class="divider" />

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

.footer {
  text-align: center;
  color: #555;
  font-size: 0.9rem;
  margin-top: 3rem;
}

.footer hr {
  border: none;
  border-top: 1px solid #ccc;
  margin-bottom: 1rem;
  width: 100%;
}
</style>
