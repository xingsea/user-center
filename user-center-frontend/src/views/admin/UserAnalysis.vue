<template>
  <div class="user-analysis page-container">
    <div class="page-title">
      <el-icon><DataAnalysis /></el-icon>
      <span>用户分析统计</span>
    </div>
    
    <div class="stats-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="24" :md="8" :lg="8">
          <el-card class="stats-card custom-card" shadow="hover">
            <div class="stats-card-content">
              <div class="stats-card-title">总用户数</div>
              <div class="stats-card-value">
                <count-to :start-val="0" :end-val="totalUsers" :duration="2000" />
              </div>
              <div class="stats-card-info">
                <span class="stats-trend positive">
                  <el-icon><ArrowUp /></el-icon>12.5%
                </span>
                <span class="stats-period">较上月</span>
              </div>
            </div>
            <!-- <div class="stats-card-icon-container">
              <el-icon class="stats-card-icon"><User /></el-icon>
            </div> -->
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8" :lg="8">
          <el-card class="stats-card custom-card" shadow="hover">
            <div class="stats-card-content">
              <div class="stats-card-title">今日新增</div>
              <div class="stats-card-value">
                <count-to :start-val="0" :end-val="newUsers" :duration="2000" />
              </div>
              <div class="stats-card-info">
                <span class="stats-trend positive">
                  <el-icon><ArrowUp /></el-icon>5.2%
                </span>
                <span class="stats-period">较昨日</span>
              </div>
            </div>
            <!-- <div class="stats-card-icon-container">
              <el-icon class="stats-card-icon"><Plus /></el-icon>
            </div> -->
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8" :lg="8">
          <el-card class="stats-card custom-card" shadow="hover">
            <div class="stats-card-content">
              <div class="stats-card-title">活跃用户</div>
              <div class="stats-card-value">
                <count-to :start-val="0" :end-val="activeUsers" :duration="2000" />
              </div>
              <div class="stats-card-info">
                <span class="stats-trend negative">
                  <el-icon><ArrowDown /></el-icon>1.3%
                </span>
                <span class="stats-period">较上周</span>
              </div>
            </div>
            <!-- <div class="stats-card-icon-container">
              <el-icon class="stats-card-icon"><Star /></el-icon>
            </div> -->
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="charts-container">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card custom-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <div class="chart-title">用户增长趋势</div>
                <div class="chart-actions">
                  <el-radio-group v-model="growthTimeRange" size="small" @change="updateGrowthChart">
                    <el-radio-button label="week">周</el-radio-button>
                    <el-radio-button label="month">月</el-radio-button>
                    <el-radio-button label="year">年</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            <div ref="growthChartRef" class="chart-container" v-loading="chartLoading"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="chart-card custom-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <div class="chart-title">用户角色分布</div>
                <div class="chart-actions">
                  <el-button size="small" @click="refreshRoleChart">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </div>
            </template>
            <div ref="roleChartRef" class="chart-container" v-loading="roleChartLoading"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card class="recent-users custom-card" shadow="hover">
      <template #header>
        <div class="recent-users-header">
          <div class="recent-users-title">
            <el-icon><Timer /></el-icon>
            <span>最近注册用户</span>
          </div>
          <div class="recent-users-actions">
            <el-tooltip content="刷新数据" placement="top">
              <el-button type="primary" :icon="Refresh" circle size="small" @click="refreshData" :loading="loading" />
            </el-tooltip>
            <el-tooltip content="导出数据" placement="top">
              <el-button type="success" :icon="Download" circle size="small" @click="exportData" />
            </el-tooltip>
          </div>
        </div>
      </template>
      <el-table 
        :data="recentUsers" 
        style="width: 100%" 
        v-loading="loading"
        border
        stripe
        highlight-current-row
        :header-cell-style="{ background: '#f5f7fa', fontWeight: '600' }"
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userAccount" label="账号" width="120" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.userAvatar">
              {{ scope.row.userName?.charAt(0) || scope.row.userAccount?.charAt(0) || 'U' }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="userRole" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.userRole === 'admin' ? 'danger' : 'success'" effect="dark">
              {{ scope.row.userRole === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="viewUserDetail(scope.row)">
              <el-icon><View /></el-icon>查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 用户详情对话框 -->
    <el-dialog
      title="用户详情"
      v-model="userDetailVisible"
      width="500px"
      append-to-body
      destroy-on-close
      class="user-detail-dialog"
    >
      <div class="user-detail-header">
        <el-avatar :size="80" :src="currentUser.userAvatar">
          {{ currentUser.userName?.charAt(0) || currentUser.userAccount?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="user-detail-info">
          <h3>{{ currentUser.userName || '未命名' }}</h3>
          <p>{{ currentUser.userAccount }}</p>
          <el-tag :type="currentUser.userRole === 'admin' ? 'danger' : 'success'" effect="dark">
            {{ currentUser.userRole === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </div>
      </div>
      <el-divider />
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ currentUser.userProfile || '暂无简介' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, defineComponent, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { listUserVoByPage, getUserVoById } from '../../api/userController'
import * as echarts from 'echarts'
import { 
  User, Plus, Star, DataAnalysis, ArrowUp, ArrowDown, 
  PieChart, DataLine, Timer, Refresh, Download, View 
} from '@element-plus/icons-vue'

// 数字动画组件
const CountTo = defineComponent({
  props: {
    startVal: {
      type: Number,
      default: 0
    },
    endVal: {
      type: Number,
      default: 0
    },
    duration: {
      type: Number,
      default: 2000
    },
    decimals: {
      type: Number,
      default: 0
    },
    autoplay: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      displayValue: this.startVal,
      localStartVal: this.startVal,
      printVal: null as number | null,
      localDuration: this.duration,
      startTime: null as number | null,
      timestamp: null as number | null,
      remaining: null as number | null,
      rAF: null as number | null
    }
  },
  computed: {
    formattedValue(): string {
      return this.displayValue.toFixed(this.decimals)
    }
  },
  watch: {
    startVal() {
      if (this.autoplay) {
        this.start()
      }
    },
    endVal() {
      if (this.autoplay) {
        this.start()
      }
    }
  },
  mounted() {
    if (this.autoplay) {
      this.start()
    }
  },
  beforeUnmount() {
    this.clear()
  },
  methods: {
    start() {
      this.clear()
      this.localStartVal = this.startVal
      this.startTime = null
      this.localDuration = this.duration
      this.rAF = requestAnimationFrame(this.count)
    },
    count(timestamp: number) {
      if (!this.startTime) this.startTime = timestamp
      this.timestamp = timestamp
      const progress = timestamp - this.startTime
      this.remaining = this.localDuration - progress
      
      if (this.localDuration === 0) {
        this.displayValue = this.endVal
      } else {
        this.displayValue = this.localStartVal + (this.endVal - this.localStartVal) * (progress / this.localDuration)
      }
      
      if (progress < this.localDuration) {
        this.rAF = requestAnimationFrame(this.count)
      } else {
        this.displayValue = this.endVal
      }
    },
    clear() {
      if (this.rAF) {
        cancelAnimationFrame(this.rAF)
        this.rAF = null
      }
    }
  },
  render() {
    return this.formattedValue
  }
})

const totalUsers = ref(0)
const newUsers = ref(0)
const activeUsers = ref(0)
const recentUsers = ref<API.UserVO[]>([])
const loading = ref(false)
const growthTimeRange = ref<'week' | 'month' | 'year'>('month')
const userDetailVisible = ref(false)
const currentUser = ref<API.UserVO>({})
const growthChartRef = ref<HTMLElement | null>(null)
const chartLoading = ref(false)
const roleChartRef = ref<HTMLElement | null>(null)
const roleChartLoading = ref(false)
let growthChart: echarts.ECharts | null = null
let roleChart: echarts.ECharts | null = null

// 模拟用户增长数据
const growthData = {
  week: {
    dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    values: [10, 15, 8, 12, 20, 25, 18],
    newUsers: [2, 5, 3, 4, 7, 9, 6]
  },
  month: {
    dates: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
    values: Array.from({ length: 30 }, () => Math.floor(Math.random() * 30) + 10),
    newUsers: Array.from({ length: 30 }, () => Math.floor(Math.random() * 10) + 1)
  },
  year: {
    dates: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    values: [120, 132, 101, 134, 90, 180, 210, 190, 150, 232, 201, 154],
    newUsers: [20, 32, 18, 34, 25, 30, 40, 32, 28, 42, 35, 29]
  }
}

// 获取用户数据
const fetchUserData = async () => {
  loading.value = true
  try {
    // 模拟获取总用户数
    const res = await listUserVoByPage({ current: 1, pageSize: 10 })
    if (res.data?.code === 0 && res.data.data) {
      totalUsers.value = res.data.data.total || 0
      recentUsers.value = res.data.data.records || []
      
      // 这里只是模拟数据，实际项目中应该从后端获取
      newUsers.value = Math.floor(Math.random() * 20)
      activeUsers.value = Math.floor(totalUsers.value * 0.7)
      
      // 初始化图表
      nextTick(() => {
        initGrowthChart()
        initRoleChart()
      })
    }
  } catch (error) {
    console.error('获取用户数据失败', error)
    ElMessage.error('获取用户数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化用户增长趋势图表
const initGrowthChart = () => {
  if (!growthChartRef.value) return
  
  // 如果已经存在图表实例，先销毁
  if (growthChart) {
    growthChart.dispose()
  }
  
  // 创建图表实例
  growthChart = echarts.init(growthChartRef.value)
  
  // 更新图表数据
  updateGrowthChart()
}

// 更新增长图表
const updateGrowthChart = () => {
  if (!growthChart) return
  
  chartLoading.value = true
  
  // 模拟加载延迟
  setTimeout(() => {
    const data = growthData[growthTimeRange.value]
    
    // 设置图表选项
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: ['累计用户', '新增用户']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.dates
      },
      yAxis: {
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      series: [
        {
          name: '累计用户',
          type: 'line',
          data: data.values,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 3,
            color: '#1890ff'
          },
          itemStyle: {
            color: '#1890ff'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
              { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
            ])
          }
        },
        {
          name: '新增用户',
          type: 'line',
          data: data.newUsers,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 3,
            color: '#52c41a'
          },
          itemStyle: {
            color: '#52c41a'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
              { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
            ])
          }
        }
      ]
    }
    
    // 设置图表选项
    growthChart?.setOption(option)
    chartLoading.value = false
  }, 500)
}

// 初始化用户角色分布图表
const initRoleChart = () => {
  if (!roleChartRef.value) return
  
  // 如果已经存在图表实例，先销毁
  if (roleChart) {
    roleChart.dispose()
  }
  
  // 创建图表实例
  roleChart = echarts.init(roleChartRef.value)
  
  // 更新图表数据
  updateRoleChart()
}

// 更新角色分布图表
const updateRoleChart = () => {
  if (!roleChart) return
  
  roleChartLoading.value = true
  
  // 模拟加载延迟
  setTimeout(() => {
    // 模拟角色分布数据
    const roleData = [
      { value: Math.round(totalUsers.value * 0.15), name: '管理员' },
      { value: Math.round(totalUsers.value * 0.85), name: '普通用户' }
    ]
    
    // 设置图表选项
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        data: roleData.map(item => item.name)
      },
      series: [
        {
          name: '用户角色',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            {
              value: roleData[0].value,
              name: roleData[0].name,
              itemStyle: { color: '#ff4d4f' }
            },
            {
              value: roleData[1].value,
              name: roleData[1].name,
              itemStyle: { color: '#52c41a' }
            }
          ]
        }
      ]
    }
    
    // 设置图表选项
    roleChart?.setOption(option)
    roleChartLoading.value = false
  }, 500)
}

// 刷新角色分布图表
const refreshRoleChart = () => {
  if (roleChart) {
    updateRoleChart()
  } else {
    initRoleChart()
  }
}

// 刷新数据
const refreshData = () => {
  fetchUserData()
}

// 刷新图表数据
const refreshChartData = () => {
  if (growthChart) {
    updateGrowthChart()
  } else {
    initGrowthChart()
  }
}

// 导出数据
const exportData = () => {
  ElMessage.info('数据导出功能开发中...')
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }: { row: any, rowIndex: number }) => {
  if (rowIndex % 2 === 0) {
    return 'even-row'
  } else {
    return 'odd-row'
  }
}

