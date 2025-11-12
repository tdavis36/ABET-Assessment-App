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
      <tr v-for="(course, index) in courses" :key="index">
        <td>
          <input
            v-if="editMode"
            v-model="course.name"
            type="text"
            class="editable-input"
          />
          <span v-else>{{ course.name }}</span>
        </td>

        <td>
          <input
            v-if="editMode"
            v-model="course.instructor"
            type="text"
            class="editable-input"
          />
          <span v-else>{{ course.instructor }}</span>
        </td>

        <td>
          <template v-if="editMode">
            <input
              v-model.number="course.completed"
              type="number"
              min="0"
              class="small-input"
            />
            <span>/</span>
            <input
              v-model.number="course.total"
              type="number"
              min="1"
              class="small-input"
            />
          </template>
          <template v-else>
            {{ course.completed }}/{{ course.total }}
          </template>
        </td>

        <td>{{ course.submitted }}</td>

        <td v-if="editMode" class="reject-cell">
          <BaseButton
            class="reject-btn"
            :class="{ active: course.rejected }"
            @click="toggleReject(course)"
          >
            X
          </BaseButton>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="editMode" class="add-row">
      <BaseInput v-model="newCourse.name" type="text" placeholder="Course" />
      <BaseInput v-model="newCourse.instructor" type="text" placeholder="Instructor" />
      <BaseInput
        v-model.number="newCourse.total"
        type="number"
        min="1"
        placeholder="Measure Count"
      />
      <BaseButton class="add-btn" @click="addCourse">Add</BaseButton>
    </div>
  </section>
</template>

<script>
import {BaseButton, BaseInput} from "@/components/ui/index.ts";
export default {
  name: "ProgramInstructorsPage",
  components: {BaseInput, BaseButton},
  props: {
    programId: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      editMode: false,
      courses: [
        {
          name: "CS360",
          instructor: "Dean Zeller",
          completed: 15,
          total: 15,
          submitted: "Y",
          rejected: false,
        },
      ],
      newCourse: {
        name: "",
        instructor: "",
        completed: 0,
        total: 1,
        submitted: "N",
        rejected: false,
      },
    };
  },
  methods: {
    toggleEdit() {
      this.editMode = !this.editMode;
    },
    toggleReject(course) {
      course.rejected = !course.rejected;
      if (course.rejected) {
        course.submitted = "N";
        course.completed = 0;
      }
    },
    addCourse() {
      if (!this.newCourse.name || !this.newCourse.instructor) return;
      this.courses.push({
        ...this.newCourse,
        submitted: "N",
        rejected: false,
        completed: 0,
      });
      this.newCourse = {
        name: "",
        instructor: "",
        completed: 0,
        total: 1,
        submitted: "N",
        rejected: false,
      };
    },
  },
};
</script>

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
  background-color: var(--color-bg-secondary);
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.courses-table th {
  background-color: var(--color-bg-tertiary);
  text-align: left;
  padding: 0.6rem;
  font-weight: 600;
}

.courses-table td {
  border-bottom: 1px solid var(--color-border-light);
  padding: 0.6rem;
  vertical-align: middle;
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

.reject-cell {
  text-align: center;
}

.reject-btn {
  background-color: transparent;
  border: 1px solid var(--color-border-dark);
  color: var(--color-error-dark);
  border-radius: 4px;
  padding: 0.2rem 0.6rem;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
}

.reject-btn.active {
  background-color: #b22222;
  color: var(--color-text-primary);
}

.add-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  margin-top: 1rem;
}

.add-btn {
  background-color: var(--color-primary);
  color: var(--color-text-primary);
  border: none;
  border-radius: 6px;
  padding: 0.4rem 1rem;
  cursor: pointer;
}
</style>
