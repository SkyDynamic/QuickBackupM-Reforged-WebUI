<script lang="ts" setup>
import { defineEmits, nextTick, ref, useAttrs, useTemplateRef, watch } from 'vue'
import { repository } from '~/../package.json'

import { isDark, toggleDark } from '~/composables'
import DarkIcon from '~/icons/dark.vue'
import LightIcon from '~/icons/light.vue'

const emit = defineEmits(['toggled'])
const attrs = useAttrs()

const darkMode = ref(isDark.value)

watch(
  () => darkMode.value,
  () => {
    toggleDark()
  },
)

const switchRef = useTemplateRef('switchRef')

function handleBeforeChange() {
  return new Promise<boolean>((resolve) => {
    const isAppearanceTransition
        // @ts-expect-error: startViewTransition 为实验性 API
        = document.startViewTransition
          && !window.matchMedia('(prefers-reduced-motion: reduce)').matches
    if (!isAppearanceTransition) {
      resolve(true)
      return
    }

    const switchElement = switchRef.value?.$el
    const rect = switchElement.getBoundingClientRect()
    const x = rect.left + rect.width / 2
    const y = rect.top + rect.height / 2

    const endRadius = Math.hypot(
      Math.max(x, innerWidth - x),
      Math.max(y, innerHeight - y),
    )

    const ratioX = (100 * x) / innerWidth
    const ratioY = (100 * y) / innerHeight
    const referR = Math.hypot(innerWidth, innerHeight) / Math.SQRT2
    const ratioR = (100 * endRadius) / referR

    const transition = document.startViewTransition(async () => {
      resolve(true)
      await nextTick()
    })
    transition.ready.then(() => {
      const clipPath = [
        `circle(0% at ${ratioX}% ${ratioY}%)`,
        `circle(${ratioR}% at ${ratioX}% ${ratioY}%)`,
      ]
      document.documentElement.animate(
        {
          clipPath: isDark.value ? [...clipPath].reverse() : clipPath,
        },
        {
          duration: 400,
          easing: 'ease-in',
          fill: 'both',
          pseudoElement: isDark.value
            ? '::view-transition-old(root)'
            : '::view-transition-new(root)',
        },
      )
    })
  })
}

function handleChange(val) {
  emit('toggled', val)
}
</script>

<template>
  <el-menu class="el-menu" mode="horizontal" :ellipsis="false" router>
    <el-menu-item index="/">
      <div class="flex items-center justify-center gap-2">
        <div class="logo-container">
          <img src="../../assets/logo.png" class="logo" alt="QBM Logo">
        </div>
        <span>QuickBackupM Reforged WebUI</span>
      </div>
    </el-menu-item>
    <div
      style="
    display: flex;
    height: 100%;
    flex-direction: column;
    justify-content: center;"
    >
      <el-switch
        ref="switchRef"
        v-model="isDark"
        v-bind="attrs"
        inline-prompt
        :before-change="handleBeforeChange"
        :active-action-icon="DarkIcon"
        :inactive-action-icon="LightIcon"
        class="p-4"
        @change="handleChange"
      />
    </div>

    <el-menu-item h="full">
      <a class="size-full flex items-center justify-center" :href="repository.url" target="_blank">
        <div i-ri-github-fill />
      </a>
    </el-menu-item>
  </el-menu>
</template>

<style lang="scss">
.el-menu {
  &.ep-menu--horizontal > .ep-menu-item:nth-child(1) {
    margin-right: auto;
  }
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo {
  height: 40px;
  width: 40px;
}

:deep(.dark-icon) {
  border-radius: 50%;
  color: #cfd3dc;
  background-color: #141414;
}

:deep(.light-icon) {
  color: #606266;
}
</style>
