<template>
  <div class="course-detail">
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="goBack">返回</el-button>
    </div>

    <div v-if="data.course" class="course-info-card">
      <div class="course-header">
        <div class="course-cover">
          <el-image :src="data.course.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"></el-image>
        </div>
        <div class="course-info">
          <h1 class="course-title">{{ data.course.name }}</h1>
          <div class="course-meta">
            <div class="teacher">
              <el-icon>
                <User />
              </el-icon>
              <span>{{ data.course.teacherName }}</span>
            </div>
            <div class="time">
              <el-icon>
                <Calendar />
              </el-icon>
              <span>{{ formatDate(data.course.createTime) }}</span>
            </div>
          </div>
          <div class="course-desc">{{ data.course.description }}</div>
        </div>
      </div>
    </div>

    <div class="course-content-card">
      <div class="card-header">
        <h2>课程内容</h2>
      </div>
      <div class="content-list">
        <el-collapse v-if="data.contents.length > 0">
          <el-collapse-item v-for="content in data.contents" :key="content.id" :title="content.title">
            <div class="content-item">
              <div class="content-text" v-html="content.content"></div>
              <div v-if="content.video" class="content-video">
                <video :src="content.video" controls></video>
              </div>
              <div v-if="content.attachment" class="content-attachment">
                <a :href="content.attachment" target="_blank" download>
                  <el-button type="primary" size="small" plain>
                    <el-icon>
                      <Download />
                    </el-icon>
                    下载附件
                  </el-button>
                </a>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
        <div v-else class="empty-tip">
          暂无课程内容
        </div>
      </div>
    </div>

    <div class="course-exercise-card">
      <div class="card-header">
        <h2>课程习题</h2>
      </div>
      <div class="exercise-list">
        <div v-if="data.exercises.length > 0">
          <div v-for="exercise in data.exercises" :key="exercise.id" class="exercise-item"
            @click="viewExercise(exercise.id)">
            <div class="exercise-title">{{ exercise.title }}</div>
            <div class="exercise-meta">
              <el-tag size="small" :type="getDifficultyType(exercise.difficulty)">
                {{ getDifficultyLabel(exercise.difficulty) }}
              </el-tag>
              <el-tag size="small" :type="getTypeType(exercise.type)">
                {{ getTypeLabel(exercise.type) }}
              </el-tag>
            </div>
          </div>
        </div>
        <div v-else class="empty-tip">
          暂无课程习题
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Calendar, Download } from '@element-plus/icons-vue';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();

const data = reactive({
  course: null,
  contents: [],
  exercises: []
});

// 加载课程信息
const loadCourse = () => {
  request.get(`/course/selectById/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.course = res.data;
    } else {
      ElMessage.error(res.msg || '获取课程信息失败');
    }
  });
};

// 加载课程内容
const loadContents = () => {
  request.get(`/courseContent/selectByCourseId/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.contents = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取课程内容失败');
    }
  });
};

// 加载课程习题
const loadExercises = () => {
  request.get(`/exercise/selectByCourseId/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.exercises = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取课程习题失败');
    }
  });
};

// 查看习题详情
const viewExercise = (id) => {
  router.push(`/manager/exerciseDetail/${id}`);
};

// 返回上一页
const goBack = () => {
  router.push('/manager/studentCourses');
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
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
  loadCourse();
  loadContents();
  loadExercises();
});
</script>

<style scoped>
.course-detail {
  padding: 20px;
}

.course-info-card,
.course-content-card,
.course-exercise-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.course-header {
  display: flex;
  gap: 20px;
}

.course-cover {
  width: 300px;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.course-meta .teacher,
.course-meta .time {
  display: flex;
  align-items: center;
  color: #666;
}

.course-meta .el-icon {
  margin-right: 5px;
}

.course-desc {
  color: #666;
  line-height: 1.6;
}

.card-header {
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.card-header h2 {
  font-size: 18px;
  color: #333;
}

.content-item {
  padding: 10px 0;
}

.content-text {
  margin-bottom: 15px;
  line-height: 1.6;
}

.content-video {
  margin-bottom: 15px;
}

.content-video video {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
}

.exercise-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.exercise-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: transform 0.3s;
}

.exercise-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
}

.exercise-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.exercise-meta {
  display: flex;
  gap: 10px;
}

.empty-tip {
  text-align: center;
  padding: 30px 0;
  color: #999;
}

@media (max-width: 768px) {
  .course-header {
    flex-direction: column;
  }

  .course-cover {
    width: 100%;
    height: 200px;
  }
}
</style>