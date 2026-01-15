<script setup lang="ts">
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user-store.ts";

import CourseListing from "@/components/CourseListing.vue";
import ProgramInstructorsPage from "@/pages/ProgramInstructorsPage.vue";

const userStore = useUserStore();
const { currentProgramId: programId } = storeToRefs(userStore);
</script>

<template>
  <section v-if="programId" class="admin-dashboard">

    <header>
      <h1>Administrator Dashboard</h1>
    </header>

    <!-- COURSE LISTING -->
    <CourseListing :program-id="programId" />

    <hr />

    <!-- PROGRAM INSTRUCTORS -->
    <ProgramInstructorsPage :program-id="programId" />

  </section>

  <section v-else class="loading-screen">
    <p>Loading program information...</p>
  </section>
</template>

<style scoped>
.admin-dashboard {
  margin: 2rem;
}
</style>
