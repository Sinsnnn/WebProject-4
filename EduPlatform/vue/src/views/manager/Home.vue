<template>
  <div>
    <!-- 欢迎卡片 - 所有角色共用 -->
    <div class="welcome-card">
      <div class="welcome-left">
        <img :src="data.user.avatar || '/src/assets/imgs/avatar.png'" alt="用户头像" class="user-avatar" />
        <div class="welcome-text">
          <h2>欢迎回来，{{ data.user.name }}</h2>
          <p>{{ getWelcomeMessage() }}</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="date-time">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
      </div>
    </div>

    <!-- 管理员首页内容 -->
    <div v-if="data.user.role === 'admin'">
      <!-- 系统概览 -->
      <div class="dashboard-row">
        <div class="stat-card">
          <div class="stat-icon user-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.stats.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.stats.totalCourses || 0 }}</div>
            <div class="stat-label">总课程数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon exercise-icon">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.stats.totalExercises || 0 }}</div>
            <div class="stat-label">总习题数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon post-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.stats.totalPosts || 0 }}</div>
            <div class="stat-label">总动态数</div>
          </div>
        </div>
      </div>

      <!-- 内容监控 -->
      <div class="dashboard-row">
        <div class="card content-monitor">
          <div class="card-header">
            <h3>内容监控</h3>
          </div>
          <div class="card-content">
            <div class="monitor-item">
              <div class="monitor-info">
                <span class="monitor-label">已发布课程</span>
                <span class="monitor-value">{{ data.stats.publishedCourses || 0 }}</span>
              </div>
              <el-progress :percentage="getPercentage(data.stats.publishedCourses, data.stats.totalCourses)"
                :format="format" :stroke-width="10" />
            </div>

            <div class="monitor-item">
              <div class="monitor-info">
                <span class="monitor-label">课程内容数</span>
                <span class="monitor-value">{{ data.stats.totalContents || 0 }}</span>
              </div>
            </div>

            <div class="quick-actions">
              <el-button type="primary" @click="router.push('/manager/adminCourses')">查看课程</el-button>
              <el-button type="success" @click="router.push('/manager/adminExercises')">查看习题</el-button>
              <el-button type="warning" @click="router.push('/manager/adminPosts')">查看动态</el-button>
            </div>
          </div>
        </div>

        <div class="card user-stats">
          <div class="card-header">
            <h3>用户统计</h3>
          </div>
          <div class="card-content">
            <div class="user-type-stats">
              <div class="user-type-item">
                <div class="user-type-icon admin-icon">
                  <i class="el-icon-s-custom"></i>
                </div>
                <div class="user-type-info">
                  <div class="user-type-value">{{ data.stats.adminCount || 0 }}</div>
                  <div class="user-type-label">管理员</div>
                </div>
              </div>

              <div class="user-type-item">
                <div class="user-type-icon teacher-icon">
                  <i class="el-icon-s-check"></i>
                </div>
                <div class="user-type-info">
                  <div class="user-type-value">{{ data.stats.teacherCount || 0 }}</div>
                  <div class="user-type-label">教师</div>
                </div>
              </div>

              <div class="user-type-item">
                <div class="user-type-icon student-icon">
                  <i class="el-icon-s-opportunity"></i>
                </div>
                <div class="user-type-info">
                  <div class="user-type-value">{{ data.stats.studentCount || 0 }}</div>
                  <div class="user-type-label">学生</div>
                </div>
              </div>
            </div>

            <div class="quick-actions">
              <el-button type="primary" @click="router.push('/manager/admin')">管理员管理</el-button>
              <el-button type="success" @click="router.push('/manager/teacher')">教师管理</el-button>
              <el-button type="info" @click="router.push('/manager/user')">学生管理</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近活动 -->
      <div class="card recent-activities">
        <div class="card-header">
          <h3>最近活动</h3>
        </div>
        <div class="card-content">
          <div v-if="data.activities.length > 0" class="activity-list">
            <div v-for="activity in data.activities" :key="activity.id" class="activity-item">
              <div :class="['activity-icon', getActivityIconClass(activity.type)]">
                <i :class="getActivityIcon(activity.type)"></i>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-desc">{{ activity.description }}</div>
                <div class="activity-time">{{ formatDate(activity.createTime) }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-tip">
            <el-empty description="暂无活动记录" />
          </div>
        </div>
      </div>
    </div>

    <!-- 教师首页内容 -->
    <div v-else-if="data.user.role === 'teacher'">
      <!-- 教师统计概览 -->
      <div class="dashboard-row">
        <div class="stat-card">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.teacherStats.totalCourses || 0 }}</div>
            <div class="stat-label">我的课程</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon exercise-icon">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.teacherStats.totalExercises || 0 }}</div>
            <div class="stat-label">习题数量</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon student-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.teacherStats.totalStudents || 0 }}</div>
            <div class="stat-label">学习人数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon record-icon">
            <i class="el-icon-data-analysis"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.teacherStats.correctRate || 0 }}%</div>
            <div class="stat-label">正确率</div>
          </div>
        </div>
      </div>

      <!-- 课程管理 -->
      <div class="dashboard-row">
        <div class="card course-management">
          <div class="card-header">
            <h3>课程管理</h3>
            <el-button type="primary" size="small" @click="router.push('/manager/course')">管理课程</el-button>
          </div>
          <div class="card-content">
            <div v-if="data.teacherCourses.length > 0" class="recent-courses">
              <div v-for="course in data.teacherCourses.slice(0, 3)" :key="course.id" class="recent-course-item">
                <div class="course-info">
                  <div class="course-title">{{ course.name }}</div>
                  <div class="course-meta">
                    <el-tag size="small" :type="course.status === 1 ? 'success' : 'info'">
                      {{ course.status === 1 ? '已发布' : '未发布' }}
                    </el-tag>
                    <span class="course-time">{{ formatDate(course.createTime) }}</span>
                  </div>
                </div>
                <div class="course-actions">
                  <el-button type="primary" size="small" @click="manageCourseContent(course.id)">管理内容</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-tip">
              <el-empty description="暂无课程" />
              <el-button type="primary" @click="router.push('/manager/course')">创建课程</el-button>
            </div>
          </div>
        </div>

        <!-- 习题管理 -->
        <div class="card exercise-management">
          <div class="card-header">
            <h3>习题管理</h3>
            <el-button type="primary" size="small" @click="router.push('/manager/exercise')">管理习题</el-button>
          </div>
          <div class="card-content">
            <div class="exercise-stats">
              <div class="exercise-type-item">
                <div class="exercise-type-icon choice-icon">
                  <i class="el-icon-check"></i>
                </div>
                <div class="exercise-type-info">
                  <div class="exercise-type-value">{{ data.teacherStats.choiceExercises || 0 }}</div>
                  <div class="exercise-type-label">选择题</div>
                </div>
              </div>

              <div class="exercise-type-item">
                <div class="exercise-type-icon fill-icon">
                  <i class="el-icon-edit"></i>
                </div>
                <div class="exercise-type-info">
                  <div class="exercise-type-value">{{ data.teacherStats.fillExercises || 0 }}</div>
                  <div class="exercise-type-label">填空题</div>
                </div>
              </div>

              <div class="exercise-type-item">
                <div class="exercise-type-icon judge-icon">
                  <i class="el-icon-circle-check"></i>
                </div>
                <div class="exercise-type-info">
                  <div class="exercise-type-value">{{ data.teacherStats.judgeExercises || 0 }}</div>
                  <div class="exercise-type-label">判断题</div>
                </div>
              </div>

              <div class="exercise-type-item">
                <div class="exercise-type-icon essay-icon">
                  <i class="el-icon-document"></i>
                </div>
                <div class="exercise-type-info">
                  <div class="exercise-type-value">
                    {{ data.teacherStats.totalExercises - data.teacherStats.choiceExercises -
                      data.teacherStats.fillExercises - data.teacherStats.judgeExercises || 0 }}
                  </div>
                  <div class="exercise-type-label">简答题</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学生首页内容 -->
    <div v-else>
      <!-- 添加轮播图 -->
      <div class="carousel-container">
        <el-carousel :interval="4000" type="card" height="400px">
          <el-carousel-item v-for="(item, index) in data.carouselImages" :key="index">
            <img :src="item.url" :alt="item.title" class="carousel-image">
            <div class="carousel-caption">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 学习进度 -->
      <div class="dashboard-row">
        <div class="stat-card">
          <div class="stat-icon course-icon">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.studentStats.totalCourses || 0 }}</div>
            <div class="stat-label">学习课程</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon exercise-icon">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.studentStats.totalExercises || 0 }}</div>
            <div class="stat-label">习题总数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon progress-icon">
            <i class="el-icon-data-line"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.studentStats.answeredExercises || 0 }}</div>
            <div class="stat-label">已答题数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon correct-icon">
            <i class="el-icon-circle-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ data.studentStats.correctRate || 0 }}%</div>
            <div class="stat-label">正确率</div>
          </div>
        </div>
      </div>

      <!-- 最近习题和推荐课程 -->
      <div class="dashboard-row">
        <!-- 最近习题 -->
        <div class="card recent-exercises">
          <div class="card-header">
            <h3>最近习题</h3>
            <el-button type="primary" size="small" @click="router.push('/manager/exerciseRecords')">查看记录</el-button>
          </div>
          <div class="card-content">
            <div v-if="data.recentExercises.length > 0" class="exercise-list">
              <div v-for="exercise in data.recentExercises" :key="exercise.id" class="exercise-item"
                @click="viewExercise(exercise.exerciseId)">
                <div class="exercise-info">
                  <div class="exercise-title">{{ exercise.exercise?.title || '未知习题' }}</div>
                  <div class="exercise-meta">
                    <el-tag size="small" :type="getTypeType(exercise.exercise?.type)">
                      {{ getTypeLabel(exercise.exercise?.type) }}
                    </el-tag>
                    <el-tag size="small" :type="exercise.isCorrect === 1 ? 'success' : 'danger'">
                      {{ exercise.isCorrect === 1 ? '正确' : '错误' }}
                    </el-tag>
                  </div>
                </div>
                <div class="exercise-time">{{ formatDate(exercise.createTime) }}</div>
              </div>
            </div>
            <div v-else class="empty-tip">
              <el-empty description="暂无习题记录" />
              <el-button type="primary" @click="router.push('/manager/exercises')">开始练习</el-button>
            </div>
          </div>
        </div>

        <!-- 推荐课程 -->
        <div class="card recommended-courses">
          <div class="card-header">
            <h3>推荐课程</h3>
            <el-button type="primary" size="small" @click="router.push('/manager/studentCourses')">更多课程</el-button>
          </div>
          <div class="card-content">
            <div v-if="data.recommendedCourses.length > 0" class="course-list">
              <div v-for="course in data.recommendedCourses" :key="course.id" class="course-item"
                @click="viewCourse(course.id)">
                <div class="course-cover">
                  <el-image :src="course.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"></el-image>
                </div>
                <div class="course-info">
                  <div class="course-title">{{ course.name }}</div>
                  <div class="course-teacher">{{ course.teacherName }}</div>
                </div>
              </div>
            </div>
            <div v-else class="empty-tip">
              <el-empty description="暂无推荐课程" />
              <el-button type="primary" @click="router.push('/manager/studentCourses')">浏览课程</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import request from "@/utils/request";
