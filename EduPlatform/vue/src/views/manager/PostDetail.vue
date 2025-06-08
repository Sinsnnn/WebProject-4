<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="goBack">返回</el-button>
    </div>

    <div v-if="data.post" class="post-detail">
      <div class="post-card">
        <div class="post-header">
          <div class="user-info">
            <img :src="data.post.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar" class="avatar" />
            <div>
              <div class="username">{{ data.post.user?.name || '用户' }}</div>
              <div class="time">{{ formatTime(data.post.createTime) }}</div>
            </div>
          </div>
          <div v-if="data.post.userId === data.user.id" class="post-actions">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-icon>
                  <More />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleDelete(data.post.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="post-content">
          <div class="text">{{ data.post.content }}</div>
          <div v-if="data.post.images" class="images">
            <el-image v-for="(img, index) in JSON.parse(data.post.images)" :key="index" :src="img"
              :preview-src-list="JSON.parse(data.post.images)" fit="cover" class="image" />
          </div>
        </div>
        <div class="post-footer">
          <div class="action-btn" @click="toggleLike(data.post)">
            <el-icon :color="data.post.isLiked ? '#409EFF' : ''">
              <component :is="data.post.isLiked ? 'Star' : 'StarFilled'" />
            </el-icon>
            <span :class="{ active: data.post.isLiked }">{{ data.post.likeCount || 0 }}</span>
          </div>
          <div class="action-btn">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            <span>{{ data.post.commentCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="comment-section">
        <div class="comment-input">
          <el-input v-model="data.commentContent" type="textarea" :rows="3" placeholder="发表评论..."></el-input>
          <el-button type="primary" @click="submitComment" style="margin-top: 10px;">发表评论</el-button>
        </div>

        <div class="comment-list">
          <div v-if="data.post.comments && data.post.comments.length > 0">
            <div v-for="comment in data.post.comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <div class="user-info">
                  <img :src="comment.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar" class="avatar" />
                  <div>
                    <div class="username">{{ comment.user?.name || '用户' }}</div>
                    <div class="time">{{ formatTime(comment.createTime) }}</div>
                  </div>
                </div>
                <div v-if="comment.userId === data.user.id" class="comment-actions">
                  <el-dropdown>
                    <span class="el-dropdown-link">
                      <el-icon>
                        <More />
                      </el-icon>
                    </span>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="handleDeleteComment(comment.id)">删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-footer">
                <span class="reply-btn" @click="showReplyInput(comment.id)">回复</span>
              </div>

              <!-- 回复输入框 -->
              <div v-if="data.replyToId === comment.id" class="reply-input">
                <el-input v-model="data.replyContent" type="textarea" :rows="2" placeholder="回复评论..."></el-input>
                <div class="reply-actions">
                  <el-button type="text" @click="cancelReply">取消</el-button>
                  <el-button type="primary" size="small" @click="submitReply(comment.id)">回复</el-button>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <div class="reply-header">
                    <div class="user-info">
                      <img :src="reply.user?.avatar || '/src/assets/imgs/avatar.png'" alt="avatar"
                        class="avatar-small" />
                      <div>
                        <div class="username">{{ reply.user?.name || '用户' }}</div>
                        <div class="time">{{ formatTime(reply.createTime) }}</div>
                      </div>
                    </div>
                    <div v-if="reply.userId === data.user.id" class="reply-actions">
                      <el-dropdown>
                        <span class="el-dropdown-link">
                          <el-icon>
                            <More />
                          </el-icon>
                        </span>
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item @click="handleDeleteComment(reply.id)">删除</el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </div>
                  </div>
                  <div class="reply-content">{{ reply.content }}</div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-comment">
            暂无评论，快来发表第一条评论吧！
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Star, StarFilled, ChatDotRound, More } from '@element-plus/icons-vue';
import request from '@/utils/request';

const router = useRouter();
const route = useRoute();

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  post: null,
  commentContent: '',
  replyContent: '',
  replyToId: null
});

// 加载动态详情
const loadPostDetail = () => {
  const postId = route.params.id;
  request.get(`/post/selectById/${postId}`).then(res => {
    if (res.code === '200') {
      data.post = res.data;
    } else {
      ElMessage.error(res.msg || '获取动态详情失败');
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
        goBack();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
  }).catch(() => { });
};

// 提交评论
const submitComment = () => {
  if (!data.commentContent.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  const comment = {
    postId: data.post.id,
    content: data.commentContent
  };

  request.post('/postComment/add', comment).then(res => {
    if (res.code === '200') {
      ElMessage.success('评论成功');
      data.commentContent = '';
      loadPostDetail(); // 重新加载动态详情
    } else {
      ElMessage.error(res.msg || '评论失败');
    }
  });
};

// 显示回复输入框
const showReplyInput = (commentId) => {
  data.replyToId = commentId;
  data.replyContent = '';
};

// 取消回复
const cancelReply = () => {
  data.replyToId = null;
  data.replyContent = '';
};

// 提交回复
const submitReply = (commentId) => {
  if (!data.replyContent.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }

  const reply = {
    postId: data.post.id,
    parentId: commentId,
    content: data.replyContent
  };

  request.post('/postComment/add', reply).then(res => {
    if (res.code === '200') {
      ElMessage.success('回复成功');
      data.replyToId = null;
      data.replyContent = '';
      loadPostDetail(); // 重新加载动态详情
    } else {
      ElMessage.error(res.msg || '回复失败');
    }
  });
};

// 删除评论
const handleDeleteComment = (id) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/postComment/delete/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功');
        loadPostDetail(); // 重新加载动态详情
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
  }).catch(() => { });
};

// 返回上一页
const goBack = () => {
  router.back();
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
  loadPostDetail();
});
</script>

<style scoped>
.post-detail {
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

.avatar-small {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 8px;
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

.comment-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.comment-input {
  margin-bottom: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-content {
  margin-bottom: 8px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.comment-footer {
  display: flex;
  justify-content: flex-end;
}

.reply-btn {
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
}

.reply-input {
  margin: 10px 0;
  padding-left: 40px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.reply-list {
  margin-top: 10px;
  padding-left: 40px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.reply-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-content {
  line-height: 1.5;
  white-space: pre-wrap;
}

.empty-comment {
  text-align: center;
  color: #999;
  padding: 20px 0;
}
</style>