<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="goBack">返回</el-button>
    </div>

    <div v-if="data.post" class="post-detail-card">
      <div class="post-header">
        <div class="user-info">
          <el-avatar :src="data.post.userAvatar || '/src/assets/imgs/avatar.png'" :size="40"></el-avatar>
          <div class="user-name">{{ data.post.userName }}</div>
        </div>
        <div class="post-time">{{ formatDate(data.post.createTime) }}</div>
      </div>

      <div class="post-content">
        <p>{{ data.post.content }}</p>
        <div v-if="data.post.images" class="post-image">
          <el-image :src="parseImageUrl(data.post.images)" fit="cover"
            :preview-src-list="getPreviewList(data.post.images)" :initial-index="0" preview-teleported></el-image>
        </div>
      </div>

      <div class="post-stats">
        <div class="stat-item">
          <el-icon>
            <Star />
          </el-icon>
          <span>{{ data.post.likeCount || 0 }} 点赞</span>
        </div>
        <div class="stat-item">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <span>{{ data.post.commentCount || 0 }} 评论</span>
        </div>
      </div>

      <div class="post-comments">
        <h3>评论列表</h3>
        <div v-if="data.comments.length > 0" class="comments-list">
          <div v-for="comment in data.comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <div class="user-info">
                <el-avatar :src="comment.userAvatar || '/src/assets/imgs/avatar.png'" :size="30"></el-avatar>
                <div class="user-name">{{ comment.userName }}</div>
              </div>
              <div class="comment-actions">
                <el-button type="danger" size="small" plain @click="deleteComment(comment.id)">删除</el-button>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-time">{{ formatDate(comment.createTime) }}</div>

            <!-- 回复评论 -->
            <div v-if="comment.children && comment.children.length > 0" class="reply-list">
              <div v-for="reply in comment.children" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <div class="user-info">
                    <el-avatar :src="reply.userAvatar || '/src/assets/imgs/avatar.png'" :size="24"></el-avatar>
                    <div class="user-name">{{ reply.userName }}</div>
                    <div class="reply-to" v-if="reply.parentUserName">回复 {{ reply.parentUserName }}</div>
                  </div>
                  <div class="reply-actions">
                    <el-button type="danger" size="small" plain @click="deleteComment(reply.id)">删除</el-button>
                  </div>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="reply-time">{{ formatDate(reply.createTime) }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-comments">
          暂无评论
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Star, ChatDotRound } from '@element-plus/icons-vue';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();

const data = reactive({
  post: null,
  comments: []
});

// 加载动态详情
const loadPost = () => {
  request.get(`/post/selectById/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.post = res.data;
    } else {
      ElMessage.error(res.msg || '获取动态详情失败');
    }
  });
};

// 加载评论列表
const loadComments = () => {
  request.get(`/postComment/selectByPostId/${route.params.id}`).then(res => {
    if (res.code === '200') {
      data.comments = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取评论列表失败');
    }
  });
};

// 删除评论
const deleteComment = (id) => {
  ElMessageBox.confirm('确定要删除这条评论吗？删除后将无法恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/postComment/delete/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功');
        loadComments();
        // 更新评论数
        if (data.post) {
          data.post.commentCount = Math.max(0, (data.post.commentCount || 0) - 1);
        }
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
  }).catch(() => { });
};

// 返回上一页
const goBack = () => {
  router.push('/manager/adminPosts');
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 解析JSON格式的图片URL
const parseImageUrl = (jsonStr) => {
  try {
    if (!jsonStr) return '';
    // 尝试解析JSON字符串
    const urls = JSON.parse(jsonStr);
    // 如果是数组，返回第一个URL
    if (Array.isArray(urls) && urls.length > 0) {
      return urls[0];
    }
    // 如果是对象，可能有url属性
    if (typeof urls === 'object' && urls.url) {
      return urls.url;
    }
    // 如果是字符串，直接返回
    if (typeof urls === 'string') {
      return urls;
    }
    return '';
  } catch (e) {
    // 如果解析失败，可能不是JSON格式，直接返回原字符串
    console.error('解析图片URL失败:', e);
    return jsonStr;
  }
};

// 获取预览图片列表
const getPreviewList = (jsonStr) => {
  try {
    if (!jsonStr) return [];
    // 尝试解析JSON字符串
    const urls = JSON.parse(jsonStr);
    // 如果是数组，直接返回
    if (Array.isArray(urls)) {
      return urls;
    }
    // 如果是对象，可能有url属性
    if (typeof urls === 'object' && urls.url) {
      return [urls.url];
    }
    // 如果是字符串，放入数组返回
    if (typeof urls === 'string') {
      return [urls];
    }
    return [];
  } catch (e) {
    // 如果解析失败，可能不是JSON格式，直接返回包含原字符串的数组
    console.error('解析图片URL列表失败:', e);
    return [jsonStr];
  }
};

onMounted(() => {
  loadPost();
  loadComments();
});
</script>

<style scoped>
.post-detail-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: bold;
}

.post-time {
  color: #999;
  font-size: 14px;
}

.post-content {
  margin-bottom: 20px;
  line-height: 1.6;
}

.post-content p {
  margin-bottom: 10px;
}

.post-image {
  margin-top: 10px;
  max-width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.post-image img {
  max-width: 100%;
}

.post-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
}

.post-comments {
  margin-top: 20px;
}

.post-comments h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-content {
  margin-bottom: 10px;
  line-height: 1.5;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.reply-list {
  margin-top: 15px;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reply-item {
  background-color: #f0f0f0;
  border-radius: 6px;
  padding: 10px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-to {
  color: #666;
  font-size: 12px;
  margin-left: 5px;
}

.reply-content {
  margin-bottom: 8px;
  line-height: 1.5;
}

.reply-time {
  color: #999;
  font-size: 12px;
}

.empty-comments {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>