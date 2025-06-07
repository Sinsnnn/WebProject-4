<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <h2>习题查看</h2>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="data.queryParams">
        <el-form-item label="习题标题">
          <el-input v-model="data.queryParams.title" placeholder="请输入习题标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="课程" style="width: 200px;">
          <el-select v-model="data.queryParams.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="course in data.courses" :key="course.id" :label="course.name"
              :value="course.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度" style="width: 200px;">
          <el-select v-model="data.queryParams.difficulty" placeholder="请选择难度" clearable>
            <el-option label="简单" :value="0"></el-option>
            <el-option label="中等" :value="1"></el-option>
            <el-option label="困难" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型" style="width: 200px;">
          <el-select v-model="data.queryParams.type" placeholder="请选择类型" clearable>
            <el-option label="选择题" :value="0"></el-option>
            <el-option label="填空题" :value="1"></el-option>
            <el-option label="判断题" :value="2"></el-option>
            <el-option label="简答题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadExercises">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.exercises" stripe>
        <el-table-column label="习题标题" prop="title"></el-table-column>
        <el-table-column label="课程" prop="courseName" width="150"></el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag size="small" :type="getTypeType(scope.row.type)">
              {{ getTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="难度" width="100">
          <template #default="scope">
            <el-tag size="small" :type="getDifficultyType(scope.row.difficulty)">
              {{ getDifficultyLabel(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain @click="viewExercise(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="data.total > 0" class="pagination">
        <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
          v-model:current-page="data.pageNum" @current-change="loadExercises" />
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
  exercises: [],
  courses: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  queryParams: {
    title: '',
    courseId: null,
    difficulty: null,
    type: null
  }
});

// 加载课程列表
const loadCourses = () => {
  request.get('/course/selectAll').then(res => {
    if (res.code === '200') {
      data.courses = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取课程列表失败');
    }
  });
};

// 加载习题列表
const loadExercises = () => {
  const exercise = {
    title: data.queryParams.title,
    courseId: data.queryParams.courseId,
    difficulty: data.queryParams.difficulty,
    type: data.queryParams.type
  };

  request.get('/exercise/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      ...exercise
    }
  }).then(res => {
    if (res.code === '200') {
      data.exercises = res.data.list || [];
      data.total = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || '获取习题列表失败');
    }
  });
};

// 重置查询条件
const resetQuery = () => {
  data.queryParams = {
    title: '',
    courseId: null,
    difficulty: null,
    type: null
  };
  data.pageNum = 1;
  loadExercises();
};

// 查看习题详情
const viewExercise = (id) => {
  router.push(`/manager/adminExerciseDetail/${id}`);
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
  loadCourses();
  loadExercises();
});
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>