import { reactive, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from 'vue-router';

// 首先在顶部导入图片
import carousel1 from '@/assets/imgs/carousel1.jpg'
import carousel2 from '@/assets/imgs/carousel2.jpg'
import carousel3 from '@/assets/imgs/carousel3.jpg'
import carousel4 from '@/assets/imgs/carousel4.jpg'

const router = useRouter();

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  currentDateTime: new Date(),
  timer: null,
  stats: {
    totalUsers: 0,
    totalCourses: 0,
    totalExercises: 0,
    totalPosts: 0,
    publishedCourses: 0,
    totalContents: 0,
    adminCount: 0,
    teacherCount: 0,
    studentCount: 0
  },
  activities: [
    {
      id: 1,
      type: 'course',
      title: '新课程发布',
      description: '教师小明发布了新课程《Java高级编程》',
      createTime: new Date().toISOString()
    },
    {
      id: 2,
      type: 'exercise',
      title: '习题更新',
      description: '教师小红更新了《数据结构》课程的习题',
      createTime: new Date(Date.now() - 3600000).toISOString()
    },
    {
      id: 3,
      type: 'post',
      title: '新动态',
      description: '学生小张发布了一条学习动态',
      createTime: new Date(Date.now() - 7200000).toISOString()
    }
  ],
  teacherStats: {
    totalCourses: 0,
    totalExercises: 0,
    totalStudents: 0,
    correctRate: 0
  },
  teacherCourses: [],
  studentStats: {
    totalCourses: 0,
    totalExercises: 0,
    answeredExercises: 0,
    correctRate: 0
  },
  recentExercises: [],
  recommendedCourses: [],
  carouselImages: [
    {
      url: carousel1,
      title: '在线学习',
      description: '随时随地，想学就学'
    },
    {
      url: carousel2,
      title: '优质课程',
      description: '海量精品课程，助你成长'
    },
    {
      url: carousel3,
      title: '在线练习',
      description: '巩固知识，提升能力'
    },
    {
      url: carousel4,
      title: '学习社区',
      description: '分享交流，共同进步'
    }
  ]
});

