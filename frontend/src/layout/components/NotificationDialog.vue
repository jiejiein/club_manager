<template>
  <el-dialog
    v-model="dialogVisible"
    title="系统通知"
    width="400px"
    :close-on-click-modal="false"
  >
    <div v-if="notifications.length === 0" class="empty-notification">
      <el-empty description="暂无通知" />
    </div>
    <div v-else class="notification-list">
      <div 
        v-for="item in notifications" 
        :key="item.id" 
        class="notification-item"
        :class="{ 'unread': item.isRead === 0 }"
      >
        <div class="notification-header">
          <span class="notification-title">{{ item.title }}</span>
          <el-tag v-if="item.isRead === 0" size="small" type="danger">未读</el-tag>
        </div>
        <div class="notification-content">{{ item.content }}</div>
        <div class="notification-footer">
          <span class="notification-time">{{ item.createTime }}</span>
          <el-button 
            v-if="item.isRead === 0" 
            type="primary" 
            size="small" 
            @click="handleMarkAsRead(item.id)"
          >
            标记已读
          </el-button>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button 
        v-if="hasUnread" 
        type="primary" 
        @click="handleMarkAllAsRead"
      >
        全部标记已读
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { markAsRead, markAllAsRead } from '@/api/notification'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  notifications: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'mark-read', 'mark-all-read', 'refresh'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const hasUnread = computed(() => {
  return props.notifications.some(item => item.isRead === 0)
})

const handleMarkAsRead = async (notificationId) => {
  try {
    await markAsRead(notificationId)
    ElMessage.success('标记已读成功')
    emit('mark-read', notificationId)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const handleMarkAllAsRead = async () => {
  try {
    await markAllAsRead()
    ElMessage.success('全部标记已读成功')
    emit('mark-all-read')
  } catch (error) {
    console.error('全部标记已读失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.empty-notification {
  padding: 40px 0;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 3px;
  }

  .notification-item {
    padding: 16px;
    border-bottom: 1px solid #e2e8f0;

    &:last-child {
      border-bottom: none;
    }

    &.unread {
      background-color: #fef2f2;
      border-radius: 8px;
    }

    .notification-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .notification-title {
        font-weight: 600;
        color: #1e293b;
      }
    }

    .notification-content {
      font-size: 14px;
      color: #475569;
      line-height: 1.4;
      margin-bottom: 12px;
    }

    .notification-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .notification-time {
        font-size: 12px;
        color: #94a3b8;
      }
    }
  }
}
</style>
