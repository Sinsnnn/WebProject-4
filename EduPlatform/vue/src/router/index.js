import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/manager',
      name: 'Manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/manager/login',
      children: [
        { path: 'password', meta: { name: '修改密码' }, component: () => import('@/views/manager/Password.vue') },
        { path: 'home', meta: { name: '系统首页' }, component: () => import('@/views/manager/Home.vue') },
        { path: 'admin', meta: { name: '管理员' }, component: () => import("@/views/manager/Admin.vue") },
        { path: 'user', meta: { name: '用户' }, component: () => import("@/views/manager/User.vue") },
        { path: 'teacher', meta: { name: '教师' }, component: () => import("@/views/manager/Teacher.vue") },
        { path: 'pAdmin', meta: { name: '个人信息' }, component: () => import("@/views/manager/pAdmin.vue") },
        { path: 'pUser', meta: { name: '个人信息' }, component: () => import("@/views/manager/pUser.vue") },
        { path: 'pTeacher', meta: { name: '个人信息' }, component: () => import("@/views/manager/pTeacher.vue") },
        { path: 'course', meta: { name: '课程管理' }, component: () => import("@/views/manager/Course.vue") },
        { path: 'courseContent/:id', meta: { name: '课程内容' }, component: () => import("@/views/manager/CourseContent.vue") },
        { path: 'exercise', meta: { name: '习题管理' }, component: () => import("@/views/manager/Exercise.vue") },
        { path: 'courseExercise/:id', meta: { name: '课程习题' }, component: () => import("@/views/manager/CourseExercise.vue") },
        { path: 'post', meta: { name: '学习动态' }, component: () => import('@/views/manager/Post.vue') },
        { path: 'myPost', meta: { name: '我的动态' }, component: () => import('@/views/manager/MyPost.vue') },
        { path: 'postDetail/:id', meta: { name: '动态详情' }, component: () => import('@/views/manager/PostDetail.vue') },
        { path: 'search', meta: { name: '搜索结果' }, component: () => import('@/views/manager/Search.vue') },
        { path: 'courseDetail/:id', meta: { name: '课程详情' }, component: () => import('@/views/manager/CourseDetail.vue') },
        { path: 'exerciseDetail/:id', meta: { name: '习题详情' }, component: () => import('@/views/manager/ExerciseDetail.vue') },
        { path: 'exercises', meta: { name: '在线答题' }, component: () => import('@/views/manager/Exercises.vue') },
        { path: 'exerciseRecords', meta: { name: '答题记录' }, component: () => import('@/views/manager/ExerciseRecords.vue') },
        { path: 'studentCourses', meta: { name: '课程列表' }, component: () => import('@/views/manager/StudentCourses.vue') },
        { path: 'adminCourses', meta: { name: '课程查看' }, component: () => import('@/views/manager/AdminCourses.vue') },
        { path: 'adminExercises', meta: { name: '习题查看' }, component: () => import('@/views/manager/AdminExercises.vue') },
        { path: 'adminCourseContent/:id', meta: { name: '课程内容查看' }, component: () => import('@/views/manager/AdminCourseContent.vue') },
        { path: 'adminExerciseDetail/:id', meta: { name: '习题详情查看' }, component: () => import('@/views/manager/AdminExerciseDetail.vue') },
        { path: 'adminPosts', meta: { name: '动态管理' }, component: () => import('@/views/manager/AdminPosts.vue') },
        { path: 'adminPostDetail/:id', meta: { name: '动态详情' }, component: () => import('@/views/manager/AdminPostDetail.vue') },
      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/register', component: () => import("@/views/Register.vue") },
    { path: '/404', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404', hidden: true }
  ]
})

export default router
