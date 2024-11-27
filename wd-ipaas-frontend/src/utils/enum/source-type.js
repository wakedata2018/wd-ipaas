import BaseEnum from './enum-base';
import images from '../datasource-images.js';

const { es, gp, hbase, hive, kafka, mysql, oracle, redis, SQLServer, vertica, kudu, clickhouse, influxdb } = images;

class SourceType extends BaseEnum {
  constructor() {
    super();
    this._mysql = {
      value: 'mysql',
      label: 'MySQL',
      port: 3306,
      imageUrl: 'mysql',
      img: mysql
    };
    this._hive = {
      value: 'hive',
      label: 'Hive',
      port: null,
      imageUrl: 'hive',
      img: hive
    };
    this._elasticsearch = {
      value: 'elasticsearch',
      label: 'ElasticSearch',
      port: 9200,
      imageUrl: es
    };
    this._hbase = {
      value: 'hbase',
      label: 'HBase',
      port: 2181,
      imageUrl: 'hbase',
      img: hbase
    };
    this._kafka = {
      value: 'kafka',
      label: 'Kafka',
      imageUrl: 'kafka',
      img: kafka
    };
    this._oracle = {
      value: 'oracle',
      label: 'Oracle',
      port: 1521,
      imageUrl: 'oracle',
      img: oracle
    };
    this._sqlServer = {
      value: 'sqlserver',
      label: 'SqlServer',
      port: 1433,
      imageUrl: 'SQLServer',
      img: SQLServer
    };
    this._gp = {
      value: 'greenplum',
      label: 'GreenPlum',
      port: 5432,
      imageUrl: gp,
      img: gp
    };
    this._redis = {
      value: 'redis',
      label: 'Redis',
      port: 6379,
      imageUrl: 'redis',
      img: redis
    };
    this._vertica = {
      value: 'vertica',
      label: 'Vertica',
      port: 5433,
      imageUrl: 'vertica',
      img: vertica
    };
    this._kudu = {
      value: 'kudu',
      label: 'Kudu',
      port: 7051,
      imageUrl: 'kudu',
      img: kudu
      
    }
    this._clickhouse = {
      value: 'clickhouse',
      label: 'Clickhouse',
      port: 8123,
      imageUrl: 'clickhouse',
      img: clickhouse
    }
    this._influxdb = {
      value: 'influxdb',
      label: 'InfluxData',
      port: 8088,
      imageUrl: 'influxdb',
      img: influxdb
    }

    this._list.push(
      this._mysql,
      this._hive,
      this._elasticsearch,
      this._hbase,
      this._kafka,
      this._oracle,
      this._sqlServer,
      this._gp,
      this._redis,
      this._vertica,
      this._kudu,
      this._clickhouse,
      this._influxdb
    );

    this.list.forEach(item => {
      const val = item.value;
      this[val.toUpperCase()] = val;
    });
  }

  get dimensionSourceTypes() {
    return [this._mysql, this._oracle, this._sqlServer, this._gp];
  }

  get UP_SINK() {
    return 'SINK';
  }
  get SINK() {
    return 'SINK';
  }
  get DIMENSION_TABLE() {
    return 'DIMENSION';
  }
  get DATAIN() {
    return 'DATAIN';
  }

  isHiveOrElasticsearch(sinkType) {
    return this._hive.value === sinkType || this._elasticsearch.value === sinkType;
  }

  isSinkType(sinkType) {
    const listSinkType = [
      this._hive.value,
      this._elasticsearch.value,
      this._hbase.value,
      this._kafka.value,
      this._redis.value,
      this._vertica.value,
      this._kudu.value,
      this._clickhouse.value
    ];
    return listSinkType.indexOf(sinkType) > -1;
  }

  isJDBC(sinkType) {
    const list = [this._mysql.value, this._oracle.value, this._sqlServer.value, this._gp.value, this._clickhouse.value];
    return list.indexOf(sinkType) > -1;
  }
}

export default new SourceType();
