<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <h2>课程查看</h2>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="data.queryParams">
        <el-form-item label="课程名称">
          <el-input v-model="data.queryParams.name" placeholder="请输入课程名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="教师名称">
          <el-input v-model="data.queryParams.teacherName" placeholder="请输入教师名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态" style="width: 200px;">
          <el-select v-model="data.queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未发布" :value="0"></el-option>
            <el-option label="已发布" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCourses">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.courses" stripe>
        <el-table-column label="课程封面" width="100">
          <template #default="scope">
            <el-image :src="scope.row.cover || '/src/assets/imgs/course-default.jpg'" fit="cover"
              style="width: 80px; height: 45px; border-radius: 4px;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="课程名称" prop="name"></el-table-column>
        <el-table-column label="教师名称" prop="teacherName"></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain @click="viewCourse(scope.row.id)">查看详情</el-button>
            <el-button type="success" plain @click="viewContents(scope.row.id)">查看内容</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="data.total > 0" class="pagination">
        <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
          v-model:current-page="data.pageNum" @current-change="loadCourses" />
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
  courses: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  queryParams: {
    name: '',
    teacherName: '',
    status: null
  }
});

// 加载课程列表
const loadCourses = () => {
  request.get('/course/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.queryParams.name,
      teacherName: data.queryParams.teacherName,
      status: data.queryParams.status
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

// 重置查询条件
const resetQuery = () => {
  data.queryParams = {
    name: '',
    teacherName: '',
    status: null
  };
  data.pageNum = 1;
  loadCourses();
};

// 查看课程详情
const viewCourse = (id) => {
  router.push(`/manager/courseDetail/${id}`);
};

// 查看课程内容
const viewContents = (id) => {
  router.push(`/manager/adminCourseContent/${id}`);
};

onMounted(() => {
  loadCourses();
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