<template>
  <section class="instructor-dashboard">
    <div class="courses">
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
            <td>{{ course.course_code }}</td>
            <td>{{ course.instructor_name }}</td>
            <td>{{ course.measures_completed }}/{{ course.measures_total }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script>
import axios from "axios";

export default {
  name: "InstructorDashboard",
  props: {
    programId: Number,
    isAdmin: Boolean,
  },
  data() {
    return {
      institutionName: "York College of Pennsylvania",
      courses: [],
    };
  },
  async mounted() {
    try {
      const response = await axios.get(`/api/courses/instructor`, {
        params: { program_id: this.programId },
      });
      this.courses = response.data;
    } catch (err) {
      console.error("Error loading instructor courses:", err);
    }
  },
};
</script>

<style scoped>
.instructor-dashboard {
  background: var(--color-bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

h2 {
  font-size: var(--font-size-2xl);
  margin-bottom: 1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border-bottom: 1px solid var(--color-border-dark);
  padding: 0.6rem;
  text-align: left;
}

th {
  background: var(--color-bg-tertiary);
}
</style>
