<template>
  <div v-loading="loading" class="fat-image-verify-code">
    <main ref="backgroundRef" class="fat-image-verify-code__bg">
      <img
        v-if="metadata.backgroundImage"
        :src="base64ToDataUrl(metadata.backgroundImage)"
        alt="background"
        :draggable="false"
      />
    </main>

    <img
      v-if="metadata.clipImage"
      ref="clipElementRef"
      :src="base64ToDataUrl(metadata.clipImage)"
      alt="clip"
      :style="clipStyle"
      class="fat-image-verify-code__clip"
      :draggable="false"
      @mousedown="handleStartDrag"
    />
    <div class="fat-image-verify-code__refresh" @click="handleRefresh">
      <i class="el-icon-refresh" />
    </div>

    <footer class="fat-image-verify-code__slider">
      <div class="fat-image-verify-code__slider-control">
        <div class="fat-image-verify-code__slider-progress" :style="progressStyle"></div>
        <div class="fat-image-verify-code__slider-bar" @mousedown="handleStartDrag"></div>
      </div>
      <div class="fat-image-verify-code__slider-placeholder">拖动滑块验证</div>
    </footer>
  </div>
</template>

<script>
  import { clamp } from '@wakeadmin/components';
  // eslint-disable-next-line wkvue/no-import-style-in-script
  import './index.scss';

  export default {
    props: {
      request: {
        type: Function,
        default: null,
      },
    },
    data() {
      return {
        loading: false,
        metadata: {},
        translate: 0,
      };
    },
    computed: {
      clipStyle() {
        return {
          transform: `translate(${this.translate}px, ${this.metadata.y || 0}px)`,
        };
      },
      progressStyle() {
        return {
          width: `${this.translate}px`,
        };
      },
    },
    mounted() {
      this.requestMetaData();
    },
    methods: {
      base64ToDataUrl(url) {
        return `data:image/png;base64,${url}`;
      },
      async requestMetaData() {
        try {
          this.loading = true;
          this.translate = 0;
          this.metadata = await this.request();
        } catch (error) {
          console.error(error);

          // TODO: 调整
          this.$message.error(error.message);
        } finally {
          this.loading = false;
        }
      },
      handleStartDrag(e) {
        const backgroundRef = this.$refs.backgroundRef;
        const clipElementRef = this.$refs.clipElementRef;
        let dragging = true;
        const current = this.translate;
        const start = e.pageX;
        const backgroundWidth = backgroundRef.offsetWidth || 350;
        const clipWidth = clipElementRef.naturalWidth || 35;
        const min = 0;
        const max = backgroundWidth - clipWidth;

        const handleMove = evt => {
          if (!dragging) {
            return;
          }

          const delta = evt.pageX - start;
          const offset = clamp(current + delta, min, max);
          this.translate = offset;
        };

        const handleUp = evt => {
          if (!dragging) {
            return;
          }

          dragging = false;
          document.removeEventListener('mousemove', handleMove);
          document.removeEventListener('mouseup', handleUp);
          document.removeEventListener('mouseleave', handleUp);

          this.$emit('change', {
            value: this.translate,
            context: this.metadata,
          });
        };

        document.addEventListener('mousemove', handleMove);
        document.addEventListener('mouseup', handleUp);
        document.addEventListener('mouseleave', handleUp);
      },
      handleRefresh() {
        this.requestMetaData();
      },
    },
  };
</script>

<style lang="scss" scoped></style>
