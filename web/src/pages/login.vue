<script setup lang="ts">
import { Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { verifyToken } from '~/composables/login'

const secret = ref('')

const requestSecretButtonDisable = ref(false)
const requestSecretButtonLoading = ref(false)

const requestLoginButtonLoding = ref(false)

async function requestSecret() {
  requestSecretButtonDisable.value = true
  requestSecretButtonLoading.value = true

  fetch('/api/requestLoginSecret', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
  }).then(async (response) => {
    const data = await response.json()
    if (data.statusCode === 0) {
      ElMessage({
        message: 'Secret Key has been sent to your console.',
        type: 'success',
      })
    }
    else {
      ElMessage({
        message: 'Failed to request Secret Key.',
        type: 'error',
      })
    }
  }).catch(async () => {
    ElMessage({
      message: 'Failed to request Secret Key.',
      type: 'error',
    })
  })

  setTimeout(() => {
    requestSecretButtonDisable.value = false
  }, 60 * 1000)

  requestSecretButtonLoading.value = false
}

async function requestLogin() {
  requestLoginButtonLoding.value = true

  const response = await fetch('/api/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    },
    body: new URLSearchParams({
      secret: secret.value,
    }),
  })

  const data = await response.json()
  if (data.statusCode === 0) {
    window.location.href = '/'
    localStorage.setItem('qbmRauthToken', data.message)
  }
  else {
    ElMessage({
      message: 'Login failed. Please check your Secret Key.',
      type: 'error',
    })
  }

  requestLoginButtonLoding.value = false
}

onMounted(async () => {
  if (await verifyToken()) {
    window.location.href = '/'
  }
})
</script>

<template>
  <div class="login-container">
    <div class="input-container">
      <el-input
        v-model="secret"
        style="width: 300px"
        placeholder="Input Login Secret Key"
        :prefix-icon="Lock"
        size="large"
      />
      <el-button
        type="primary"
        style="width: 150px"
        :loading="requestSecretButtonLoading"
        :disabled="requestSecretButtonDisable"
        size="large"
        @click="requestSecret"
      >
        Request Secret Key
      </el-button>
    </div>

    <el-button
      type="primary"
      style="width: 80px"
      :loading="requestLoginButtonLoding"
      size="large"
      @click="requestLogin"
    >
      Login
    </el-button>
  </div>
</template>

<style scoped>
.login-container {
  width: 100%;
  height: calc(100vh - var(--ep-menu-item-height) - 4px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;

  .input-container {
    display: flex;
    flex-direction: row;
    gap: 16px;
  }
}
</style>
