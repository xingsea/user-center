import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/user/login'
    },
    {
      path: '/user/login',
      name: 'login',
      component: () => import('../views/user/LoginView.vue')
    },
    {
      path: '/user/register',
      name: 'register',
      component: () => import('../views/user/RegisterView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      redirect: '/admin/analysis',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'analysis',
          name: 'analysis',
          component: () => import('../views/admin/UserAnalysis.vue')
        },
        {
          path: 'management',
          name: 'management',
          component: () => import('../views/admin/UserManagement.vue')
        }
      ]
    }
  ],
})

// Navigation guard to check if user is authenticated
router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    try {
      const response = await import('../api/userController').then(module => module.getLoginUser())
      if (response.data?.code === 0) {
        next()
      } else {
        next(`/user/login?redirect=${to.fullPath}`)
      }
    } catch (error) {
      next(`/user/login?redirect=${to.fullPath}`)
    }
  } else {
    next()
  }
})

export default router
