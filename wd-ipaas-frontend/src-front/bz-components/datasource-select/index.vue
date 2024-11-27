<template>
  <div class="store-select">
    <!-- {{data}} -->
    <div
      class="store-item"
      :class="{actived: item.databaseName === value}"
      v-for="item in data"
      :key="item.id"
      :title="item.databaseName"
      @click="onActive(item)"
    >
      <img class="store-img" :src="imgs[item.picUrl]" />

      <template v-if="!!value && item.databaseName === value.toLowerCase()">
        <div class="actived">
          <img class="checked" src="../../assets/images/task/check.png" />
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import es from "@/assets/images/sink/es.png";
import gp from "@/assets/images/sink/gp.png";
import pg from "@/assets/images/sink/pg.png";
import h2 from "@/assets/images/sink/h2.png";
import kylin from "@/assets/images/sink/kylin.png";
import hbase from "@/assets/images/sink/hbase.png";
import kafka from "@/assets/images/sink/kafka.png";
import mysql from "@/assets/images/sink/mysql.png";
import oracle from "@/assets/images/sink/oracle.png";
import redis from "@/assets/images/sink/redis.png";
import sqlserve from "@/assets/images/sink/sqlserve.png";
import hive from "@/assets/images/sink/hive.png";
export default {
  props: {
    value: {
      type: String | Number,
      default: null
    },
    data: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      h2:'',kylin:'',
      imgs: {
        es,
        pg,
        gp,
        h2,
        kylin,
        hbase,
        kafka,
        mysql,
        oracle,
        redis,
        sqlserve,
        hive
      }
    };
  },
  methods: {
    onActive(item) {
      let oldVal = this.value;
      this.$emit('input', item.databaseName);

      this.$emit('change', item.databaseName);
    }
  }
};
</script>

<style lang="less" scoped>
.store-select {
  margin-bottom: -20px;

  .store-item {
    position: relative;
    cursor: pointer;
    margin-bottom: 20px;
    margin-right: 20px;
    width: 120px;
    height: 80px;
    display: inline-block;
    text-align: center;
    line-height: 80px;
    background: #bbdefb;
    border: 1px solid #f5f5f5;

    .store-img {
      width: 120px;
      height: 80px;
    }

    .actived {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(33, 150, 243, 0.69);
    }

    .checked {
      position: absolute;
      width: 26px;
      height: 26px;
      right: 6px;
      bottom: 6px;
    }
  }
}
</style>