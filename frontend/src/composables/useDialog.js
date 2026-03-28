import { ref } from 'vue'

/**
 * 弹窗组合式函数
 * 封装通用的弹窗逻辑
 * 
 * @param {Object} options - 配置选项
 * @returns {Object} 弹窗相关的状态和方法
 */
export function useDialog(options = {}) {
  const {
    onConfirm,
    onCancel,
    onClose
  } = options

  // 弹窗显示状态
  const visible = ref(false)

  // 弹窗标题
  const title = ref('')

  // 弹窗数据
  const formData = ref({})

  // 是否编辑模式
  const isEdit = ref(false)

  /**
   * 打开弹窗（新增）
   */
  const open = (data = {}, dialogTitle = '') => {
    formData.value = { ...data }
    title.value = dialogTitle || '新增'
    isEdit.value = false
    visible.value = true
  }

  /**
   * 打开弹窗（编辑）
   */
  const openEdit = (data, dialogTitle = '') => {
    formData.value = { ...data }
    title.value = dialogTitle || '编辑'
    isEdit.value = true
    visible.value = true
  }

  /**
   * 关闭弹窗
   */
  const close = () => {
    visible.value = false
    onClose?.()
  }

  /**
   * 确认
   */
  const confirm = async () => {
    try {
      await onConfirm?.(formData.value, isEdit.value)
      close()
    } catch (error) {
      console.error('确认失败:', error)
      throw error
    }
  }

  /**
   * 取消
   */
  const cancel = () => {
    onCancel?.()
    close()
  }

  return {
    visible,
    title,
    formData,
    isEdit,
    open,
    openEdit,
    close,
    confirm,
    cancel
  }
}
