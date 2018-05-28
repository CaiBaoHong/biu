<template>
  <div class="errPage-container">
    <errorA></errorA>
    <errorB></errorB>
    <h3>tips</h3>
    <code>
      description
      <a target="_blank" class="link-type" href="https://panjiachen.github.io/vue-element-admin-site/#/error?id=%e4%bb%a3%e7%a0%81">
        documentation
      </a>
    </code>
    <a href="#">
      <img src='https://wpimg.wallstcn.com/360e4842-4db5-42d0-b078-f9a84a825546.gif'>
    </a>
  </div>
</template>

<script>
import errorA from './errorTestA'
import errorB from './errorTestB'

export default {
  name: 'errorLog',
  components: { errorA, errorB }
}
</script>

<style scoped>
  .errPage-container {
    padding: 30px;
  }
</style>
