<script setup>
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/user-store.ts";
import api from "@/api";

const userStore = useUserStore();

// Reactive values
const courses = ref([]);
const programUserId = ref(null);
const isLoading = ref(true);
const errorMessage = ref(null);

// Program ID always comes from store
const programId = computed(() => userStore.currentProgramId);

// ----------------------------
// Load ProgramUser for current user
// ----------------------------
async function loadProgramUserId() {
  // MUST have programId first
  if (!programId.value) return;

  try {
    // Correct backend route:
    const res = await api.get(`/program/${programId.value}/users`);
    const programUsers = res.data.data; // List<ProgramUser>

    const myPU = programUsers.find(
      (pu) => pu.userId === userStore.userId
    );

    if (!myPU) {
      throw new Error("User is not assigned to this program");
    }

    programUserId.value = myPU.id;
  } catch (err) {
    console.error("Error loading program user ID:", err);
    errorMessage.value = "Failed to load user assignment";
    throw err;
  }
}

// ----------------------------
// Load instructor's courses
// ----------------------------
async function loadInstructorCourses() {
  if (!programUserId.value) return;

  try {
    const cRes = await api.get("/courses/instructor", {
      params: { programUserId: programUserId.value },
    });

    const raw = cRes.data.data;

    courses.value = await Promise.all(
      raw.map(async (course) => {
        const compRes = await api.get(`/courses/${course.id}/completeness`);
        const comp = compRes.data.data;

        return {
          id: course.id,
          courseCode: course.courseCode ?? course.course_code,
          instructorName: `${userStore.user?.firstName} ${userStore.user?.lastName}`,
          measuresCompleted: comp.completedMeasures,
          measuresTotal: comp.totalMeasures,
        };
      })
    );
  } catch (err) {
    console.error("Error loading instructor courses:", err);
    errorMessage.value = "Failed to load courses";
    throw err;
  }
}

onMounted(async () => {
  try {
    isLoading.value = true;
    await userStore.loadFromStorage();
    await loadProgramUserId();
    await loadInstructorCourses();
  } catch (err) {
    console.error("Error loading instructor dashboard:", err);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <!-- Wait for programId to be available -->
  <section v-if="programId && !isLoading" class="instructor-dashboard">

    <!-- Dashboard Header -->
    <header class="dashboard-header">
      <h1>Instructor Dashboard</h1>
    </header>

    <!-- Error Message -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- Courses Section -->
    <section class="courses-section">
      <h3>My Courses</h3>

      <div v-if="courses.length === 0" class="empty-state">
        <p>No courses assigned yet.</p>
      </div>

      <table v-else>
        <thead>
        <tr>
          <th>Course</th>
          <th>Instructor</th>
          <th>Measures Completed</th>
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
.instructor-dashboard {
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

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.error-message {
  background-color: var(--color-error, #fee);
  color: var(--color-text-primary, #c33);
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border-left: 4px solid var(--color-error, #c33);
}

.courses-section {
  background: var(--color-bg-secondary);
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 2rem;
}

.courses-section h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--color-text-secondary);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border-bottom: 1px solid var(--color-border-dark);
  padding: 0.75rem;
  text-align: left;
}

th {
  background: var(--color-bg-tertiary);
  font-weight: 600;
  color: var(--color-text-primary);
}

tbody tr:hover {
  background-color: var(--color-bg-tertiary, rgba(0, 0, 0, 0.02));
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
