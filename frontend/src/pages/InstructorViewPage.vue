<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import api from "@/api";
import { useUserStore } from "@/stores/user-store.ts";
import BaseCard from "../components/ui/BaseCard.vue";
import BaseModal from "../components/ui/BaseModal.vue";

const userStore = useUserStore();

/* -----------------------------
 * TypeScript interfaces
 * ----------------------------- */

interface Program {
  id: number;
  name: string;
  institution: string;
}

interface ProgramUser {
  id: number;
  userId: number;
  role?: string;
}

interface Course {
  id: number;
  courseCode?: string;
  course_code?: string;
  courseName?: string;
  course_name?: string;
  measuresCompleted?: number;
  measuresTotal?: number;
}

interface Instructor {
  programUserId: number;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  courseCount: number;
  courses: Course[];
}

/* -----------------------------
 * Reactive state
 * ----------------------------- */

const programs = ref<Program[]>([]);
const selectedProgramId = ref<number | null>(null);

const instructors = ref<Instructor[]>([]);
const selectedInstructor = ref<Instructor | null>(null);

const showModal = ref(false);
const loading = ref(false);
const loadingPrograms = ref(false);
const error = ref<string | null>(null);

/* -----------------------------
 * Computed values
 * ----------------------------- */

const selectedProgramName = computed<string>(() => {
  const program = programs.value.find(
    (p) => p.id === selectedProgramId.value
  );
  return program ? program.name : "";
});

/* -----------------------------
 * Load user's programs
 * ----------------------------- */

async function loadUserPrograms(): Promise<void> {
  loadingPrograms.value = true;
  try {
    const res = await api.get("/program");
    const data = (res.data?.data ?? []) as Program[];
    programs.value = data;

    // Auto-select first program if available and none selected
    if (programs.value.length > 0 && !selectedProgramId.value) {
      selectedProgramId.value = programs.value[0].id;
    }
  } catch (err) {
    console.error("Error loading programs:", err);
    error.value = "Failed to load programs";
  } finally {
    loadingPrograms.value = false;
  }
}

/* -----------------------------
 * Load all instructors in program
 * ----------------------------- */

async function loadProgramInstructors(): Promise<void> {
  if (!selectedProgramId.value) return;

  loading.value = true;
  error.value = null;

  try {
    // Get all program users
    const res = await api.get(
      `/program/${selectedProgramId.value}/users`
    );
    const programUsers = (res.data?.data ?? []) as ProgramUser[];

    // Fetch detailed user info for each program user
    const loaded = await Promise.all(
      programUsers.map(
        async (pu: ProgramUser): Promise<Instructor | null> => {
          try {
            // Get user details
            const userRes = await api.get(`/users/${pu.userId}`);
            const user = userRes.data?.data as {
              firstName: string;
              lastName: string;
              email: string;
            };

            // Get courses taught by this instructor
            const coursesRes = await api.get(
              "/courses/instructor",
              {
                params: { programUserId: pu.id },
              }
            );
            const courses = (coursesRes.data?.data ??
              []) as Course[];

            return {
              programUserId: pu.id,
              userId: pu.userId,
              firstName: user.firstName,
              lastName: user.lastName,
              email: user.email,
              role: pu.role ?? "Instructor",
              courseCount: courses.length,
              courses,
            };
          } catch (err) {
            console.error(
              `Error loading user ${pu.userId}:`,
              err
            );
            return null;
          }
        }
      )
    );

    // Filter out any null entries from errors
    instructors.value = loaded.filter(
      (i): i is Instructor => i !== null
    );
  } catch (err) {
    console.error("Error loading program instructors:", err);
    error.value = "Failed to load instructors";
  } finally {
    loading.value = false;
  }
}

/* -----------------------------
 * Show instructor details
 * ----------------------------- */

async function showInstructorDetails(
  instructor: Instructor
): Promise<void> {
  selectedInstructor.value = instructor;

  // Load course completeness data if not already loaded
  if (
    selectedInstructor.value &&
    selectedInstructor.value.courses &&
    selectedInstructor.value.courses.length > 0
  ) {
    try {
      const coursesWithCompleteness = await Promise.all(
        selectedInstructor.value.courses.map(
          async (course: Course): Promise<Course> => {
            try {
              const compRes = await api.get(
                `/courses/${course.id}/completeness`
              );
              const comp = compRes.data?.data as {
                completedMeasures: number;
                totalMeasures: number;
              };

              return {
                ...course,
                measuresCompleted: comp.completedMeasures,
                measuresTotal: comp.totalMeasures,
              };
            } catch (err) {
              console.error(
                `Error loading completeness for course ${course.id}:`,
                err
              );
              return {
                ...course,
                measuresCompleted: 0,
                measuresTotal: 0,
              };
            }
          }
        )
      );

      if (selectedInstructor.value) {
        selectedInstructor.value = {
          ...selectedInstructor.value,
          courses: coursesWithCompleteness,
        };
      }
    } catch (err) {
      console.error("Error loading course details:", err);
    }
  }

  showModal.value = true;
}

/* -----------------------------
 * Close modal
 * ----------------------------- */

function closeModal(): void {
  showModal.value = false;
  selectedInstructor.value = null;
}

/* -----------------------------
 * Watchers
 * ----------------------------- */

watch(selectedProgramId, async (newProgramId) => {
  if (newProgramId) {
    await loadProgramInstructors();
  } else {
    instructors.value = [];
  }
});

/* -----------------------------
 * Lifecycle
 * ----------------------------- */

onMounted(async () => {
  try {
    await userStore.loadFromStorage();
    await loadUserPrograms();
    // loadProgramInstructors will be called by the watcher
  } catch (err) {
    console.error("Error loading instructor view:", err);
    error.value = "Failed to initialize page";
  }
});
</script>

