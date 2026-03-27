import request from '@/utils/request'

export function getSystemLogPage(params) {
  return request({
    url: '/system-log/page',
    method: 'get',
    params
  })
}

export function deleteSystemLog(id) {
  return request({
    url: `/system-log/${id}`,
    method: 'delete'
  })
}
