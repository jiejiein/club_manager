import request from '@/utils/request'

export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/user/status/${id}`,
    method: 'put',
    params: { status }
  })
}

export function updateUserRole(id, role) {
  return request({
    url: `/user/role/${id}`,
    method: 'put',
    params: { role }
  })
}

export function resetPassword(id, newPassword) {
  return request({
    url: `/user/reset-password/${id}`,
    method: 'put',
    params: { newPassword }
  })
}

export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/user/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}
