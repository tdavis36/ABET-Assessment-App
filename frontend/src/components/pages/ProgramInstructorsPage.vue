<template>
  <section class="admin-dashboard">
    <header>
      <h2>{{ institutionName }} ~ Admin Dashboard</h2>
    </header>

    <!-- Courses Section -->
    <div class="section">
      <div class="section-header">
        <h3>Courses</h3>
        <button @click="editCourses">Edit</button>
      </div>
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

    <!-- Instructors Section -->
    <div class="section">
      <div class="section-header">
        <h3>Instructors</h3>
        <button @click="editInstructors">Edit</button>
      </div>
      <ul>
        <li v-for="inst in instructors" :key="inst.id">{{ inst.name }}</li>
      </ul>
    </div>
  </section>
</template>

<script>
import axios from "axios";

export default {
  name: "AdminDashboard",
  props: {
    programId: Number,
  },
  data() {
    return {
      institutionName: "York College of Pennsylvania",
      courses: [],
      instructors: [],
    };
  },
  async mounted() {
    try {
      const [courseRes, instructorRes] = await Promise.all([
        axios.get(`/api/courses`, { params: { program_id: this.programId } }),
        axios.get(`/api/instructors`, { params: { program_id: this.programId } }),
      ]);
      this.courses = courseRes.data;
      this.instructors = instructorRes.data;
    } catch (err) {
      console.error("Error loading admin data:", err);
    }
  },
  methods: {
    editCourses() {
      this.$router.push({ name: "Program Courses", params: { program_id: this.programId } });
    },
    editInstructors() {
      this.$router.push({ name: "Program Instructors", params: { program_id: this.programId } });
    },
  },
};
</script>

<style scoped>
.admin-dashboard {
  background: #f5f5f5;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.section {
  margin-top: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

button {
  background: #f6203d;
  color: #fff;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background: #004a7d;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border-bottom: 1px solid #ddd;
  padding: 0.6rem;
  text-align: left;
}

th {
  background: #e9e9e9;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  border-bottom: 1px solid #ddd;
  padding: 0.5rem 0;
}
</style>
