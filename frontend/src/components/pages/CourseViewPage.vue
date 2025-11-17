<script lang="ts" setup>
    import { ref } from 'vue';
    import { useRoute } from 'vue-router';
    import api from '@/api';
    import InstructorListing from '@/components/InstructorListing.vue';
    import IndicatorListing from '@/components/IndicatorListing.vue';
    import {BaseCard} from "@/components/ui";

    const route = useRoute()

    /*
    const course_id = ref(NaN);
    const course_obj = ref({
        id: NaN,
        course_code: '',
        course_name: '',
        course_description: '',
        semester_id: '',
        created_at: '',
        is_active: false
    });
    const semester_name = ref('');
    const instructor_ids = ref([]);
    const indicator_ids = ref([])
    */

    //--------TEST DATA--------

    const course_id = ref(1);
    const course_obj = ref({
        id: 1,
        course_code: 'CS101',
        course_name: 'Fundamentals of Computer Science I',
        course_description: 'Basic programming concepts and problem solving',
        semester_id: '1',
        created_at: '2025-11-10T10:16:56.456221',
        is_active: true
    });
    const semester_name = ref('Fall 2025');
    const instructor_ids = ref([1, 2]);
    const indicator_ids = ref([1,2])
    //--------------------------

    async function initialize(){
        course_id.value = parseInt(route.params.course_id as string, 10)

        //Fetch Course data
        try {
            const { data } = await api.get(`/courses/${course_id.value}`);
            course_obj.value = {
                id: data.data.id,
                course_code: data.data.courseCode,
                course_name: data.data.courseName,
                course_description: data.data.courseDescription,
                semester_id: data.data.semesterId,
                created_at: data.data.createdAt,
                is_active: data.data.isActive
            }
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }

        //Fetch Semester data
        try {
            const { data } = await api.get(`/semesters/${course_obj.value.semester_id}`);
            console.log('Fetched JSON data:', data);
            semester_name.value = `${data.data.season} ${data.data.semesterYear}`
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }

        //Fetch Instructor IDs
        try {
            const { data } = await api.get(`/courses/${course_id.value}/instructors`);
            instructor_ids.value = data
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }

        //Fetch Indicator IDs
        try {
            const { data } = await api.get(`/courses/${course_id.value}/indicators`);
            indicator_ids.value = data
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }
    }

    //initialize();

</script>

<template>
  <section class="course-page">

    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="course-title">
          {{ course_obj.course_code }} — {{ course_obj.course_name }}
        </h2>

        <p class="subtitle">
          {{ semester_name }}
        </p>
      </div>
    </div>

    <!-- Description -->
    <p class="course-description">
      {{ course_obj.course_description }}
    </p>

    <!-- Instructors -->
    <section class="detail-section">
      <h3>Instructors</h3>
      <div class="instructor-list">
        <BaseCard
          v-for="iid in instructor_ids"
          :key="iid"
          variant="elevated"
          hoverable
          class="mini-card"
        >
          <InstructorListing :iid="iid" />
        </BaseCard>
      </div>
    </section>

    <!-- Indicators -->
    <section class="detail-section">
      <h3>Performance Indicators</h3>

      <div class="indicator-list">
        <BaseCard
          v-for="piid in indicator_ids"
          :key="piid"
          variant="default"
          class="indicator-card"
        >
          <IndicatorListing :piid="piid" />
        </BaseCard>
      </div>
    </section>

  </section>
</template>

<style>
.course-page {
  padding: 2rem;
  max-width: 1100px;
  margin: 0 auto;
}

/* Header */
.page-header {
  margin-bottom: 2rem;
}

.header-content {
  margin-bottom: 0.25rem;
}

.course-title {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 2rem;
  font-weight: 700;
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1rem;
}

/* Description */
.course-description {
  margin-top: 0.75rem;
  margin-bottom: 2rem;
  color: var(--color-text-secondary);
  font-style: italic;
  font-size: 1rem;
}

/* Sections */
.detail-section {
  margin-top: 2.5rem;
}

.detail-section h3 {
  margin: 0 0 1rem 0;
  font-size: 1.25rem;
  color: var(--color-text-primary);
  border-bottom: 2px solid var(--color-border-light);
  padding-bottom: 0.5rem;
}

/* Instructor list (cards) */
.instructor-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1rem;
}

.mini-card {
  padding: 1rem;
}

/* Indicator cards */
.indicator-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.indicator-card {
  padding: 1.25rem;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border-light);
  border-radius: 0.5rem;
}

@media (max-width: 768px) {
  .course-page {
    padding: 1rem;
  }

  .instructor-list {
    grid-template-columns: 1fr;
  }
}
</style>
