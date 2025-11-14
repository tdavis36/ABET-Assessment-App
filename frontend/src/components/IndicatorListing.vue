<script lang="ts" setup>
    import { ref } from 'vue';
    const props = defineProps({piid: Number})

    const indicator = ref({})

    async function initialize(){
        //Fetch instructor name
        /*
        try {
            const response = await fetch(`/api/indicators/${props.piid}`);
            const data = await response.json();
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }
        */
    }

    initialize()
</script>

<template>
    <div id="indicator_box">
        Performance Indicator: {{ piid }}
    </div>
</template>

<style>

</style>