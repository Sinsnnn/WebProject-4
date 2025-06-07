<template>
  <div>
    <div
      class="card"
      style="margin-bottom: 5px"
      v-if="data.user.role === 'admin' || data.user.role === 'teacher'"
    >
      <el-button type="primary" plain @click="handleAdd">新增章节</el-button>
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
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template
            v-slot="scope"
            v-if="data.user.role === 'admin' || data.user.role === 'teacher'"
          >
            <el-button type="primary" plain @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button type="danger" plain @click="del(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      title="章节信息"
      v-model="data.formVisible"
      width="60%"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="data.form" label-width="80px" style="padding: 20px 30px">
        <el-form-item label="章节标题" prop="title">
          <el-input
            v-model="data.form.title"
            placeholder="请输入章节标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="data.form.sort"
            :min="0"
            :max="100"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="章节内容" prop="content">
          <div style="border: 1px solid #ccc">
            <textarea
              v-model="data.form.content"
              style="width: 100%; height: 300px; padding: 10px"
            ></textarea>
          </div>
        </el-form-item>
        <el-form-item label="视频链接">
          <el-upload
            class="upload-demo"
            action="http://localhost:9090/files/upload"
            :on-success="handleVideoSuccess"
            :file-list="videoFileList"
          >
            <el-button type="primary">上传视频</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持mp4、webm等格式，文件大小不超过500MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="附件">
          <el-upload
            class="upload-demo"
            action="http://localhost:9090/files/upload"
            :on-success="handleAttachmentSuccess"
            :file-list="attachmentFileList"
          >
            <el-button type="primary">上传附件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持pdf、doc、docx等格式，文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, watch } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const baseApi = "courseContent";
const courseApi = "course";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  course: {},
  tableData: [],
  formVisible: false,
  form: {
    courseId: parseInt(route.params.id),
    sort: 0,
    content: "",
  },
  videoFileList: ref([]),
  attachmentFileList: ref([]),
});

// 加载课程信息
const loadCourse = () => {
  request
    .get(courseApi + "/selectById/" + route.params.id)
    .then((res) => {
      if (res.code === "200") {
        data.course = res.data;
      } else {
        ElMessage.error(res.msg || "获取课程信息失败");
      }
    })
    .catch((err) => {
      console.error("获取课程信息出错", err);
      ElMessage.error("获取课程信息出错");
    });
};

// 加载章节数据
const load = () => {
  request
    .get(baseApi + "/selectByCourseId/" + route.params.id)
    .then((res) => {
      if (res.code === "200") {
        data.tableData = res.data || [];
      } else {
        ElMessage.error(res.msg || "获取章节列表失败");
      }
    })
    .catch((err) => {
      console.error("获取章节列表出错", err);
      ElMessage.error("获取章节列表出错");
    });
};

// 返回课程列表
const back = () => {
  router.push("/manager/course");
};

// 打开新增弹窗
const handleAdd = () => {
  data.form = {
    courseId: parseInt(route.params.id),
    sort: 0,
    content: "",
  };
  data.formVisible = true;
};

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 新增
const add = () => {
  request
    .post(baseApi + "/add", data.form)
    .then((res) => {
      if (res.code === "200") {
        ElMessage.success("添加成功");
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || "添加失败");
      }
    })
    .catch((err) => {
      console.error("添加章节出错", err);
      ElMessage.error("添加章节出错");
    });
};

// 更新
const update = () => {
  request
    .put(baseApi + "/update", data.form)
    .then((res) => {
      if (res.code === "200") {
        ElMessage.success("更新成功");
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || "更新失败");
      }
    })
    .catch((err) => {
      console.error("更新章节出错", err);
      ElMessage.error("更新章节出错");
    });
};

// 删除
const del = (id) => {
  ElMessageBox.confirm("删除后数据无法恢复，您确定删除吗?", "删除确认", {
    type: "warning",
  })
    .then(() => {
      request
        .delete(baseApi + "/delete/" + id)
        .then((res) => {
          if (res.code === "200") {
            ElMessage.success("删除成功");
            load();
          } else {
            ElMessage.error(res.msg || "删除失败");
          }
        })
        .catch((err) => {
          console.error("删除章节出错", err);
          ElMessage.error("删除章节出错");
        });
    })
    .catch(() => {
      // 用户取消删除，不做任何操作
    });
};

const save = () => {
  if (!data.form.title) {
    ElMessage.warning("请输入章节标题");
    return;
  }
  data.form.id ? update() : add();
};

// 添加处理视频和附件上传的方法
const handleVideoSuccess = (response) => {
  if (response.code === "200") {
    data.form.video = response.data;
    data.videoFileList = [{ name: "视频文件", url: response.data }];
  }
};

const handleAttachmentSuccess = (response) => {
  if (response.code === "200") {
    data.form.attachment = response.data;
    data.attachmentFileList = [{ name: "附件文件", url: response.data }];
  }
};

// 在表单初始化时设置文件列表
const initFileList = () => {
  if (data.form.video) {
    data.videoFileList = [{ name: "视频文件", url: data.form.video }];
  }
  if (data.form.attachment) {
    data.attachmentFileList = [{ name: "附件文件", url: data.form.attachment }];
  }
};

// 在编辑时调用初始化文件列表
watch(
  () => data.form,
  (newVal) => {
    if (newVal.id) {
      initFileList();
    }
  },
  { deep: true }
);

onMounted(() => {
  loadCourse();
  load();
});
</script>

<style>
textarea {
  resize: vertical;
  font-family: Arial, sans-serif;
  line-height: 1.5;
  border: none;
  outline: none;
}
</style>