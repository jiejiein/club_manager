<template>
  <div class="my-payment-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2>我的缴费</h2>
        <p>查看缴费项目，管理您的缴费记录</p>
      </div>
      <div class="stats-overview">
        <div class="stat-item">
          <div class="stat-value">¥{{ stats.totalUnpaid }}</div>
          <div class="stat-label">待缴金额</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.unpaidCount }}</div>
          <div class="stat-label">待缴项目</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">¥{{ stats.totalPaid }}</div>
          <div class="stat-label">已缴金额</div>
        </div>
      </div>
    </div>

    <!-- 待缴费项目 -->
    <el-card class="payment-section" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="title">
            <el-icon><Wallet /></el-icon>
            待缴费项目
          </span>
          <el-tag v-if="unpaidPayments.length > 0" type="danger" effect="dark">{{ unpaidPayments.length }} 项待缴</el-tag>
        </div>
      </template>

      <el-row :gutter="20" v-if="unpaidPayments.length > 0">
        <el-col :xs="24" :sm="12" :lg="8" v-for="payment in unpaidPayments" :key="payment.id">
          <div class="payment-card unpaid">
            <div class="card-header">
              <div class="payment-icon">
                <el-icon :size="28" color="#fff"><Wallet /></el-icon>
              </div>
              <div class="payment-status">
                <el-tag type="danger" effect="light">待缴费</el-tag>
              </div>
            </div>
            <h3 class="payment-title">{{ payment.title }}</h3>
            <div class="payment-club">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ payment.clubName }}</span>
            </div>
            <p class="payment-desc">{{ payment.description || '暂无说明' }}</p>
            <div class="payment-amount">
              <span class="currency">¥</span>
              <span class="amount">{{ payment.amount }}</span>
            </div>
            <div class="payment-deadline">
              <el-icon><Timer /></el-icon>
              <span>截止：{{ formatDate(payment.deadline) }}</span>
            </div>
            <div class="card-actions">
              <el-button type="danger" size="large" @click="handlePay(payment)">
                <el-icon><Money /></el-icon>
                立即缴费
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-else description="暂无待缴费项目">
        <template #image>
          <el-icon :size="80" color="#dcdfe6"><CircleCheck /></el-icon>
        </template>
        <p class="empty-text">太棒了！您已完成所有缴费</p>
      </el-empty>
    </el-card>

    <!-- 缴费记录 -->
    <el-card class="records-section" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="title">
            <el-icon><DocumentChecked /></el-icon>
            缴费记录
          </span>
          <el-button type="primary" link @click="exportRecords">
            <el-icon><Download /></el-icon>
            导出记录
          </el-button>
        </div>
      </template>

      <el-timeline v-if="records.length > 0">
        <el-timeline-item
          v-for="record in records"
          :key="record.id"
          :type="'success'"
          :icon="Check"
          :timestamp="formatDateTime(record.payTime)"
          placement="top"
        >
          <div class="record-card">
            <div class="record-info">
              <h4>{{ record.title }}</h4>
              <p class="record-club">{{ record.clubName }}</p>
            </div>
            <div class="record-amount">
              <span class="amount">-¥{{ record.amount }}</span>
              <span class="transaction-no">流水号：{{ record.transactionNo }}</span>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-else description="暂无缴费记录" :image-size="80" />
    </el-card>

    <!-- 缴费确认弹窗 -->
    <el-dialog v-model="payDialogVisible" title="缴费确认" width="400px" center>
      <div v-if="currentPayment" class="pay-dialog-content">
        <div class="pay-amount">
          <span class="currency">¥</span>
          <span class="amount">{{ currentPayment.amount }}</span>
        </div>
        <div class="pay-info">
          <p><strong>缴费项目：</strong>{{ currentPayment.title }}</p>
          <p><strong>所属社团：</strong>{{ currentPayment.clubName }}</p>
          <p><strong>截止时间：</strong>{{ formatDate(currentPayment.deadline) }}</p>
        </div>
        <el-divider />
        <div class="pay-methods">
          <h4>选择支付方式</h4>
          <div class="method-list">
            <div 
              v-for="method in payMethods" 
              :key="method.value"
              class="method-item"
              :class="{ active: selectedMethod === method.value }"
              @click="selectedMethod = method.value"
            >
              <el-icon :size="24"><component :is="method.icon" /></el-icon>
              <span>{{ method.label }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="paying" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyPayments, getMyPaymentRecords, pay } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Wallet, OfficeBuilding, Timer, Money, 
  DocumentChecked, Download, Check, CircleCheck
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const payments = ref([])
const records = ref([])
const payDialogVisible = ref(false)
const currentPayment = ref(null)
const paying = ref(false)
const selectedMethod = ref('alipay')

const payMethods = [
  { label: '支付宝', value: 'alipay', icon: 'Money' },
  { label: '微信支付', value: 'wechat', icon: 'Money' },
  { label: '校园卡', value: 'campus', icon: 'Wallet' }
]

// 待缴费项目
const unpaidPayments = computed(() => {
  return payments.value.filter(p => p.status !== 1)
})

