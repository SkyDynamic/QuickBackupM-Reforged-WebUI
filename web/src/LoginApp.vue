<script setup lang="ts">
import Login from '~/pages/login.vue'
</script>

<template>
  <el-config-provider namespace="ep">
    <BaseHeader ignore-index-nav />
    <div class="main-container flex">
      <Login />
    </div>
  </el-config-provider>
</template>

<style lang="scss">
#loginApp {
  text-align: center;
  color: var(--ep-text-color-primary);
}

.main-container {
  height: calc(100vh - var(--ep-menu-item-height) - 4px);
}
</style>
