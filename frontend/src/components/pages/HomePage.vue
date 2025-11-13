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

  </main>
</template>
