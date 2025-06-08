<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-form :inline="true" :model="data.queryParams">
        <el-form-item label="课程" style="width: 200px">
          <el-select
            v-model="data.queryParams.courseId"
            placeholder="选择课程"
            clearable
            @change="loadExercises"
          >
            <el-option
              v-for="course in data.courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" style="width: 200px">
          <el-select
            v-model="data.queryParams.difficulty"
            placeholder="选择难度"
            clearable
            @change="loadExercises"
          >
            <el-option label="简单" :value="0" />
            <el-option label="中等" :value="1" />
            <el-option label="困难" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" style="width: 200px">
          <el-select
            v-model="data.queryParams.type"
            placeholder="选择类型"
            clearable
            @change="loadExercises"
          >
            <el-option label="选择题" :value="0" />
            <el-option label="填空题" :value="1" />
            <el-option label="判断题" :value="2" />
            <el-option label="简答题" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadExercises">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div
      v-if="data.queryParams.courseId"
      class="card"
      style="margin-bottom: 5px"
    >
      <div class="stats-card">
        <div class="stats-item">
          <div class="stats-label">总习题数</div>
          <div class="stats-value">{{ data.stats.totalExercises || 0 }}</div>
        </div>
        <div class="stats-item">
          <div class="stats-label">已答题数</div>
          <div class="stats-value">{{ data.stats.answeredExercises || 0 }}</div>
        </div>
        <div class="stats-item">
          <div class="stats-label">正确题数</div>
          <div class="stats-value">{{ data.stats.correctExercises || 0 }}</div>
        </div>
        <div class="stats-item">
          <div class="stats-label">正确率</div>
          <div class="stats-value">{{ data.stats.correctRate || 0 }}%</div>
        </div>
      </div>
    </div>

    <div class="exercise-list">
      <div
        v-for="exercise in data.exercises"
        :key="exercise.id"
        class="exercise-card"
        @click="goToExercise(exercise.id)"
      >
        <div class="exercise-header">
          <div class="exercise-title">{{ exercise.title }}</div>
          <div class="exercise-meta">
            <el-tag size="small" :type="getDifficultyType(exercise.difficulty)">
              {{ getDifficultyLabel(exercise.difficulty) }}
            </el-tag>
            <el-tag size="small" :type="getTypeType(exercise.type)">
              {{ getTypeLabel(exercise.type) }}
            </el-tag>
            <div v-if="exercise.courseName" class="course-name">
              {{ exercise.courseName }}
            </div>
          </div>
        </div>
        <div class="exercise-content">{{ exercise.content }}</div>
        <div class="exercise-footer">
          <div v-if="exercise.record" class="record-info">
            <el-tag
              size="small"
              :type="exercise.record.isCorrect === 1 ? 'success' : 'danger'"
            >
              {{ exercise.record.isCorrect === 1 ? "已答对" : "已答错" }}
            </el-tag>
          </div>
          <div v-else class="record-info">
            <el-tag size="small" type="info">未作答</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div v-if="data.total > 0" class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="data.total"
        :page-size="data.pageSize"
        v-model:current-page="data.pageNum"
        @current-change="loadExercises"
      />
    </div>

    <div v-if="data.exercises.length === 0" class="empty-tip">
      暂无习题，请选择课程或调整筛选条件
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const router = useRouter();

const data = reactive({
  exercises: [],
  courses: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  queryParams: {
    courseId: null,
    difficulty: null,
    type: null,
  },
  stats: {},
});

// 加载课程列表
const loadCourses = () => {
  request
    .get("/course/selectAll", {
      params: {
        status: 1, // 只查询已发布的课程
      },
    })
    .then((res) => {
      if (res.code === "200") {
        data.courses = res.data || [];
      } else {
        ElMessage.error(res.msg || "获取课程列表失败");
      }
    });
};

// 加载习题列表
const loadExercises = () => {
  request
    .get("/exercise/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        courseId: data.queryParams.courseId,
        difficulty: data.queryParams.difficulty,
        type: data.queryParams.type,
      },
    })
    .then((res) => {
      if (res.code === "200") {
        data.exercises = res.data.list || [];
        data.total = res.data.total || 0;

        // 查询每道题的答题记录
        data.exercises.forEach((exercise) => {
          checkExerciseRecord(exercise.id);
        });
      } else {
        ElMessage.error(res.msg || "获取习题列表失败");
      }
    });

  // 如果选择了课程，加载统计信息
  if (data.queryParams.courseId) {
    loadStats();
  }
};

// 查询习题的答题记录
const checkExerciseRecord = (exerciseId) => {
  request
    .get(`/exerciseRecord/selectByExerciseId/${exerciseId}`)
    .then((res) => {
      if (res.code === "200" && res.data) {
        // 找到对应的习题，添加答题记录
        const exercise = data.exercises.find((e) => e.id === exerciseId);
        if (exercise) {
          exercise.record = res.data;
        }
      }
    });
};

// 加载统计信息
const loadStats = () => {
  request
    .get(`/exerciseRecord/getStatsByCourse/${data.queryParams.courseId}`)
    .then((res) => {
      if (res.code === "200") {
        data.stats = res.data || {};
      }
    });
};

// 重置查询条件
const resetQuery = () => {
  data.queryParams = {
    courseId: null,
    difficulty: null,
    type: null,
  };
  data.pageNum = 1;
  loadExercises();
};

// 跳转到习题详情页
const goToExercise = (id) => {
  router.push(`/manager/exerciseDetail/${id}`);
};

// 获取难度标签
const getDifficultyLabel = (difficulty) => {
  const labels = ["简单", "中等", "困难"];
  return labels[difficulty] || "未知";
};

// 获取难度类型
const getDifficultyType = (difficulty) => {
  const types = ["success", "warning", "danger"];
  return types[difficulty] || "info";
};

// 获取题目类型标签
const getTypeLabel = (type) => {
  const labels = ["选择题", "填空题", "判断题", "简答题"];
  return labels[type] || "未知";
};

// 获取题目类型类型
const getTypeType = (type) => {
  const types = ["", "success", "warning", "danger"];
  return types[type] || "info";
};

onMounted(() => {
  loadCourses();
  loadExercises();
});
</script>

<style scoped>
.stats-card {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
}

.stats-item {
  text-align: center;
}

.stats-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.exercise-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.exercise-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.exercise-card:hover {
  transform: translateY(-5px);
}

.exercise-header {
  margin-bottom: 10px;
}

.exercise-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.exercise-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.course-name {
  font-size: 12px;
  color: #666;
}

.exercise-content {
  color: #666;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.exercise-footer {
  display: flex;
  justify-content: flex-end;
}

.record-info {
  font-size: 12px;
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