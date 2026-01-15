<template>
  <section class="fcar-page">
    <h1>FCAR ~ ABET Assessments</h1>

    <label>Student Outcome #:</label>
    <input v-model="form.outcome" type="text" placeholder="e.g., 1.1" />

    <label>Course:</label>
    <input v-model="form.course" type="text" placeholder="CS 101 - Fundamentals of Computer Science I" />

    <label>Work Used:</label>
    <input v-model="form.work" type="text" placeholder="Assessment 1, Question 5" />

    <label>Description:</label>
    <textarea v-model="form.description" placeholder="Briefly describe the assignment or activity used for assessment..."></textarea>

    <div class="divider"></div>

    <label>Target Goal:</label>
    <input
      v-model="form.targetGoal"
      type="text"
      placeholder="70% of students meeting or exceeding expectations"
    />

    <div class="row">
      <div>
        <label>Needs Improvement proportion:</label>
        <input v-model="form.needsImprovement" type="text" placeholder="e.g., 5/15 (33%)" />
      </div>

      <div>
        <label>Meets Expectations:</label>
        <input v-model="form.meetsExpectations" type="text" placeholder="10/15 (66%)" />
      </div>

      <div>
        <label>Exceeds Expectations:</label>
        <input v-model="form.exceedsExpectations" type="text" placeholder="5/15 (33%)" />
      </div>
    </div>

    <div class="divider"></div>

    <label>Summary & Observations:</label>
    <textarea
      v-model="form.summary"
      placeholder="Summarize findings, trends, and recommendations..."
    ></textarea>

    <button @click="generateReport">Generate Report</button>
  </section>
</template>

<script>
export default {
  name: "FCARPage",
  data() {
    return {
      form: {
        outcome: "",
        course: "",
        work: "",
        description: "",
        targetGoal: "70% of students meeting or exceeding expectations",
        needsImprovement: "",
        meetsExpectations: "",
        exceedsExpectations: "",
        summary: "",
        resultsMet: false,
      },
    };
  },
  methods: {
    generateReport() {
      alert("Report generated successfully (placeholder function).");

      const lines = [];
      lines.push(`(${this.form.outcome || ""})`);
      lines.push("");

      if (this.form.course) lines.push(`Course: ${this.form.course}`);
      if (this.form.work) lines.push(`Work used: ${this.form.work}`);
      lines.push("");

      lines.push("Description");
      lines.push(this.form.description || "");
      lines.push("");

      lines.push(`Target goal: ${this.form.targetGoal}`);
      lines.push("");

      lines.push(
        `Needs improvement: ${this.form.needsImprovement} : [${this.makeTallies(
          this.form.needsImprovement
        )}]`
      );

      lines.push(
        `Meets expectations: ${this.form.meetsExpectations} : [${this.makeTallies(
          this.form.meetsExpectations
        )}]`
      );

      lines.push(
        `Exceeds expectations: ${this.form.exceedsExpectations} : [${this.makeTallies(
          this.form.exceedsExpectations
        )}]`
      );
      lines.push("");

      const resultLine = this.form.targetGoal
        ? `Results: The target of ${this.form.targetGoal} was ${
            this.form.resultsMet ? "achieved." : "not achieved."
          }`
        : "Results: Target goal data unavailable.";
      lines.push(resultLine);
      lines.push("");

      lines.push(`Summary/Observations: ${this.form.summary}`);
      lines.push("");

      const reportText = lines.join("\n");

      const blob = new Blob([reportText], { type: "text/plain;charset=utf-8" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");

      const safeCourse = (this.form.course || "FCAR_Report").replace(/[^\w\-]+/g, "_");
      a.href = url;
      a.download = `${safeCourse}.txt`;

      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    },

    makeTallies(text) {
      const match = text.match(/^(\d+)/);
      if (!match) return "";
      const n = parseInt(match[1]);
      return "1".repeat(Math.min(n, 15));
    },
  },
};
</script>
<style scoped>
.fcar-page {
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  max-width: 750px;
  margin: auto;
  padding: 1.5rem;
}

label {
  margin-top: 1rem;
  display: block;
  font-weight: 600;
}

input,
textarea {
  width: 100%;
  margin-top: 0.25rem;
  padding: 0.5rem;
  border: 1px solid #bbb;
  border-radius: 6px;
  font-size: 1rem;
}

textarea {
  min-height: 80px;
}

.row {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.divider {
  height: 2px;
  background: #ddd;
  margin: 1.5rem 0;
}

button {
  margin-top: 1.5rem;
  padding: 0.75rem 1.25rem;
  background: #1B5E20; /* DARK GREEN */
  color: white;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:hover {
  background: #14451A; /* DARKER GREEN */
}
</style>

