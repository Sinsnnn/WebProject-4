<template>
  <div v-if="data.user.role === 'user'" class="student-container">
    <!-- 学生端顶部导航 -->
    <div class="student-header">
      <div class="header-left">
        <img src="@/assets/imgs/logo.png" alt="logo" />
        <span class="title">在线学习平台</span>
      </div>

      <!-- 导航菜单 -->
      <div class="header-center">
        <div class="nav-container">
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            router
            class="nav-menu"
            background-color="#23262f"
            :ellipsis="false"
          >
            <el-menu-item index="/manager/home">
              <el-icon>
                <HomeFilled />
              </el-icon>
              首页
            </el-menu-item>
            <el-menu-item index="/manager/studentCourses">
              <el-icon>
                <Collection />
              </el-icon>
              课程学习
            </el-menu-item>
            <el-menu-item index="/manager/exercises">
              <el-icon>
                <Reading />
              </el-icon>
              在线练习
            </el-menu-item>
            <el-menu-item index="/manager/post">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              学习社区
            </el-menu-item>
            <el-menu-item index="/manager/myPost">
              <el-icon>
                <ChatDotSquare />
              </el-icon>
              我的动态
            </el-menu-item>
          </el-menu>
        </div>
      </div>

      <!-- 右侧搜索和用户信息 -->
      <div class="header-right">
        <GlobalSearch />
        <el-dropdown trigger="click">
          <div class="user-info">
            <img
              :src="data.user?.avatar || '@/assets/imgs/avatar.png'"
              alt="avatar"
            />
            <span>{{ data.user?.name }}</span>
            <el-icon class="el-icon--right">
              <CaretBottom />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown">
              <el-dropdown-item @click="goToPerson">
                <el-icon> <User /> </el-icon>个人资料
              </el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/myPost')">
                <el-icon> <ChatDotRound /> </el-icon>我的动态
              </el-dropdown-item>
              <el-dropdown-item
                @click="router.push('/manager/exerciseRecords')"
              >
                <el-icon> <List /> </el-icon>答题记录
              </el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/password')">
                <el-icon> <Lock /> </el-icon>修改密码
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon> <SwitchButton /> </el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="student-main">
      <router-view @updateUser="updateUser" />
    </div>
  </div>

  <!-- 管理员和教师端保持原有布局 -->
  <div v-else class="manager-container">
    <div class="manager-header">
      <div class="manager-header-left">
        <img style="border-radius: 50%" src="@/assets/imgs/logo.png" alt="" />
        <div class="title">管理系统</div>
      </div>
      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item to="/manager/home">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            router.currentRoute.value.meta.name
          }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 添加全局搜索组件 -->
      <div class="manager-header-search" v-if="data.user.role === 'user'">
        <GlobalSearch />
      </div>

      <div class="manager-header-right">
        <el-dropdown style="cursor: pointer">
          <div style="padding-right: 20px; display: flex; align-items: center">
            <img
              v-if="data.user.avatar"
              :src="data.user?.avatar"
              alt=""
              style="
                width: 40px;
                height: 40px;
                display: block;
                border-radius: 50%;
              "
            />
            <img
              v-else
              src="@/assets/imgs/avatar.png"
              alt=""
              style="
                width: 40px;
                height: 40px;
                display: block;
                border-radius: 50%;
              "
            />
            <span style="margin-left: 5px">{{ data.user?.name }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click.native="goToPerson"
                >个人资料</el-dropdown-item
              >
              <el-dropdown-item @click.native="router.push('/manager/password')"
                >修改密码</el-dropdown-item
              >
              <el-dropdown-item @click.native="logout"
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div style="display: flex">
      <div class="manager-main-left">
        <el-menu
          :default-active="router.currentRoute.value.path"
          :default-openeds="['info', 'user', 'sys', 'post']"
          router
        >
          <el-menu-item index="/manager/home">
            <el-icon><home-filled /></el-icon><span>系统首页</span>
          </el-menu-item>
          <el-sub-menu index="info" v-if="data.user.role === 'teacher'">
            <template #title
              ><el-icon> <Menu /> </el-icon><span>信息管理</span></template
            >
            <el-menu-item index="/manager/course">课程管理</el-menu-item>
            <el-menu-item index="/manager/exercise">习题管理</el-menu-item>
          </el-sub-menu>

          <!-- 用户菜单 -->
          <el-sub-menu index="learning" v-if="data.user.role === 'user'">
            <template #title
              ><el-icon> <Collection /> </el-icon
              ><span>课程学习</span></template
            >
            <el-menu-item index="/manager/studentCourses"
              >课程列表</el-menu-item
            >
          </el-sub-menu>

          <el-sub-menu index="post" v-if="data.user.role === 'user'">
            <template #title
              ><el-icon> <ChatDotRound /> </el-icon
              ><span>动态交流</span></template
            >
            <el-menu-item index="/manager/post">学习动态</el-menu-item>
            <el-menu-item index="/manager/myPost">我的动态</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="exercise" v-if="data.user.role === 'user'">
            <template #title
              ><el-icon> <Reading /> </el-icon><span>在线答题</span></template
            >
            <el-menu-item index="/manager/exercises">习题练习</el-menu-item>
            <el-menu-item index="/manager/exerciseRecords"
              >答题记录</el-menu-item
            >
          </el-sub-menu>

          <el-sub-menu index="user" v-if="data.user.role === 'admin'">
            <template #title
              ><el-icon> <UserFilled /> </el-icon
              ><span>用户信息</span></template
            >
            <el-menu-item
              v-if="data.user.role === 'admin'"
              index="/manager/admin"
              >管理员</el-menu-item
            >
            <el-menu-item
              v-if="data.user.role === 'admin'"
              index="/manager/user"
              >学生</el-menu-item
            >
            <el-menu-item
              v-if="data.user.role === 'admin'"
              index="/manager/teacher"
              >教师</el-menu-item
            >
          </el-sub-menu>

          <!-- 在用户信息菜单后添加内容查看菜单 -->
          <el-sub-menu index="content" v-if="data.user.role === 'admin'">
            <template #title
              ><el-icon> <Document /> </el-icon><span>内容查看</span></template
            >
            <el-menu-item index="/manager/adminCourses">课程查看</el-menu-item>
            <el-menu-item index="/manager/adminExercises"
              >习题查看</el-menu-item
            >
            <el-menu-item index="/manager/adminPosts">动态管理</el-menu-item>
          </el-sub-menu>

          <!-- 系统管理 -->
          <el-sub-menu index="sys">
            <template #title
              ><el-icon> <Setting /> </el-icon><span>系统管理</span></template
            >
            <el-menu-item index="/manager/password">修改密码</el-menu-item>
            <el-menu-item @click.native="logout">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <div class="manager-main-right">
        <router-view @updateUser="updateUser" />
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  HomeFilled,
  UserFilled,
  ChatDotRound,
  Menu,
  Setting,
  Reading,
  Collection,
  Document,
  CaretBottom,
  User,
  ChatDotSquare,
  List,
  Lock,
  SwitchButton,
} from "@element-plus/icons-vue";
import { reactive, computed } from "vue";
import router from "@/router";
import GlobalSearch from "@/components/GlobalSearch.vue";
import { useRouter, useRoute } from "vue-router";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
});
console.log(data.user);
const goToPerson = () => {
  if (data.user.role === "admin") {
    router.push("/manager/pAdmin");
  }
  if (data.user.role === "user") {
    router.push("/manager/pUser");
  }
  if (data.user.role === "teacher") {
    router.push("/manager/pTeacher");
  }
};
const logout = () => {
  localStorage.removeItem("xm-user");
  router.push("/login");
};

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem("xm-user") || "{}"); // 重新获取下用户的最新信息
};

