<template>
  <div id="app">
    <header>
      <h1>ABET Assessment App</h1>
      <nav>
        <router-link to="/">Home</router-link> |
        <router-link to="/test-connection">Test Connection</router-link>
      </nav>
    </header>

    <main>
      <router-view />
    </main>
  </div>
</template>

<style>
#app {
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

nav {
  margin: 1rem 0;
}

nav a {
  margin: 0 1rem;
  color: #007bff;
  text-decoration: none;
}

nav a:hover {
  text-decoration: underline;
}

nav a.router-link-active {
  color: #0056b3;
  font-weight: bold;
}

main {
  margin-top: 2rem;
}
</style>
