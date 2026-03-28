import request from '@/utils/request'

export function getActivityPage(params) {
  return request({
    url: '/activity/page',
    method: 'get',
    params
  })
}

export function getActivityById(id) {
  return request({
    url: `/activity/${id}`,
    method: 'get'
  })
}

export function createActivity(data, clubId) {
  return request({
    url: '/activity',
    method: 'post',
    data,
    params: { clubId }
  })
}

export function updateActivity(data) {
  return request({
    url: '/activity',
    method: 'put',
    data
  })
}

export function deleteActivity(id) {
  return request({
    url: `/activity/${id}`,
    method: 'delete'
  })
}

export function auditActivity(id, status, rejectReason) {
  return request({
    url: `/activity/audit/${id}`,
    method: 'put',
    params: { status, rejectReason }
  })
}

export function signUpActivity(activityId) {
  return request({
    url: `/activity/sign-up/${activityId}`,
    method: 'post'
  })
}

export function cancelSignUp(activityId) {
  return request({
    url: `/activity/sign-up/${activityId}`,
    method: 'delete'
  })
}

export function auditSignUp(signUpId, status) {
  return request({
    url: `/activity/sign-up/audit/${signUpId}`,
    method: 'put',
    params: { status }
  })
}

export function checkIn(signUpId) {
  return request({
    url: `/activity/check-in/${signUpId}`,
    method: 'put'
  })
}

export function getSignUpList(activityId, params) {
  return request({
    url: `/activity/sign-up/list/${activityId}`,
    method: 'get',
    params
  })
}

export function getMyActivities() {
  return request({
    url: '/activity/my',
    method: 'get'
  })
}

export function getAvailableActivities() {
  return request({
    url: '/activity/available',
    method: 'get'
  })
}

export function submitRating(signUpId, rating, comment) {
  return request({
    url: `/activity/rating/${signUpId}`,
    method: 'put',
    params: { rating, comment }
  })
}
