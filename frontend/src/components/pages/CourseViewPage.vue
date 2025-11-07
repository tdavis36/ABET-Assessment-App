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
        is_active: ''
    });
    const semester_name = ref('');

    
    async function initialize(){
        course_id.value = parseInt(route.params.course_id as string, 10)

        try {
            const response = await fetch(`/api/courses/${course_id.value}`);
            const data = await response.json(); // Await the JSON parsing
            console.log('Fetched JSON data:', data);
            /*course_obj.value = {
                id: data.data.firstName,
                course_code: data.data.fullName
            }*/
        } catch (error) {
            console.error('Error fetching or parsing data:', error);
        }
    }

    initialize();
    
</script>

<template>
    <p>Course id {{ route.params.course_id }}</p>
</template>
