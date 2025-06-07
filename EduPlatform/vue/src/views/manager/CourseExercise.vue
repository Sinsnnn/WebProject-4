<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" plain @click="handleAdd">新增习题</el-button>
      <el-button type="info" plain @click="back">返回课程列表</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <h3>{{ data.course.name }} - 习题管理</h3>
      <p>{{ data.course.description }}</p>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-input v-model="data.search.title" prefix-icon="Search"
        style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入题目标题"></el-input>
      <el-select v-model="data.search.type" placeholder="题目类型" style="width: 120px; margin-right: 10px">
        <el-option :value="0" label="选择题"></el-option>
        <el-option :value="1" label="填空题"></el-option>
        <el-option :value="2" label="简答题"></el-option>
      </el-select>
      <el-select v-model="data.search.difficulty" placeholder="难度" style="width: 120px; margin-right: 10px">
        <el-option :value="0" label="简单"></el-option>
        <el-option :value="1" label="中等"></el-option>
        <el-option :value="2" label="困难"></el-option>
      </el-select>
      <el-button type="info" plain @click="load">查询</el-button>
      <el-button type="warning" plain style="margin-right: 10px" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" strip>
        <el-table-column label="题目标题" prop="title"></el-table-column>
        <el-table-column label="题目类型" prop="type">
          <template v-slot="scope">
            <el-tag v-if="scope.row.type === 0" type="success">选择题</el-tag>
            <el-tag v-else-if="scope.row.type === 1" type="info">填空题</el-tag>
            <el-tag v-else-if="scope.row.type === 2" type="warning">简答题</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="难度" prop="difficulty">
          <template v-slot="scope">
            <el-tag v-if="scope.row.difficulty === 0" type="success">简单</el-tag>
            <el-tag v-else-if="scope.row.difficulty === 1" type="warning">中等</el-tag>
            <el-tag v-else-if="scope.row.difficulty === 2" type="danger">困难</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template v-slot="scope">
            <el-button type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="习题信息" v-model="data.formVisible" width="60%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="80px" style="padding: 20px 30px">
        <el-form-item label="题目标题" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入题目标题"></el-input>
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-radio-group v-model="data.form.type">
            <el-radio :label="0">选择题</el-radio>
            <el-radio :label="1">填空题</el-radio>
            <el-radio :label="2">简答题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="data.form.difficulty">
            <el-radio :label="0">简单</el-radio>
            <el-radio :label="1">中等</el-radio>
            <el-radio :label="2">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="data.form.content" type="textarea" :rows="4" placeholder="请输入题目内容"></el-input>
        </el-form-item>
        <el-form-item v-if="data.form.type === 0" label="选项" prop="options">
          <div v-for="(option, index) in data.options" :key="index"
            style="margin-bottom: 10px; display: flex; align-items: center;">
            <span style="width: 30px;">{{ String.fromCharCode(65 + index) }}.</span>
            <el-input v-model="data.options[index]" placeholder="请输入选项内容"></el-input>
            <el-button type="danger" icon="Delete" circle @click="removeOption(index)"
              style="margin-left: 10px;"></el-button>
          </div>
          <el-button type="primary" @click="addOption" style="margin-top: 10px;">添加选项</el-button>
        </el-form-item>
        <el-form-item label="参考答案" prop="answer">
          <el-input v-if="data.form.type !== 0" v-model="data.form.answer" type="textarea" :rows="3"
            placeholder="请输入参考答案"></el-input>
          <el-select v-else v-model="data.form.answer" placeholder="选择正确答案" style="width: 100%">
            <el-option v-for="(option, index) in data.options" :key="index" :label="String.fromCharCode(65 + index)"
              :value="String.fromCharCode(65 + index)"></el-option>
          </el-select>
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
import { reactive, onMounted, watch } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const baseApi = "exercise";
const courseApi = "course";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  course: {},
  tableData: [],
  formVisible: false,
  form: {
    type: 0,
    difficulty: 0,
    courseId: parseInt(route.params.id)
  },
  search: {},
  options: ['', '', '', ''] // 默认4个选项
});

