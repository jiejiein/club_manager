import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', roles: [1] }
      },
      {
        path: 'club',
        name: 'Club',
        component: () => import('@/views/club/index.vue'),
        meta: { title: '社团管理' }
      },
      {
        path: 'club-type',
        name: 'ClubType',
        component: () => import('@/views/club/type.vue'),
        meta: { title: '社团类型', roles: [1] }
      },
      {
        path: 'club-member/:id',
        name: 'ClubMember',
        component: () => import('@/views/club/member.vue'),
        meta: { title: '成员管理' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/activity/index.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'activity-sign/:id',
        name: 'ActivitySign',
        component: () => import('@/views/activity/sign.vue'),
        meta: { title: '报名管理' }
      },
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/apply/index.vue'),
        meta: { title: '申请管理' }
      },
      {
        path: 'notification',
        name: 'Notification',
        component: () => import('@/views/notification/index.vue'),
        meta: { title: '通知管理' }
      },
      {
        path: 'payment',
        name: 'Payment',
        component: () => import('@/views/payment/index.vue'),
        meta: { title: '费用管理' }
      },
      {
        path: 'president-transfer',
        name: 'PresidentTransfer',
        component: () => import('@/views/president-transfer/index.vue'),
        meta: { title: '社长转移申请', roles: [1] }
      },
      {
        path: 'system-log',
        name: 'SystemLog',
        component: () => import('@/views/system-log/index.vue'),
        meta: { title: '系统日志', roles: [1] }
      },
      {
        path: 'payment-record/:id',
        name: 'PaymentRecord',
        component: () => import('@/views/payment/record.vue'),
        meta: { title: '缴费记录' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'my-club',
        name: 'MyClub',
        component: () => import('@/views/my/club.vue'),
        meta: { title: '我的社团' }
      },
      {
        path: 'my-activity',
        name: 'MyActivity',
        component: () => import('@/views/my/activity.vue'),
        meta: { title: '我的活动' }
      },
      {
        path: 'my-payment',
        name: 'MyPayment',
        component: () => import('@/views/my/payment.vue'),
        meta: { title: '我的缴费' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 社团管理平台` : '社团管理平台'
  
  const userStore = useUserStore()
  const token = userStore.token

  if (!token && to.path !== '/login' && to.path !== '/register') {
    return next('/login')
  }

  if (token && !userStore.userInfo) {
    await userStore.getUserInfo()
  }

  if (to.meta.roles && userStore.userInfo) {
    if (!to.meta.roles.includes(userStore.userInfo.role)) {
      return next('/dashboard')
    }
  }

  next()
})

export default router
