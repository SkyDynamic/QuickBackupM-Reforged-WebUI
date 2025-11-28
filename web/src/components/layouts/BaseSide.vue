<script lang="ts" setup>
import {
  Menu as IconMenu,
} from '@element-plus/icons-vue'
</script>

<template>
  <el-menu
    router
    default-active="1"
    class="el-menu-vertical-demo"
  >
    <el-menu-item index="/nav/BackupManager">
      <el-icon>
        <IconMenu />
      </el-icon>
      <template #title>
        Manager Backup
      </template>
    </el-menu-item>
  </el-menu>
</template>
