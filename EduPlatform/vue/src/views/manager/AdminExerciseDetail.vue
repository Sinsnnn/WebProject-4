<template>
  <div class="exercise-detail">
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="goBack">返回</el-button>
    </div>

    <div v-if="data.exercise" class="exercise-card">
      <div class="exercise-header">
        <h1 class="exercise-title">{{ data.exercise.title }}</h1>
        <div class="exercise-meta">
          <el-tag size="small" :type="getDifficultyType(data.exercise.difficulty)">
            {{ getDifficultyLabel(data.exercise.difficulty) }}
          </el-tag>
          <el-tag size="small" :type="getTypeType(data.exercise.type)">
            {{ getTypeLabel(data.exercise.type) }}
          </el-tag>
          <div class="course-name" v-if="data.exercise.courseName">
            所属课程: {{ data.exercise.courseName }}
          </div>
        </div>
      </div>

      <div class="exercise-content">
        <div class="content-text">{{ data.exercise.content }}</div>
      </div>

      <div class="exercise-options" v-if="data.exercise.type === 0">
        <h3>选项</h3>
        <div class="options-list">
          <div v-for="(option, index) in JSON.parse(data.exercise.options || '[]')" :key="index" class="option-item">
            <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
            <span class="option-text">{{ option }}</span>
          </div>
        </div>
      </div>

      <div class="exercise-answer">
        <h3>参考答案</h3>
        <div class="answer-content">{{ data.exercise.answer }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();

const data = reactive({
  exercise: null
});

// 加载习题信息
const loadExercise = () => {
  request.get(`/exercise/selectById/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.exercise = res.data;
    } else {
      ElMessage.error(res.msg || '获取习题信息失败');
    }
  });
};

// 返回上一页
const goBack = () => {
  router.push('/manager/adminExercises');
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
  loadExercise();
});
</script>

<style scoped>
.exercise-detail {
  padding: 20px;
}

.exercise-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.exercise-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.exercise-title {
  font-size: 22px;
  margin-bottom: 15px;
  color: #333;
}

.exercise-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-name {
  color: #666;
  font-size: 14px;
}

.exercise-content {
  margin-bottom: 30px;
}

.content-text {
  line-height: 1.6;
  font-size: 16px;
}

.exercise-options,
.exercise-answer {
  margin-bottom: 30px;
}

.exercise-options h3,
.exercise-answer h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.option-label {
  font-weight: bold;
  min-width: 20px;
}

.answer-content {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  line-height: 1.6;
}
</style>