<script lang="ts" setup>
    import { ref } from 'vue';
    import MeasureListing from '@/components/MeasureListing.vue'

    const props = defineProps({piid: Number})

    /*
    const indicator_obj = ref({
        id: NaN,
        ind_number: NaN,
        ind_value: NaN,
        ind_description: '',
        evaluation: '',
        student_outcome_id: NaN,
        threshold_percentage: NaN,
        created_at: '',
        is_active: false
    })
    const measures = ref([])
    */

    //-----TEST DATA-----
    const indicator_obj = ref({
        id: 1,
        ind_number: 1,
        ind_value: null,
        ind_description: 'Identify and formulate computing problems',
        evaluation: null,
        student_outcome_id: 1,
        threshold_percentage: 75.00,
        created_at: '2025-11-06 12:06:00.872447	',
        is_active: true
    })
    const measures = ref([1,2])
    //-------------------

    async function initialize(){
        //Fetch indicator object
        /*
        try {
            const response = await fetch(`/api/indicators/${props.piid}`);
            const data = await response.json();
        } catch (error) {
            console.error('Error fetching or parsing course data:', error);
        }
        */
        //Fetch measures
    }

    initialize()
</script>

<template>
    <div id="indicator_box">
        <div id="title"><span id="pi-label">Performance Indicator: </span>{{ indicator_obj.ind_description }}</div>
        <div id="threshold-percent-title">Threshold: <span id="threshold-percent">{{ indicator_obj.threshold_percentage }}%</span></div>
        <MeasureListing v-for="measure_id in measures" :mid="measure_id"></MeasureListing>
    </div>
</template>

<style>
    #title{
        font-size: 24px;
    }

    #pi-label{
        color: green;
    }

    #threshold-percent-title{
        color: lightgray;
        margin-bottom: 1rem;
    }
</style>