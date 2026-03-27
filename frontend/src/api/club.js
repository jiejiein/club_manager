import request from '@/utils/request'

export function getClubPage(params) {
  return request({
    url: '/club/page',
    method: 'get',
    params
  })
}

export function getClubById(id) {
  return request({
    url: `/club/${id}`,
    method: 'get'
  })
}

export function createClub(data) {
  return request({
    url: '/club',
    method: 'post',
    data
  })
}

export function updateClub(data) {
  return request({
    url: '/club',
    method: 'put',
    data
  })
}

export function deleteClub(id) {
  return request({
    url: `/club/${id}`,
    method: 'delete'
  })
}

export function auditClub(id, status, rejectReason) {
  return request({
    url: `/club/audit/${id}`,
    method: 'put',
    params: { status, rejectReason }
  })
}

export function getAllClubTypes() {
  return request({
    url: '/club/types',
    method: 'get'
  })
}

export function createClubType(data) {
  return request({
    url: '/club/type',
    method: 'post',
    params: data
  })
}

export function updateClubType(id, data) {
  return request({
    url: `/club/type/${id}`,
    method: 'put',
    params: { id, ...data }
  })
}

export function deleteClubType(id) {
  return request({
    url: `/club/type/${id}`,
    method: 'delete'
  })
}

export function getMyClubs() {
  return request({
    url: '/club/my',
    method: 'get'
  })
}

export function getActiveClubs() {
  return request({
    url: '/club/active',
    method: 'get'
  })
}

export function quitClub(clubId) {
  return request({
    url: `/club/quit/${clubId}`,
    method: 'delete'
  })
}

export function transferPresident(clubId, newPresidentId) {
  return request({
    url: `/club/transfer/${clubId}`,
    method: 'put',
    params: { newPresidentId }
  })
}

export function getTransferPage(params) {
  return request({
    url: '/president-transfer/page',
    method: 'get',
    params
  })
}

export function createTransfer(clubId, newPresidentId, reason) {
  return request({
    url: '/president-transfer',
    method: 'post',
    params: { clubId, newPresidentId, reason }
  })
}

export function auditTransfer(id, status, rejectReason) {
  return request({
    url: `/president-transfer/audit/${id}`,
    method: 'put',
    params: { status, rejectReason }
  })
}
