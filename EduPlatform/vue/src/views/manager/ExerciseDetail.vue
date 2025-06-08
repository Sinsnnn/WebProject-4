<template>
  <div class="exercise-detail">
    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" plain @click="goBack">返回</el-button>
    </div>

    <div v-if="data.exercise" class="exercise-card">
      <div class="exercise-header">
        <h1 class="exercise-title">{{ data.exercise.title }}</h1>
        <div class="exercise-meta">
          <el-tag
            size="small"
            :type="getDifficultyType(data.exercise.difficulty)"
          >
            {{ getDifficultyLabel(data.exercise.difficulty) }}
          </el-tag>
          <el-tag size="small" :type="getTypeType(data.exercise.type)">
            {{ getTypeLabel(data.exercise.type) }}
          </el-tag>
          <div class="course-name" v-if="data.exercise.courseName">
            所属课程: {{ data.exercise.courseName }}
          </div>
        </div>
      </div>

      <div class="exercise-content">
        <div class="content-text">{{ data.exercise.content }}</div>
      </div>

      <div class="exercise-answer" v-if="!data.submitted">
        <div v-if="data.exercise.type === 0" class="options">
          <el-radio-group v-model="data.userAnswer">
            <div
              v-for="(option, index) in JSON.parse(
                data.exercise.options || '[]'
              )"
              :key="index"
              class="option-item"
            >
              <el-radio :label="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}. {{ option }}
              </el-radio>
            </div>
          </el-radio-group>
        </div>
        <div v-else-if="data.exercise.type === 1" class="fill-blank">
          <el-input
            v-model="data.userAnswer"
            type="text"
            placeholder="请输入答案"
          ></el-input>
        </div>
        <div v-else-if="data.exercise.type === 2" class="true-false">
          <el-radio-group v-model="data.userAnswer">
            <el-radio label="正确">正确</el-radio>
            <el-radio label="错误">错误</el-radio>
          </el-radio-group>
        </div>
        <div v-else class="short-answer">
          <el-input
            v-model="data.userAnswer"
            type="textarea"
            :rows="4"
            placeholder="请输入答案"
          ></el-input>
        </div>

        <div class="submit-area">
          <el-button type="primary" @click="submitAnswer">提交答案</el-button>
        </div>
      </div>

      <div v-if="data.submitted" class="result-area">
        <div
          class="result-header"
          :class="{ correct: data.isCorrect, incorrect: !data.isCorrect }"
        >
          <el-icon v-if="data.isCorrect">
            <CircleCheckFilled />
          </el-icon>
          <el-icon v-else>
            <CircleCloseFilled />
          </el-icon>
          <span>{{ data.isCorrect ? "回答正确" : "回答错误" }}</span>
        </div>
        <div class="your-answer">
          <h3>你的答案</h3>
          <div class="answer-content">{{ data.userAnswer }}</div>
        </div>
        <div class="reference-answer">
          <h3>参考答案</h3>
          <div class="answer-content">{{ data.exercise.answer }}</div>
        </div>
        <div class="retry-area">
          <el-button type="primary" @click="resetAnswer">重新作答</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { CircleCheckFilled, CircleCloseFilled } from "@element-plus/icons-vue";
import request from "@/utils/request";

const route = useRoute();
const router = useRouter();

const data = reactive({
  exercise: null,
  userAnswer: "",
  submitted: false,
  isCorrect: false,
});

// 加载习题信息
const loadExercise = () => {
  request.get(`/exercise/selectById/${route.params.id}`).then((res) => {
    if (res.code === "200") {
      data.exercise = res.data;

      // 检查是否已经回答过
      checkRecord();
    } else {
      ElMessage.error(res.msg || "获取习题信息失败");
    }
  });
};

// 检查是否已经回答过
const checkRecord = () => {
  request
    .get(`/exerciseRecord/selectByExerciseId/${route.params.id}`)
    .then((res) => {
      if (res.code === "200" && res.data) {
        data.userAnswer = res.data.userAnswer;
        data.submitted = true;
        data.isCorrect = res.data.isCorrect === 1;
      }
    });
};

// 提交答案
const submitAnswer = () => {
  if (!data.userAnswer) {
    ElMessage.warning("请输入答案");
    return;
  }

  const record = {
    exerciseId: data.exercise.id,
    userAnswer: data.userAnswer,
  };

  request.post("/exerciseRecord/add", record).then((res) => {
    if (res.code === "200") {
      data.submitted = true;
      data.isCorrect = res.data.isCorrect === 1;
      ElMessage.success("提交成功");
    } else {
      ElMessage.error(res.msg || "提交失败");
    }
  });
};

// 重新作答
const resetAnswer = () => {
  data.submitted = false;
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 获取难度标签
const getDifficultyLabel = (difficulty) => {
  const labels = ["简单", "中等", "困难"];
  return labels[difficulty] || "未知";
};

// 获取难度类型
const getDifficultyType = (difficulty) => {
  const types = ["success", "warning", "danger"];
  return types[difficulty] || "info";
};

// 获取题目类型标签
const getTypeLabel = (type) => {
  const labels = ["选择题", "填空题", "判断题", "简答题"];
  return labels[type] || "未知";
};

// 获取题目类型类型
const getTypeType = (type) => {
  const types = ["", "success", "warning", "danger"];
  return types[type] || "info";
};

onMounted(() => {
  loadExercise();
});
</script>

<style scoped>
.exercise-detail {
  padding: 20px;
}

.exercise-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.exercise-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.exercise-title {
  font-size: 22px;
  margin-bottom: 15px;
  color: #333;
}

.exercise-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-name {
  color: #666;
  font-size: 14px;
}

.exercise-content {
  margin-bottom: 30px;
}

.content-text {
  line-height: 1.6;
  font-size: 16px;
}

.exercise-answer {
  margin-bottom: 30px;
}

.options,
.fill-blank,
.true-false,
.short-answer {
  margin-bottom: 20px;
}

.option-item {
  margin-bottom: 10px;
}

.submit-area {
  margin-top: 20px;
}

.result-area {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  margin-bottom: 15px;
}

.result-header.correct {
  color: #67c23a;
}

.result-header.incorrect {
  color: #f56c6c;
}

.your-answer,
.reference-answer {
  margin-bottom: 20px;
}

.your-answer h3,
.reference-answer h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.answer-content {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  line-height: 1.6;
}

.retry-area {
  margin-top: 20px;
}
</style>