// 计算属性：当前日期
const currentDate = computed(() => {
  const date = data.currentDateTime;
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
});

// 计算属性：当前时间
const currentTime = computed(() => {
  const date = data.currentDateTime;
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
});

// 获取欢迎消息
const getWelcomeMessage = () => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了，注意休息！';
  if (hour < 9) return '早上好，开始新的一天！';
  if (hour < 12) return '上午好，工作效率最佳时段！';
  if (hour < 14) return '中午好，适当休息一下吧！';
  if (hour < 18) return '下午好，继续加油！';
  if (hour < 22) return '晚上好，工作充电时间！';
  return '夜深了，注意休息！';
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 获取活动图标类名
const getActivityIconClass = (type) => {
  return `activity-icon-${type}`;
};

// 获取活动图标
const getActivityIcon = (type) => {
  const icons = {
    'course': 'el-icon-reading',
    'exercise': 'el-icon-edit-outline',
    'post': 'el-icon-chat-dot-round',
    'comment': 'el-icon-chat-line-round'
  };
  return icons[type] || 'el-icon-info';
};

// 获取百分比
const getPercentage = (value, total) => {
  if (!total) return 0;
  return Math.min(Math.round((value / total) * 100), 100);
};

// 格式化进度条
const format = (percentage) => {
  return '';
};

