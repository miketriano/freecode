<template id="dashboard-view">
  <div class="header">
    <h1>FreeCode</h1>
    <h3>Coding interview style problems to improve your Python skills</h3>
    <h4>Select a problem to get started</h4>
  </div>
  <table>
    <thead>
      <tr>
        <th>Name</th>
        <th>Difficulty</th>
        <th>Solved</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="problem in problems" :key="problem.id">
        <td>
          <a :href="`problems/${problem.id}`">{{ problem.name }}</a>
        </td>
        <td>{{ problem.difficulty }}</td>
        <td>{{ problem.solved ? '✅' : '❌' }}</td>
      </tr>
    </tbody>
  </table>
</template>
<script>
app.component("dashboard-view", {
  template: "#dashboard-view",
  data: () => ({
    problems: []
  }),
  created() {
    fetch("/freecode/api/problems")
      .then(res => res.json())
      .then(res => {
        this.problems = res;
        this.problems.forEach(p => p.solved = localStorage.getItem(`${p.id}_solved`));
      })
      .catch(() => alert('Error while fetching problems'));
  }
});
</script>