// 统计数据
const stats = computed(() => {
  const unpaid = unpaidPayments.value
  const paid = records.value
  return {
    totalUnpaid: unpaid.reduce((sum, p) => sum + p.amount, 0).toFixed(2),
    unpaidCount: unpaid.length,
    totalPaid: paid.reduce((sum, r) => sum + r.amount, 0).toFixed(2)
  }
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return '无期限'
  return dayjs(date).format('YYYY-MM-DD')
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return dayjs(datetime).format('YYYY-MM-DD HH:mm')
}

const loadData = async () => {
  loading.value = true
  try {
    const [paymentsRes, recordsRes] = await Promise.all([
      getMyPayments(),
      getMyPaymentRecords()
    ])
    payments.value = paymentsRes.data || []
    records.value = recordsRes.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePay = (payment) => {
  currentPayment.value = payment
  selectedMethod.value = 'alipay'
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!currentPayment.value) return
  
  paying.value = true
  try {
    // 模拟支付，无论选择哪种支付方式都成功
    await pay(currentPayment.value.id)
    // 获取选择的支付方式名称
    const selectedMethodName = payMethods.find(m => m.value === selectedMethod.value)?.label || '未知方式'
    ElMessage.success(`${selectedMethodName}支付成功`)
    payDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    paying.value = false
  }
}

const exportRecords = () => {
  // 导出缴费记录为CSV
  const headers = ['缴费项目', '所属社团', '金额', '缴费时间', '交易流水号']
  const rows = records.value.map(r => [
    r.title,
    r.clubName,
    r.amount,
    formatDateTime(r.payTime),
    r.transactionNo
  ])
  
  const csvContent = [headers, ...rows]
    .map(row => row.join(','))
    .join('\n')
  
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `缴费记录_${dayjs().format('YYYY-MM-DD')}.csv`
  link.click()
  
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.my-payment-page {
  padding: 20px;

  .welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 30px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;

    .welcome-content {
      h2 {
        margin: 0 0 8px;
        font-size: 28px;
      }
      p {
        margin: 0;
        opacity: 0.9;
        font-size: 14px;
      }
    }

    .stats-overview {
      display: flex;
      gap: 40px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 32px;
          font-weight: bold;
          line-height: 1;
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.8;
          margin-top: 4px;
        }
      }
    }
  }

  .payment-section,
  .records-section {
    margin-bottom: 24px;
    border-radius: 12px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 18px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 8px;

        .el-icon {
          color: var(--el-color-primary);
        }
      }
    }

    .empty-text {
      color: #67c23a;
      font-size: 14px;
      margin-top: 8px;
    }
  }

  .payment-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    border: 2px solid transparent;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }

    &.unpaid {
      border-color: #fde2e2;
      background: linear-gradient(135deg, #fff 0%, #fef0f0 100%);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .payment-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .payment-title {
      margin: 0 0 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .payment-club {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;

      .el-icon {
        color: var(--el-color-primary);
      }
    }

    .payment-desc {
      margin: 0 0 16px;
      font-size: 13px;
      color: #909399;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .payment-amount {
      margin-bottom: 12px;

      .currency {
        font-size: 20px;
        color: #f56c6c;
        font-weight: 600;
      }

      .amount {
        font-size: 36px;
        color: #f56c6c;
        font-weight: bold;
      }
    }

    .payment-deadline {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: #e6a23c;
      margin-bottom: 16px;

      .el-icon {
        font-size: 14px;
      }
    }

    .card-actions {
      .el-button {
        width: 100%;
      }
    }
  }

  .record-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #f8f9fa;
    border-radius: 8px;

    .record-info {
      h4 {
        margin: 0 0 4px;
        font-size: 15px;
        color: #303133;
      }

      .record-club {
        font-size: 13px;
        color: #909399;
      }
    }

    .record-amount {
      text-align: right;

      .amount {
        display: block;
        font-size: 18px;
        font-weight: 600;
        color: #67c23a;
      }

      .transaction-no {
        font-size: 12px;
        color: #c0c4cc;
      }
    }
  }

  .pay-dialog-content {
    text-align: center;

    .pay-amount {
      margin-bottom: 20px;

      .currency {
        font-size: 24px;
        color: #f56c6c;
        font-weight: 600;
      }

      .amount {
        font-size: 48px;
        color: #f56c6c;
        font-weight: bold;
      }
    }

    .pay-info {
      text-align: left;
      background: #f8f9fa;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 20px;

      p {
        margin: 0 0 8px;
        font-size: 14px;
        color: #606266;

        &:last-child {
          margin-bottom: 0;
        }

        strong {
          color: #303133;
        }
      }
    }

    .pay-methods {
      text-align: left;

      h4 {
        margin: 0 0 12px;
        font-size: 14px;
        color: #606266;
      }

      .method-list {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .method-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 16px;
          border: 2px solid #e4e7ed;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #c0c4cc;
          }

          &.active {
            border-color: var(--el-color-primary);
            background: #f5f7fa;
          }

          span {
            font-size: 14px;
            color: #303133;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .my-payment-page {
    padding: 12px;

    .welcome-banner {
      flex-direction: column;
      text-align: center;
      gap: 20px;

      .stats-overview {
        gap: 24px;

        .stat-item .stat-value {
          font-size: 24px;
        }
      }
    }

    .record-card {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;

      .record-amount {
        text-align: left;
      }
    }
  }
}
</style>
