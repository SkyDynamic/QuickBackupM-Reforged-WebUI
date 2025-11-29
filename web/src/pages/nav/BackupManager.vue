<script setup lang="ts">
import type { InputInstance } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { computed, onMounted, ref } from 'vue'
import { verifyToken } from '~/composables/login'

const backupList = ref<{ name: string, desc: string, timestamp: bigint, useIncrementalStorage: boolean }[]>([])

const query = ref('')
const searchRef = ref<InputInstance>()

const dialogCreateBackupVisible = ref(false)
const createBackupName = ref('')
const createBackupDesc = ref('')
const waitCreateBackup = ref(false)
const createButtonDisable = ref(false)

const intervalId = ref()

const filteredBackups = computed(() =>
  backupList.value
    .slice(1)
    .filter((group) => {
      const value = query.value.trim().toLowerCase()
      return (
        group.name.toLowerCase().includes(value)
        || group.desc.toLowerCase().includes(value)
      )
    }),
)

async function fetchBackups() {
  const response = await fetch('/api/backups/getAll', { method: 'GET' })
  const data = await response.json()

  return data.message
}

async function refreshBackups() {
  backupList.value = await fetchBackups()
}

async function restoreBackup(name: string) {
  ElMessageBox.confirm(
    'Are you sure you want to restore this backup? This will overwrite your current data.',
    'Restore Backup',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  ).then(async () => {
    const headers = new Headers()
    headers.append('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8')
    headers.append('X-Login-Token', <string>localStorage.getItem('qbmRauthToken'))

    const response = await fetch('/api/backups/restore', {
      method: 'POST',
      headers: headers,
      body: new URLSearchParams({
        name,
      }),
    })
    const data = await response.json()
    if (data.statusCode === 0) {
      ElMessage({
        type: 'success',
        message: 'Backup restored successfully.',
      })
    }
    else {
      ElMessage({
        type: 'error',
        message: `Failed to restore backup.`,
      })
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: 'Restore failed',
    })
  })
}

async function deleteBackup(name: string) {
  ElMessageBox.confirm(
    'Are you sure you want to delete this backup? This action cannot be undone.',
    'Delete Backup',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  ).then(async () => {
    const headers = new Headers()
    headers.append('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8')
    headers.append('X-Login-Token', <string>localStorage.getItem('qbmRauthToken'))

    const response = await fetch('/api/backups/delete', {
      method: 'POST',
      headers: headers,
      body: new URLSearchParams({
        name,
      }),
    })
    const data = await response.json()
    if (data.statusCode === 0) {
      ElMessage({
        type: 'success',
        message: 'Backup deleted successfully.',
      })
      await refreshBackups()
    }
    else {
      ElMessage({
        type: 'error',
        message: `Failed to delete backup.`,
      })
    }
  }).catch(async () => {
    ElMessage({
      type: 'info',
      message: 'Delete canceled',
    })
  })
}

async function createBackup() {
  if (createBackupName.value === '') {
    ElMessage({
      type: 'warning',
      message: 'Please enter a name for the backup.',
    })
    return
  }

  waitCreateBackup.value = true
  createButtonDisable.value = true

  const headers = new Headers()
  headers.append('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8')
  headers.append('X-Login-Token', <string>localStorage.getItem('qbmRauthToken'))

  const response = await fetch('/api/backups/create', {
    method: 'POST',
    headers: headers,
    body: new URLSearchParams({
      name: createBackupName.value.replace(' ', '_'),
      desc: createBackupDesc.value,
    }),
  })

  const data = await response.json()
  if (data.statusCode === 0) {
    ElMessage({
      type: 'success',
      message: 'Backup created successfully.',
    })
    await refreshBackups()
  }
  else {
    ElMessage({
      type: 'error',
      message: `Failed to create backup.`,
    })
  }

  waitCreateBackup.value = false

  setTimeout(() => {
    createBackupName.value = ''
    createBackupDesc.value = ''
    dialogCreateBackupVisible.value = false
  }, 3000)

  await refreshBackups()
}

onMounted(async () => {
  if (!await verifyToken()) {
    document.location.href = '/login'
  }
  else {
    await refreshBackups()

    intervalId.value = setInterval(() => {
      refreshBackups()
    }, 30 * 1000)
  }
})
</script>

<template>
  <div class="backup-manager-container">
    <div class="title">
      Backup Manager
    </div>
    <div class="search-content">
      <el-input
        ref="searchRef"
        v-model="query"
        :prefix-icon="Search"
        size="large"
        placeholder="Search Backups"
      />
      <el-button
        type="primary"
        @click="dialogCreateBackupVisible = true"
      >
        Create
      </el-button>
    </div>
    <div class="main-container">
      <el-table
        :data="filteredBackups"
        :default-sort="{ prop: 'timestamp', order: 'descending' }"
        empty-text="No Backup Found"
        border
      >
        <el-table-column prop="name" label="Name" max-width="180" />
        <el-table-column prop="desc" label="Description" max-width="300" />
        <el-table-column
          prop="timestamp"
          label="Date"
          width="220"
          sortable
          :formatter="(row) => new Date(Number(row.timestamp)).toLocaleString()"
        />
        <el-table-column fixed="right" width="180">
          <template #header>
            <el-text>
              Operations
            </el-text>
            <el-button
              link
              @click.prevent="refreshBackups()"
            >
              <el-icon>
                <Refresh />
              </el-icon>
            </el-button>
          </template>
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              @click.prevent="deleteBackup(scope.row.name)"
            >
              Delete
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click.prevent="restoreBackup(scope.row.name)"
            >
              Restore
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>

  <el-dialog v-model="dialogCreateBackupVisible" title="Create Backup" width="500">
    <div
      style="display: flex; flex-direction: column; gap: 1em;"
    >
      <el-input v-model="createBackupName" placeholder="Backups Name" />
      <el-input v-model="createBackupDesc" placeholder="Backup Description" />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogCreateBackupVisible = false">
          Cancel
        </el-button>
        <el-button
          type="primary"
          :loading="waitCreateBackup"
          :disabled="createButtonDisable"
          @click="createBackup"
        >
          Confirm
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.backup-manager-container {
  display: flex;
  flex-direction: column;
  gap: 1em;
  margin: 2em;

  .title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
    width: 100%;
    text-align: center;
  }

  .search-content {
    display: flex;
    flex-direction: row;
    align-items: center;

    position: sticky;
    top: 60px;
    z-index: 10;

    gap: 0.5em;

    .el-input {
      background: var(--bg-color);
    }
  }

  .main-container {
    width: 100%;
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}

.el-button .custom-loading .circular {
  margin-right: 6px;
  width: 18px;
  height: 18px;
  animation: loading-rotate 2s linear infinite;
}
.el-button .custom-loading .circular .path {
  animation: loading-dash 1.5s ease-in-out infinite;
  stroke-dasharray: 90, 150;
  stroke-dashoffset: 0;
  stroke-width: 2;
  stroke: var(--el-button-text-color);
  stroke-linecap: round;
}
</style>
