<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/api";
import { useUserStore } from "@/stores/user-store.ts";

const userStore = useUserStore();

// Reactive values
const courses = ref([]);
const programUserId = ref(null);

// Program ID always comes from store
const programId = computed(() => userStore.currentProgramId);

// ----------------------------
// Load ProgramUser for current user
// ----------------------------
async function loadProgramUserId() {

  // MUST have programId first
  if (!programId.value) return;

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
}

// ----------------------------
// Load instructor’s courses
// ----------------------------
async function loadInstructorCourses() {

  if (!programUserId.value) return;

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
}

onMounted(async () => {
  try {
    await userStore.loadFromStorage();
    await loadProgramUserId();        // props removed → use store programId
    await loadInstructorCourses();
  } catch (err) {
    console.error("Error loading instructor view:", err);
  }
});
</script>

<template>
  <section class="instructor-dashboard">
    <h3>My Courses</h3>

    <table>
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
</template>

<style scoped>
.instructor-dashboard {
  background: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  align-items: center;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border-bottom: 1px solid var(--color-border-dark);
  padding: 0.6rem;
  text-align: left;
}

th {
  background: var(--color-bg-tertiary);
}
</style>
