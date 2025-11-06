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

        /*
        try {
            const response = await fetch(`/api/users/${user_id}`);
            const data = await response.json(); // Await the JSON parsing
            console.log('Fetched JSON data:', data);
            user_name.value = {
                first: data.data.firstName,
                full: data.data.fullName
            }
            console.log('First name:', data.data.firstName)
        } catch (error) {
            console.error('Error fetching or parsing data:', error);
        }*/
    }

    initialize();
</script>

<template>

</template>
