<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user-store.ts";

import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
import CourseViewPage from "@/components/pages/CourseViewPage.vue";
import CourseListing from "@/components/CourseListing.vue";
import api from "@/api";

const userStore = useUserStore();

const programs = ref([]);
const selectedProgramId = ref(null);
const loadingPrograms = ref(false);
const error = ref(null);

// Load user + programs
onMounted(async () => {
  await userStore.loadFromStorage();
  await loadPrograms();
});

// Load programs from backend
async function loadPrograms() {
  loadingPrograms.value = true;
  try {
    const res = await api.get("/program", { params: { page: 0, size: 100 } });
    const paged = res.data;
    programs.value = paged.content ?? [];

    // Default to userStore program OR first available
    selectedProgramId.value =
      userStore.currentProgramId ?? programs.value[0]?.id ?? null;

  } catch (err) {
    console.error("Error loading programs:", err);
    error.value = "Failed to load programs";
  } finally {
    loadingPrograms.value = false;
  }
}
</script>

<template>
  <section class="combined-dashboard">
    <!-- Loading state for initial program list -->
    <div v-if="loadingPrograms" class="loading-screen">
      <p>Loading programs...</p>
    </div>

    <div v-else-if="!selectedProgramId" class="loading-screen">
      <p>Please select a program to continue.</p>
    </div>

    <template v-else>
      <!-- DASHBOARD CONTENT -->
      <header class="dashboard-header">
        <h1>Administrator Dashboard</h1>
      </header>

      <!-- PROGRAM SELECTOR (Shared for All Components) -->
      <div class="program-selector">
        <label for="program-select" class="selector-label">
          Select Program:
        </label>

        <select
          id="program-select"
          v-model.number="selectedProgramId"
          class="program-select"
          :disabled="loadingPrograms || programs.length === 0"
        >
          <option :value="null" disabled>Choose a program...</option>
          <option
            v-for="program in programs"
            :key="program.id"
            :value="program.id"
          >
            {{ program.name }} - {{ program.institution }}
          </option>
        </select>
      </div>

      <CourseListing :program-id="selectedProgramId" />
      <hr class="divider" />

      <ProgramInstructorsPage :program-id="selectedProgramId" />
    </template>
  </section>
</template>

<style scoped>
.program-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: 0.5rem;
  margin-bottom: 1rem;
}

.divider {
  border: none;
  margin: 1rem 0;
}

.selector-label {
  font-weight: 500;
  color: var(--color-text-primary);
  font-size: 0.875rem;
  white-space: nowrap;
}

.program-select {
  flex: 1;
  max-width: 500px;
  padding: 0.625rem 0.875rem;
  font-size: 0.875rem;
  border: 1px solid var(--color-border-dark);
  border-radius: 0.375rem;
  background: var(--color-bg-primary);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.combined-dashboard {
  display: flex;
  flex-direction: column;
  margin: 2rem;
}

.loading-screen {
  text-align: center;
  padding: 2rem;
  color: var(--color-text-secondary);
}
</style>
