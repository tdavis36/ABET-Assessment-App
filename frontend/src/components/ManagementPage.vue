<template>
  <div class="management-page">
    <div class="page-header">
      <h1>Management Dashboard</h1>
      <p class="subtitle">View and manage courses and instructors</p>
    </div>

    <!-- Tab Navigation -->
    <div class="tab-navigation">
      <button
        :class="['tab-button', { active: activeTab === 'courses' }]"
        @click="activeTab = 'courses'"
      >
        Courses
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'instructors' }]"
        @click="activeTab = 'instructors'"
      >
        Instructors
      </button>
    </div>

    <!-- Content Area -->
    <div class="content-area" :class="{ 'no-padding': activeTab === 'instructors' }">
      <!-- Courses Tab -->
      <div v-if="activeTab === 'courses'" class="tab-content">
        <div class="search-section">
          <label for="course-select">Select Course:</label>

          <select
            id="course-select"
            v-model.number="selectedCourseId"
            class="course-select"
            @change="onCourseChange"
            :disabled="isLoading || courses.length === 0"
          >
            <option :value="null" disabled>
              {{ courses.length === 0 ? 'No courses available' : '-- Select a Course --' }}
            </option>
            <option
              v-for="course in courses"
              :key="course.id"
              :value="course.id"
            >
              {{ course.courseCode }} - {{ course.courseName }}
            </option>
          </select>
          <button
            class="edit-button"
            v-if="selectedCourseId"
            @click="openEditModal"
          >
            Edit
          </button>
        </div>

        <div v-if="error" class="empty-state">
          <p>{{ error }}</p>
        </div>

        <div v-else-if="isLoading" class="empty-state">
          <p>Loading courses...</p>
        </div>

        <div v-else-if="selectedCourseId" class="view-container">
          <CourseViewPage :course-id="selectedCourseId" />
        </div>

        <div v-else class="empty-state">
          <p>
            {{ courses.length === 0
            ? 'No courses found for the current semester.'
            : 'Select a course to view details' }}
          </p>
        </div>
      </div>

      <!-- Instructors Tab -->
      <div v-if="activeTab === 'instructors'" class="tab-content">
        <InstructorSelector />
      </div>
      <CourseEditorModal
        :course="editingCourse"
        @close="closeEditModal"
        @saved="handleCourseSaved"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CourseViewPage from '@/components/pages/CourseViewPage.vue'
import InstructorSelector from '@/components/pages/InstructorSelector.vue'
import api from '@/api'
import CourseEditorModal from "@/components/CourseEditorModal.vue";

interface Course {
  id: number
  courseCode: string
  courseName: string
  courseDescription?: string
  isActive: boolean
  semesterId: number
  studentCount?: number
}


const route = useRoute()
const router = useRouter()

// Tabs
const activeTab = ref<'courses' | 'instructors'>('courses')

// Course state
const courses = ref<Course[]>([])
const selectedCourseId = ref<number | null>(null)
const semesterId = ref<number | null>(null)

// Loading + error
const isLoading = ref(false)
const error = ref<string | null>(null)

const editingCourse = ref<Course | null>(null)

function openEditModal() {
  if (!selectedCourseId.value) return

  // Find selected course from the list
  const course = courses.value.find(c => c.id === selectedCourseId.value)
  if (course) {
    editingCourse.value = course
  }
}

function closeEditModal() {
  editingCourse.value = null
}

// Refresh after save
async function handleCourseSaved() {
  await fetchCourses()

  // maintain selection (if the course still exists)
  const stillExists = courses.value.some(c => c.id === selectedCourseId.value)
  if (!stillExists) {
    selectedCourseId.value = null
  }

  editingCourse.value = null
}

/**
 * Fetch courses for the current semester.
 * Matches CourseController#getAllCourses:
 *   GET /api/courses?semesterId=...&page=&size=&sort=&direction=
 */
async function fetchCourses() {
  try {
    isLoading.value = true;
    error.value = null;

    const res = await api.get('/courses/active/all');
    courses.value = res.data.data ?? [];
  } catch (err) {
    console.error('Error fetching courses:', err);
    error.value = 'Failed to load courses';
  } finally {
    isLoading.value = false;
  }
}

/**
 * Handle course selection -> update route query.
 */
function onCourseChange() {
  if (selectedCourseId.value != null) {
    router.push({
      name: 'Setup',
      query: {
        tab: 'courses',
        courseId: selectedCourseId.value,
        ...(semesterId.value ? { semesterId: semesterId.value } : {})
      }
    })
  }
}

/**
 * Initialize state from route query params.
 *  - tab
 *  - courseId
 *  - semesterId
 */
function initializeFromQuery() {
  const tab = route.query.tab as string | undefined
  const courseId = route.query.courseId as string | undefined
  const semId = route.query.semesterId as string | undefined

  activeTab.value = tab === 'instructors' ? 'instructors' : 'courses'

  if (courseId) {
    const parsed = Number(courseId)
    selectedCourseId.value = Number.isNaN(parsed) ? null : parsed
  }

  if (semId) {
    const parsed = Number(semId)
    semesterId.value = Number.isNaN(parsed) ? null : parsed
  }
}

// React to URL query changes
watch(
  () => route.query,
  () => {
    initializeFromQuery()
    fetchCourses()
  }
)

onMounted(() => {
  initializeFromQuery()
  fetchCourses()
})
</script>

<style scoped>
.management-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.5rem;
}

.subtitle {
  color: var(--color-text-secondary);
  font-size: 1rem;
}

.tab-navigation {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid var(--color-border-dark);
}

.tab-button {
  padding: 0.75rem 1.5rem;
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: var(--color-text-primary);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: -2px;
}

.tab-button:hover {
  color: #1f2937;
}

.tab-button.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-border-dark);
}

.content-area {
  background: var(--color-bg-secondary);
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.content-area.no-padding {
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.tab-content {
  min-height: 400px;
}

.search-section {
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.search-section label {
  font-weight: 500;
  color: #374151;
}

.course-select {
  flex: 1;
  max-width: 500px;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 1rem;
  color: #1f2937;
  background-color: var(--color-bg-tertiary);
  cursor: pointer;
  transition: border-color 0.2s;
}

.course-select:hover {
  border-color: #9ca3af;
}

.course-select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.view-container {
  margin-top: 1.5rem;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #9ca3af;
  font-size: 1.125rem;
}

/* Responsive */
@media (max-width: 768px) {
  .management-page {
    padding: 1rem;
  }

  .page-header h1 {
    font-size: 1.5rem;
  }

  .search-section {
    flex-direction: column;
    align-items: stretch;
  }

  .course-select {
    max-width: 100%;
  }

  .content-area {
    padding: 1rem;
  }
}
</style>
