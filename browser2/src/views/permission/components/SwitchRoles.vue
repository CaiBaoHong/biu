<template>
  <div>
    <div style="margin-bottom:15px;">角色： {{roles}}</div>
    切换角色：
    <el-radio-group v-model="switchRoles">
      <el-radio-button label="editor"></el-radio-button>
      <el-radio-button label="admin"></el-radio-button>
    </el-radio-group>
  </div>
</template>

<script>
export default {
  computed: {
    roles() {
      return this.$store.getters.roles
    },
    switchRoles: {
      get() {
        return this.roles[0]
      },
      set(val) {
        this.$store.dispatch('ChangeRoles', val).then(() => {
          this.$emit('change')
        })
      }
    }
  }
}
</script>