// 修改 loadStats 函数，直接调用后端接口
const loadStats = () => {
  // 获取用户统计数据
  request.get('/user/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 1
    }
  }).then(res => {
    if (res.code === '200') {
      data.stats.totalUsers = res.data.total || 0;

      // 计算不同角色的用户数量
      let adminCount = 0;
      let teacherCount = 0;
      let studentCount = 0;

      // 请求获取所有学生
      request.get('/user/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 100 // 假设用户数量不会太多
        }
      }).then(res => {
        if (res.code === '200' && res.data.list) {
          data.stats.studentCount = res.data.list.length;
        }
      });

      // 请求获取所有管理员
      request.get('/admin/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 100 // 假设用户数量不会太多
        }
      }).then(res => {
        if (res.code === '200' && res.data.list) {
          data.stats.adminCount = res.data.list.length;
        }
      });

      // 请求获取所有教师
      request.get('/teacher/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 100 // 假设用户数量不会太多
        }
      }).then(res => {
        if (res.code === '200' && res.data.list) {
          data.stats.teacherCount = res.data.list.length;
        }
      });
    }
  });

  // 获取课程统计数据
  request.get('/course/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 1
    }
  }).then(res => {
    if (res.code === '200') {
      data.stats.totalCourses = res.data.total || 0;

      // 获取已发布课程数量
      request.get('/course/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1,
          status: 1
        }
      }).then(res => {
        if (res.code === '200') {
          data.stats.publishedCourses = res.data.total || 0;
        }
      });

      // 获取课程内容数量
      request.get('/courseContent/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1
        }
      }).then(res => {
        if (res.code === '200') {
          data.stats.totalContents = res.data.total || 0;
        }
      });
    }
  });

  // 获取习题统计数据
  request.get('/exercise/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 1
    }
  }).then(res => {
    if (res.code === '200') {
      data.stats.totalExercises = res.data.total || 0;
    }
  });

  // 获取动态统计数据
  request.get('/post/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 1
    }
  }).then(res => {
    if (res.code === '200') {
      data.stats.totalPosts = res.data.total || 0;
    }
  });
};

