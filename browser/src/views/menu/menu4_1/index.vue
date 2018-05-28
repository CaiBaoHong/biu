<template>
  <transition name="fade" mode="out-in">
    <keep-alive :include='cachedViews'>
      <router-view></router-view>
    </keep-alive>
  </transition>
</template>

<script>
export default {
  name: 'Menu4_1',
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    }
  }
}
</script>
