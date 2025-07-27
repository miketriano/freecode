<template id="problem-view">
  <div class="header">
    <h1>FreeCode</h1>
  </div>
  <div class="problem-container">
    <h2>{{ problem.name }}</h2>
    <div class="problem-description">
      <div v-for="line in problem.description">
        {{ line }}
      </div>
    </div>
    <div>
      <textarea v-model="code"></textarea>
    </div>
    <div class="button-group">
      <button @click="submitCode" :disabled="loading">
        <span v-if="loading">Executing...</span>
        <span v-else>Submit</span>
      </button>
      <button @click="resetCode" :disabled="loading">
        <span>Reset</span>
      </button>
    </div>
    <div>
      <table>
        <thead>
          <tr>
            <th>Input</th>
            <th>Output</th>
            <th>Result</th>
            <th>Passed</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="testCase in testCases">
            <td>
              <div v-for="input in testCase.displayInput">
                {{ input }}
              </div>
            </td>
            <td>{{ testCase.output }}</td>
            <td>{{ testCase.result }}</td>
            <td>{{ testCase.passed ? "✅" : "❌" }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script>
app.component("problem-view", {
  template: "#problem-view",
  data: () => ({
    problem: null,
    code: '',
    testCases: null,
    loading: false
  }),
  created() {
    const id = this.$javalin.pathParams['id'];
    fetch(`/freecode/api/problems/${id}`)
      .then(res => res.json())
      .then(res => {
        this.problem = res;
        this.testCases = this.problem.testCases
        this.code = localStorage.getItem(`${id}_code`);
        if (!this.code) {
          this.code = this.problem.template;
        }
      })
      .catch(() => {
        alert('Invalid problem id');
        location = '/freecode';
      });
  },
  methods: {
    submitCode() {
      this.loading = true;
      const id = this.$javalin.pathParams['id'];
      localStorage.setItem(`${id}_code`, this.code);
      fetch(`/freecode/api/submit/${id}`, { method: 'POST', body: this.code })
        .then(res => res.json())
        .then(res => {
          this.testCases = res;
          if (this.testCases.every(tc => tc.passed)) {
            localStorage.setItem(`${id}_solved`, true);
          }
          this.loading = false;
        });
    },
    resetCode() {
      this.code = this.problem.template;
    }
  }
});
</script>
