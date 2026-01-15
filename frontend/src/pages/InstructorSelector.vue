<template>
  <div class="instructor-selector">
    <!-- Program Selection -->
    <BaseCard title="Select Program" variant="bordered">
      <BaseSelect
        v-model="selectedProgramId"
        :options="programOptions"
        label="Program"
        placeholder="Select a program"
        :disabled="loading || programsLoading"
        @update:modelValue="handleProgramChange"
      />
    </BaseCard>

    <!-- Instructor Selection -->
    <BaseCard
      v-if="selectedProgramId"
      title="Select Instructor"
      variant="bordered"
      class="instructor-card"
    >
      <div v-if="instructorsLoading" class="loading-state">
        <BaseSpinner size="md" />
        <p>Loading instructors...</p>
      </div>

      <div v-else-if="instructorError" class="error-state">
        <p class="error-message">{{ instructorError }}</p>
        <BaseButton variant="secondary" size="sm" @click="loadInstructors">
          Retry
        </BaseButton>
      </div>

      <div v-else-if="instructors.length === 0" class="empty-state">
        <p>No instructors found for this program.</p>
      </div>

      <BaseSelect
        v-else
        v-model="selectedInstructorId"
        :options="instructorOptions"
        label="Instructor"
        placeholder="Select an instructor"
        @update:modelValue="handleInstructorChange"
      />
    </BaseCard>

    <!-- Instructor Details -->
    <transition name="fade">
      <BaseCard
        v-if="selectedInstructor"
        title="Instructor Details"
        variant="elevated"
        class="details-card"
      >
        <div class="instructor-details">
          <!-- Basic Information -->
          <section class="detail-section">
            <h3 class="section-title">Basic Information</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Name:</span>
                <span class="detail-value">{{ selectedInstructor.fullName }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Email:</span>
                <span class="detail-value">{{ selectedInstructor.email }}</span>
              </div>
              <div v-if="selectedInstructor.title" class="detail-item">
                <span class="detail-label">Title:</span>
                <span class="detail-value">{{ selectedInstructor.title }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Status:</span>
                <span
                  :class="[
                    'status-badge',
                    selectedInstructor.active ? 'status-active' : 'status-inactive'
                  ]"
                >
                  {{ selectedInstructor.active ? 'Active' : 'Inactive' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Role:</span>
                <span
                  :class="[
                    'role-badge',
                    selectedInstructor.isAdmin ? 'role-admin' : 'role-instructor'
                  ]"
                >
                  {{ selectedInstructor.isAdmin ? 'Administrator' : 'Instructor' }}
                </span>
              </div>
            </div>
          </section>

          <!-- Teaching Assignments -->
          <section class="detail-section">
            <h3 class="section-title">Teaching Assignments</h3>
            <div v-if="coursesLoading" class="loading-state">
              <BaseSpinner size="sm" />
              <p>Loading courses...</p>
            </div>
            <div v-else-if="courses.length === 0" class="empty-state">
              <p>No courses assigned to this instructor.</p>
            </div>
            <div v-else class="courses-list">
              <div
                v-for="course in courses"
                :key="course.id"
                class="course-item"
              >
                <div class="course-header">
                  <span class="course-code">{{ course.courseCode }}</span>
                  <span class="course-name">{{ course.courseName }}</span>
                </div>
                <p class="course-description">{{ course.courseDescription }}</p>
                <div class="course-meta">
                  <span class="meta-item">
                    Semester: {{ formatSemester(course.semester) }}
                  </span>
                </div>
              </div>
            </div>
          </section>

          <!-- Account Information -->
          <section class="detail-section">
            <h3 class="section-title">Account Information</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">User ID:</span>
                <span class="detail-value">{{ selectedInstructor.id }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Program User ID:</span>
                <span class="detail-value">{{ selectedInstructor.programUserId }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Created:</span>
                <span class="detail-value">{{ formatDate(selectedInstructor.createdAt) }}</span>
              </div>
              <div v-if="selectedInstructor.updatedAt" class="detail-item">
                <span class="detail-label">Last Updated:</span>
                <span class="detail-value">{{ formatDate(selectedInstructor.updatedAt) }}</span>
              </div>
            </div>
          </section>
        </div>
      </BaseCard>
    </transition>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api'
import BaseCard from '../components/ui/BaseCard.vue'
import BaseSelect from '../components/ui/BaseSelect.vue'
import BaseButton from '../components/ui/BaseButton.vue'
import BaseSpinner from '../components/ui/BaseSpinner.vue'
import type { SelectOption } from '@/components/ui'

interface Program {
  id: number
  name: string
  institution: string
  active?: boolean
}

interface ProgramUser {
  id: number
  userId: number
  adminStatus?: boolean
  active?: boolean
  createdAt?: string
  updatedAt?: string
}

interface Instructor {
  id: number
  programUserId: number
  email: string
  firstName: string
  lastName: string
  title?: string
  fullName: string
  active: boolean
  isAdmin: boolean
  createdAt?: string
  updatedAt?: string
}

interface Course {
  id: number
  courseCode: string
  courseName: string
  courseDescription: string
  semester: {
    id: number
    season: string
    semesterYear: number
  }
  active?: boolean
}

// Reactive state
const selectedProgramId = ref<number | ''>('')
const selectedInstructorId = ref<number | ''>('')

const programs = ref<Program[]>([])
const instructors = ref<Instructor[]>([])
const courses = ref<Course[]>([])

// Loading / error
const programsLoading = ref(false)
const instructorsLoading = ref(false)
const coursesLoading = ref(false)
const loading = ref(false)
const instructorError = ref<string | null>(null)

// Computed options
const programOptions = computed<SelectOption[]>(() =>
  programs.value
    .filter(p => p.active !== false)
    .map(program => ({
      label: `${program.name} - ${program.institution}`,
      value: program.id
    }))
)

const instructorOptions = computed<SelectOption[]>(() =>
  instructors.value.map(instructor => ({
    label: instructor.fullName,
    value: instructor.programUserId
  }))
)

const selectedInstructor = computed<Instructor | null>(() => {
  if (!selectedInstructorId.value) return null
  return (
    instructors.value.find(
      i => i.programUserId === Number(selectedInstructorId.value)
    ) ?? null
  )
})

// ----- API calls -----

// GET /api/program (paged)
const loadPrograms = async () => {
  programsLoading.value = true
  try {
    const res = await api.get('/program', {
      params: { page: 0, size: 100 }
    })
    const paged = res.data
    programs.value = paged.content ?? paged ?? []
  } catch (error) {
    console.error('Error loading programs:', error)
  } finally {
    programsLoading.value = false
  }
}

// GET /api/program/{programId}/users -> ApiResponse<List<ProgramUser>>
const loadInstructors = async () => {
  if (!selectedProgramId.value) return

  instructorsLoading.value = true
  instructorError.value = null

  try {
    const res = await api.get(`/program/${selectedProgramId.value}/users`)
    const apiResponse = res.data
    const programUsers = (apiResponse.data ?? []) as ProgramUser[]

    const instructorPromises = programUsers.map(async (pu: ProgramUser) => {
      try {
        const userRes = await api.get(`/users/${pu.userId}`)
        const userApi = userRes.data
        const user = userApi.data as {
          id: number
          email: string
          firstName: string
          lastName: string
          title?: string
          active?: boolean
          createdAt?: string
          updatedAt?: string
        }

        return {
          id: user.id,
          programUserId: pu.id,
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          title: user.title,
          fullName: user.title
            ? `${user.title} ${user.firstName} ${user.lastName}`
            : `${user.firstName} ${user.lastName}`,
          active: (user.active ?? true) && (pu.active ?? true),
          isAdmin: pu.adminStatus === true,
          createdAt: pu.createdAt ?? user.createdAt,
          updatedAt: pu.updatedAt ?? user.updatedAt
        } as Instructor
      } catch (error) {
        console.error(`Error loading user ${pu.userId}:`, error)
        return null
      }
    })

    const results = await Promise.all(instructorPromises)
    instructors.value = results.filter((i): i is Instructor => i !== null)
  } catch (error) {
    console.error('Error loading instructors:', error)
    instructorError.value = 'Failed to load instructors. Please try again.'
  } finally {
    instructorsLoading.value = false
  }
}

// GET /api/courses/instructor?programUserId=... -> ApiResponse<List<Course>>
const loadCourses = async () => {
  if (!selectedInstructor.value) return

  coursesLoading.value = true
  courses.value = []

  try {
    const res = await api.get('/courses/instructor', {
      params: {
        programUserId: selectedInstructor.value.programUserId
      }
    })

    const apiResp = res.data
    const allCourses = (apiResp.data ?? []) as Course[]
    courses.value = allCourses.filter(c => c.active !== false)
  } catch (error) {
    console.error('Error loading courses:', error)
  } finally {
    coursesLoading.value = false
  }
}

// ----- Handlers -----

const handleProgramChange = () => {
  selectedInstructorId.value = ''
  instructors.value = []
  courses.value = []
  if (selectedProgramId.value) {
    loadInstructors()
  }
}

const handleInstructorChange = () => {
  if (selectedInstructorId.value) {
    loadCourses()
  } else {
    courses.value = []
  }
}

// ----- Helpers -----

const formatDate = (dateString?: string): string => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatSemester = (semester: Course['semester']): string => {
  return `${semester.season} ${semester.semesterYear}`
}

// Lifecycle
onMounted(() => {
  loadPrograms()
})
</script>

<style scoped>
.instructor-selector {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.instructor-card,
.details-card {
  margin-top: 0;
}

.loading-state,
.empty-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  gap: 1rem;
}

.loading-state p,
.empty-state p {
  color: var(--color-text-secondary);
  margin: 0;
}

.error-state .error-message {
  color: var(--color-error);
  margin: 0 0 1rem 0;
}

/* Instructor Details */
.instructor-details {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--color-border-dark);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

/* Badges */
.status-badge,
.role-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-active {
  background-color: #dcfce7;
  color: #166534;
}

.status-inactive {
  background-color: #fee2e2;
  color: #991b1b;
}

.role-admin {
  background-color: #dbeafe;
  color: #1e40af;
}

.role-instructor {
  background-color: #e0e7ff;
  color: #4338ca;
}

/* Courses List */
.courses-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.course-item {
  padding: 1rem;
  background-color: var(--color-bg-secondary);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  transition: var(--transition-base);
}

.course-item:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

.course-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.course-code {
  font-weight: 700;
  color: var(--color-primary);
  font-size: var(--font-size-base);
}

.course-name {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: var(--font-size-base);
}

.course-description {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  margin: 0.5rem 0;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  gap: 1rem;
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid var(--color-border-light);
}

.meta-item {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .instructor-selector {
    padding: 0.5rem;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .course-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
}
</style>
