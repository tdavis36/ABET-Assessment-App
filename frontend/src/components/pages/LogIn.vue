<script lang="ts" setup>
    import { ref } from 'vue';

    const email_input = ref('');
    const password_input = ref('');

    const display_error = true;
    const error_message = "Email or password is incorrect."

    function login(): Number{
        
        return 0;
    }

</script>

<template>
    <div id="login_component">
        <h3>Log In</h3>
        <div class="input_div">
            <input id="email" v-model="email_input" placeholder="Email"></input>
        </div>
        <div class="input_div">
            <input id="password" v-model="password_input" placeholder="Password" type="password"></input>
        </div>
        <div id="submit_div"><button id="submit" @click="login">Submit</button></div>
        <p>Don't have an account? Sign up <router-link to="/signup">here</router-link>.</p>
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

    #error{
        background-color: rgb(255, 168, 168);
        border: 1px solid red;
        width: 30%;
        margin-left: auto;
        margin-right: auto;
        margin-top: 0.5rem;
    }

    #error p{
        color: rgb(204, 0, 0);
    }
</style>