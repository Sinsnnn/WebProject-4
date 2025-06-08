<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" plain @click="handleAdd">发布动态</el-button>
    </div>

    <div class="post-list">
      <div v-for="post in data.posts" :key="post.id" class="post-card">
        <div class="post-header">
          <div class="user-info">
            <img
              :src="post.user?.avatar || '/src/assets/imgs/avatar.png'"
              alt="avatar"
              class="avatar"
            />
            <div>
              <div class="username">{{ post.user?.name || "用户" }}</div>
              <div class="time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>
          <div v-if="post.userId === data.user.id" class="post-actions">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-icon>
                  <More />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleDelete(post.id)"
                    >删除</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="post-content" @click="goToDetail(post.id)">
          <div class="text">{{ post.content }}</div>
          <div v-if="post.images" class="images">
            <el-image
              v-for="(img, index) in JSON.parse(post.images)"
              :key="index"
              :src="img"
              :preview-src-list="JSON.parse(post.images)"
              fit="cover"
              class="image"
            />
          </div>
        </div>
        <div class="post-footer">
          <div class="action-btn" @click="toggleLike(post)">
            <el-icon :color="post.isLiked ? '#409EFF' : ''">
              <component :is="post.isLiked ? 'Star' : 'StarFilled'" />
            </el-icon>
            <span :class="{ active: post.isLiked }">{{
              post.likeCount || 0
            }}</span>
          </div>
          <div class="action-btn" @click="goToDetail(post.id)">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            <span>{{ post.commentCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div v-if="data.total > 0" class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="data.total"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          @current-change="loadPosts"
        />
      </div>

      <div v-if="data.posts.length === 0" class="empty-tip">
        暂无动态，快来发布第一条动态吧！
      </div>
    </div>

    <!-- 发布动态对话框 -->
    <el-dialog
      title="发布动态"
      v-model="data.dialogVisible"
      width="550px"
      :close-on-click-modal="false"
    >
      <div class="publish-dialog">
        <div class="dialog-header">
          <div class="user-info">
            <img
              :src="data.user?.avatar || '/src/assets/imgs/avatar.png'"
              alt="avatar"
              class="avatar"
            />
            <span class="username">{{ data.user?.name || "用户" }}</span>
          </div>
        </div>

        <div class="dialog-content">
          <el-input
            v-model="data.form.content"
            type="textarea"
            :rows="5"
            placeholder="分享你的学习心得..."
            resize="none"
            maxlength="500"
            show-word-limit
            class="content-input"
          ></el-input>

          <div class="upload-section">
            <el-upload
              action="http://localhost:9090/files/upload"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :limit="9"
              :on-exceed="handleExceed"
              multiple
              class="image-uploader"
            >
              <div class="upload-icon-container">
                <el-icon class="upload-icon">
                  <Plus />
                </el-icon>
                <div class="upload-text">添加图片</div>
              </div>
            </el-upload>
            <div class="upload-tip" v-if="data.form.images.length > 0">
              已上传 {{ data.form.images.length }} 张图片
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <el-button @click="data.dialogVisible = false" plain>取消</el-button>
          <el-button
            type="primary"
            @click="submitPost"
            :disabled="!data.form.content.trim()"
            >发布</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Star,
  StarFilled,
  ChatDotRound,
  More,
  Plus,
} from "@element-plus/icons-vue";
import request from "@/utils/request";

const router = useRouter();

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  posts: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  dialogVisible: false,
  form: {
    content: "",
    images: [],
  },
});

// 加载动态列表
const loadPosts = () => {
  request
    .get("/post/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
      },
    })
    .then((res) => {
      if (res.code === "200") {
        data.posts = res.data.list || [];
        data.total = res.data.total || 0;
      } else {
        ElMessage.error(res.msg || "获取动态列表失败");
      }
    });
};

// 点赞/取消点赞
const toggleLike = (post) => {
  request.post(`/post/toggleLike/${post.id}`).then((res) => {
    if (res.code === "200") {
      post.isLiked = res.data;
      post.likeCount = post.isLiked ? post.likeCount + 1 : post.likeCount - 1;
    } else {
      ElMessage.error(res.msg || "操作失败");
    }
  });
};

// 打开发布动态对话框
const handleAdd = () => {
  data.form = {
    content: "",
    images: [],
  };
  data.dialogVisible = true;
};

