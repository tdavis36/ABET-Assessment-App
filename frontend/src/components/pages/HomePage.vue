<script lang="ts" setup>
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user-store.js";

import ProgramInstructorsPage from "@/components/pages/ProgramInstructorsPage.vue";
import InstructorViewPage from "@/components/pages/InstructorViewPage.vue";

const userStore = useUserStore();
const { isLoggedIn, isAdmin, isInstructor } = storeToRefs(userStore);
</script>

<template>
  <main class="homepage">

    <!-- Not logged in -->
    <div v-if="!isLoggedIn" id="log-in-popup">
      Log in to view course information
    </div>

    <!-- Logged in -->
    <div v-else id="dashboards">

      <!-- Admin Dashboard -->
      <template v-if="isAdmin">
        <header class="dashboard-header">
          <h1>Administrator Dashboard</h1>
        </header>

        <ProgramInstructorsPage :programId="userStore.currentProgramId || 1" />

        <hr class="section-divider" />
      </template>

      <!-- Instructor Dashboard -->
      <template v-if="isInstructor || isAdmin">
        <InstructorViewPage :programId="userStore.currentProgramId || 1" />
      </template>

      <!-- Fallback (logged in but not admin or instructor) -->
      <template v-if="!isAdmin && !isInstructor">
        <h2>You are logged in, but your account has no dashboard privileges.</h2>
      </template>
    </div>

  </main>
</template>