<template>
  <section class="instructors-page">
    <div class="page-header">
      <div class="header-content">
        <h2>Program Instructors</h2>
        <p
          class="subtitle"
          v-if="
            instructors.length > 0 && selectedProgramName
          "
        >
          {{ instructors.length }} instructor{{
            instructors.length !== 1 ? "s" : ""
          }}
          in {{ selectedProgramName }}
        </p>
      </div>

      <!-- Program Selector -->
      <div class="program-selector">
        <label
          for="program-select"
          class="selector-label"
        >Select Program:</label
        >
        <select
          id="program-select"
          v-model.number="selectedProgramId"
          class="program-select"
          :disabled="
            loadingPrograms || programs.length === 0
          "
        >
          <option :value="null" disabled>
            Choose a program...
          </option>
          <option
            v-for="program in programs"
            :key="program.id"
            :value="program.id"
          >
            {{ program.name }} - {{ program.institution }}
          </option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <div
      v-if="loadingPrograms"
      class="loading-state"
    >
      <p>Loading programs...</p>
    </div>

    <div
      v-else-if="
        programs.length === 0 && !loadingPrograms
      "
      class="empty-state"
    >
      <p>No programs found. Please contact an administrator.</p>
    </div>

    <div
      v-else-if="!selectedProgramId"
      class="empty-state"
    >
      <p>Please select a program to view instructors.</p>
    </div>

    <!-- Loading Instructors State -->
    <div
      v-else-if="loading"
      class="loading-state"
    >
      <p>Loading instructors...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="error-state"
    >
      <p>{{ error }}</p>
    </div>

    <!-- Instructors Grid -->
    <div
      v-else-if="instructors.length > 0"
      class="instructors-grid"
    >
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
            {{
              instructor.firstName?.charAt(0)
            }}{{ instructor.lastName?.charAt(0) }}
          </div>

          <div class="instructor-info">
            <h3 class="instructor-name">
              {{ instructor.firstName }}
              {{ instructor.lastName }}
            </h3>
            <p class="instructor-email">
              {{ instructor.email }}
            </p>
            <p class="instructor-meta">
              {{ instructor.courseCount }} course{{
                instructor.courseCount !== 1 ? "s" : ""
              }}
            </p>
          </div>
        </div>
      </BaseCard>
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <p>No instructors found in this program.</p>
    </div>

    <!-- Instructor Details Modal -->
    <BaseModal
      v-model:isOpen="showModal"
      :title="
        selectedInstructor
          ? `${selectedInstructor.firstName} ${selectedInstructor.lastName}`
          : 'Instructor Details'
      "
      size="lg"
      @close="closeModal"
    >
      <div
        v-if="selectedInstructor"
        class="instructor-details"
      >
        <!-- Personal Information -->
        <section class="detail-section">
          <h3>Personal Information</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Name:</span>
              <span class="detail-value">
                {{ selectedInstructor.firstName }}
                {{ selectedInstructor.lastName }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Email:</span>
              <span class="detail-value">
                <a
                  :href="`mailto:${selectedInstructor.email}`"
                >
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
          <h3>
            Courses ({{
              selectedInstructor.courses?.length || 0
            }})
          </h3>

          <div
            v-if="
              selectedInstructor.courses &&
              selectedInstructor.courses.length > 0
            "
          >
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
                <td>
                  {{
                    course.courseCode ||
                    course.course_code
                  }}
                </td>
                <td>
                  {{
                    course.courseName ||
                    course.course_name ||
                    "—"
                  }}
                </td>
                <td>
                    <span
                      v-if="
                        course.measuresCompleted !==
                        undefined
                      "
                    >
                      {{
                        course.measuresCompleted
                      }}/{{ course.measuresTotal }}
                      <span
                        class="progress-percent"
                      >
                        ({{
                          course.measuresTotal &&
                          course.measuresTotal > 0
                            ? Math.round(
                              ((course.measuresCompleted ||
                                  0) /
                                course.measuresTotal) *
                              100
                            )
                            : 0
                        }}%)
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
        <button
          class="btn-primary"
          @click="closeModal"
        >
          Close
        </button>
      </template>
    </BaseModal>
  </section>
</template>

<style scoped>
.instructors-page {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
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
  font-size: 2rem;
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1rem;
}

/* Program Selector */
.program-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--color-bg-secondary);
  border-radius: 0.5rem;
  border: 1px solid var(--color-border-light);
}

.selector-label {
  font-weight: 500;
  color: var(--color-text-primary);
  font-size: 0.875rem;
  white-space: nowrap;
}

.program-select {
  flex: 1;
  max-width: 500px;
  padding: 0.625rem 0.875rem;
  font-size: 0.875rem;
  border: 1px solid var(--color-border-dark);
  border-radius: 0.375rem;
  background: var(--color-bg-primary);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.2s;
}

.program-select:hover:not(:disabled) {
  border-color: var(--color-primary);
}

.program-select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.program-select:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  grid-template-columns: repeat(
    auto-fill,
    minmax(320px, 1fr)
  );
  gap: 1.5rem;
}

.instructor-card {
  cursor: pointer;
  transition: all 0.2s ease;
}

.instructor-card-content {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.instructor-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(
    135deg,
    var(--color-primary),
    var(--color-primary-dark)
  );
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
  border-bottom: 2px solid
  var(--color-border-light);
  padding-bottom: 0.5rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(
    auto-fit,
    minmax(250px, 1fr)
  );
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
  border-bottom: 1px solid
  var(--color-border-light);
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

/* Responsive Design */
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
