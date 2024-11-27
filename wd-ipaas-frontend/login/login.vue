<template>
  <div class="login-bg">
    <div class="login-bg-img"></div>
    <div class="login-content">
      <div class="display hidden-sm-and-down">
        <div class="display-title">
          <div class="title-bottom">{{ info.systemName }}</div>
          <img :src="info.logoUrl" class="logo" />
        </div>
        <div class="hexagon hexagon-small"></div>
        <div class="hexagon hexagon-ordinary"></div>
      </div>
      <div class="main">
        <div class="main-title">请登录</div>
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item prop="userName">
            <el-input v-model.trim="form.userName" class="input-line" placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              class="input-line"
              type="password"
              placeholder="密码"
              @keyup.enter.native="onSubmit('form')"
            ></el-input>
          </el-form-item>
          <el-form-item prop="captchaKey">
            <ImageVerifyCode ref="verifyCode" :request="requestVerifyMetaData" @change="handleVerifyCodeChange" />
          </el-form-item>
        </el-form>
        <div class="el-form-item__content">
          <el-button :disabled="disabled" type="primary" class="login-btn" @click="onSubmit('form')">登录</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import services from '@/common/services';
  import SettingApi from '@/api/setting';
  import CryptoJS from 'crypto-js';
  import ImageVerifyCode from './image-verify-code/index.vue';

  export default {
    components: { ImageVerifyCode },
    data() {
      return {
        disabled: false,
        form: {
          userName: '',
          password: '',
          captchaKey: '',
          x: 0,
        },
        rules: {
          userName: [{ required: true, message: '请输入用户名', trigger: 'change' }],
          password: [{ required: true, message: '请输入密码', trigger: 'change' }],
          captchaKey: {
            required: true,
            message: '请完成滑块验证',
          },
        },
        info: {
          systemName: '',
          logoUrl: '',
        },
      };
    },
    async created() {
      await this.getNameAndLogoUrl();
      const name = localStorage.getItem('systemName');
      window.document.title = this.info.systemName;
      if (!name) {
        // 缓存系统名称
        localStorage.setItem('systemName', this.info.systemName);
      }
    },
    methods: {
      async getNameAndLogoUrl() {
        const { systemDetailInfo } = await SettingApi.fetchSetting();
        this.info = systemDetailInfo;
      },
      onSubmit(formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
            this.disabled = true;
            services
              .json(
                `/dw/open/manage/login/login`,
                {
                  captchaKey: this.form.captchaKey,
                  x: this.form.x,
                  y: this.form.y,
                  username: this.form.userName,
                  password: this.encrypt(this.form.password, this.form.userName),
                },
                {
                  action: '登录',
                }
              )
              .done(
                res => {
                  const { host, protocol } = window.location;
                  const back = this.getUrlParam('back');
                  if (back) {
                    window.top.location.href = back;
                  } else {
                    const redirect = `${protocol}//${host}`;
                    window.top.location.href = redirect;
                  }
                },
                () => {
                  this.$refs.verifyCode.handleRefresh();
                  // this.requestVerifyMetaData();
                }
              )
              .always(() => {
                this.disabled = false;
              });
          }
        });
      },
      getUrlParam(name) {
        const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
        const r = window.location.search.substr(1).match(reg);
        if (r != null) {
          return decodeURIComponent(r[2]);
        }
        return null;
      },
      // 取长度为16的字符串作为秘钥，不足16在末尾补0，超出截取
      lengthSlice(key) {
        let stringKey = String(key);
        if (stringKey.length > 16) {
          return stringKey.slice(1, 17);
        }
        while (stringKey.length < 16) {
          stringKey = stringKey + '0';
        }
        return stringKey;
      },
      encrypt(word, key) {
        if (!word || !key) {
          return '';
        }
        const parseKey = CryptoJS.enc.Utf8.parse(this.lengthSlice(key));
        const srcs = CryptoJS.enc.Utf8.parse(word);
        const encrypted = CryptoJS.AES.encrypt(srcs, parseKey, {
          mode: CryptoJS.mode.ECB,
          padding: CryptoJS.pad.Pkcs7,
        });
        return encrypted.ciphertext.toString();
      },
      handleVerifyCodeChange(evt) {
        this.form.x = evt.value;
        this.form.y = evt.context.y;
        this.form.captchaKey = evt.context.extra.redisKey;
      },
      /**
       * 查询滑块验证码数据
       * @param {} params
       */
      querySlideVerificationData(params) {
        return services.json('/permission/web/permission/login/captcha/get/image', params, {
          action: 'login.getCodeImg',
        });
      },
      async requestVerifyMetaData() {
        try {
          const { success, data } = await this.querySlideVerificationData({ type: 'slide' });
          if (success && data) {
            this.form.captchaKey = '';
            return {
              backgroundImage: data.shadeImage,
              clipImage: data.cutoutImage,
              y: data.y,
              extra: data,
            };
          } else {
            throw new Error('获取滑动验证码失败');
          }
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>
<style scoped lang="less">
  /deep/ .input-line input {
    width: 100%;
    height: 28px;
    font-size: 14px;
    color: #333;
    margin-bottom: 4px;
    background: none;
    border-radius: 0px;
    border: hsla(0, 0%, 100%, 0);
    border-bottom: 1px solid #e6e6e6;
    outline: none !important;
  }
  @media only screen and (max-width: 991px) {
    .hidden-sm-and-down {
      display: none !important;
    }
  }
  .login-bg {
    height: 100vh;
    width: 100vw;
    background: linear-gradient(180deg, #474c64, #222534);
    display: flex;
    display: -webkit-flex;
    justify-content: center;
    align-items: center;
    color: #333;
    font-family: PingFangSC-Semibold, PingFang SC, Helvetica Neue, Helvetica, Hiragino Sans GB, Microsoft YaHei,
      '微软雅黑', Arial, sans-serif;
    .login-bg-img {
      height: 100vh;
      width: 100vw;
      position: absolute;
      top: 0;
      left: 0;
      background: url(../src/assets/images/bg2x.59beb607.jpg) no-repeat 50%;
      background-size: cover;
    }
    .login-content {
      position: relative;
      height: 500px;
      background: #fff;
      border-radius: 4px;
      z-index: 2;
      .display {
        height: 520px;
        width: 410px;
        float: left;
        padding-top: 150px;
        margin-left: -10px;
        background: #272a3a;
        border-radius: 4px;
        box-sizing: border-box;
        .display-title {
          padding-left: 58px;
          color: #fff;
          .title-bottom {
            font-size: 28px;
            margin-bottom: 15px;
          }
          .logo {
            // width: 100px;
            height: 25px;
            object-fit: cover;
          }
        }
        .hexagon-ordinary {
          height: 271px;
          width: 256px;
          bottom: -62px;
          left: -52px;
        }
        .hexagon {
          background: url(../src/assets/images/hexagon2x.44fe2ee8.png) no-repeat 50%;
          background-size: cover;
          position: absolute;
        }
      }
      .main {
        height: 500px;
        width: 400px;
        box-sizing: border-box;
        padding: 20px 30px;
        float: left;
        position: relative;
        .login-btn {
          height: 42px;
          width: 100%;
          font-size: 14px;
          background: linear-gradient(90deg, #45c7fa, #2776fb);
          border-radius: 4px;
          border: none;
          outline: none !important;
        }
        .main-title {
          font-size: 28px;
          margin-top: 8px;
          margin-bottom: 20px;
        }
      }
    }
  }
</style>
