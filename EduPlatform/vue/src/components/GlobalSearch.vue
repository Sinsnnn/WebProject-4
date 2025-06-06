<template>
  <div class="global-search">
    <el-input v-model="keyword" placeholder="搜索课程、内容、动态..." prefix-icon="Search" clearable @keyup.enter="handleSearch">
      <template #append>
        <el-button @click="handleSearch">搜索</el-button>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const keyword = ref('');

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({
      path: '/manager/search',
      query: { keyword: keyword.value.trim() }
    });
  }
};
</script>

<style scoped>
.global-search {
  width: 300px;
}

.global-search :deep(.el-input__inner) {
  border-radius: 20px 0 0 20px;
}

.global-search :deep(.el-input-group__append) {
  border-radius: 0 20px 20px 0;
}
</style>