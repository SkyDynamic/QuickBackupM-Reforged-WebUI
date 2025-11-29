<script setup lang="ts">
import { onMounted } from 'vue'
import { verifyToken } from '~/composables/login'

onMounted(async () => {
  if (!await verifyToken()) {
    window.location.href = '/login'
  }
})
</script>

<template>
  <div class="container">
    <Logos my="4" />
    <h1 color="$ep-color-primary">
      QuickBackupMulti Reforged WebUI
    </h1>
  </div>
</template>

<style lang="scss">
.container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
