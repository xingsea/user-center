<template>
  <div class="user-management">
    <h2>用户管理</h2>
    
    <div class="search-container">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="账号">
          <el-input v-model="queryParams.userAccount" placeholder="请输入账号" clearable />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.userRole" placeholder="请选择角色" clearable>
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="text" @click="showAdvanced = !showAdvanced">
            {{ showAdvanced ? '收起' : '高级搜索' }}
            <el-icon>
              <component :is="showAdvanced ? 'ArrowUp' : 'ArrowDown'" />
            </el-icon>
          </el-button>
        </el-form-item>
        
        <template v-if="showAdvanced">
          <div class="advanced-search">
            <el-divider content-position="left">高级搜索</el-divider>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="排序字段">
                  <el-select v-model="queryParams.sortField" placeholder="请选择排序字段">
                    <el-option label="创建时间" value="createTime" />
                    <el-option label="ID" value="id" />
                    <el-option label="账号" value="userAccount" />
                    <el-option label="用户名" value="userName" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="排序方式">
                  <el-select v-model="queryParams.sortOrder" placeholder="请选择排序方式">
                    <el-option label="升序" value="asc" />
                    <el-option label="降序" value="desc" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="每页显示">
                  <el-select v-model="queryParams.pageSize" placeholder="请选择每页显示条数">
                    <el-option label="10条/页" :value="10" />
                    <el-option label="20条/页" :value="20" />
                    <el-option label="50条/页" :value="50" />
                    <el-option label="100条/页" :value="100" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </template>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
        <div class="view-switch">
          <el-radio-group v-model="viewType" size="small">
            <el-radio-button label="table">表格视图</el-radio-button>
            <el-radio-button label="card">卡片视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      
      <!-- 表格视图 -->
      <el-table
        v-if="viewType === 'table'"
        :data="userList"
        style="width: 100%"
        v-loading="loading"
        border
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" sortable="custom" />
        <el-table-column prop="userAccount" label="账号" width="120" sortable="custom" />
        <el-table-column prop="userName" label="用户名" width="120" sortable="custom" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.userAvatar">
              {{ scope.row.userName?.charAt(0) || scope.row.userAccount?.charAt(0) || 'U' }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="userRole" label="角色" width="100" sortable="custom">
          <template #default="scope">
            <el-tag :type="scope.row.userRole === 'admin' ? 'danger' : 'success'">
              {{ scope.row.userRole === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable="custom" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <el-button type="info" link class="edit-btn" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="info" link class="view-btn" @click="handleView(scope.row)">查看</el-button>
            <el-button type="danger" link class="delete-btn" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 卡片视图 -->
      <div v-else class="card-view" v-loading="loading">
        <el-empty v-if="userList.length === 0" description="暂无数据" />
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="user in userList" :key="user.id" class="card-item-col">
            <el-card class="user-card" shadow="hover">
              <div class="user-card-header">
                <el-avatar :size="60" :src="user.userAvatar">
                  {{ user.userName?.charAt(0) || user.userAccount?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-card-info">
                  <div class="user-card-name">{{ user.userName || '未命名' }}</div>
                  <div class="user-card-account">{{ user.userAccount }}</div>
                  <el-tag :type="user.userRole === 'admin' ? 'danger' : 'success'" size="small">
                    {{ user.userRole === 'admin' ? '管理员' : '普通用户' }}
                  </el-tag>
                </div>
              </div>
              <div class="user-card-content">
                <div class="user-card-item">
                  <span class="label">邮箱：</span>
                  <span>{{ user.email || '未设置' }}</span>
                </div>
                <div class="user-card-item">
                  <span class="label">手机：</span>
                  <span>{{ user.phone || '未设置' }}</span>
                </div>
                <div class="user-card-item">
                  <span class="label">创建时间：</span>
                  <span>{{ user.createTime || '未知' }}</span>
                </div>
              </div>
              <div class="user-card-actions">
                <el-button type="info" link class="edit-btn" @click="handleEdit(user)">编辑</el-button>
                <el-button type="info" link class="view-btn" @click="handleView(user)">查看</el-button>
                <el-button type="danger" link class="delete-btn" @click="handleDelete(user)">删除</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div class="pagination-container">
        <div class="pagination-options">
          <span class="pagination-label">每页显示:</span>
          <el-select 
            v-model="queryParams.pageSize" 
            size="small" 
            style="width: 100px; margin-right: 16px;"
            @change="handleSizeChange"
          >
            <el-option 
              v-for="size in pageSizes" 
              :key="size" 
              :label="`${size}条/页`" 
              :value="size" 
            />
          </el-select>
          
          <span class="pagination-label">跳转到:</span>
          <el-input-number
            v-model="queryParams.current"
            :min="1"
            :max="maxPage"
            size="small"
            style="width: 100px; margin-right: 16px;"
            @change="handleCurrentChange"
          />
        </div>
        
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="pageSizes"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
          :disabled="loading"
        />
        <div class="pagination-info" v-if="total > 0">
          {{ paginationInfo }}
        </div>
      </div>
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="80px"
      >
        <el-form-item label="账号" prop="userAccount" v-if="dialogType === 'add'">
          <el-input v-model="userForm.userAccount" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="头像" prop="userAvatar">
          <el-input v-model="userForm.userAvatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="userRole">
          <el-select v-model="userForm.userRole" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="userProfile">
          <el-input
            v-model="userForm.userProfile"
            type="textarea"
            placeholder="请输入简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog
      title="用户详情"
      v-model="viewDialogVisible"
      width="500px"
      append-to-body
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ viewUser.id }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ viewUser.userAccount }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewUser.userName }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-avatar :size="60" :src="viewUser.userAvatar">
            {{ viewUser.userName?.charAt(0) || viewUser.userAccount?.charAt(0) || 'U' }}
          </el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ viewUser.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="viewUser.userRole === 'admin' ? 'danger' : 'success'">
            {{ viewUser.userRole === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="简介">{{ viewUser.userProfile }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { listUserVoByPage, addUser, updateUser, deleteUserById, getUserVoById } from '../../api/userController'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'

const loading = ref(false)
const userList = ref<API.UserVO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref<'add' | 'edit'>('add')
const userFormRef = ref<FormInstance>()
const showAdvanced = ref(false)
const viewType = ref<'table' | 'card'>('table')
const pageSizes = [10, 20, 50, 100]

// 查询参数
const queryParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'desc',
  userAccount: '',
  userName: '',
  userRole: ''
})

// 计算最大页码
const maxPage = computed(() => {
  return Math.max(1, Math.ceil(total.value / (queryParams.pageSize || 10)))
})

// 表单数据
const userForm = reactive<API.UserAddRequest & API.UserUpdateRequest>({
  id: undefined,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  phone: '',
  email: '',
  userRole: 'user'
})

// 查看用户详情
const viewUser = ref<API.UserVO>({})

// 表单验证规则
const userRules = reactive<FormRules>({
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, message: '账号长度不能小于4位', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  userRole: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
})

// 分页信息计算属性
const paginationInfo = computed(() => {
  if (!queryParams.current || !queryParams.pageSize || total.value === 0) return ''
  const start = (queryParams.current - 1) * queryParams.pageSize + 1
  const end = Math.min(queryParams.current * queryParams.pageSize, total.value)
  return `显示 ${start} 到 ${end} 条，共 ${total.value} 条记录`
})

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    // 确保参数正确
    const params = {
      ...queryParams,
      sortField: queryParams.sortField || 'createTime',
      sortOrder: queryParams.sortOrder || 'desc'
    }
    
    const res = await listUserVoByPage(params)
    if (res.data?.code === 0 && res.data.data) {
      userList.value = res.data.data.records || []
      total.value = res.data.data.total || 0
      
      // 如果当前页没有数据且不是第一页，则回到上一页
      if (userList.value.length === 0 && queryParams.current && queryParams.current > 1) {
        queryParams.current--
        getUserList()
      }
    } else {
      userList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
    ElMessage.error('获取用户列表失败')
    userList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleQuery = () => {
  if (queryParams.current) queryParams.current = 1
  getUserList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.userAccount = ''
  queryParams.userName = ''
  queryParams.userRole = ''
  if (queryParams.current) queryParams.current = 1
  getUserList()
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  if (queryParams.pageSize) queryParams.pageSize = val
  if (queryParams.current) queryParams.current = 1  // 切换每页显示数量时，重置为第一页
  getUserList()
}

// 分页页码改变
const handleCurrentChange = (val: number) => {
  if (queryParams.current) queryParams.current = val
  getUserList()
}

// 表格排序变化
const handleSortChange = (column: { prop: string, order: string }) => {
  queryParams.sortField = column.prop
  queryParams.sortOrder = column.order === 'ascending' ? 'asc' : 'desc'
  getUserList()
}

// 新增按钮点击事件
const handleAdd = () => {
  resetForm()
  dialogType.value = 'add'
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

// 编辑按钮点击事件
const handleEdit = (row: API.UserVO) => {
  resetForm()
  dialogType.value = 'edit'
  dialogTitle.value = '编辑用户'
  userForm.id = row.id
  userForm.userName = row.userName || ''
  userForm.userAvatar = row.userAvatar || ''
  userForm.phone = row.phone || ''
  userForm.email = row.email || ''
  userForm.userRole = row.userRole || 'user'
  userForm.userProfile = row.userProfile || ''
  dialogVisible.value = true
}

// 查看按钮点击事件
const handleView = async (row: API.UserVO) => {
  try {
    loading.value = true
    if (row.id) {
      const res = await getUserVoById({ id: row.id })
      if (res.data?.code === 0 && res.data.data) {
        viewUser.value = res.data.data
        viewDialogVisible.value = true
      }
    }
  } catch (error) {
    console.error('获取用户详情失败', error)
    ElMessage.error('获取用户详情失败')
  } finally {
    loading.value = false
  }
}

// 删除按钮点击事件
const handleDelete = (row: API.UserVO) => {
  ElMessageBox.confirm(`确认删除用户 ${row.userName || row.userAccount} 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      loading.value = true
      if (row.id) {
        const res = await deleteUserById({ id: row.id })
        if (res.data?.code === 0) {
          ElMessage.success('删除成功')
          getUserList()
        } else {
          ElMessage.error(res.data?.message || '删除失败')
        }
      }
    } catch (error) {
      console.error('删除用户失败', error)
      ElMessage.error('删除用户失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  userForm.id = undefined
  userForm.userAccount = ''
  userForm.userName = ''
  userForm.userAvatar = ''
  userForm.userProfile = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.userRole = 'user'
}

// 提交表单
const submitForm = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        if (dialogType.value === 'add') {
          // 新增用户
          const res = await addUser(userForm)
          if (res.data?.code === 0) {
            ElMessage.success('新增成功')
            dialogVisible.value = false
            getUserList()
          } else {
            ElMessage.error(res.data?.message || '新增失败')
          }
        } else {
          // 更新用户
          const res = await updateUser(userForm)
          if (res.data?.code === 0) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            getUserList()
          } else {
            ElMessage.error(res.data?.message || '更新失败')
          }
        }
      } catch (error) {
        console.error('提交表单失败', error)
        ElMessage.error('提交表单失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-management {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.user-management h2 {
  margin-top: 0;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.search-container {
  margin-bottom: 24px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.advanced-search {
  width: 100%;
  margin-top: 16px;
  padding-top: 8px;
}

.table-container {
  background-color: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.table-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.view-switch {
  margin-left: auto;
}

.card-view {
  margin-bottom: 24px;
}

.card-item-col {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.user-card {
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.user-card-header {
  display: flex;
  margin-bottom: 16px;
  padding: 8px 0;
}

.user-card-info {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-card-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #303133;
}

.user-card-account {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.user-card-content {
  margin-bottom: 16px;
}

.user-card-item {
  margin-bottom: 10px;
  font-size: 14px;
  display: flex;
}

.user-card-item .label {
  color: #909399;
  min-width: 60px;
}

.user-card-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  padding-top: 14px;
  margin-top: 8px;
  gap: 12px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.pagination-options {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.pagination-label {
  margin-right: 8px;
  color: #606266;
  font-size: 14px;
}

.pagination-info {
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-button--primary) {
  background-color: #1890ff;
  border-color: #1890ff;
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

:deep(.el-pagination) {
  padding: 0;
  margin-top: 8px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
}

:deep(.el-avatar) {
  background-color: #1890ff;
  color: #fff;
}

.edit-btn {
  color: #1890ff !important;
}

.view-btn {
  color: #1890ff !important;
}

.delete-btn {
  color: #1890ff !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  border-color: #d9d9d9;
  background-color: #ffffff;
  color: #595959;
}

.cancel-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.submit-btn {
  background-color: #1890ff;
  border-color: #1890ff;
  color: #ffffff;
}

.submit-btn:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
  color: #ffffff;
}
</style> 