// 加载课程信息
const loadCourse = () => {
  request.get(courseApi + "/selectById/" + route.params.id).then((res) => {
    if (res.code === "200") {
      data.course = res.data;
    } else {
      ElMessage.error(res.msg || "获取课程信息失败");
    }
  });
};

// 加载表格数据
const load = () => {
  // 构建查询参数
  const params = {
    ...data.search,
    courseId: parseInt(route.params.id)
  };

  request.get(baseApi + "/selectAll", {
    params: params
  }).then((res) => {
    if (res.code === "200") {
      data.tableData = res.data || [];
    } else {
      ElMessage.error(res.msg || "获取习题列表失败");
    }
  }).catch(err => {
    console.error("获取习题列表出错", err);
    ElMessage.error("获取习题列表出错");
  });
};

// 返回课程列表
const back = () => {
  router.push("/manager/course");
};

// 打开新增弹窗
const handleAdd = () => {
  data.form = {
    type: 0,
    difficulty: 0,
    courseId: parseInt(route.params.id)
  };
  data.options = ['', '', '', '']; // 重置选项
  data.formVisible = true;
};

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));

  // 处理选项
  if (row.type === 0 && row.options) {
    try {
      data.options = JSON.parse(row.options);
    } catch (e) {
      data.options = ['', '', '', ''];
    }
  } else {
    data.options = ['', '', '', ''];
  }

  data.formVisible = true;
};

// 添加选项
const addOption = () => {
  if (data.options.length < 26) { // 最多26个选项 A-Z
    data.options.push('');
  } else {
    ElMessage.warning('最多只能添加26个选项');
  }
};

// 删除选项
const removeOption = (index) => {
  if (data.options.length > 2) { // 至少保留2个选项
    data.options.splice(index, 1);
  } else {
    ElMessage.warning('至少需要2个选项');
  }
};

// 新增
const add = () => {
  // 处理选项
  if (data.form.type === 0) {
    data.form.options = JSON.stringify(data.options);
  }

  request.post(baseApi + "/add", data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("添加成功");
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg || "添加失败");
    }
  });
};

// 更新
const update = () => {
  // 处理选项
  if (data.form.type === 0) {
    data.form.options = JSON.stringify(data.options);
  }

  request.put(baseApi + "/update", data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("更新成功");
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg || "更新失败");
    }
  });
};

// 删除
const del = (id) => {
  ElMessageBox.confirm("删除后数据无法恢复，您确定删除吗?", "删除确认", { type: "warning" }).then(() => {
    request.delete(baseApi + "/delete/" + id).then(res => {
      if (res.code === "200") {
        ElMessage.success("删除成功");
        load();
      } else {
        ElMessage.error(res.msg || "删除失败");
      }
    });
  }).catch(() => {
    // 用户取消删除，不做任何操作
  });
};

const save = () => {
  if (!data.form.title) {
    ElMessage.warning("请输入题目标题");
    return;
  }

  if (!data.form.content) {
    ElMessage.warning("请输入题目内容");
    return;
  }

  if (data.form.type === 0) {
    // 检查选项是否为空
    const emptyOptions = data.options.filter(option => !option.trim());
    if (emptyOptions.length > 0) {
      ElMessage.warning("选项内容不能为空");
      return;
    }

    if (!data.form.answer) {
      ElMessage.warning("请选择正确答案");
      return;
    }
  } else if (!data.form.answer) {
    ElMessage.warning("请输入参考答案");
    return;
  }

  data.form.id ? update() : add();
};

const reset = () => {
  data.search = {};
  load();
};

// 监听题目类型变化
watch(() => data.form.type, (newVal) => {
  if (newVal !== 0) {
    data.form.answer = ''; // 清空答案
  }
});

onMounted(() => {
  loadCourse();
  load();
});
</script>

<style scoped>
.el-tag {
  margin-right: 5px;
}
</style>