<template>
  <div>
    <div class="post-list">
      <div v-for="post in data.posts" :key="post.id" class="post-card">
        <div class="post-header">
          <div class="user-info">
            <img :src="post.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar" class="avatar" />
            <div>
              <div class="username">{{ post.user?.name || '用户' }}</div>
              <div class="time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>
          <div class="post-actions">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-icon>
                  <More />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleDelete(post.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="post-content" @click="goToDetail(post.id)">
          <div class="text">{{ post.content }}</div>
          <div v-if="post.images" class="images">
            <el-image v-for="(img, index) in JSON.parse(post.images)" :key="index" :src="img"
              :preview-src-list="JSON.parse(post.images)" fit="cover" class="image" />
          </div>
        </div>
        <div class="post-footer">
          <div class="action-btn" @click="toggleLike(post)">
            <el-icon :color="post.isLiked ? '#409EFF' : ''">
              <component :is="post.isLiked ? 'Star' : 'StarFilled'" />
            </el-icon>
            <span :class="{ active: post.isLiked }">{{ post.likeCount || 0 }}</span>
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
        <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
          v-model:current-page="data.pageNum" @current-change="loadPosts" />
      </div>

      <div v-if="data.posts.length === 0" class="empty-tip">
        你还没有发布过动态，快来发布第一条动态吧！
      </div>
    </div>

    <!-- 发布动态对话框 -->
    <el-dialog title="发布动态" v-model="data.dialogVisible" width="500px">
      <el-form :model="data.form" label-width="0">
        <el-form-item>
          <el-input v-model="data.form.content" type="textarea" :rows="4" placeholder="分享你的学习心得..."></el-input>
        </el-form-item>
        <el-form-item>
          <el-upload action="http://localhost:9090/files/upload" list-type="picture-card"
            :on-success="handleUploadSuccess" :on-remove="handleRemove" multiple>
            <el-icon>
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPost">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Star, StarFilled, ChatDotRound, More, Plus } from '@element-plus/icons-vue';
import request from '@/utils/request';

const router = useRouter();

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  posts: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  dialogVisible: false,
  form: {
    content: '',
    images: []
  }
});

// 加载我的动态列表
const loadPosts = () => {
  request.get('/post/selectMyPosts', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize
    }
  }).then(res => {
    if (res.code === '200') {
      data.posts = res.data.list || [];
      data.total = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || '获取动态列表失败');
    }
  });
};

// 点赞/取消点赞
const toggleLike = (post) => {
  request.post(`/post/toggleLike/${post.id}`).then(res => {
    if (res.code === '200') {
      post.isLiked = res.data;
      post.likeCount = post.isLiked ? (post.likeCount + 1) : (post.likeCount - 1);
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  });
};

// 打开发布动态对话框
const handleAdd = () => {
  data.form = {
    content: '',
    images: []
  };
  data.dialogVisible = true;
};

// 上传图片成功回调
const handleUploadSuccess = (res) => {
  if (res.code === '200') {
    data.form.images.push(res.data);
  } else {
    ElMessage.error(res.msg || '上传失败');
  }
};

// 移除图片
const handleRemove = (file) => {
  const index = data.form.images.indexOf(file.response.data);
  if (index !== -1) {
    data.form.images.splice(index, 1);
  }
};

// 提交发布动态
const submitPost = () => {
  if (!data.form.content.trim()) {
    ElMessage.warning('请输入动态内容');
    return;
  }

  const post = {
    content: data.form.content,
    images: data.form.images.length > 0 ? JSON.stringify(data.form.images) : null
  };

  request.post('/post/add', post).then(res => {
    if (res.code === '200') {
      ElMessage.success('发布成功');
      data.dialogVisible = false;
      loadPosts();
    } else {
      ElMessage.error(res.msg || '发布失败');
    }
  });
};

// 删除动态
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条动态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/post/delete/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功');
        loadPosts();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
  }).catch(() => { });
};

// 跳转到动态详情页
const goToDetail = (id) => {
  router.push(`/manager/postDetail/${id}`);
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
  color: #409EFF;
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
</style>