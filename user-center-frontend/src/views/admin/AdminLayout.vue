<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px" class="sidebar">
        <div class="logo-container">
          <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg" class="logo-icon" />
          <h2 class="logo-title">用户中心</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          router
          background-color="var(--sidebar-bg)"
          text-color="var(--sidebar-text)"
          active-text-color="var(--sidebar-active)"
        >
          <el-menu-item index="/admin/analysis" class="menu-item">
            <el-icon><DataAnalysis /></el-icon>
            <span>用户分析统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/management" class="menu-item">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
        <div class="sidebar-footer">
          <span>© {{ currentYear }} 用户中心</span>
        </div>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleSidebar"><Fold v-if="!sidebarCollapsed" /><Expand v-else /></el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-tooltip content="全屏" placement="bottom">
              <el-icon class="header-icon" @click="toggleFullScreen"><FullScreen /></el-icon>
            </el-tooltip>
            <el-badge  class="notification-badge">
              <el-tooltip content="通知" placement="bottom">
                <el-icon class="header-icon" @click="showNotification = true"><Bell /></el-icon>
              </el-tooltip>
            </el-badge>
            <div class="user-info">
              <span class="welcome-text">欢迎，{{ userInfo.userName || userInfo.userAccount }}</span>
              <el-dropdown @command="handleCommand" trigger="click">
                <span class="el-dropdown-link">
                  <el-avatar :size="32" :src="userInfo.userAvatar || defaultAvatar">
                    {{ userInfo.userName?.charAt(0) || userInfo.userAccount?.charAt(0) || 'U' }}
                  </el-avatar>
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><UserFilled /></el-icon>个人信息
                    </el-dropdown-item>
                    <el-dropdown-item command="settings">
                      <el-icon><Setting /></el-icon>设置
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main class="main-content">
          <router-view v-if="userInfoLoaded"></router-view>
          <div v-else class="loading-container">
            <el-skeleton :rows="10" animated />
          </div>
        </el-main>
        <el-footer class="footer">
          用户中心 &copy; {{ currentYear }} | Powered by Vue & Element Plus
        </el-footer>
      </el-container>
    </el-container>
    
    <!-- 通知抽屉 -->
    <el-drawer
      v-model="showNotification"
      title="系统通知"
      direction="rtl"
      size="300px"
    >
      <div v-if="notifications.length > 0">
        <div v-for="(item, index) in notifications" :key="index" class="notification-item">
          <div class="notification-title">
            <el-icon class="notification-icon" :class="item.type"><component :is="getNotificationIcon(item.type)" /></el-icon>
            <span>{{ item.title }}</span>
          </div>
          <div class="notification-content">{{ item.content }}</div>
          <div class="notification-time">{{ item.time }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无通知" />
    </el-drawer>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLoginUser, userLogout } from '../../api/userController'
import { 
  DataAnalysis, User, Fold, Expand, FullScreen, 
  Bell, ArrowDown, UserFilled, Setting, SwitchButton,
  Warning, InfoFilled, SuccessFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = ref<API.UserLoginVO>({})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const userInfoLoaded = ref(false)
const sidebarCollapsed = ref(false)
const showNotification = ref(false)
const hasNotification = ref(true)
const currentYear = new Date().getFullYear()

// 模拟通知数据
const notifications = ref([
  { 
    type: 'info', 
    title: '系统更新', 
    content: '系统将于今晚22:00-23:00进行例行维护，请提前做好准备。',
    time: '2023-06-15 10:30'
  },
  { 
    type: 'success', 
    title: '操作成功', 
    content: '您已成功导入用户数据，共导入1500条记录。',
    time: '2023-06-14 16:45'
  },
  { 
    type: 'warning', 
    title: '安全提醒', 
    content: '您的账号今日有5次异常登录尝试，请检查账号安全。',
    time: '2023-06-14 09:20'
  }
])

// 获取通知图标
const getNotificationIcon = (type: string) => {
  switch(type) {
    case 'warning': return Warning
    case 'success': return SuccessFilled
    default: return InfoFilled
  }
}

// 获取当前路由名称
const currentRoute = computed(() => {
  if (route.path.includes('analysis')) return '用户分析统计'
  if (route.path.includes('management')) return '用户管理'
  return '首页'
})

// 获取当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 切换全屏
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 获取当前登录用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getLoginUser()
    if (res.data?.code === 0 && res.data.data) {
      userInfo.value = res.data.data
      userInfoLoaded.value = true
    } else {
      router.push('/user/login')
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
    router.push('/user/login')
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确认退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        const res = await userLogout()
        if (res.data?.code === 0) {
          ElMessage.success('退出登录成功')
          router.push('/user/login')
        }
      } catch (error) {
        console.error('退出登录失败', error)
      }
    }).catch(() => {})
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中...')
  } else if (command === 'settings') {
    ElMessage.info('设置功能开发中...')
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.sidebar {
  background-color: var(--sidebar-bg);
  color: white;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
  overflow: hidden;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.logo-title {
  margin: 0;
  font-size: 18px;
  color: white;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
  flex: 1;
}

.menu-item {
  margin: 4px 0;
}

.sidebar-footer {
  padding: 16px;
  text-align: center;
  color: var(--sidebar-text);
  font-size: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  background-color: white;
  color: #333;
  line-height: 60px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-icon {
  font-size: 18px;
  cursor: pointer;
  padding: 0 12px;
  color: #666;
}

.header-icon:hover {
  color: var(--primary-color);
}

.notification-badge {
  margin-right: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.welcome-text {
  margin-right: 15px;
  font-size: 14px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.main-content {
  background-color: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}

.loading-container {
  padding: 20px;
}

.footer {
  text-align: center;
  background-color: white;
  color: #666;
  font-size: 12px;
  line-height: 60px;
  border-top: 1px solid #eee;
}

.notification-item {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.notification-title {
  display: flex;
  align-items: center;
  font-weight: 500;
  margin-bottom: 8px;
}

.notification-icon {
  margin-right: 8px;
  font-size: 16px;
}

.notification-icon.info {
  color: var(--primary-color);
}

.notification-icon.success {
  color: var(--success-color);
}

.notification-icon.warning {
  color: var(--warning-color);
}

.notification-content {
  color: var(--text-color);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.notification-time {
  color: var(--text-color-secondary);
  font-size: 12px;
}
</style> 