<script lang="ts" setup>
import { ref, onMounted, watch } from "vue";
import { storeToRefs } from "pinia";
import api from "@/api";

import { useUserStore } from "@/stores/user-store.ts";
import AdminDashboard from "@/pages/AdminDashboard.vue";
import InstructorDashboard from "@/components/InstructorDashboard.vue";

// User store
const userStore = useUserStore();
const { isLoggedIn, isAdmin, isInstructor } = storeToRefs(userStore);

// Program selector data
interface Program {
  id: number;
  name: string;
  institution: string;
  active: boolean;
}

const programs = ref<Program[]>([]);
const selectedProgramId = ref<number | null>(null);
const loadingPrograms = ref(false);
const error = ref<string | null>(null);

// Load user and available programs
onMounted(async () => {
  userStore.loadFromStorage();
  await loadPrograms();
});

// Load programs from backend
async function loadPrograms() {
  loadingPrograms.value = true;
  try {
    const res = await api.get("/program", { params: { page: 0, size: 100 } });
    const paged = res.data;
    programs.value = paged.content ?? [];

    selectedProgramId.value =
      userStore.currentProgramId ?? programs.value[0]?.id ?? null;

  } catch (err) {
    console.error("Error loading programs:", err);
    error.value = "Failed to load programs";
  } finally {
    loadingPrograms.value = false;
  }
}

// Sync program changes back to the store
watch(selectedProgramId, (newId) => {
  if (newId) {
    userStore.currentProgramId = newId;
    userStore.saveToStorage();
  }
});
</script>


<template>
  <main class="homepage">

    <!-- Not logged in -->
    <div v-if="!isLoggedIn" id="log-in-popup">
      Log in to view course information
    </div>

    <!-- Logged in -->
    <div v-else id="dashboards">

      <!-- PROGRAM SELECTOR (now at top of home page!) -->
      <div class="program-selector" v-if="!loadingPrograms">
        <label for="program-select" class="selector-label">Select Program:</label>

        <select
          id="program-select"
          v-model.number="selectedProgramId"
          class="program-select"
        >
          <option
            v-for="program in programs"
            :key="program.id"
            :value="program.id"
          >
            {{ program.name }} - {{ program.institution }}
          </option>
        </select>
      </div>

      <div v-if="loadingPrograms" class="loading-screen">
        <p>Loading programs...</p>
      </div>

      <!-- ADMIN DASHBOARD -->
      <template v-if="isAdmin && selectedProgramId">
        <AdminDashboard :programId="selectedProgramId" />
      </template>

      <!-- Divider only if user is both admin and instructor -->
      <hr v-if="isAdmin && isInstructor" class="section-divider" />

      <!-- INSTRUCTOR DASHBOARD -->
      <template v-if="isInstructor && selectedProgramId">
        <InstructorDashboard :programId="selectedProgramId" />
      </template>

      <!-- Fallback -->
      <template v-if="!isAdmin && !isInstructor">
        <h2>You are logged in, but your account has no dashboard privileges.</h2>
      </template>

    </div>
  </main>
</template>


<style scoped>
.program-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: 0.5rem;
  margin: 2rem 2rem 1rem;
}

.selector-label {
  font-weight: 500;
  color: var(--color-text-primary);
  font-size: 0.875rem;
}

.program-select {
  flex: 1;
  color: var(--color-text-primary);
  max-width: 350px;
  padding: 0.625rem;
  font-size: 0.875rem;
  border: 1px solid var(--color-border-dark);
  border-radius: 0.375rem;
  background: var(--color-bg-primary);
}

.loading-screen {
  text-align: center;
  padding: 2rem;
}
</style>
