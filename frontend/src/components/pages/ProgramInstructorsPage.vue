<template>
  <section class="instructors-page">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h2>Instructors</h2>
        <p
          class="subtitle"
          v-if="instructors.length > 0"
        >
          {{ instructors.length }} instructor{{ instructors.length !== 1 ? 's' : '' }}
          in selected program
        </p>
      </div>
    </div>

    <!-- Loading / error / grid -->
    <div v-if="loading" class="loading-state">
      <p>Loading instructors...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="instructors.length > 0" class="instructors-grid">
      <BaseCard
        v-for="instructor in instructors"
        :key="instructor.programUserId"
        variant="elevated"
        :hoverable="true"
        class="instructor-card"
        @click="showInstructorDetails(instructor)"
      >
        <div class="instructor-card-content">
          <div class="instructor-avatar">
            {{ instructor.firstName?.charAt(0) }}{{ instructor.lastName?.charAt(0) }}
          </div>

          <div class="instructor-info">
            <h3 class="instructor-name">
              {{ instructor.firstName }} {{ instructor.lastName }}
            </h3>
            <p class="instructor-email">
              {{ instructor.email }}
            </p>
            <p class="instructor-meta">
              {{ instructor.courseCount }} course{{ instructor.courseCount !== 1 ? 's' : '' }}
            </p>
          </div>
        </div>
      </BaseCard>
    </div>

    <div v-else class="empty-state">
      <p>No instructors found in this program.</p>
    </div>

    <!-- Instructor Modal -->
    <BaseModal
      v-model:isOpen="showModal"
      :title="selectedInstructor
        ? `${selectedInstructor.firstName} ${selectedInstructor.lastName}`
        : 'Instructor Details'"
      size="lg"
      @close="closeModal"
    >
      <div v-if="selectedInstructor" class="instructor-details">
        <!-- Personal Info -->
        <section class="detail-section">
          <h3>Personal Information</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Name:</span>
              <span class="detail-value">
                {{ selectedInstructor.firstName }} {{ selectedInstructor.lastName }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Email:</span>
              <span class="detail-value">
                <a :href="`mailto:${selectedInstructor.email}`">
                  {{ selectedInstructor.email }}
                </a>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Role:</span>
              <span class="detail-value">
                {{ selectedInstructor.role }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">User ID:</span>
              <span class="detail-value">
                {{ selectedInstructor.userId }}
              </span>
            </div>
          </div>
        </section>

        <!-- Courses -->
        <section class="detail-section">
          <h3>Courses ({{ selectedInstructor.courses?.length || 0 }})</h3>

          <div v-if="selectedInstructor.courses?.length > 0">
            <table class="courses-table">
              <thead>
              <tr>
                <th>Course Code</th>
                <th>Course Name</th>
                <th>Measures Progress</th>
              </tr>
              </thead>
              <tbody>
              <tr
                v-for="course in selectedInstructor.courses"
                :key="course.id"
              >
                <td>{{ course.courseCode || course.course_code }}</td>
                <td>{{ course.courseName || course.course_name || '—' }}</td>
                <td>
                  <span v-if="course.measuresCompleted !== undefined">
                    {{ course.measuresCompleted }}/{{ course.measuresTotal }}
                    <span class="progress-percent">
                      ({{ course.measuresTotal && course.measuresTotal > 0
                      ? Math.round(
                        ((course.measuresCompleted || 0) / course.measuresTotal) * 100
                      )
                      : 0 }}%)
                    </span>
                  </span>
                  <span v-else>—</span>
                </td>
              </tr>
              </tbody>
            </table>
          </div>

          <p v-else class="no-courses">
            No courses assigned to this instructor.
          </p>
        </section>
      </div>

      <template #footer>
        <button class="btn-primary" @click="closeModal">
          Close
        </button>
      </template>
    </BaseModal>
  </section>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import api from "@/api";
import BaseCard from "@/components/ui/BaseCard.vue";
import BaseModal from "@/components/ui/BaseModal.vue";

interface Course {
  id: number;
  courseCode?: string;
  courseName?: string;

  // API fallback naming (snake_case)
  course_code?: string;
  course_name?: string;

  // Measures progress fields
  measuresCompleted?: number;
  measuresTotal?: number;
}

interface Instructor {
  programUserId: number;
  userId: number;

  firstName: string;
  lastName: string;
  email: string;

  role: "ADMIN" | "INSTRUCTOR";

  courseCount: number;
  courses: Course[];
}

interface ProgramUser {
  id: number;
  userId: number;
  adminStatus: boolean;
}

const props = defineProps<{
  programId: number | null
}>();

const instructors = ref<Instructor[]>([]);
const selectedInstructor = ref<Instructor | null>(null);
const showModal = ref(false);
const loading = ref(false);
const error = ref<string | null>(null);

/* -----------------------------
 * Load instructors for program
 * ----------------------------- */
async function loadProgramInstructors() {
  if (!props.programId) return;

  loading.value = true;
  error.value = null;

  try {
    const res = await api.get(`/program/${props.programId}/users`);
    const programUsers = res.data.data ?? [];

    const loaded = await Promise.all(
      programUsers.map(async (pu: ProgramUser) => {
        try {
          const userRes = await api.get(`/users/${pu.userId}`);
          const user = userRes.data.data;

          const coursesRes = await api.get(`/courses/instructor`, {
            params: { programUserId: pu.id }
          });

          return {
            programUserId: pu.id,
            userId: pu.userId,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            role: pu.adminStatus ? "ADMIN" : "INSTRUCTOR",
            courseCount: (coursesRes.data.data ?? []).length,
            courses: coursesRes.data.data ?? []
          };
        } catch {
          return null;
        }
      })
    );

    instructors.value = loaded.filter((x): x is Instructor => x !== null);

  } catch (err) {
    console.error("Error loading instructors:", err);
    error.value = "Failed to load instructors";
  } finally {
    loading.value = false;
  }
}

watch(() => props.programId, () => {
  loadProgramInstructors();
});

onMounted(() => {
  if (props.programId) loadProgramInstructors();
});

function closeModal() { showModal.value = false; selectedInstructor.value = null; }
function showInstructorDetails(i: Instructor) { selectedInstructor.value = i; showModal.value = true; }
</script>

<style scoped>
.instructors-page {
  width: 100%;
  padding: 0.4rem 0.75rem;
}

.page-header {
  margin-bottom: 2rem;
}

.header-content {
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1.5rem;
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1rem;
}

/* Loading, Error, Empty States */
.loading-state,
.error-state,
.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-text-secondary);
}

