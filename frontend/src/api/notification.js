import request from '@/utils/request'

export function getNotificationPage(params) {
  return request({
    url: '/notification/page',
    method: 'get',
    params
  })
}

export function getNotificationById(id) {
  return request({
    url: `/notification/${id}`,
    method: 'get'
  })
}

export function createNotification(data) {
  return request({
    url: '/notification',
    method: 'post',
    data
  })
}

export function updateNotification(data) {
  return request({
    url: '/notification',
    method: 'put',
    data
  })
}

export function deleteNotification(id) {
  return request({
    url: `/notification/${id}`,
    method: 'delete'
  })
}

export function markAsRead(notificationId) {
  return request({
    url: `/notification/read/${notificationId}`,
    method: 'put'
  })
}

export function markAllAsRead() {
  return request({
    url: '/notification/read-all',
    method: 'put'
  })
}

export function getMyNotifications() {
  return request({
    url: '/notification/my',
    method: 'get'
  })
}

export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}
