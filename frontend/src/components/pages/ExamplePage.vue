<template>
  <div class="example-page">
    <h1>UI Components Example</h1>

    <!-- Buttons -->
    <section class="example-section">
      <h2>Buttons</h2>
      <div class="button-grid">
        <BaseButton variant="primary" @click="showSuccessToast">Primary Button</BaseButton>
        <BaseButton variant="secondary" @click="showErrorToast">Secondary Button</BaseButton>
        <BaseButton variant="success">Success Button</BaseButton>
        <BaseButton variant="danger">Danger Button</BaseButton>
        <BaseButton variant="ghost">Ghost Button</BaseButton>
        <BaseButton variant="primary" :loading="true">Loading...</BaseButton>
        <BaseButton variant="primary" :disabled="true">Disabled</BaseButton>
        <BaseButton variant="primary" size="sm">Small</BaseButton>
        <BaseButton variant="primary" size="lg">Large</BaseButton>
      </div>
    </section>

    <!-- Inputs -->
    <section class="example-section">
      <h2>Inputs</h2>
      <div class="input-grid">
        <BaseInput
          v-model="formData.name"
          label="Name"
          placeholder="Enter your name"
          required
        />
        <BaseInput
          v-model="formData.email"
          type="email"
          label="Email"
          placeholder="Enter your email"
          hint="We'll never share your email"
        />
        <BaseInput
          v-model="formData.password"
          type="password"
          label="Password"
          :error="passwordError"
        />
        <BaseInput label="Disabled Input" :disabled="true" modelValue="Cannot edit" />
      </div>
    </section>

    <!-- Select -->
    <section class="example-section">
      <h2>Select Dropdowns</h2>
      <div class="input-grid">
        <BaseSelect
          v-model="formData.country"
          label="Country"
          placeholder="Select a country"
          :options="countries"
          required
        />
        <BaseSelect
          v-model="formData.role"
          label="Role"
          :options="roles"
          hint="Choose your role"
        />
      </div>
    </section>

    <!-- Cards -->
    <section class="example-section">
      <h2>Cards</h2>
      <div class="card-grid">
        <BaseCard title="Default Card">
          <p>This is the card content with default styling.</p>
        </BaseCard>

        <BaseCard variant="bordered" title="Bordered Card">
          <p>This card has a thicker border.</p>
        </BaseCard>

        <BaseCard variant="elevated" hoverable>
          <template #header>
            <h3>Elevated Hoverable Card</h3>
          </template>
          <p>Hover over this card to see the effect!</p>
          <template #footer>
            <BaseButton variant="primary" size="sm">Action</BaseButton>
          </template>
        </BaseCard>

        <BaseCard :padded="false">
          <template #header>
            <h3>No Padding Card</h3>
          </template>
          <img
            src="https://via.placeholder.com/400x200"
            alt="Placeholder"
            style="width: 100%; display: block"
          />
        </BaseCard>
      </div>
    </section>

    <!-- Modal -->
    <section class="example-section">
      <h2>Modal</h2>
      <BaseButton variant="primary" @click="showModal = true">Open Modal</BaseButton>

      <BaseModal v-model:isOpen="showModal" title="Example Modal" size="md">
        <p>This is a modal dialog. You can put any content here.</p>
        <p>Click outside or press ESC to close.</p>

        <template #footer>
          <BaseButton variant="ghost" @click="showModal = false">Cancel</BaseButton>
          <BaseButton variant="primary" @click="handleModalSubmit">Submit</BaseButton>
        </template>
      </BaseModal>
    </section>

    <!-- Loading Spinner -->
    <section class="example-section">
      <h2>Loading Spinner</h2>
      <div class="spinner-grid">
        <BaseSpinner size="sm" />
        <BaseSpinner size="md" text="Loading..." />
        <BaseSpinner size="lg" text="Please wait" />
      </div>
    </section>

    <!-- Toast Notifications -->
    <section class="example-section">
      <h2>Toast Notifications</h2>
      <div class="button-grid">
        <BaseButton variant="success" @click="showSuccessToast">Show Success</BaseButton>
        <BaseButton variant="danger" @click="showErrorToast">Show Error</BaseButton>
        <BaseButton variant="secondary" @click="showWarningToast">Show Warning</BaseButton>
        <BaseButton variant="primary" @click="showInfoToast">Show Info</BaseButton>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import {
  BaseButton,
  BaseInput,
  BaseSelect,
  BaseCard,
  BaseModal,
  BaseSpinner,
} from '@/components/ui'
import { useToast } from '@/composables/useToast'

const toast = useToast()

// Form data
const formData = reactive({
  name: '',
  email: '',
  password: '',
  country: '',
  role: '',
})

const passwordError = ref('')

// Select options
const countries = [
  { label: 'United States', value: 'us' },
  { label: 'Canada', value: 'ca' },
  { label: 'United Kingdom', value: 'uk' },
  { label: 'Germany', value: 'de' },
]

const roles = ['Admin', 'Instructor', 'Student']

// Modal
const showModal = ref(false)

const handleModalSubmit = () => {
  toast.success('Modal submitted successfully!')
  showModal.value = false
}

// Toast functions
const showSuccessToast = () => {
  toast.success('Operation completed successfully!', 'Success')
}

const showErrorToast = () => {
  toast.error('An error occurred while processing your request.', 'Error')
}

const showWarningToast = () => {
  toast.warning('Please review your input before submitting.', 'Warning')
}

const showInfoToast = () => {
  toast.info('This is an informational message.', 'Info')
}
</script>

<style scoped>
.example-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.example-section {
  margin-bottom: 3rem;
}

.example-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
}

.button-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.input-grid {
  display: grid;
  gap: 1.5rem;
  max-width: 600px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.spinner-grid {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}
</style>