.error-state {
  color: var(--color-error);
}

/* Instructors Grid */
.instructors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.25rem;
}

.instructor-card {
  cursor: pointer;
  transition: all 0.2s ease;
}

.instructor-card-content {
  display: flex;
  align-items: center;
  text-align: left;
  gap: 1.25rem;
}

.instructor-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--color-primary);

  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 600;
  flex-shrink: 0;
}

.instructor-info {
  flex: 1;
  min-width: 0;
}

.instructor-name {
  margin: 0 0 0.25rem 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.instructor-email {
  margin: 0 0 0.5rem 0;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.instructor-meta {
  margin: 0;
  font-size: 0.875rem;
  color: var(--color-text-tertiary);
}

/* Instructor Details */
.instructor-details {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detail-section h3 {
  margin: 0 0 1rem 0;
  font-size: 1.125rem;
  color: var(--color-text-primary);
  border-bottom: 2px solid var(--color-border-light);
  padding-bottom: 0.5rem;
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
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.detail-value {
  font-size: 1rem;
  color: var(--color-text-primary);
}

.detail-value a {
  color: var(--color-primary);
  text-decoration: none;
}

.detail-value a:hover {
  text-decoration: underline;
}

/* Courses Table */
.courses-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 0.5rem;
}

.courses-table th,
.courses-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border-light);
}

.courses-table th {
  background: var(--color-bg-tertiary);
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.courses-table tbody tr:hover {
  background: var(--color-bg-secondary);
}

.progress-percent {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.no-courses {
  color: var(--color-text-secondary);
  font-style: italic;
  margin: 1rem 0;
}

/* Button Styles */
.btn-primary {
  background: var(--color-primary);
  color: white;
  border: none;
  padding: 0.625rem 1.5rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: var(--color-primary-dark);
}

/* Responsive */
@media (max-width: 768px) {
  .instructors-page {
    padding: 1rem;
  }

  .program-selector {
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
  }

  .program-select {
    max-width: 100%;
  }

  .instructors-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .courses-table {
    font-size: 0.875rem;
    display: block;
    overflow-x: auto;
  }

  .courses-table th,
  .courses-table td {
    padding: 0.5rem;
  }
}
</style>
