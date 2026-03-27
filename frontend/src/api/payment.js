import request from '@/utils/request'

export function getPaymentPage(params) {
  return request({
    url: '/payment/page',
    method: 'get',
    params
  })
}

export function getPaymentById(id) {
  return request({
    url: `/payment/${id}`,
    method: 'get'
  })
}

export function createPayment(data) {
  return request({
    url: '/payment',
    method: 'post',
    data
  })
}

export function updatePayment(data) {
  return request({
    url: '/payment',
    method: 'put',
    data
  })
}

export function deletePayment(id) {
  return request({
    url: `/payment/${id}`,
    method: 'delete'
  })
}

export function pay(paymentId) {
  return request({
    url: `/payment/pay/${paymentId}`,
    method: 'post'
  })
}

export function getPaymentRecords(paymentId, params) {
  return request({
    url: `/payment/records/${paymentId}`,
    method: 'get',
    params
  })
}

export function getMyPayments() {
  return request({
    url: '/payment/my',
    method: 'get'
  })
}

export function getMyPaymentRecords() {
  return request({
    url: '/payment/my-records',
    method: 'get'
  })
}
