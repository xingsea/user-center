<template>
  <div class="register-container">
    <div class="register-background">
      <div class="register-shape"></div>
      <div class="register-shape"></div>
    </div>
    <div class="register-content">
      <div class="register-header">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg" class="logo-icon" alt="logo" />
        <h2 class="register-title">用户中心</h2>
      </div>
      <div class="register-welcome">创建新账户</div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <el-form-item prop="userAccount">
          <el-input 
            v-model="registerForm.userAccount" 
            placeholder="请输入账号" 
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input
            v-model="registerForm.userPassword"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="checkPassword">
          <el-input
            v-model="registerForm.checkPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Key"
            size="large"
            show-password
          />
        </el-form-item>
        
        <div class="register-agreement">
          <el-checkbox v-model="agreement">我已阅读并同意<el-button type="text" @click="showTerms">服务条款</el-button></el-checkbox>
        </div>
        
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleRegister"
            class="register-button"
            size="large"
            round
            :disabled="!agreement"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <span>已有账号?</span>
        <el-button type="text" @click="goToLogin" class="login-link">立即登录</el-button>
      </div>
      
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { userRegister } from '../../api/userController'
import { User, Lock, Key } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref<FormInstance>()
const agreement = ref(false)

const registerForm = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
})

const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.checkPassword !== '') {
      if (!registerFormRef.value) return
      registerFormRef.value.validateField('checkPassword')
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.userPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = reactive<FormRules>({
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, message: '账号长度不能小于4位', trigger: 'blur' }
  ],
  userPassword: [
    { validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  checkPassword: [{ validator: validatePass2, trigger: 'blur' }]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  if (!agreement.value) {
    ElMessage.warning('请先阅读并同意服务条款')
    return
  }
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const res = await userRegister(registerForm)
        if (res.data?.code === 0) {
          ElMessage({
            message: '注册成功，请登录',
            type: 'success',
            duration: 2000
          })
          router.push('/user/login')
        } else {
          ElMessage.error(res.data?.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败', error)
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/user/login')
}

const showTerms = () => {
  ElMessageBox.alert(
    '欢迎使用用户中心服务！在使用我们的服务之前，请您仔细阅读以下条款。使用我们的服务即表示您同意这些条款。我们致力于保护您的个人信息安全，并提供优质的用户体验。',
    '服务条款',
    {
      confirmButtonText: '我已阅读',
      callback: () => {
        agreement.value = true
      }
    }
  )
}
</script>

<style scoped>
.register-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  overflow: hidden;
}

.register-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.register-shape {
  position: absolute;
  border-radius: 50%;
}

.register-shape:first-child {
  background: linear-gradient(#ff512f, #f09819);
  width: 300px;
  height: 300px;
  top: -100px;
  left: -50px;
  opacity: 0.4;
  animation: float 6s ease-in-out infinite;
}

.register-shape:last-child {
  background: linear-gradient(to right, #1845ad, #23a2f6);
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
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

.register-content {
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

.register-header {
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

.register-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--heading-color);
  margin: 0;
}

.register-welcome {
  font-size: 18px;
  color: var(--text-color);
  text-align: center;
  margin-bottom: 30px;
}

.register-form {
  margin-bottom: 20px;
}

.register-agreement {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  border: none;
  margin-top: 10px;
  transition: transform 0.3s;
}

.register-button:hover:not([disabled]) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: var(--text-color);
}

.login-link {
  font-weight: 600;
  margin-left: 5px;
}

.register-tips {
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-content {
    width: 90%;
    padding: 30px;
  }
}
</style> 