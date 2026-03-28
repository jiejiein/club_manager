import { reactive, ref } from 'vue'

/**
 * 分页组合式函数
 * 封装通用的分页逻辑
 * 
 * @param {Function} fetchFn - 获取数据的异步函数
 * @param {Object} options - 配置选项
 * @returns {Object} 分页相关的状态和方法
 */
export function usePagination(fetchFn, options = {}) {
  const {
    defaultCurrent = 1,
    defaultSize = 10,
    immediate = true
  } = options

  // 加载状态
  const loading = ref(false)

  // 表格数据
  const tableData = ref([])

  // 选中的数据ID
  const selectedIds = ref([])

  // 分页配置
  const pagination = reactive({
    current: defaultCurrent,
    size: defaultSize,
    total: 0
  })

  /**
   * 加载数据
   */
  const loadData = async (params = {}) => {
    loading.value = true
    try {
      const res = await fetchFn({
        current: pagination.current,
        size: pagination.size,
        ...params
      })
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
      return res
    } catch (error) {
      console.error('加载数据失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 处理页码变化
   */
  const handleCurrentChange = (current) => {
    pagination.current = current
    return loadData()
  }

  /**
   * 处理每页条数变化
   */
  const handleSizeChange = (size) => {
    pagination.size = size
    pagination.current = 1
    return loadData()
  }

  /**
   * 处理选择变化
   */
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map(item => item.id)
  }

  /**
   * 刷新数据（回到第一页）
   */
  const refresh = () => {
    pagination.current = 1
    return loadData()
  }

  // 立即加载数据
  if (immediate && fetchFn) {
    loadData()
  }

  return {
    loading,
    tableData,
    selectedIds,
    pagination,
    loadData,
    handleCurrentChange,
    handleSizeChange,
    handleSelectionChange,
    refresh
  }
}
