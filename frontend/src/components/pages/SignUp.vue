<script lang="ts" setup>
    import { ref } from 'vue';

    const firstname_input = ref('');
    const lastname_input = ref('');
    const email_input = ref('');
    const password_input = ref('');
    const confirm_password_input = ref('');

    const display_error = true;
    const error_message = "error message"

    function signup(): number{
        
        return 0;
    }

</script>

<template>
    <div id="login_component">
        <h3>Sign Up</h3>
        <div class="input_div">
            <input v-model="firstname_input" placeholder="First Name"></input>
        </div>
        <div class="input_div">
            <input v-model="lastname_input" placeholder="Last Name"></input>
        </div>
        <div class="input_div">
            <input v-model="email_input" placeholder="Email"></input>
        </div>
        <div class="input_div">
            <input v-model="password_input" placeholder="Password" type="password"></input>
        </div>
        <div class="input_div">
            <input v-model="confirm_password_input" placeholder="Confirm Password" type="password"></input>
        </div>
        <div id="submit_div"><button id="submit" @click="signup">Submit</button></div>
        <p>Already have an account? Log in <router-link to="/login">here</router-link>.</p>
    </div>

    <div v-if="display_error" id="error">
        <p>Error: {{ error_message }}</p>
    </div>
</template>

<style>
    #login_component{
        padding: 1rem;
        background-color: #e2e2e2;
        width: 30%;
        border: 1px solid black;
        padding: 2rem;
        margin: auto;
        text-align: left;
    }
    
    .input_div{
        margin-top: 1rem;
        margin-bottom: 1rem;
    }

    #submit_div{
        margin: auto;
    }

    h3{
        margin-top: 1rem;
    }
</style>