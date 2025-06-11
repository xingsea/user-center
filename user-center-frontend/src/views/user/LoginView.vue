<template>
  <div class="login-container">
    <div class="login-background">
      <div class="login-shape"></div>
      <div class="login-shape"></div>
    </div>
    <div class="login-content">
      <div class="login-header">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg" class="logo-icon" alt="logo" />
        <h2 class="login-title">用户中心</h2>
      </div>
      <div class="login-welcome">欢迎回来</div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="userAccount">
          <el-input 
            v-model="loginForm.userAccount" 
            placeholder="请输入账号" 
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input
            v-model="loginForm.userPassword"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
            show-password
          />
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button type="text" @click="forgotPassword">忘记密码?</el-button>
        </div>
        
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleLogin"
            class="login-button"
            size="large"
            round
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>还没有账号?</span>
        <el-button type="text" @click="goToRegister" class="register-link">立即注册</el-button>
      </div>
      
      <div class="login-divider">
        <span>其他登录方式</span>
      </div>
      
      <div class="social-login">
        <el-button circle class="social-icon">
          <el-icon><ChatDotRound /></el-icon>
        </el-button>
        <el-button circle class="social-icon">
          <el-icon><Promotion /></el-icon>
        </el-button>
        <el-button circle class="social-icon">
          <el-icon><Connection /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { userLogin } from '../../api/userController'
import { User, Lock, ChatDotRound, Promotion, Connection } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const loginFormRef = ref<FormInstance>()
const rememberMe = ref(false)

const loginForm = reactive({
  userAccount: '',
  userPassword: ''
})

const loginRules = reactive<FormRules>({
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, message: '账号长度不能小于4位', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const res = await userLogin(loginForm)
        if (res.data?.code === 0) {
          ElMessage({
            message: '登录成功',
            type: 'success',
            duration: 2000
          })
          // 如果有重定向地址，则跳转到重定向地址
          const redirectUrl = route.query.redirect as string
          router.push(redirectUrl || '/admin')
        } else {
          ElMessage.error(res.data?.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败', error)
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/user/register')
}

const forgotPassword = () => {
  ElMessage.info('忘记密码功能开发中...')
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.login-shape {
  position: absolute;
  border-radius: 50%;
}

.login-shape:first-child {
  background: linear-gradient(#1845ad, #23a2f6);
  width: 300px;
  height: 300px;
  top: -100px;
  right: -50px;
  opacity: 0.4;
  animation: float 6s ease-in-out infinite;
}

.login-shape:last-child {
  background: linear-gradient(to right, #ff512f, #f09819);
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  opacity: 0.4;
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes float {
  0% {
    transform: translatey(0px);
  }
  50% {
    transform: translatey(-20px);
  }
  100% {
    transform: translatey(0px);
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  animation: fadeIn 1s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--heading-color);
  margin: 0;
}

.login-welcome {
  font-size: 18px;
  color: var(--text-color);
  text-align: center;
  margin-bottom: 30px;
}

.login-form {
  margin-bottom: 20px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  border: none;
  margin-top: 10px;
  transition: transform 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: var(--text-color);
}

.register-link {
  font-weight: 600;
  margin-left: 5px;
}

.login-divider {
  display: flex;
  align-items: center;
  margin: 30px 0 20px;
  color: var(--text-color-secondary);
  font-size: 14px;
}

.login-divider::before,
.login-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border-color);
}

.login-divider span {
  padding: 0 15px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-icon {
  font-size: 20px;
  transition: all 0.3s;
}

.social-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-content {
    width: 90%;
    padding: 30px;
  }
}
</style> 