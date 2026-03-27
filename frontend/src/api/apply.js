import request from '@/utils/request'

export function getApplyPage(params) {
  return request({
    url: '/apply/page',
    method: 'get',
    params
  })
}

export function submitApply(clubId, reason) {
  return request({
    url: `/apply/${clubId}`,
    method: 'post',
    params: { reason }
  })
}

export function auditApply(applyId, status, rejectReason) {
  return request({
    url: `/apply/audit/${applyId}`,
    method: 'put',
    params: { status, rejectReason }
  })
}

export function batchAudit(applyIds, status, rejectReason) {
  return request({
    url: '/apply/batch-audit',
    method: 'put',
    data: applyIds,
    params: { status, rejectReason }
  })
}

export function getMyApplies() {
  return request({
    url: '/apply/my',
    method: 'get'
  })
}

export function getClubMembers(clubId, params) {
  return request({
    url: `/apply/members/${clubId}`,
    method: 'get',
    params
  })
}

export function removeMember(clubId, userId) {
  return request({
    url: `/apply/member/${clubId}/${userId}`,
    method: 'delete'
  })
}

export function batchRemoveMembers(clubId, userIds) {
  return request({
    url: `/apply/members/${clubId}`,
    method: 'delete',
    data: userIds
  })
}

export function cancelApply(applyId) {
  return request({
    url: `/apply/${applyId}`,
    method: 'delete'
  })
}