// 加载最近活动
const loadActivities = () => {
  // 获取最近课程
  request.get('/course/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 3,
      orderBy: 'createTime desc'
    }
  }).then(res => {
    if (res.code === '200' && res.data.list && res.data.list.length > 0) {
      const courseActivities = res.data.list.map(course => ({
        id: 'course_' + course.id,
        type: 'course',
        title: '新课程发布',
        description: `教师${course.teacherName || ''}发布了新课程《${course.name}》`,
        createTime: course.createTime
      }));

      // 获取最近习题
      request.get('/exercise/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 3,
          orderBy: 'createTime desc'
        }
      }).then(res2 => {
        if (res2.code === '200' && res2.data.list && res2.data.list.length > 0) {
          const exerciseActivities = res2.data.list.map(exercise => ({
            id: 'exercise_' + exercise.id,
            type: 'exercise',
            title: '习题更新',
            description: `新习题《${exercise.title}》已添加`,
            createTime: exercise.createTime
          }));

          // 获取最近动态
          request.get('/post/selectPage', {
            params: {
              pageNum: 1,
              pageSize: 3,
              orderBy: 'createTime desc'
            }
          }).then(res3 => {
            if (res3.code === '200' && res3.data.list && res3.data.list.length > 0) {
              const postActivities = res3.data.list.map(post => ({
                id: 'post_' + post.id,
                type: 'post',
                title: '新动态',
                description: `${post.userName || '用户'}发布了一条学习动态`,
                createTime: post.createTime
              }));

              // 合并所有活动并按时间排序
              const allActivities = [...courseActivities, ...exerciseActivities, ...postActivities];
              allActivities.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

              // 取前5条
              data.activities = allActivities.slice(0, 5);
            } else {
              data.activities = [...courseActivities, ...exerciseActivities];
            }
          }).catch(() => {
            data.activities = [...courseActivities, ...exerciseActivities];
          });
        } else {
          data.activities = courseActivities;
        }
      }).catch(() => {
        data.activities = courseActivities;
      });
    }
  }).catch(() => {
    // 如果获取失败，保持空数组
    data.activities = [];
  });
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

// 查看习题详情
const viewExercise = (id) => {
  router.push(`/manager/exerciseDetail/${id}`);
};

// 查看课程详情
const viewCourse = (id) => {
  router.push(`/manager/courseDetail/${id}`);
};

// 管理课程内容
const manageCourseContent = (id) => {
  router.push(`/manager/courseContent/${id}`);
};

