<template>
    <div class="box">{{title}}</div>
</template>

<script>
export default {
    props:['title']
}
</script>

<style scoped lang="less">
    .box{
        padding: 22px 25px;
        font-size: 16px;
        font-weight: 800;
        background-color: #fff;
        width: 100%;
    }
</style>