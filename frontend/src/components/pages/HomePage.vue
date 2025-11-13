<script lang="ts" setup>
  import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
  import InstructorViewPage from "@/components/pages/InstructorViewPage.vue";

  const props = defineProps({
    loggedIn: Boolean,
  })

  console.log(props.loggedIn)
</script>

<template>
  <main class="homepage">

    <div v-if="!loggedIn" id="log-in-popup">
      Log in to view course information
    </div>

    <div v-else id="dashboards">
      <header class="dashboard-header">
        <h1>Administrator Dashboard</h1>
      </header>

      <!-- Admin Dashboard -->
      <ProgramInstructorsPage :programId="1" />

      <hr class="section-divider" />

      <!-- Instructor Dashboard -->
      <InstructorViewPage :programId="1" />
    </div>

  </main>
</template>
