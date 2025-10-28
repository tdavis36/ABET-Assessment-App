<template>
  <section class="admin-dashboard">
    <header>
      <h2>{{ institutionName }} ~ Admin Dashboard</h2>
    </header>

    <!-- Courses Section -->
    <div class="section">
      <div class="section-header">
        <h3>Courses</h3>
        <button @click="showCoursePopup = true">Edit</button>
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
        <button @click="showInstructorAdd = true">Edit</button>
      </div>
      <ul>
        <li v-for="inst in instructors" :key="inst.id">{{ inst.name }}</li>
      </ul>
    </div>

    <!-- Course Popup -->
    <div v-if="showCoursePopup" class="popup-overlay" @click.self="closeCoursePopup">
      <div class="popup-window">
        <header class="popup-header">
          <h3>Edit Courses</h3>
          <button class="close-btn" @click="closeCoursePopup">×</button>
        </header>
        <div class="popup-body">
          <table class="edit-table">
            <thead>
              <tr>
                <th>Course Code</th>
                <th>Instructor</th>
                <th>Measures</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="course in courses" :key="course.id">
                <td><input v-model="course.course_code" /></td>
                <td><input v-model="course.instructor_name" /></td>
                <td>{{ course.measures_completed }}/{{ course.measures_total }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <footer class="popup-footer">
          <button class="save-btn" @click="saveCourseChanges">Save</button>
          <button class="cancel-btn" @click="closeCoursePopup">Cancel</button>
        </footer>
      </div>
    </div>

    <!-- Instructor Add Popup -->
    <div v-if="showInstructorAdd" class="popup-overlay" @click.self="closeInstructorAdd">
      <div class="popup-window">
        <header class="popup-header">
          <h3>Edit Instructors</h3>
          <button class="close-btn" @click="closeInstructorAdd">×</button>
        </header>
        <div class="popup-body">
          <ul>
            <li v-for="inst in instructors" :key="inst.id">
              <input v-model="inst.name" />
            </li>
          </ul>
          <div class="add-new">
            <input v-model="newInstructorName" placeholder="Add new instructor..." />
            <button class="add-btn" @click="addInstructor">Add</button>
          </div>
        </div>
        <footer class="popup-footer">
          <button class="save-btn" @click="saveInstructorChanges">Save</button>
          <button class="cancel-btn" @click="closeInstructorAdd">Cancel</button>
        </footer>
      </div>
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
      showCoursePopup: false,
      showInstructorAdd: false,
      newInstructorName: "",
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
    // ---- Course Popup ----
    closeCoursePopup() {
      this.showCoursePopup = false;
    },
    async saveCourseChanges() {
      try {
        await axios.post(`/api/courses/update`, this.courses);
        alert("Course changes saved successfully!");
        this.closeCoursePopup();
      } catch (err) {
        console.error("Error saving courses:", err);
        alert("Failed to save courses.");
      }
    },

    // ---- Instructor Add Popup ----
    closeInstructorAdd() {
      this.showInstructorAdd = false;
    },
    addInstructor() {
      if (this.newInstructorName.trim()) {
        this.instructors.push({
          id: Date.now(),
          name: this.newInstructorName.trim(),
        });
        this.newInstructorName = "";
      }
    },
    async saveInstructorChanges() {
      try {
        await axios.post(`/api/instructors/update`, this.instructors);
        alert("Instructor changes saved successfully!");
        this.closeInstructorAdd();
      } catch (err) {
        console.error("Error saving instructors:", err);
        alert("Failed to save instructors.");
      }
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
  transition: background 0.2s ease;
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

/* Popup sec */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-window {
  background: white;
  width: 90%;
  max-width: 700px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2);
  overflow: hidden;
}

.popup-header {
  background: #d83047df;
  color: rgb(0, 0, 0);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1.2rem;
}

.close-btn {
  background: none;
  color: white;
  font-size: 1.5rem;
  border: none;
  cursor: pointer;
}

.popup-body {
  padding: 1rem 1.2rem;
}

.add-new {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}

.add-new input {
  flex: 1;
  padding: 0.4rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.add-btn {
  background: #004a7d;
  color: white;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.popup-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.6rem;
  padding: 0.8rem 1.2rem;
  border-top: 1px solid #ddd;
}

.save-btn {
  background: #004a7d;
  color: #fff;
  padding: 0.4rem 1rem;
  border-radius: 4px;
  border: none;
}

.cancel-btn {
  background: #888;
  color: #fff;
  padding: 0.4rem 1rem;
  border-radius: 4px;
  border: none;
}
</style>