// 查看用户详情
const viewUserDetail = async (user: API.UserVO) => {
  try {
    loading.value = true
    if (user.id) {
      const res = await getUserVoById({ id: user.id })
      if (res.data?.code === 0 && res.data.data) {
        currentUser.value = res.data.data
        userDetailVisible.value = true
      }
    }
  } catch (error) {
    console.error('获取用户详情失败', error)
    ElMessage.error('获取用户详情失败')
  } finally {
    loading.value = false
  }
}

// 处理窗口大小变化，重新调整图表大小
const handleResize = () => {
  growthChart?.resize()
  roleChart?.resize()
}

// 组件挂载时初始化
onMounted(() => {
  fetchUserData()
  window.addEventListener('resize', handleResize)
})

// 组件卸载前清理资源
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  growthChart?.dispose()
})
</script>

<style scoped>
.user-analysis {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-title {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-title .el-icon {
  margin-right: 8px;
  font-size: 24px;
}

.stats-cards {
  margin-bottom: 32px;
}

.stats-card {
  height: 140px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  transition: all 0.3s;
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stats-card-content {
  display: flex;
  flex-direction: column;
}

.stats-card-title {
  font-size: 16px;
  color: var(--text-color-secondary);
  margin-bottom: 16px;
}

.stats-card-value {
  font-size: 36px;
  font-weight: bold;
  color: var(--heading-color);
  margin-bottom: 16px;
}

.stats-card-info {
  display: flex;
  align-items: center;
}

.stats-trend {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-right: 8px;
  font-weight: 500;
}

.stats-trend.positive {
  color: #52c41a;
}

.stats-trend.negative {
  color: #f5222d;
}

.stats-period {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.stats-card-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background-color: rgba(24, 144, 255, 0.1);
}

.stats-card-icon {
  font-size: 36px;
  color: #1890ff;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.charts-container {
  margin-bottom: 32px;
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--heading-color);
  display: flex;
  align-items: center;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.recent-users {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.recent-users-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.recent-users-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--heading-color);
  display: flex;
  align-items: center;
}

.recent-users-title .el-icon {
  margin-right: 8px;
}

.recent-users-actions {
  display: flex;
  gap: 8px;
}

.user-detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-detail-info {
  margin-left: 20px;
}

.user-detail-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.user-detail-info p {
  margin: 0 0 8px 0;
  color: var(--text-color-secondary);
}

.custom-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.custom-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

:deep(.even-row) {
  background-color: #fafafa;
}

:deep(.odd-row) {
  background-color: #ffffff;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.user-detail-dialog .el-dialog__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-right: 0;
}

:deep(.user-detail-dialog .el-dialog__body) {
  padding: 24px;
}

:deep(.user-detail-dialog .el-descriptions__label) {
  font-weight: 500;
  background-color: #fafafa;
}

:deep(.user-detail-dialog .el-descriptions__content) {
  padding: 12px 16px;
}

@media (max-width: 768px) {
  .stats-card {
    height: auto;
    padding: 16px;
  }
  
  .stats-card-value {
    font-size: 28px;
  }
  
  .stats-card-icon-container {
    width: 50px;
    height: 50px;
  }
  
  .stats-card-icon {
    font-size: 24px;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .chart-actions {
    margin-top: 10px;
  }
}
</style> 