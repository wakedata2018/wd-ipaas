const CONSTANTS = {
  /**
   * 有些获取的数据源字段和保存数据源接口的入参字段名不一致，需要转换
   * key: 保存数据源入参字段名
   * value: 获取的数据源详情字段名
   */
  paramsMap: {
    dataSourceId: 'id',
    name: 'name',
    dbType: 'type',
    hdfsPathPrefix: 'hdfs.path.prefix',
    hdfsPathSuffix: 'hdfs.path.suffix',
    rootPath: 'root.path',
    dbName: 'db.database',
    host: 'db.host',
    port: 'db.port',
    usr: 'db.user',
    pwd: 'db.password',
    catalog: 'db.catalog',
    advancedParam: 'db.params.advanced',
    additionalParam: 'db.params.additional',
    // redis
    redisType: 'redis.type',
    redisIpAndPort: 'redis.ip.port',
    redisAuth: 'redis.auth',
    // ssh
    sshHost: 'ssh.host',
    sshPort: 'ssh.port',
    sshUser: 'ssh.user',
    localPort: 'ssh.local.port',
    sshType: 'ssh.type',
    privateKeyPath: 'ssh.private.key.path',
    passphrase: 'ssh.passphrase',
    sshPwd: 'ssh.pwd',
    // 明源
    mingyuanParamR: 'mingyuan.param.r',
    mingyuanParamTenantId: 'mingyuan.param.tenant_id',
    mingyuanTokenHeadAlg: 'mingyuan.token.head.alg',
    mingyuanTokenHeadTyp: 'mingyuan.token.head.typ',
    mingyuanTokenPayloadIss: 'mingyuan.token.payload.iss',
    mingyuanTokenSecret: 'mingyuan.token.secret',
    mingyuanUrl: 'mingyuan.url'
  },

  // 测试环境--数据安全那边所有平台标识
  platformKeyEnum: {
    flow: '离线开发',
    CDP: '客户中心',
    athena: '埋点上报',
    'business-analysis': '经营分析平台',
    ydtest: 'ydtest',
    stream: '实时开发',
    agilefox: '可视化建模',
    cloud: '数据服务',
    dq: '数据质量',
    mlp: '机器学习',
    spider: '爬虫平台',
    compass: '地产大运营',
    portal: '首页',
    dam: '数据资产',
    permission: '数据安全',
    masterdata: '主数据',
    masterdatadc: '主数据dc',
    wakedatabi: '惟客BI',
    datasource: '数据源'
  },

  /**
   * mysqlssh类型接口未返回的参数
   * key: 参数名
   * value: 默认值
   */
  sshParams: {
    ssh: true,
    ssh_$host: '',
    ssh_$port: null,
    ssh_$user: '',
    ssh_$local_$port: null,
    ssh_$type: 'password',
    ssh_$private_$key_$path: '',
    ssh_$passphrase: '',
    ssh_$pwd: ''
  },

  /**
   * key: 获取的数据源详情字段名
   * value: 用来展示的中文名
   */
  labelMap: {
    dataSourceName: '数据源名称',
    dataSourceType: '数据库类型',
    createUser: '创建者',
    createTime: '创建时间',
    businessSystem: '业务系统',
    remark: '描述',
    lastModifiedUser: '更新人',
    lastModifiedTime: '更新时间',
    reference: '引用数',
    'hdfs.path.suffix': '目录',
    'root.path': '根目录',
    'db.database': '数据库',
    'db.host': '主机名/IP',
    'db.port': '端口',
    'db.user': '用户名',
    'db.password': '密码',
    'db.catalog': 'catalog',
    'redis.type': '主机类型',
    'redis.ip.port': '主机名/IP:端口',
    'redis.auth': '密码',
    'ssh.host': 'SSH主机',
    'ssh.port': 'SSH端口',
    'ssh.user': 'SSH用户名',
    'ssh.local.port': '本地端口',
    'ssh.type': '验证方法',
    'ssh.private.key.path': '私钥文件',
    'ssh.passphrase': '密码短语',
    'ssh.pwd': '密码',
    'mingyuan.param.r': '参数 r',
    'mingyuan.param.tenant_id': '参数 tenant_id',
    'mingyuan.token.head.alg': 'Token head_alg',
    'mingyuan.token.head.typ': 'Token head_typ',
    'mingyuan.token.payload.iss': 'Token payload_iss',
    'mingyuan.token.secret': 'Token secret'
  },

  /**
   * key: 获取的数据源详情字段名
   * value: 用来展示的中文名
   */
  sourceDetailLabel: {
    dataSourceName: '数据源名称',
    dataSourceType: '数据库类型',
    createUser: '创建者',
    createTime: '创建时间',
    businessSystem: '业务系统',
    remark: '描述',
    lastModifiedUser: '更新人',
    lastModifiedTime: '更新时间',
    reference: '引用数',
    'redis.type': '主机类型',
    'redis.ip.port': '主机名/IP:端口',
    'redis.auth': '密码',
    'ssh.host': 'SSH主机',
    'ssh.port': 'SSH端口',
    'ssh.user': 'SSH用户名',
    'ssh.local.port': '本地端口',
    'ssh.type': '验证方法',
    'ssh.private.key.path': '私钥文件',
    'ssh.passphrase': '密码短语',
    'ssh.pwd': '密码',
    'mingyuan.param.r': '参数 r',
    'mingyuan.param.tenant_id': '参数 tenant_id',
    'mingyuan.token.head.alg': 'Token head_alg',
    'mingyuan.token.head.typ': 'Token head_typ',
    'mingyuan.token.payload.iss': 'Token payload_iss',
    'mingyuan.token.secret': 'Token secret'
  },

  /**
   * redis主机类型
   */
  redisType: {
    client: '单机',
    cluster: '集群'
  },

  /**
   * 支持测试连接的数据库类型
   */
  supportTestTypes: [
    'hana',
    'mysql',
    'oracle',
    'sqlserver',
    'mongo',
    'mysqlssh',
    'postgresql',
    'sybasease',
    'sybaseiq',
    'influxDb',
    'kafka',
    'presto',
    'starrocks',
    'apachedoris',
    'ftp',
    'hdfs',
    'postgis',
    'sftp'
  ],
  /**
   * 密码类型的字段
   */
  passwordKey: [
    'redis.auth',
    'pwd',
    'db.password',
    'mingyuan.token.secret',
    // mysqlssh相关：
    'ssh.pwd',
    'passphrase'
  ],
  /**
   * 验证方法map
   */
  validatorMap: {
    'db.params.advanced': (rule, val, callback) => {
      console.log(rule, val, callback);
      const arr = val ? JSON.parse(val) : [];
      if (arr && arr.length > 0) {
        let result = 0;
        for (let i = 0; i < arr.length; i++) {
          let item = arr[i];
          const patt = /^[_0-9A-Za-z.-]+$/i;
          if (!item.key) {
            result = 1;
            break;
          }
          if (!patt.test(item.key)) {
            result = 2;
            break;
          }
          if (item.value && !patt.test(item.value)) {
            result = 3;
            break;
          }
        }
        if (result === 1) {
          return callback(new Error('高级参数的键不能为空'));
        } else if (result === 2) {
          return callback(new Error('高级参数的键格式不正确'));
        } else if (result === 3) {
          return callback(new Error('高级参数的值格式不正确'));
        }
      }
      return callback();
      // return fieldValidator(value, callback, value.prop);
    }
  }
};
export default CONSTANTS;
