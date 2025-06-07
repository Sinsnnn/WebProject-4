<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <h2>动态管理</h2>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="data.queryParams">
        <el-form-item label="内容关键词">
          <el-input v-model="data.queryParams.content" placeholder="请输入内容关键词" clearable></el-input>
        </el-form-item>
        <el-form-item label="发布者">
          <el-input v-model="data.queryParams.userName" placeholder="请输入发布者名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPosts">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.posts" stripe>
        <el-table-column label="发布者" prop="user.name" width="120"></el-table-column>
        <el-table-column label="内容">
          <template #default="scope">
            <div class="post-content">{{ scope.row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image v-if="scope.row.images" :src="parseImageUrl(scope.row.images)" fit="cover"
              style="width: 80px; height: 45px; border-radius: 4px;"
              :preview-src-list="getPreviewList(scope.row.images)" :initial-index="0" preview-teleported></el-image>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="点赞数" prop="likeCount" width="80"></el-table-column>
        <el-table-column label="评论数" prop="commentCount" width="80"></el-table-column>
        <el-table-column label="发布时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain @click="viewPost(scope.row.id)">查看详情</el-button>
            <el-button type="danger" plain @click="deletePost(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="data.total > 0" class="pagination">
        <el-pagination background layout="prev, pager, next" :total="data.total" :page-size="data.pageSize"
          v-model:current-page="data.pageNum" @current-change="loadPosts" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const router = useRouter();

const data = reactive({
  posts: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  queryParams: {
    content: '',
    userName: ''
  }
});

// 加载动态列表
const loadPosts = () => {
  request.get('/post/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      content: data.queryParams.content,
      userName: data.queryParams.userName
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

// 重置查询条件
const resetQuery = () => {
  data.queryParams = {
    content: '',
    userName: ''
  };
  data.pageNum = 1;
  loadPosts();
};

// 查看动态详情
const viewPost = (id) => {
  router.push(`/manager/adminPostDetail/${id}`);
};

// 删除动态
const deletePost = (id) => {
  ElMessageBox.confirm('确定要删除这条动态吗？删除后将无法恢复！', '提示', {
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

// 解析JSON格式的图片URL
const parseImageUrl = (jsonStr) => {
  try {
    if (!jsonStr) return '';

    // 如果已经是字符串且不是JSON格式，直接返回
    if (typeof jsonStr === 'string' && !jsonStr.startsWith('[') && !jsonStr.startsWith('{')) {
      return jsonStr;
    }

    // 尝试解析JSON字符串
    const urls = JSON.parse(jsonStr);

    // 如果是数组，返回第一个URL
    if (Array.isArray(urls) && urls.length > 0) {
      return urls[0];
    }

    // 如果是对象，可能有url属性
    if (typeof urls === 'object' && urls !== null) {
      if (urls.url) return urls.url;
      // 尝试获取对象的第一个属性值
      const firstKey = Object.keys(urls)[0];
      if (firstKey && typeof urls[firstKey] === 'string') {
        return urls[firstKey];
      }
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

    // 如果已经是字符串且不是JSON格式，放入数组返回
    if (typeof jsonStr === 'string' && !jsonStr.startsWith('[') && !jsonStr.startsWith('{')) {
      return [jsonStr];
    }

    // 尝试解析JSON字符串
    const urls = JSON.parse(jsonStr);

    // 如果是数组，直接返回
    if (Array.isArray(urls)) {
      return urls.filter(url => url); // 过滤掉空值
    }

    // 如果是对象，可能有url属性
    if (typeof urls === 'object' && urls !== null) {
      if (urls.url) return [urls.url];
      // 尝试获取对象的所有属性值
      const values = Object.values(urls).filter(val => typeof val === 'string' && val);
      if (values.length > 0) {
        return values;
      }
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
  loadPosts();
});
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}

.post-content {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 确保图片预览弹窗正常显示 */
.el-image-viewer__wrapper {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2000;
}

.el-image-viewer__img {
  max-width: 100%;
  max-height: 100%;
  display: block;
  margin: 0 auto;
}
</style>