// 加载教师数据
const loadTeacherData = () => {
  // 获取教师课程
  request.get('/course/selectMyCourses').then(res => {
    if (res.code === '200') {
      data.teacherCourses = res.data || [];
      data.teacherStats.totalCourses = data.teacherCourses.length;
      data.teacherStats.publishedCourses = data.teacherCourses.filter(course => course.status === 1).length;

      // 获取课程内容数量
      let contentCount = 0;
      const courseIds = data.teacherCourses.map(course => course.id);

      if (courseIds.length > 0) {
        // 获取所有课程内容
        const promises = courseIds.map(courseId =>
          request.get('/courseContent/selectByCourseId/' + courseId)
        );

        Promise.all(promises).then(results => {
          results.forEach(res => {
            if (res.code === '200') {
              contentCount += res.data.length || 0;
            }
          });
          data.teacherStats.totalContents = contentCount;
        });
      }

      // 获取习题数量
      request.get('/exercise/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1
        }
      }).then(res => {
        if (res.code === '200') {
          data.teacherStats.totalExercises = res.data.total || 0;

          // 获取选择题数量
          request.get('/exercise/selectPage', {
            params: {
              pageNum: 1,
              pageSize: 1,
              type: 0 // 选择题
            }
          }).then(res => {
            if (res.code === '200') {
              data.teacherStats.choiceExercises = res.data.total || 0;
            }
          });

          // 获取填空题数量
          request.get('/exercise/selectPage', {
            params: {
              pageNum: 1,
              pageSize: 1,
              type: 1 // 填空题
            }
          }).then(res => {
            if (res.code === '200') {
              data.teacherStats.fillExercises = res.data.total || 0;
            }
          });

          // 获取判断题数量
          request.get('/exercise/selectPage', {
            params: {
              pageNum: 1,
              pageSize: 1,
              type: 2 // 判断题
            }
          }).then(res => {
            if (res.code === '200') {
              data.teacherStats.judgeExercises = res.data.total || 0;
            }
          });
        }
      });

      // 获取学生学习情况
      request.get('/exerciseRecord/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1,
          userId: data.user.id
        }
      }).then(res => {
        if (res.code === '200') {
          const totalRecords = res.data.total || 0;
          data.teacherStats.totalRecords = totalRecords;

          // 获取正确记录数
          request.get('/exerciseRecord/selectPage', {
            params: {
              pageNum: 1,
              pageSize: 1,
              userId: data.user.id,
              isCorrect: 1
            }
          }).then(res => {
            if (res.code === '200') {
              const correctRecords = res.data.total || 0;
              data.teacherStats.correctRecords = correctRecords;

              // 计算正确率
              if (totalRecords > 0) {
                data.teacherStats.correctRate = Math.round((correctRecords / totalRecords) * 100);
              }
            }
          });
        }
      });

      // 获取学习人数
      request.get('/user/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1,
          role: 'user'
        }
      }).then(res => {
        if (res.code === '200') {
          data.teacherStats.totalStudents = res.data.total || 0;
          data.teacherStats.activeStudents = Math.round(data.teacherStats.totalStudents * 0.7); // 假设70%的学生是活跃的
        }
      });
    }
  });
};

// 加载学生数据
const loadStudentData = () => {
  // 使用现有接口获取学生课程数据
  request.get('/course/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 1,
      status: 1 // 已发布的课程
    }
  }).then(res => {
    if (res.code === '200') {
      // 获取总课程数
      const totalCourses = res.data.total || 0;
      data.studentStats.totalCourses = totalCourses;

      // 获取习题总数
      request.get('/exercise/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1
        }
      }).then(res => {
        if (res.code === '200') {
          data.studentStats.totalExercises = res.data.total || 0;
        }
      });

      // 获取已答题数量和正确率
      request.get('/exerciseRecord/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1
        }
      }).then(res => {
        if (res.code === '200') {
          const totalRecords = res.data.total || 0;
          data.studentStats.answeredExercises = totalRecords;

          if (totalRecords > 0) {
            request.get('/exerciseRecord/selectPage', {
              params: {
                pageNum: 1,
                pageSize: 1,
                isCorrect: 1
              }
            }).then(res => {
              if (res.code === '200') {
                const correctRecords = res.data.total || 0;
                data.studentStats.correctRate = Math.round((correctRecords / totalRecords) * 100);
              }
            });
          } else {
            data.studentStats.correctRate = 0;
          }
        }
      });
    }
  });

  // 加载最近习题
  request.get('/exerciseRecord/getRecentRecords', {
    params: { limit: 5 }
  }).then(res => {
    if (res.code === '200') {
      data.recentExercises = res.data || [];
    }
  });

  // 加载推荐课程
  request.get('/course/selectPage', {
    params: {
      pageNum: 1,
      pageSize: 4,
      status: 1,
      orderBy: 'createTime desc'
    }
  }).then(res => {
    if (res.code === '200') {
      data.recommendedCourses = res.data.list || [];
    }
  });
};

onMounted(() => {
  // 更新时间
  data.timer = setInterval(() => {
    data.currentDateTime = new Date();
  }, 1000);

  // 根据角色加载数据
  if (data.user.role === 'admin') {
    loadStats();
    loadActivities();
  } else if (data.user.role === 'teacher') {
    loadTeacherData();
  } else {
    loadStudentData();
  }
});

onBeforeUnmount(() => {
  if (data.timer) {
    clearInterval(data.timer);
  }
});
</script>

<style scoped>
.welcome-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.welcome-text h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
  color: #333;
}

.welcome-text p {
  margin: 0;
  color: #666;
}

.date-time {
  text-align: right;
}

.date {
  font-size: 16px;
  color: #666;
  margin-bottom: 5px;
}