const route = useRoute();
const activeMenu = computed(() => route.path);
</script>

<style scoped>
@import "@/assets/css/manager.css";

.manager-header-search {
  margin-right: 20px;
}

/* 学生端新增样式 */
.student-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.student-header {
  height: 70px;
  background-color: #23262f;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  margin-right: 48px;
}

.header-left img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.header-left .title {
  margin-left: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;

  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  width: 100%;
}

.nav-container {
  width: 100%;
  max-width: 700px;
}

.nav-menu {
  background-color: transparent;
  border-bottom: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.nav-menu :deep(.el-menu-item) {
  height: 64px;
  line-height: 64px;
  font-size: 15px;
  padding: 0 16px;
  color: #e0e0e0;
  flex: 1;
  text-align: center;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #ffffff !important;
  background-color: transparent !important;
  border-bottom-color: #ffffff !important;
}

.nav-menu :deep(.el-menu-item) {
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.1) !important;
  --el-menu-hover-text-color: #ffffff !important;
  --el-menu-active-color: #ffffff !important;
}

.nav-menu :deep(.el-menu--horizontal .el-menu-item) {
  color: #e0e0e0;
}

.nav-menu :deep(.el-menu--horizontal) {
  border-bottom: none;
}

.nav-menu :deep(.el-menu-item) {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.nav-menu :deep(.el-menu-item .el-icon) {
  margin-right: 4px;
  font-size: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-right :deep(.el-input__inner) {
  background-color: rgba(255, 255, 255, 0.1);
  border: none;
  color: #009688;
}

.header-right :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

.header-right :deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.7);
}

.header-right :deep(.el-input-group__append) {
  background-color: rgba(255, 255, 255, 0.2);
  border: none;
  color: #ffffff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 24px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.user-info img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-info span {
  color: #ffffff;
  font-size: 14px;
  margin-right: 4px;
}

.user-info .el-icon {
  color: #ffffff;
}

.user-dropdown :deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  padding: 10px 16px;
}

.user-dropdown :deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 8px;
  font-size: 16px;
}

.student-main {
  padding: 84px 24px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 响应式布局优化 */
@media screen and (max-width: 768px) {
  .student-header {
    padding: 0 16px;
  }

  .header-left .title {
    display: none;
  }

  .header-center {
    display: none;
  }

  .header-right .global-search {
    display: none;
  }

  .user-info span {
    display: none;
  }
}

@media screen and (max-width: 480px) {
  .student-header {
    padding: 0 12px;
  }

  .header-left img {
    width: 32px;
    height: 32px;
  }
}

@media screen and (max-width: 992px) {
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
    font-size: 14px;
  }

  .nav-menu :deep(.el-menu-item .el-icon) {
    margin-right: 2px;
  }
}
</style>
