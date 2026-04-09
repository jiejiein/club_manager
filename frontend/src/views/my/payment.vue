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
/* ========== 现代简约科技风 - 学生端 ========== */
@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.my-payment-page {
  padding: 20px;
  position: relative;

  /* ========== 欢迎横幅流动渐变动画 ========== */
  .welcome-banner {
    background: linear-gradient(135deg, #0284c7, #38bdf8, #3b82f6, #0284c7, #38bdf8);
    background-size: 300% 300%;
    animation: gradientFlow 8s ease infinite;
    border-radius: 16px;
    padding: 30px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;
    position: relative;
    overflow: hidden;

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 50%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
      animation: shimmer 4s ease-in-out infinite;
    }

    .welcome-content {
      position: relative;
      z-index: 1;

      h2 {
        margin: 0 0 8px;
        font-size: 28px;
        text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
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
      position: relative;
      z-index: 1;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 32px;
          font-weight: bold;
          line-height: 1;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.8;
          margin-top: 4px;
        }
      }
    }
  }

  /* ========== el-card 渐变边框 + 毛玻璃 ========== */
  .payment-section,
  .records-section {
    margin-bottom: 24px;
    border-radius: 16px;
    position: relative;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: none;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 16px;
      padding: 1.5px;
      background: linear-gradient(135deg, rgba(2, 132, 199, 0.4), rgba(56, 189, 248, 0.3), rgba(59, 130, 246, 0.2));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
    }

    :deep(.el-card__header) {
      position: relative;
      padding: 16px 20px;
      border-bottom: 1px solid rgba(2, 132, 199, 0.08);

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 12px;
        bottom: 12px;
        width: 4px;
        border-radius: 4px;
        background: linear-gradient(180deg, #0284c7, #38bdf8, #3b82f6);
        box-shadow: 0 0 12px rgba(2, 132, 199, 0.3);
      }
    }

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
        color: #1e293b;

        .el-icon {
          color: #0284c7;
        }
      }
    }

    .empty-text {
      color: #67c23a;
      font-size: 14px;
      margin-top: 8px;
    }
  }

  /* ========== 缴费卡片渐变边框 + 悬停3D微效果 ========== */
  .payment-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    border: 2px solid transparent;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 12px;
      padding: 2px;
      background: linear-gradient(135deg, rgba(245, 108, 108, 0.4), rgba(230, 162, 60, 0.3), rgba(245, 108, 108, 0.2));
      -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      pointer-events: none;
      opacity: 0;
      transition: opacity 0.4s ease;
    }

    &:hover {
      transform: translateY(-6px) rotateX(2deg);
      box-shadow: 0 16px 40px rgba(245, 108, 108, 0.12), 0 0 0 1px rgba(245, 108, 108, 0.1);

      &::before {
        opacity: 1;
      }
    }

    &.unpaid {
      border-color: rgba(245, 108, 108, 0.15);
      background: linear-gradient(135deg, #fff 0%, rgba(254, 240, 240, 0.6) 100%);
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
        box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
      }
    }

    .payment-title {
      margin: 0 0 8px;
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }

    .payment-club {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: #64748b;
      margin-bottom: 8px;

      .el-icon {
        color: #0284c7;
      }
    }

    .payment-desc {
      margin: 0 0 16px;
      font-size: 13px;
      color: #94a3b8;
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
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 0 20px rgba(245, 108, 108, 0.35);
          transform: translateY(-1px);
        }
      }
    }
  }

  /* ========== 时间线渐变色节点 ========== */
  :deep(.el-timeline) {
    .el-timeline-item__node {
      background: linear-gradient(135deg, #0284c7, #38bdf8);
      box-shadow: 0 0 12px rgba(2, 132, 199, 0.3);
    }

    .el-timeline-item__tail {
      border-left: 2px solid linear-gradient(180deg, rgba(2, 132, 199, 0.3), rgba(56, 189, 248, 0.1));
      border-left-color: rgba(2, 132, 199, 0.15);
    }
  }

  .record-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: rgba(2, 132, 199, 0.03);
    border-radius: 10px;
    border: 1px solid rgba(2, 132, 199, 0.06);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(2, 132, 199, 0.06);
      border-color: rgba(2, 132, 199, 0.12);
    }

    .record-info {
      h4 {
        margin: 0 0 4px;
        font-size: 15px;
        color: #1e293b;
      }

      .record-club {
        font-size: 13px;
        color: #94a3b8;
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

  /* ========== 缴费确认弹窗科技感样式 ========== */
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    box-shadow: 0 24px 80px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(255, 255, 255, 0.2);

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #0284c7, #38bdf8, #3b82f6, #0284c7);
      background-size: 200% 100%;
      animation: borderFlow 3s linear infinite;
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
        text-shadow: 0 2px 12px rgba(245, 108, 108, 0.2);
      }
    }

    .pay-info {
      text-align: left;
      background: rgba(2, 132, 199, 0.03);
      padding: 16px;
      border-radius: 10px;
      margin-bottom: 20px;
      border: 1px solid rgba(2, 132, 199, 0.06);

      p {
        margin: 0 0 8px;
        font-size: 14px;
        color: #64748b;

        &:last-child {
          margin-bottom: 0;
        }

        strong {
          color: #1e293b;
        }
      }
    }

    .pay-methods {
      text-align: left;

      h4 {
        margin: 0 0 12px;
        font-size: 14px;
        color: #475569;
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
          border: 2px solid rgba(2, 132, 199, 0.1);
          border-radius: 10px;
          cursor: pointer;
          transition: all 0.3s;
          background: rgba(255, 255, 255, 0.6);

          &:hover {
            border-color: rgba(2, 132, 199, 0.25);
            background: rgba(2, 132, 199, 0.04);
          }

          &.active {
            border-color: #0284c7;
            background: linear-gradient(135deg, rgba(2, 132, 199, 0.06), rgba(56, 189, 248, 0.06));
            box-shadow: 0 0 16px rgba(2, 132, 199, 0.12);
          }

          span {
            font-size: 14px;
            color: #334155;
          }
        }
      }
    }
  }
}

@keyframes borderFlow {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
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
