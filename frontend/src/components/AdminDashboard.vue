<script setup>
import { computed, onMounted } from "vue";
import { useUserStore } from "@/stores/user-store.ts";

import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
import InstructorViewPage from "@/components/pages/InstructorViewPage.vue";

const userStore = useUserStore();

// Ensure stored data exists before rendering
onMounted(() => {
  userStore.loadFromStorage();
});

// Only render dashboard when programId is ready
const programId = computed(() => userStore.currentProgramId);
</script>

<template>
  <!-- Wait for programId to be available -->
  <section v-if="programId" class="combined-dashboard">

    <!-- Admin Section -->
    <header class="dashboard-header">
      <h1>Administrator Dashboard</h1>
    </header>

    <ProgramInstructorsPage/>

    <hr class="divider" />

    <!-- Instructor Section -->
    <header class="dashboard-header">
      <h1>Instructor Dashboard</h1>
    </header>

    <InstructorViewPage/>

    <!-- Footer -->
    <footer class="footer">
      <hr />
      <p>© 2025 ABET Assessment App</p>
      <p>Definitions adapted from ABET documentation.</p>
    </footer>

  </section>

  <!-- Loading state before userStore initializes -->
  <section v-else class="loading-screen">
    <p>Loading your dashboard...</p>
  </section>
</template>

<style scoped>
.combined-dashboard {
  display: flex;
  flex-direction: column;
  margin: 2rem;
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
  border-top: 2px solid var(--color-border-dark);
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

.loading-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
  color: var(--color-text-secondary);
  font-size: var(--font-size-lg);
}
</style>
