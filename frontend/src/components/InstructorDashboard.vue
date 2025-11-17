<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { useUserStore } from "@/stores/user-store";
import { storeToRefs } from "pinia";
import api from "@/api";

// ------------------------------
// TYPES
// ------------------------------
interface ProgramUser {
  id: number;
  userId: number;
  programId: number;
  role: string;
}

interface RawCourse {
  id: number;
  courseCode: string;
}

interface InstructorDashboardCourse {
  id: number;
  courseCode: string;
  instructorName: string;
  measuresCompleted: number;
  measuresTotal: number;
}

// ------------------------------
// STORE REFERENCES
// ------------------------------
const userStore = useUserStore();
const { currentProgramId: programId } = storeToRefs(userStore);

// ------------------------------
// STATE
// ------------------------------
const courses = ref<InstructorDashboardCourse[]>([]);
const programUserId = ref<number | null>(null);
const isLoading = ref<boolean>(false);
const errorMessage = ref<string | null>(null);

// ------------------------------
// LOAD PROGRAM USER FOR CURRENT PROGRAM
// ------------------------------
async function loadProgramUserId() {
  if (!programId.value) return;

  const res = await api.get(`/program/${programId.value}/users`);
  const programUsers = res.data.data as ProgramUser[];

  const me = programUsers.find((pu) => pu.userId === userStore.userId);
  if (!me) throw new Error("User is not assigned to this program");

  programUserId.value = me.id;
}

// ------------------------------
// LOAD INSTRUCTOR COURSE LIST
// ------------------------------
async function loadInstructorCourses() {
  if (!programUserId.value) return;

  const cRes = await api.get("/courses/instructor", {
    params: { programUserId: programUserId.value },
  });

  const rawCourses = cRes.data.data as RawCourse[];

  const results: InstructorDashboardCourse[] = [];

  for (const course of rawCourses) {
    const completenessRes = await api.get(`/courses/${course.id}/completeness`);
    const comp = completenessRes.data.data;

    results.push({
      id: course.id,
      courseCode: course.courseCode,
      instructorName: `${userStore.user?.firstName} ${userStore.user?.lastName}`,
      measuresCompleted: comp.completedMeasures,
      measuresTotal: comp.totalMeasures,
    });
  }

  courses.value = results;
}

// ------------------------------
// COMBINED LOADER
// ------------------------------
async function reload() {
  if (!programId.value) return;

  isLoading.value = true;
  errorMessage.value = null;

  try {
    await loadProgramUserId();
    await loadInstructorCourses();
  } catch (err) {
    console.error(err);
    errorMessage.value = "Failed to load instructor dashboard";
  } finally {
    isLoading.value = false;
  }
}

onMounted(reload);

// Reload when program ID changes
watch(programId, reload);
</script>

<template>
  <section v-if="programId && !isLoading" class="instructor-dashboard">
    <h1>Instructor Dashboard</h1>

    <div v-if="errorMessage" class="error">
      {{ errorMessage }}
    </div>

    <div v-if="courses.length === 0">
      <p>No courses assigned.</p>
    </div>

    <table v-else>
      <thead>
      <tr>
        <th>Course</th>
        <th>Instructor</th>
        <th>Measures</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="course in courses" :key="course.id">
        <td>{{ course.courseCode }}</td>
        <td>{{ course.instructorName }}</td>
        <td>{{ course.measuresCompleted }}/{{ course.measuresTotal }}</td>
      </tr>
      </tbody>
    </table>
  </section>

  <section v-else class="loading-screen">
    Loading instructor dashboard...
  </section>
</template>

<style scoped>
.instructor-dashboard { margin: 2rem; }
.error { color: red; margin-bottom: 1rem; }
</style>
