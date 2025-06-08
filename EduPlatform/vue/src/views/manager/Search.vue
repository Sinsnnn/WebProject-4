<template>
  <div class="search-container">
    <div class="search-header">
      <h2>搜索结果: {{ keyword }}</h2>
      <div class="search-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="课程" name="courses"></el-tab-pane>
          <el-tab-pane label="课程内容" name="courseContents"></el-tab-pane>
          <el-tab-pane label="动态" name="posts"></el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <div class="search-results">
      <!-- 全部结果 -->
      <div v-if="activeTab === 'all'">
        <!-- 课程结果 -->
        <div v-if="data.courses.length > 0" class="result-section">
          <h3>课程 ({{ data.courses.length }})</h3>
          <div class="course-list">
            <div v-for="course in data.courses" :key="course.id" class="course-item" @click="viewCourse(course.id)">
              <div class="course-cover">
                <el-image :src="course.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"></el-image>
              </div>
              <div class="course-info">
                <div class="course-title">{{ course.name }}</div>
                <div class="course-desc">{{ course.description }}</div>
                <div class="course-teacher">教师: {{ course.teacherName }}</div>
              </div>
            </div>
          </div>
          <div class="view-more" v-if="data.courses.length > 3" @click="activeTab = 'courses'">
            查看更多课程
          </div>
        </div>

        <!-- 课程内容结果 -->
        <div v-if="data.courseContents.length > 0" class="result-section">
          <h3>课程内容 ({{ data.courseContents.length }})</h3>
          <div class="content-list">
            <div v-for="content in data.courseContents.slice(0, 5)" :key="content.id" class="content-item"
              @click="viewCourseContent(content.courseId)">
              <div class="content-title">{{ content.title }}</div>
              <div class="content-course">所属课程: {{ content.courseName }}</div>
              <div class="content-text" v-html="highlightKeyword(content.content)"></div>
            </div>
          </div>
          <div class="view-more" v-if="data.courseContents.length > 5" @click="activeTab = 'courseContents'">
            查看更多课程内容
          </div>
        </div>

        <!-- 动态结果 -->
        <div v-if="data.posts.length > 0" class="result-section">
          <h3>动态 ({{ data.posts.length }})</h3>
          <div class="post-list">
            <div v-for="post in data.posts.slice(0, 3)" :key="post.id" class="post-item" @click="viewPost(post.id)">
              <div class="post-header">
                <img :src="post.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar" class="avatar" />
                <div>
                  <div class="username">{{ post.user?.name || '用户' }}</div>
                  <div class="time">{{ formatTime(post.createTime) }}</div>
                </div>
              </div>
              <div class="post-content">
                <div class="text" v-html="highlightKeyword(post.content)"></div>
                <div v-if="post.images" class="images">
                  <el-image v-for="(img, index) in JSON.parse(post.images)" :key="index" :src="img" fit="cover"
                    class="image"></el-image>
                </div>
              </div>
            </div>
          </div>
          <div class="view-more" v-if="data.posts.length > 3" @click="activeTab = 'posts'">
            查看更多动态
          </div>
        </div>

        <div v-if="data.courses.length === 0 && data.courseContents.length === 0 && data.posts.length === 0"
          class="no-results">
          没有找到与 "{{ keyword }}" 相关的内容
        </div>
      </div>

      <!-- 课程结果 -->
      <div v-if="activeTab === 'courses'">
        <div v-if="data.courses.length > 0" class="course-list">
          <div v-for="course in data.courses" :key="course.id" class="course-item" @click="viewCourse(course.id)">
            <div class="course-cover">
              <el-image :src="course.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"></el-image>
            </div>
            <div class="course-info">
              <div class="course-title">{{ course.name }}</div>
              <div class="course-desc">{{ course.description }}</div>
              <div class="course-teacher">教师: {{ course.teacherName }}</div>
            </div>
          </div>
        </div>
        <div v-else class="no-results">
          没有找到与 "{{ keyword }}" 相关的课程
        </div>
      </div>

      <!-- 课程内容结果 -->
      <div v-if="activeTab === 'courseContents'">
        <div v-if="data.courseContents.length > 0" class="content-list">
          <div v-for="content in data.courseContents" :key="content.id" class="content-item"
            @click="viewCourseContent(content.courseId)">
            <div class="content-title">{{ content.title }}</div>
            <div class="content-course">所属课程: {{ content.courseName }}</div>
            <div class="content-text" v-html="highlightKeyword(content.content)"></div>
          </div>
        </div>
        <div v-else class="no-results">
          没有找到与 "{{ keyword }}" 相关的课程内容
        </div>
      </div>

      <!-- 动态结果 -->
      <div v-if="activeTab === 'posts'">
        <div v-if="data.posts.length > 0" class="post-list">
          <div v-for="post in data.posts" :key="post.id" class="post-item" @click="viewPost(post.id)">
            <div class="post-header">
              <img :src="post.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar" class="avatar" />
              <div>
                <div class="username">{{ post.user?.name || '用户' }}</div>
                <div class="time">{{ formatTime(post.createTime) }}</div>
              </div>
            </div>
            <div class="post-content">
              <div class="text" v-html="highlightKeyword(post.content)"></div>
              <div v-if="post.images" class="images">
                <el-image v-for="(img, index) in JSON.parse(post.images)" :key="index" :src="img" fit="cover"
                  class="image"></el-image>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-results">
          没有找到与 "{{ keyword }}" 相关的动态
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();
const keyword = ref('');
const activeTab = ref('all');

