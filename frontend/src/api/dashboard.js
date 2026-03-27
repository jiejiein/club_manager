import request from '@/utils/request'

export function getAdminDashboard() {
  return request({
    url: '/dashboard/admin',
    method: 'get'
  })
}

export function getPresidentDashboard(clubId) {
  return request({
    url: `/dashboard/president/${clubId}`,
    method: 'get'
  })
}

export function getMemberStats(clubId) {
  return request({
    url: `/dashboard/stats/member/${clubId}`,
    method: 'get'
  })
}

export function getActivityStats(clubId) {
  return request({
    url: `/dashboard/stats/activity/${clubId}`,
    method: 'get'
  })
}

export function getPaymentStats(clubId) {
  return request({
    url: `/dashboard/stats/payment/${clubId}`,
    method: 'get'
  })
}
