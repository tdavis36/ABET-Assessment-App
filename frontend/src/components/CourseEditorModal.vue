<script setup lang="ts">
import { ref, watch } from "vue";
import api from "@/api";
import BaseModal from "@/components/ui/BaseModal.vue";
import BaseButton from "@/components/ui/BaseButton.vue";

interface Course {
  id: number;
  courseCode: string;
  courseName: string;
  courseDescription?: string;
  isActive: boolean;
}

const props = defineProps<{
  course: Course | null;  // null when closed
}>();

const emit = defineEmits(["close", "saved"]);

/* -----------------------------------------------
 * Form state - rehydrated when a new course is sent
 * ----------------------------------------------- */
const courseCode = ref("");
const courseName = ref("");
const courseDescription = ref("");
const isActive = ref(true);

const saving = ref(false);
const error = ref<string | null>(null);

/* -----------------------------------------------
 * Populate modal when a course is selected
 * ----------------------------------------------- */
watch(
  () => props.course,
  (c) => {
    if (!c) return;

    courseCode.value = c.courseCode;
    courseName.value = c.courseName;
    courseDescription.value = c.courseDescription ?? "";
    isActive.value = c.isActive;
    error.value = null;
  },
  { immediate: true }
);

/* -----------------------------------------------
 * Save Changes
 * ----------------------------------------------- */
async function save() {
  if (!props.course) return;

  saving.value = true;
  error.value = null;

  try {
    await api.put(`/course/${props.course.id}`, {
      courseCode: courseCode.value,
      courseName: courseName.value,
      courseDescription: courseDescription.value,
      isActive: isActive.value
    });

    emit("saved");  // tell parent to refresh
    emit("close");
  } catch (err: any) {
    console.error("Failed to save course:", err);
    error.value =
      err?.response?.data?.message || "Failed to save course. Please try again.";
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <BaseModal
    :is-open="!!course"
    @close="emit('close')"
    @update:is-open="value => { if (!value) emit('close') }"
  >

    <template #header>
      <h2>Edit Course</h2>
    </template>

    <template #body>
      <form class="form" @submit.prevent="save">

        <!-- Error Message -->
        <p v-if="error" class="error-message">{{ error }}</p>

        <!-- Course Code -->
        <label class="label">Course Code</label>
        <input
          class="input"
          v-model="courseCode"
          required
          placeholder="e.g. CMSC131"
        />

        <!-- Course Name -->
        <label class="label">Course Name</label>
        <input
          class="input"
          v-model="courseName"
          required
          placeholder="e.g. Object-Oriented Programming I"
        />

        <!-- Description -->
        <label class="label">Course Description</label>
        <textarea
          class="textarea"
          v-model="courseDescription"
          rows="4"
          placeholder="Optional description"
        />

        <!-- Active toggle -->
        <label class="checkbox">
          <input type="checkbox" v-model="isActive" />
          Active
        </label>
      </form>
    </template>

    <template #footer>
      <BaseButton variant="secondary" @click="emit('close')" :disabled="saving">
        Cancel
      </BaseButton>

      <BaseButton variant="primary" @click="save" :disabled="saving">
        <span v-if="saving">Saving…</span>
        <span v-else>Save Changes</span>
      </BaseButton>
    </template>

  </BaseModal>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.label {
  font-weight: 600;
  font-size: 0.9rem;
}

.input,
.textarea {
  padding: 0.6rem 0.75rem;
  border-radius: 6px;
  border: 1px solid var(--color-border-dark);
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  width: 100%;
  font-size: 0.95rem;
}

.textarea {
  resize: vertical;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.error-message {
  color: var(--color-error);
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}
</style>
