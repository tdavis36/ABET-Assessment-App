<script lang="ts" setup>
    import { ref } from 'vue';
    import { useRoute } from 'vue-router';

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

    
    async function initialize(){
        course_id.value = parseInt(route.params.course_id as string, 10)

        try {
            const response = await fetch(`/api/courses/${course_id.value}`);
            const data = await response.json(); // Await the JSON parsing
            console.log('Fetched JSON data:', data);
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
            console.error('Error fetching or parsing data:', error);
        }



    }

    initialize();
    
</script>

<template>
    <p>Course ID {{ route.params.course_id }}</p>
    <p>Course Code: {{ course_obj.course_code }}</p>
    <p>Course Name: {{ course_obj.course_name }}</p>
    <p>Course Description: {{ course_obj.course_description }}</p>
    <p>Semester ID: {{ course_obj.semester_id }}</p>
    <p>Created At: {{ course_obj.created_at }}</p>
    <p>Is Active: {{ course_obj.is_active }}</p>
</template>
