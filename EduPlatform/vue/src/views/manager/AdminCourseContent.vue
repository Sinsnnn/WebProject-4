<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="back">返回课程列表</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <h3>{{ data.course.name }}</h3>
      <p>{{ data.course.description }}</p>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" strip>
        <el-table-column label="章节标题" prop="title"></el-table-column>
        <el-table-column label="章节内容" prop="content"></el-table-column>
        <el-table-column label="排序" prop="sort" width="80"></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template v-slot="scope">
            <el-button type="primary" plain @click="viewContent(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="章节详情" v-model="data.contentVisible" width="60%">
      <div class="content-detail">
        <h2>{{ data.currentContent.title }}</h2>
        <div class="content-text" v-html="data.currentContent.content"></div>
        <div v-if="data.currentContent.video" class="content-video">
          <h3>视频资料</h3>
          <video :src="data.currentContent.video" controls style="width: 100%"></video>
        </div>
        <div v-if="data.currentContent.attachment" class="content-attachment">
          <h3>附件资料</h3>
          <a :href="data.currentContent.attachment" target="_blank" download>
            <el-button type="primary" size="small" plain>
              <el-icon>
                <Download />
              </el-icon>
              下载附件
            </el-button>
          </a>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { useRouter, useRoute } from "vue-router";
import { Download } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const data = reactive({
  course: {},
  tableData: [],
  contentVisible: false,
  currentContent: {}
});

// 加载课程信息
const loadCourse = () => {
  request.get("/course/selectById/" + route.params.id).then(res => {
    if (res.code === "200") {
      data.course = res.data;
    } else {
      ElMessage.error(res.msg || "获取课程信息失败");
    }
  });
};

// 加载课程内容
const load = () => {
  request.get("/courseContent/selectByCourseId/" + route.params.id).then(res => {
    if (res.code === "200") {
      data.tableData = res.data;
    } else {
      ElMessage.error(res.msg || "获取课程内容失败");
    }
  });
};

// 查看内容详情
const viewContent = (row) => {
  data.currentContent = row;
  data.contentVisible = true;
};

// 返回课程列表
const back = () => {
  router.push("/manager/adminCourses");
};

onMounted(() => {
  loadCourse();
  load();
});
</script>

<style scoped>
.content-detail {
  padding: 20px;
}

.content-detail h2 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.content-text {
  margin-bottom: 20px;
  line-height: 1.6;
}

.content-video,
.content-attachment {
  margin-top: 20px;
}

.content-video h3,
.content-attachment h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}
</style>