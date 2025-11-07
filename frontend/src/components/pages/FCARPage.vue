<template>
  <section class="fcar-container">
    <h1>FCAR ~ ABET Assessments</h1>

    <label>Student Outcome #:</label>
    <input type="text" placeholder="e.g., 1.1" />

    <label>Course:</label>
    <input type="text" placeholder="CS 101 - Fundamentals of Computer Science I" />

    <label>Work Used:</label>
    <input type="text" placeholder="Assessment 1, Question 5" />

    <label>Description:</label>
    <textarea placeholder="Briefly describe the assignment or activity used for assessment..."></textarea>

    <div class="divider"></div>

    <label>Target Goal:</label>
    <input type="text" placeholder="70% of students meeting or exceeding expectations" />

    <div class="row">
      <div>
        <label>Needs Improvement proportion:</label>
        <input type="text" placeholder="e.g., 5/15 (33%)" />
      </div>
      <div>
        <label>Meets Expectations:</label>
        <input type="text" placeholder="10/15 (66%)" />
      </div>
      <div>
        <label>Exceeds Expectations:</label>
        <input type="text" placeholder="5/15 (33%)" />
      </div>
    </div>

    <div class="divider"></div>

    <label>Summary & Observations:</label>
    <textarea placeholder="Summarize findings, trends, and recommendations..."></textarea>

    <button @click="generateReport">Generate Report</button>

    <footer>
      <hr class="divider" />
      <p>© 2025 ABET Assessment App</p>
      <p>Definitions adapted from ABET documentation.</p>
    </footer>
  </section>
</template>

<script>
export default {
  name: "FCARPage",
  methods: {
    generateReport() {
      alert("Report generated successfully (placeholder function).");
    },
  },
};
</script>

<style scoped>
body {
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  background-color: #f8f8f8;
  color: #222;
  padding: 2rem;
  display: flex;
  justify-content: center;
}

.fcar-container {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  width: 700px;
  padding: 2rem;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #b22222;
  margin-bottom: 1.5rem;
}

label {
  font-weight: 600;
  display: block;
  margin-top: 1rem;
}

input[type="text"],
textarea {
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-top: 0.3rem;
  font-size: 1rem;
  background-color: #fafafa;
  transition: border-color 0.2s ease;
}

input[type="text"]:focus,
textarea:focus {
  outline: none;
  border-color: #b22222;
  background-color: #fff;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

.row {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
  align-items: flex-end;
}

.row > div:nth-child(1) {
  flex: 1.2;
}
.row > div:nth-child(2),
.row > div:nth-child(3) {
  flex: 1;
}

.divider {
  border-top: 2px solid #ccc;
  margin: 2rem 0;
}

button {
  display: block;
  margin: 2rem auto 0;
  background-color: #b22222;
  color: #fff;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  padding: 0.7rem 2rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #8b1a1a;
}

footer {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
  margin-top: 3rem;
}
</style>
