import { reactive, ref } from 'vue'

/**
 * 搜索组合式函数
 * 封装通用的搜索表单逻辑
 * 
 * @param {Function} onSearch - 搜索回调函数
 * @param {Object} defaultForm - 默认表单值
 * @returns {Object} 搜索相关的状态和方法
 */
export function useSearch(onSearch, defaultForm = {}) {
  // 搜索表单
  const searchForm = reactive({ ...defaultForm })

  // 是否正在搜索（有搜索条件）
  const isSearching = ref(false)

  /**
   * 执行搜索
   */
  const handleSearch = () => {
    isSearching.value = Object.values(searchForm).some(
      value => value !== null && value !== undefined && value !== ''
    )
    onSearch?.()
  }

  /**
   * 重置搜索
   */
  const handleReset = () => {
    Object.keys(searchForm).forEach(key => {
      searchForm[key] = defaultForm[key] ?? null
    })
    isSearching.value = false
    onSearch?.()
  }

  /**
   * 更新单个字段
   */
  const setField = (field, value) => {
    searchForm[field] = value
  }

  /**
   * 获取搜索参数（过滤空值）
   */
  const getSearchParams = () => {
    const params = {}
    Object.keys(searchForm).forEach(key => {
      const value = searchForm[key]
      if (value !== null && value !== undefined && value !== '') {
        params[key] = value
      }
    })
    return params
  }

  return {
    searchForm,
    isSearching,
    handleSearch,
    handleReset,
    setField,
    getSearchParams
  }
}
