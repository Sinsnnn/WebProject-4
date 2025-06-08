<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <h2>课程列表</h2>
    </div>

    <div class="search-bar">
      <el-input v-model="data.searchKeyword" placeholder="搜索课程名称" clearable @keyup.enter="loadCourses">
        <template #append>
          <el-button @click="loadCourses">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="course-list">
      <div v-for="course in data.courses" :key="course.id" class="course-card" @click="viewCourse(course.id)">
        <div class="course-cover">
          <el-image :src="course.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"></el-image>
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.name }}</h3>
          <div class="course-desc">{{ course.description }}</div>
          <div class="course-meta">
            <div class="teacher">
              <el-icon>
                <User />
              </el-icon>
              <span>{{ course.teacherName }}</span>
            </div>
            <div class="time">
              <el-icon>
                <Calendar />
              </el-icon>
              <span>{{ formatDate(course.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="data.total > 0" class="pagination">
      <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
        v-model:current-page="data.pageNum" @current-change="loadCourses" />
    </div>

    <div v-if="data.courses.length === 0" class="empty-tip">
      暂无课程，请稍后再来查看
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Calendar } from '@element-plus/icons-vue';
import request from '@/utils/request';

const router = useRouter();

const data = reactive({
  courses: [],
  total: 0,
  pageNum: 1,
  pageSize: 8,
  searchKeyword: ''
});

// 加载课程列表
const loadCourses = () => {
  request.get('/course/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.searchKeyword,
      status: 1 // 只查询已发布的课程
    }
  }).then(res => {
    if (res.code === '200') {
      data.courses = res.data.list || [];
      data.total = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || '获取课程列表失败');
    }
  });
};

// 查看课程详情
const viewCourse = (id) => {
  router.push(`/manager/courseDetail/${id}`);
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

onMounted(() => {
  loadCourses();
});
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
  max-width: 500px;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  height: 160px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.course-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.teacher,
.time {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #999;
}
</style>