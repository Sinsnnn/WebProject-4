<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input v-model="data.search.name" prefix-icon="Search"
        style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入课程名称"></el-input>
      <el-button type="info" plain @click="load">查询</el-button>
      <el-button type="warning" plain style="margin-right: 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" plain @click="handleAdd">新增课程</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" strip>
        <el-table-column label="课程名称" prop="name"></el-table-column>
        <el-table-column label="课程描述" prop="description" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="cover" label="封面">
          <template v-slot="scope">
            <el-image style="width: 80px; height: 45px; display: block" v-if="scope.row.cover" :src="scope.row.cover"
              :preview-src-list="[scope.row.cover]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
            <el-tag v-else type="info">未发布</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" align="center" width="350" fixed="right">
          <template v-slot="scope">
            <el-button type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" plain @click="handleContent(scope.row.id)">内容管理</el-button>
            <el-button type="warning" plain @click="handleExercise(scope.row.id)">习题管理</el-button>
            <el-button type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="card" v-if="data.total">
      <el-pagination @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize"
        v-model:current-page="data.pageNum" :total="data.total" />
    </div>

    <el-dialog title="课程信息" v-model="data.formVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="80px" style="padding: 20px 30px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="data.form.description" type="textarea" :rows="4" placeholder="请输入课程描述"></el-input>
        </el-form-item>
        <el-form-item label="课程封面" prop="cover">
          <el-upload class="avatar-uploader" :action="'http://localhost:9090/files/upload'"
            :on-success="coverSuccessUpload" list-type="picture-card">
            <el-icon>
              <Plus />
            </el-icon>
          </el-upload>
          <el-image v-if="data.form.cover" style="width: 100px; height: 100px" :src="data.form.cover"></el-image>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="data.form.status">
            <el-radio :label="0">未发布</el-radio>
            <el-radio :label="1">已发布</el-radio>
          </el-radio-group>
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
import { reactive } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();
const baseApi = "course";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  tableData: [],
  total: 0,
  pageNum: 1, // 当前的页码
  pageSize: 5, // 每页的个数
  formVisible: false,
  form: {
    status: 0
  },
  search: {},
});

// 加载表格数据
const load = () => {
  request
    .get(baseApi + "/selectMyCourses")
    .then((res) => {
      data.tableData = res.data || [];
      data.total = data.tableData.length;
    });
};

// 打开新增弹窗
const handleAdd = () => {
  data.form = {
    status: 0
  };
  data.formVisible = true;
};

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 跳转到课程内容管理页面
const handleContent = (id) => {
  router.push(`/manager/courseContent/${id}`);
};

// 跳转到课程习题管理页面
const handleExercise = (id) => {
  router.push(`/manager/courseExercise/${id}`);
};

// 新增
const add = () => {
  request.post(baseApi + "/add", data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("添加成功");
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 更新
const update = () => {
  request.put(baseApi + "/update", data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("更新成功");
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 删除
const del = (id) => {
  ElMessageBox.confirm("删除后数据无法恢复，您确定删除吗?", "删除确认", { type: "warning" }).then(res => {
    request.delete(baseApi + "/delete/" + id).then(res => {
      if (res.code === "200") {
        ElMessage.success("删除成功");
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch(err => {
    console.error(err);
  });
};

const save = () => {
  if (!data.form.name) {
    ElMessage.warning("请输入课程名称");
    return;
  }
  data.form.id ? update() : add();
};

const reset = () => {
  data.search = {};
  load();
};

const coverSuccessUpload = (res) => {
  data.form.cover = res.data;
};

// 加载模块数据
load();
</script>