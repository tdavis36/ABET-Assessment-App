<script lang="ts" setup>
    import { ref } from 'vue';
    const props = defineProps({iid: Number})

    const name = ref('')
    const link = ref('')

    async function initialize(){
        //Fetch instructor name
        try {
            const response = await fetch(`/api/users/${props.iid}`);
            const data = await response.json(); // Await the JSON parsing
            name.value = data.data.fullName
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
            name.value = props.iid?.toString() as string
        }

        link.value = `/instructor/${props.iid}`
    }

    initialize()
</script>

<template>
    <router-link :to="`/instructor/${props.iid}`" id="instructor-listing">{{ name }}</router-link>
</template>

<style>
    #instructor-listing{
        background-color: rgb(253, 101, 101);
        color: white;
        padding-left: 1rem;
        padding-right: 1rem;
        padding-bottom: 0.5rem;
        padding-top: 0.5rem;
        margin: 0.2rem;
        border-radius: 1rem;
        text-decoration: none;
    }

    #instructor-listing:hover{
        background-color: rgb(252, 35, 35);
    }
</style>