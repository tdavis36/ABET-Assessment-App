<script lang="ts" setup>
    import { ref } from 'vue';
    import { useRoute } from 'vue-router';
    import api from '@/api';
    import InstructorListing from '@/components/InstructorListing.vue';
    import IndicatorListing from '@/components/IndicatorListing.vue';

    const route = useRoute()

    
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

    //--------TEST DATA--------
    /*
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
    const indicator_ids = ref([1,2])*/
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

    initialize();
    
</script>

<template>
    <div id="course_body">
        <div id="course_header">
            <span id="course_code">{{ course_obj.course_code }}</span>
            <span id="course_name">{{ course_obj.course_name }}</span>
        </div>
        <div id="semester"><a>{{ semester_name }}</a></div>
        <p id="description">{{ course_obj.course_description }}</p>

        <div id="instructors">
            Instructors: <InstructorListing v-for="iid in instructor_ids" :iid="iid"></InstructorListing>
        </div>

        <div id="performance-indicators">
            <IndicatorListing v-for="piid in indicator_ids" :piid="piid"></IndicatorListing>
        </div>
    </div>
</template>

<style>
    #course_body{
        margin: auto;
        width: 70%;

        font-family:
            system-ui,
            -apple-system,
            "Segoe UI",
            Roboto,
            Helvetica,
            Arial,
            sans-serif;
        text-align: left;
    }

    #course_header{
        margin-top: 2rem;
        margin-bottom: 0.5rem;
        font-size: 32px;
    }

    #course_code{
        color: green;
        font-weight: bold;
        margin-right: 1rem;
    }

    #description{
        font-style: italic;
        color: rgb(196, 196, 196);
        margin-bottom: 1.5rem;
    }

    #performance-indicators{
        margin: 2rem;
    }
</style>
