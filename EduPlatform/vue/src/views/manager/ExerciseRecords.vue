<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <h2>我的答题记录</h2>
    </div>

    <div class="records-list">
      <el-table :data="data.records" stripe>
        <el-table-column label="习题标题" prop="exercise.title" min-width="200"></el-table-column>
        <el-table-column label="课程" prop="exercise.courseName" width="150"></el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag size="small" :type="getTypeType(scope.row.exercise.type)">
              {{ getTypeLabel(scope.row.exercise.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="难度" width="100">
          <template #default="scope">
            <el-tag size="small" :type="getDifficultyType(scope.row.exercise.difficulty)">
              {{ getDifficultyLabel(scope.row.exercise.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="我的答案" prop="userAnswer" min-width="150"></el-table-column>
        <el-table-column label="正确答案" prop="exercise.answer" min-width="150"></el-table-column>
        <el-table-column label="结果" width="80">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.isCorrect === 1 ? 'success' : 'danger'">
              {{ scope.row.isCorrect === 1 ? '正确' : '错误' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="答题时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain size="small" @click="goToExercise(scope.row.exerciseId)">
              重新作答
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="data.total > 0" class="pagination">
        <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
          v-model:current-page="data.pageNum" @current-change="loadRecords" />
      </div>

      <div v-if="data.records.length === 0" class="empty-tip">
        暂无答题记录，快去练习吧！
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const router = useRouter();

const data = reactive({
  records: [],
  total: 0,
  pageNum: 1,
  pageSize: 10
});

// 加载答题记录
const loadRecords = () => {
  request.get('/exerciseRecord/selectByUserId', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize
    }
  }).then(res => {
    if (res.code === '200') {
      data.records = res.data.list || [];
      data.total = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || '获取答题记录失败');
    }
  });
};

// 跳转到习题详情页
const goToExercise = (id) => {
  router.push(`/manager/exerciseDetail/${id}`);
};

// 获取难度标签
const getDifficultyLabel = (difficulty) => {
  const labels = ['简单', '中等', '困难'];
  return labels[difficulty] || '未知';
};

// 获取难度类型
const getDifficultyType = (difficulty) => {
  const types = ['success', 'warning', 'danger'];
  return types[difficulty] || 'info';
};

// 获取题目类型标签
const getTypeLabel = (type) => {
  const labels = ['选择题', '填空题', '判断题', '简答题'];
  return labels[type] || '未知';
};

// 获取题目类型类型
const getTypeType = (type) => {
  const types = ['', 'success', 'warning', 'danger'];
  return types[type] || 'info';
};

onMounted(() => {
  loadRecords();
});
</script>

<style scoped>
.records-list {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #999;
}
</style>