.time {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.dashboard-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.user-icon {
  background-color: #409EFF;
}

.course-icon {
  background-color: #67C23A;
}

.exercise-icon {
  background-color: #E6A23C;
}

.post-icon {
  background-color: #F56C6C;
}

.stat-info {
  flex-grow: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.content-monitor,
.user-stats {
  grid-column: span 2;
}

.recent-activities {
  margin-bottom: 20px;
}

.card-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.card-content {
  padding: 20px;
}

.monitor-item {
  margin-bottom: 20px;
}

.monitor-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.monitor-label {
  font-size: 16px;
  color: #333;
}

.monitor-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.user-type-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.user-type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.user-type-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  margin-bottom: 10px;
}

.admin-icon {
  background-color: #409EFF;
}

.teacher-icon {
  background-color: #67C23A;
}

.student-icon {
  background-color: #E6A23C;
}

.user-type-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.user-type-label {
  font-size: 14px;
  color: #666;
}

.quick-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: transform 0.3s;
}

.activity-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.activity-icon-course {
  background-color: #67c23a;
}

.activity-icon-exercise {
  background-color: #e6a23c;
}

.activity-icon-post {
  background-color: #409eff;
}

.activity-icon-comment {
  background-color: #909399;
}

.activity-content {
  flex-grow: 1;
}

.activity-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.activity-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
  line-height: 1.5;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
}

@media (max-width: 1200px) {
  .dashboard-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-monitor,
  .user-stats {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .dashboard-row {
    grid-template-columns: 1fr;
  }

  .welcome-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .welcome-right {
    margin-top: 15px;
    align-self: flex-end;
  }
}

/* 教师首页样式 */
.course-management,
.exercise-management {
  grid-column: span 2;
}

.recent-courses {
  margin-bottom: 20px;
}

.recent-course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: transform 0.3s;
}

.recent-course-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-time {
  font-size: 12px;
  color: #999;
}

.course-actions {
  display: flex;
  gap: 10px;
}

.exercise-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.exercise-type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: transform 0.3s;
}

.exercise-type-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.exercise-type-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  margin-bottom: 10px;
}

.choice-icon {
  background-color: #67C23A;
}

.fill-icon {
  background-color: #E6A23C;
}

.judge-icon {
  background-color: #409EFF;
}

.essay-icon {
  background-color: #F56C6C;
}

.exercise-type-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.exercise-type-label {
  font-size: 14px;
  color: #666;
}

.record-icon {
  background-color: #909399;
}

/* 学生首页样式 */
.complete-icon {
  background-color: #67C23A;
}

.progress-icon {
  background-color: #E6A23C;
}

.correct-icon {
  background-color: #F56C6C;
}

.recent-exercises,
.recommended-courses {
  grid-column: span 2;
}

.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.exercise-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.exercise-info {
  flex-grow: 1;
}

.exercise-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.exercise-meta {
  display: flex;
  gap: 10px;
}

.exercise-time {
  font-size: 12px;
  color: #999;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.course-item {
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
}

.course-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 120px;
  overflow: hidden;
}

.course-cover .el-image {
  width: 100%;
  height: 100%;
}

.course-info {
  padding: 15px;
}

.course-teacher {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

/* 响应式调整 */
@media (max-width: 1200px) {

  .course-management,
  .exercise-management,
  .recent-exercises,
  .recommended-courses {
    grid-column: span 1;
  }

  .exercise-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .course-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .exercise-stats {
    grid-template-columns: 1fr;
  }
}

.carousel-container {
  margin: -10px -24px 20px;
  /* 抵消父容器的内边距 */
  background-color: #f0f2f5;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.carousel-caption {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}

.carousel-caption h3 {
  font-size: 24px;
  margin: 0 0 8px;
}

.carousel-caption p {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

/* Element Plus 轮播图自定义样式 */
:deep(.el-carousel__item) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-carousel__item--card) {
  border: none;
}

:deep(.el-carousel__mask) {
  border-radius: 8px;
  opacity: 0.5;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .carousel-container {
    margin: -10px -16px 16px;
  }

  :deep(.el-carousel) {
    height: 300px !important;
  }

  .carousel-caption h3 {
    font-size: 20px;
  }

  .carousel-caption p {
    font-size: 14px;
  }
}
</style>
