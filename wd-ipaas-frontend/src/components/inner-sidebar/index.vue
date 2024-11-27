<template>
  <div
    ref="innerSidebar"
    :class="'inner-sidebar '+(rightSide===true?'right-inner-sidebar':'left-inner-sidebar')+' '+(isDark===true?'dark':'')"
    :style="sidebarStyle"
  >
    <div class="sidebar-content">
      <slot></slot>
    </div>
    <div
    v-if="resizable"
    ref="resizeBar"
    class="resize-bar"
    :draggable="false"
    @mousedown="dragResizeBar"
    ></div>
    <div
    ref="resizeArea"
    class="active-resize"
    :draggable="false"
    :class="{'resize-area': resizeAreaDisplay}"
    v-if="resizeAreaDisplay&&resizable"
    ></div>
  </div>
</template>

<script>
export default {
  name: 'InnerSidebar',
  props: {
    sidebarStyle: {
      type: Object
    },
    ///宽度限制参数
    widthStyle: {
      type: Object,
      default: _ => ({})
    },
    rightSide: {
      type: Boolean,
      default: false
    },
    isDark: {
      type: Boolean,
      default: false
    },
    resizable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      resizeAreaDisplay: false
    };
  },
  methods: {
    dragResizeBar(event) {
      this.resizeAreaDisplay = true;
      $(this.$refs.resizeBar).addClass('drag-resize');
      $(this.$refs.resizeBar).off('mousedown');
      $(document).off('mousemove');
      $(document).off('mouseup');


      $(document).on('mousemove', event => {
        this.dragOverResizeBar(event);
      });
      $(document).on('mouseup', event => {
        this.dragEndResizeBar(event);
      });
      // $(document).on('mouseup', event => {
      //   this.dragEndResizeBar(event);
      // });
    },
    dragOverResizeBar(event) {
      event.preventDefault();
      if (!this.rightSide) {
        $(this.$refs.resizeBar).css({
          left: event.clientX - $(this.$refs.innerSidebar).offset().left
        });
      } else {
        $(this.$refs.resizeBar).css({
          left: (event.clientX - $(this.$refs.innerSidebar).offset().left - 4)
        });
      }
    },
    dragEndResizeBar(event) {
      ///拖动宽条的宽度
      let resizeBarWidth = this.rightSide ? 10 : 0;
      let formerWidth = $(this.$refs.innerSidebar).outerWidth();
      let finalWidth = this.rightSide ? (formerWidth - event.clientX + $(this.$refs.innerSidebar).offset().left - resizeBarWidth ) : (event.clientX - $(this.$refs.innerSidebar).offset().left);
      let minWidth = (this.widthStyle.minWidth ? this.widthStyle.minWidth : 200) - resizeBarWidth;
      let maxWidth = (this.widthStyle.maxWidth ? this.widthStyle.maxWidth : 400) - resizeBarWidth;
      if (finalWidth < minWidth) {
        $(this.$refs.resizeBar).css({
          left: ''
        });
        finalWidth = minWidth;
      } else if (finalWidth > maxWidth) {
        $(this.$refs.resizeBar).css({
          left: ''
        });
        finalWidth = maxWidth;
      } else {
        if (this.rightSide) {
          $(this.$refs.resizeBar).css({
            left: ''
          });
        }
      }
      $(this.$refs.innerSidebar).width(finalWidth);
      this.resizeAreaDisplay = false;
      // // $(this.$refs.resizeBar).css({
      // //   "right": -event.layerX
      // // });
      $(this.$refs.resizeBar).removeClass('drag-resize');
      $(document).off('mouseup');
      $(document).off('mousemove');
      $(this.$refs.resizeBar).on('mousedown', this.dragResizeBar);
      this.$emit('drag-resize-end', finalWidth - formerWidth);
    }
  }
};
</script>

<style scoped lang = 'less'>
@import './style.less';
</style>
