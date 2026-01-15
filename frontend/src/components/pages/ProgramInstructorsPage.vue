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
              <span v-if="instructor.role === 'ADMIN'" class="role-badge">Admin</span>
            </p>
          </div>
        </div>
      </BaseCard>
    </div>

    <div v-else class="empty-state">
      <p>No instructors found in this program.</p>
    </div>

    <!-- Instructor Details Modal -->
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
          <div class="section-header">
            <h3>Personal Information</h3>
            <button
              v-if="!isEditingInfo"
              class="btn-secondary btn-small"
              @click="startEditingInfo"
            >
              Edit Info
            </button>
          </div>

          <div v-if="!isEditingInfo" class="detail-grid">
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

          <!-- Edit Form -->
          <div v-else class="edit-form">
            <div class="form-row">
              <div class="form-group">
                <label>First Name</label>
                <BaseInput
                  v-model="editForm.firstName"
                  placeholder="First Name"
                  :error="editErrors.firstName"
                />
              </div>
              <div class="form-group">
                <label>Last Name</label>
                <BaseInput
                  v-model="editForm.lastName"
                  placeholder="Last Name"
                  :error="editErrors.lastName"
                />
              </div>
            </div>

            <div class="form-group">
              <label>Email</label>
              <BaseInput
                v-model="editForm.email"
                type="email"
                placeholder="Email"
                :error="editErrors.email"
              />
            </div>

            <div class="form-group">
              <label>Role</label>
              <BaseSelect
                v-model="editForm.role"
                :options="roleOptions"
              />
            </div>

            <div class="form-actions">
              <button
                class="btn-secondary"
                @click="cancelEditingInfo"
                :disabled="saving"
              >
                Cancel
              </button>
              <button
                class="btn-primary"
                @click="saveInstructorInfo"
                :disabled="saving"
              >
                {{ saving ? 'Saving...' : 'Save Changes' }}
              </button>
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
import { ref, watch, onMounted, reactive } from "vue";
import api from "@/api";
import BaseCard from "@/components/ui/BaseCard.vue";
import BaseModal from "@/components/ui/BaseModal.vue";
import BaseInput from "@/components/ui/BaseInput.vue";
import BaseSelect from "@/components/ui/BaseSelect.vue";
import { useToast } from "@/composables/use-toast";

const toast = useToast();

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

interface EditForm {
  firstName: string;
  lastName: string;
  email: string;
  role: "ADMIN" | "INSTRUCTOR";
}

interface EditErrors {
  firstName?: string;
  lastName?: string;
  email?: string;
}

const props = defineProps<{
  programId: number | null
}>();

const instructors = ref<Instructor[]>([]);
const selectedInstructor = ref<Instructor | null>(null);
const showModal = ref(false);
const loading = ref(false);
const error = ref<string | null>(null);

// Editing state
const isEditingInfo = ref(false);
const saving = ref(false);
const editForm = reactive<EditForm>({
  firstName: "",
  lastName: "",
  email: "",
  role: "INSTRUCTOR"
});
const editErrors = reactive<EditErrors>({});

const roleOptions = [
  { value: "INSTRUCTOR", label: "Instructor" },
  { value: "ADMIN", label: "Admin" }
];

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

/* -----------------------------
 * Editing functions
 * ----------------------------- */
function startEditingInfo() {
  if (!selectedInstructor.value) return;

  editForm.firstName = selectedInstructor.value.firstName;
  editForm.lastName = selectedInstructor.value.lastName;
  editForm.email = selectedInstructor.value.email;
  editForm.role = selectedInstructor.value.role;

  Object.keys(editErrors).forEach(key => delete editErrors[key as keyof EditErrors]);
  isEditingInfo.value = true;
}

function cancelEditingInfo() {
  isEditingInfo.value = false;
  Object.keys(editErrors).forEach(key => delete editErrors[key as keyof EditErrors]);
}

function validateEditForm(): boolean {
  Object.keys(editErrors).forEach(key => delete editErrors[key as keyof EditErrors]);

  let isValid = true;

  if (!editForm.firstName.trim()) {
    editErrors.firstName = "First name is required";
    isValid = false;
  }

  if (!editForm.lastName.trim()) {
    editErrors.lastName = "Last name is required";
    isValid = false;
  }

  if (!editForm.email.trim()) {
    editErrors.email = "Email is required";
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.email)) {
    editErrors.email = "Invalid email format";
    isValid = false;
  }

  return isValid;
}

async function saveInstructorInfo() {
  if (!selectedInstructor.value || !validateEditForm()) return;

  saving.value = true;

  try {
    // Update user info
    await api.put(`/users/${selectedInstructor.value.userId}`, {
      firstName: editForm.firstName,
      lastName: editForm.lastName,
      email: editForm.email
    });

    // Update program user role (admin status)
    const adminStatus = editForm.role === "ADMIN";
    await api.put(`/program/${props.programId}/users/${selectedInstructor.value.programUserId}`, {
      adminStatus
    });

    // Update local state
    selectedInstructor.value.firstName = editForm.firstName;
    selectedInstructor.value.lastName = editForm.lastName;
    selectedInstructor.value.email = editForm.email;
    selectedInstructor.value.role = editForm.role;

    // Update in instructors list
    const instructorIndex = instructors.value.findIndex(
      i => i.programUserId === selectedInstructor.value?.programUserId
    );
    if (instructorIndex !== -1) {
      instructors.value[instructorIndex] = { ...selectedInstructor.value };
    }

    toast.success("Instructor updated successfully");
    isEditingInfo.value = false;

  } catch (err: any) {
    console.error("Error saving instructor:", err);
    toast.error(
      err.response?.data?.message || "Failed to update instructor"
    );
  } finally {
    saving.value = false;
  }
}

watch(() => props.programId, () => {
  loadProgramInstructors();
});

onMounted(() => {
  if (props.programId) loadProgramInstructors();
});

function closeModal() {
  showModal.value = false;
  selectedInstructor.value = null;
  isEditingInfo.value = false;
}

function showInstructorDetails(i: Instructor) {
  selectedInstructor.value = i;
  showModal.value = true;
}
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
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.role-badge {
  display: inline-block;
  padding: 0.125rem 0.5rem;
  background: var(--color-primary);
  color: white;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  border-bottom: 2px solid var(--color-border-light);
  padding-bottom: 0.5rem;
}

.section-header h3 {
  margin: 0;
  border: none;
  padding: 0;
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

/* Edit Form */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border-light);
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

.btn-primary:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
  border: 1px solid var(--color-border-dark);
  padding: 0.625rem 1.5rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover:not(:disabled) {
  background: var(--color-bg-secondary);
  border-color: var(--color-border-dark);
}

.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-small {
  padding: 0.375rem 1rem;
  font-size: 0.8125rem;
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

  .form-row {
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

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
