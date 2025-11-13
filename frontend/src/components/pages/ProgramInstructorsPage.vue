<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/api";
import { useUserStore } from "@/stores/user-store.ts";

import { BaseButton, BaseInput } from "@/components/ui/index.ts";

const userStore = useUserStore();

// Always use program + semester from the store
const programId = computed(() => userStore.currentProgramId);
const semesterId = computed(() => userStore.currentSemesterId);

// State
const editMode = ref(false);
const courses = ref([]);
const programInstructors = ref([]);

const newCourse = ref({
  courseName: "",
  instructorName: "",
  total: 1,
});

// --------------------------------------
// Load instructors for the current program
// --------------------------------------
async function loadProgramInstructors() {
  if (!programId.value) return;

  const res = await api.get(`/program/${programId.value}/instructors`);
  programInstructors.value = res.data.data;
}

// --------------------------------------
// Load courses for the CURRENT SEMESTER
// --------------------------------------
async function loadCourses() {
  if (!semesterId.value) return;

  const res = await api.get("/courses", {
    params: { semesterId: semesterId.value },
  });

  const rawCourses = res.data.content;

  // Build formatted course list
  courses.value = await Promise.all(
    rawCourses.map(async (c) => {
      // Completeness check
      const compRes = await api.get(`/courses/${c.id}/completeness`);
      const comp = compRes.data.data;

      // Match instructor
      const instructorPU = programInstructors.value.find(
        (pi) => pi.userId === c.instructorId
      );

      const instructorName =
        instructorPU && instructorPU.user
          ? `${instructorPU.user.firstName} ${instructorPU.user.lastName}`
          : "Unassigned";

      return {
        id: c.id,
        courseName: c.courseName,
        instructorName,
        completed: comp.completedMeasures,
        total: comp.totalMeasures,
        submitted: comp.completedMeasures >= comp.totalMeasures,
        rejected: false,
      };
    })
  );
}

// --------------------------------------
// Edit mode toggle
// --------------------------------------
function toggleEdit() {
  editMode.value = !editMode.value;
}

// --------------------------------------
// Reject a course
// --------------------------------------
function toggleReject(course) {
  course.rejected = !course.rejected;

  if (course.rejected) {
    course.submitted = false;
    course.completed = 0;
  }
}

// --------------------------------------
// Add new course to semester
// --------------------------------------
async function addCourse() {
  if (!newCourse.value.courseName) return;

  try {
    const dto = {
      courseName: newCourse.value.courseName,
      courseCode: newCourse.value.courseName.replace(/\s+/g, "").toUpperCase(),
      semesterId: semesterId.value,
      totalMeasures: newCourse.value.total,
    };

    const res = await api.post("/courses", dto);
    const created = res.data.data;

    courses.value.push({
      id: created.id,
      courseName: created.courseName,
      instructorName: newCourse.value.instructorName,
      completed: 0,
      total: newCourse.value.total,
      submitted: false,
      rejected: false,
    });

    newCourse.value = { courseName: "", instructorName: "", total: 1 };
  } catch (e) {
    console.error("Failed to add course", e);
  }
}

// --------------------------------------
// Init on load
// --------------------------------------
onMounted(async () => {
  await userStore.loadFromStorage();
  await loadProgramInstructors();
  await loadCourses();
});
</script>

<template>
  <section class="courses-section">
    <div class="section-header">
      <h3 class="h3">Courses</h3>
      <BaseButton class="edit-btn" @click="toggleEdit">
        {{ editMode ? "Done" : "Edit" }}
      </BaseButton>
    </div>

    <table class="courses-table">
      <thead>
      <tr>
        <th>Course</th>
        <th>Instructor</th>
        <th>Measures Completed</th>
        <th>Submitted (Y/N)</th>
        <th v-if="editMode">Reject</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="course in courses" :key="course.id">
        <td>
          <input v-if="editMode" v-model="course.courseName" class="editable-input" />
          <span v-else>{{ course.courseName }}</span>
        </td>

        <td>
          <input v-if="editMode" v-model="course.instructorName" class="editable-input" />
          <span v-else>{{ course.instructorName }}</span>
        </td>

        <td>
          <template v-if="editMode">
            <input class="small-input" type="number" min="0" v-model.number="course.completed" />
            <span>/</span>
            <input class="small-input" type="number" min="1" v-model.number="course.total" />
          </template>
          <template v-else>
            {{ course.completed }}/{{ course.total }}
          </template>
        </td>

        <td>{{ course.submitted ? "Y" : "N" }}</td>

        <td v-if="editMode" class="reject-cell">
          <BaseButton class="reject-btn" :class="{ active: course.rejected }" @click="toggleReject(course)">
            X
          </BaseButton>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="editMode" class="add-row">
      <BaseInput v-model="newCourse.courseName" placeholder="Course" />
      <BaseInput v-model="newCourse.instructorName" placeholder="Instructor" />
      <BaseInput v-model.number="newCourse.total" type="number" min="1" placeholder="Measures" />
      <BaseButton class="add-btn" @click="addCourse">Add</BaseButton>
    </div>
  </section>
</template>

<style scoped>
.h3 {
  font-size: var(--font-size-xl);
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
  align-items: center;
}
.courses-section {
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.edit-btn {
  background-color: var(--color-primary);
  color: var(--color-text-primary);
  border: none;
  border-radius: 6px;
  padding: 0.3rem 0.8rem;
  cursor: pointer;
  font-size: 0.9rem;
}
.courses-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}
.courses-table th {
  background-color: var(--color-bg-tertiary);
  padding: 0.6rem;
  font-weight: 600;
}
.courses-table td {
  border-bottom: 1px solid var(--color-border-light);
  padding: 0.6rem;
}
.editable-input,
.small-input {
  padding: 0.4rem;
  border: 1px solid var(--color-border-light);
  border-radius: 4px;
}
.small-input {
  width: 3rem;
  text-align: center;
}
.reject-btn {
  border: 1px solid var(--color-border-dark);
  color: var(--color-error-dark);
}
.reject-btn.active {
  background-color: #b22222;
  color: white;
}
</style>