// 上传图片成功回调
const handleUploadSuccess = (res) => {
  if (res.code === "200") {
    data.form.images.push(res.data);
  } else {
    ElMessage.error(res.msg || "上传失败");
  }
};

// 移除图片
const handleRemove = (file) => {
  const index = data.form.images.indexOf(file.response.data);
  if (index !== -1) {
    data.form.images.splice(index, 1);
  }
};

// 处理图片上传超出限制
const handleExceed = () => {
  ElMessage.warning("最多只能上传9张图片");
};

// 提交发布动态
const submitPost = () => {
  if (!data.form.content.trim()) {
    ElMessage.warning("请输入动态内容");
    return;
  }

  const post = {
    content: data.form.content,
    images:
      data.form.images.length > 0 ? JSON.stringify(data.form.images) : null,
  };

  request.post("/post/add", post).then((res) => {
    if (res.code === "200") {
      ElMessage.success("发布成功");
      data.dialogVisible = false;
      loadPosts();
    } else {
      ElMessage.error(res.msg || "发布失败");
    }
  });
};

// 删除动态
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除这条动态吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      request.delete(`/post/delete/${id}`).then((res) => {
        if (res.code === "200") {
          ElMessage.success("删除成功");
          loadPosts();
        } else {
          ElMessage.error(res.msg || "删除失败");
        }
      });
    })
    .catch(() => {});
};

// 跳转到动态详情页
const goToDetail = (id) => {
  router.push(`/manager/postDetail/${id}`);
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return "";

  try {
    // 解析时间字符串
    const date = new Date(timeStr);

    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.warn("Invalid date:", timeStr);
      return timeStr;
    }

    const now = new Date();

    // 计算时间差（毫秒）
    const diff = now.getTime() - date.getTime();

    // 如果时间差为负数（未来时间），直接显示具体日期
    if (diff < 0) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");

      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }

    // 小于1分钟
    if (diff < 60 * 1000) {
      return "刚刚";
    }
    // 小于1小时
    if (diff < 60 * 60 * 1000) {
      return Math.floor(diff / (60 * 1000)) + "分钟前";
    }
    // 小于1天
    if (diff < 24 * 60 * 60 * 1000) {
      return Math.floor(diff / (60 * 60 * 1000)) + "小时前";
    }
    // 小于1周
    if (diff < 7 * 24 * 60 * 60 * 1000) {
      return Math.floor(diff / (24 * 60 * 60 * 1000)) + "天前";
    }

    // 格式化日期
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
  } catch (error) {
    console.error("Error formatting date:", error);
    return timeStr;
  }
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.post-list {
  margin-top: 20px;
}

.post-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
}

.username {
  font-weight: bold;
  font-size: 16px;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.post-content {
  margin-bottom: 16px;
  cursor: pointer;
}

.text {
  margin-bottom: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
  object-fit: cover;
}

.post-footer {
  display: flex;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  margin-right: 24px;
  cursor: pointer;
}

.action-btn span {
  margin-left: 4px;
  color: #666;
}

.action-btn .active {
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

/* 发布动态弹窗样式 */
.publish-dialog {
  padding: 0 10px;
}

.dialog-header {
  margin-bottom: 15px;
}

.dialog-header .user-info {
  display: flex;
  align-items: center;
}

.dialog-header .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.dialog-header .username {
  font-weight: bold;
  font-size: 16px;
}

.dialog-content {
  margin-bottom: 20px;
}

.content-input {
  margin-bottom: 15px;
}

.content-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px;
  font-size: 15px;
  resize: none;
  border: 1px solid #e0e0e0;
  transition: all 0.3s;
}

.content-input :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.upload-section {
  margin-top: 15px;
}

.image-uploader :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
  border-radius: 8px;
  transition: all 0.3s;
}

.image-uploader :deep(.el-upload--picture-card:hover) {
  border-color: #409eff;
  color: #409eff;
}

.upload-icon-container {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #8c939d;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 5px;
}

.upload-text {
  font-size: 12px;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-footer .el-button {
  padding: 10px 20px;
  font-size: 14px;
}

.dialog-footer .el-button--primary {
  background-color: #409eff;
}

.dialog-footer .el-button--primary:disabled {
  background-color: #a0cfff;
  border-color: #a0cfff;
  color: #fff;
}
</style>