const data = reactive({
  courses: [],
  courseContents: [],
  posts: []
});

// 搜索
const search = () => {
  if (!keyword.value) return;

  request.get('/search', {
    params: { keyword: keyword.value }
  }).then(res => {
    if (res.code === '200') {
      data.courses = res.data.courses || [];
      data.courseContents = res.data.courseContents || [];
      data.posts = res.data.posts || [];
    } else {
      ElMessage.error(res.msg || '搜索失败');
    }
  });
};

// 高亮关键词
const highlightKeyword = (text) => {
  if (!text) return '';

  // 转义HTML特殊字符
  const escapeHtml = (str) => {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  };

  const escapedText = escapeHtml(text);
  const escapedKeyword = escapeHtml(keyword.value);

  // 截取内容，显示关键词前后的一部分文本
  const maxLength = 200;
  let startIndex = escapedText.toLowerCase().indexOf(escapedKeyword.toLowerCase());

  if (startIndex === -1) {
    // 如果没有找到关键词，显示前maxLength个字符
    return escapedText.length > maxLength ? escapedText.substring(0, maxLength) + '...' : escapedText;
  }

  // 计算截取的起始位置
  startIndex = Math.max(0, startIndex - 50);
  let endIndex = Math.min(escapedText.length, startIndex + maxLength);

  let result = '';
  if (startIndex > 0) {
    result += '...';
  }

  result += escapedText.substring(startIndex, endIndex);

  if (endIndex < escapedText.length) {
    result += '...';
  }

  // 高亮关键词（不区分大小写）
  const regex = new RegExp(escapedKeyword, 'gi');
  return result.replace(regex, match => `<span class="highlight">${match}</span>`);
};

// 查看课程
const viewCourse = (courseId) => {
  // 跳转到课程详情页
  router.push(`/manager/courseDetail/${courseId}`);
};

// 查看课程内容
const viewCourseContent = (courseId) => {
  // 跳转到课程内容页
  router.push(`/manager/courseContent/${courseId}`);
};

// 查看动态
const viewPost = (postId) => {
  // 跳转到动态详情页
  router.push(`/manager/postDetail/${postId}`);
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';

  const date = new Date(timeStr);
  const now = new Date();
  const diff = now - date;

  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 1000)) + '分钟前';
  }
  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 60 * 1000)) + '小时前';
  }
  // 小于1周
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前';
  }

  // 其他情况显示具体日期
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 监听路由参数变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword;
    search();
  }
}, { immediate: true });

onMounted(() => {
  keyword.value = route.query.keyword || '';
  if (keyword.value) {
    search();
  }
});
</script>

<style scoped>
.search-container {
  padding: 20px;
}

.search-header {
  margin-bottom: 20px;
}

.search-header h2 {
  margin-bottom: 10px;
  color: #333;
}

.search-tabs {
  margin-bottom: 20px;
}

.result-section {
  margin-bottom: 30px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.result-section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 18px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-item {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.course-item:hover {
  transform: translateY(-5px);
}

.course-cover {
  height: 150px;
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
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.course-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-teacher {
  font-size: 12px;
  color: #999;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.content-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.content-item:hover {
  transform: translateY(-3px);
}

.content-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.content-course {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.content-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.username {
  font-weight: bold;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.post-content .text {
  margin-bottom: 10px;
  line-height: 1.5;
}

.post-content .images {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.post-content .image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.view-more {
  text-align: center;
  color: #409EFF;
  cursor: pointer;
  margin-top: 10px;
  font-size: 14px;
}

.no-results {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 16px;
}

:deep(.highlight) {
  background-color: #FFFF00;
  font-weight: bold;
  padding: 0 2px;
}
</style>