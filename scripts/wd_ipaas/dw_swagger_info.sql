create table dw_swagger_info
(
    id                int auto_increment comment '自增Id'
        primary key,
    swagger_name      varchar(20)       not null comment 'swagger名称',
    description       varchar(80)       not null comment 'swagger描述',
    swagger_url       varchar(1024)     null comment 'swagger数据获取地址',
    api_amount        int     default 0 not null comment 'api数量',
    api_group_id      int               null comment 'Api主题Id',
    api_domain_name   varchar(100)      null comment 'Api映射外部域名',
    create_user       varchar(20)       not null comment '创建人名称',
    create_time       datetime          not null comment '创建日期',
    update_time       datetime          not null comment '更新日期',
    lessee_id         int               null comment '租户ID',
    execute_status    tinyint           null comment '导入确认执行状态 1 未导入 2 部分导入 3 全部导入',
    import_type       tinyint default 0 null comment '导入方式，0:url导入、1:文件导入',
    swagger_json      mediumtext        null comment 'swagger文档json数据',
    resp_mapping_rule int     default 0 null comment '响应参数映射规则 0：惟客云 1：其他',
    parse_time        datetime          null comment '解析时间'
)
    comment 'swagger导入记录表;1、使用swagger导入api的记录管理';

create index idx_lessee_id
    on dw_swagger_info (lessee_id);

INSERT INTO wd_ipaas.dw_swagger_info (id, swagger_name, description, swagger_url, api_amount, api_group_id, api_domain_name, create_user, create_time, update_time, lessee_id, execute_status, import_type, swagger_json, resp_mapping_rule, parse_time) VALUES (5, '组织', '', '', 54, 3, 'http://wd-organizaiton.wdcloud.svc.cluster.local:8080', '13012345678', '2024-07-18 17:49:55', '2024-07-18 17:49:55', 3, 1, 1, '{
  "swagger": "2.0",
  "info": {
    "description": "组织架构对外提供的rpc接口文档，目前通过openfeign提供rpc",
    "version": "1.0",
    "title": "组织架构对外提供的rpc接口文档"
  },
  "host": "wdcloud-test.huamaocloud.com",
  "tags": [
    {
      "name": "数据权限rpc",
      "description": "数据权限相关接口"
    },
    {
      "name": "组织层级相关RPC",
      "description": "组织层级相关RPC"
    },
    {
      "name": "组织属性相关RPC",
      "description": "组织属性相关RPC"
    },
    {
      "name": "组织树相关RPC",
      "description": "Org Tree Config Service Impl"
    },
    {
      "name": "组织负责人相关rpc",
      "description": "组织负责人相关rpc"
    },
    {
      "name": "节点属性相关rpc",
      "description": "节点属性相关rpc"
    },
    {
      "name": "节点相关rpc",
      "description": "节点相关rpc"
    }
  ],
  "paths": {
    "/wd-organization/rpc/access/query": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体的数据权限",
        "operationId": "queryDataAccessUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织列表dto»»",
              "$ref": "#/definitions/ResultDTO«List«组织列表dto»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query.v2": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体的数据权限",
        "operationId": "queryDataAccessV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织列表dto»»",
              "$ref": "#/definitions/ResultDTO«List«组织列表dto»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query/ids": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体数据权限，返回权限范围内的节点id列表",
        "operationId": "queryDataAccessIdsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query/ids.v2": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体数据权限，返回权限范围内的节点id列表",
        "operationId": "queryDataAccessIdsV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query/map/resource": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体的数据权限，返回的有权限的资源的ids",
        "operationId": "queryDataAccessResourceIdsUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«Map«long,List«string»»»",
              "$ref": "#/definitions/ResultDTO«Map«long,List«string»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query/resource/ids": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体数据权限，返回权限范围内的资源id列表",
        "operationId": "queryDataAccessResourceIdsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«string»»",
              "$ref": "#/definitions/ResultDTO«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/query/resource/ids.v2": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "查外部实体数据权限，返回权限范围内的资源id列表",
        "operationId": "queryDataAccessResourceIdsV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "数据权限查询条件",
              "$ref": "#/definitions/数据权限查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«string»»",
              "$ref": "#/definitions/ResultDTO«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/save": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "保存数据权限",
        "operationId": "saveDataAccessUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "accessCreateDTO",
            "description": "accessCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "保存数据权限dto",
              "$ref": "#/definitions/保存数据权限dto"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/access/save.v2": {
      "post": {
        "tags": [
          "数据权限rpc"
        ],
        "summary": "保存数据权限",
        "operationId": "saveDataAccessV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "accessCreateDTO",
            "description": "accessCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "保存数据权限dto",
              "$ref": "#/definitions/保存数据权限dto"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/attr/create": {
      "post": {
        "tags": [
          "组织属性相关RPC"
        ],
        "summary": "新建组织属性",
        "description": "新建组织属性",
        "operationId": "createUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "attributeCreateDTO",
            "description": "attributeCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "新增组织属性DTO",
              "$ref": "#/definitions/新增组织属性DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/attr/delete": {
      "get": {
        "tags": [
          "组织属性相关RPC"
        ],
        "summary": "删除组织属性",
        "operationId": "deleteUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "description": "id",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/attr/list": {
      "post": {
        "tags": [
          "组织属性相关RPC"
        ],
        "summary": "组织属性列表",
        "operationId": "listUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«AttributeDTO»»",
              "$ref": "#/definitions/ResultDTO«List«AttributeDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/attr/page.query": {
      "post": {
        "tags": [
          "组织属性相关RPC"
        ],
        "summary": "查询组织属性列表",
        "description": "支持分页；支持按组织属性名称进行模糊筛选；",
        "operationId": "attributeListPageQueryUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "AttributeListPageQuery",
              "$ref": "#/definitions/AttributeListPageQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«AttributeDTO»»",
              "$ref": "#/definitions/PageResultDTO«List«AttributeDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/attr/update": {
      "post": {
        "tags": [
          "组织属性相关RPC"
        ],
        "summary": "修改组织属性",
        "operationId": "updateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "attrUpdateDTO",
            "description": "attrUpdateDTO",
            "required": true,
            "schema": {
              "originalRef": "修改组织属性DTO",
              "$ref": "#/definitions/修改组织属性DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/level/create": {
      "post": {
        "tags": [
          "组织层级相关RPC"
        ],
        "summary": "创建层级列表",
        "description": "创建层级列表",
        "operationId": "createUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationLevelCreateDTO",
            "description": "organizationLevelCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "创建组织层级DTO",
              "$ref": "#/definitions/创建组织层级DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/level/delete": {
      "get": {
        "tags": [
          "组织层级相关RPC"
        ],
        "summary": "删除组织层级",
        "operationId": "deleteUsingGET_1",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "description": "id",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/level/list": {
      "post": {
        "tags": [
          "组织层级相关RPC"
        ],
        "summary": "查询层级列表",
        "description": "查询层级列表",
        "operationId": "listUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "层级分页查询条件",
              "$ref": "#/definitions/层级分页查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«OrganizationLevelListDTO»»",
              "$ref": "#/definitions/ResultDTO«List«OrganizationLevelListDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/level/update": {
      "post": {
        "tags": [
          "组织层级相关RPC"
        ],
        "summary": "更新层级列表",
        "operationId": "updateUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationLevelUpdate",
            "description": "organizationLevelUpdate",
            "required": true,
            "schema": {
              "originalRef": "更新组织层级DTO",
              "$ref": "#/definitions/更新组织层级DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/allIdsByParentIdList": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据idList查所在节点和所有的下级节点",
        "operationId": "allIdsByParentIdListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "通过节点id列表,查询所在节点和下级节点的数据",
              "$ref": "#/definitions/通过节点id列表,查询所在节点和下级节点的数据"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/allIdsByParentIdList.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据idList查所在节点和所有的下级节点",
        "operationId": "allIdsByParentIdListV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "通过节点id列表,查询所在节点和下级节点的数据",
              "$ref": "#/definitions/通过节点id列表,查询所在节点和下级节点的数据"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/attribute/list": {
      "get": {
        "tags": [
          "节点属性相关rpc"
        ],
        "summary": "按租户查询所有组织节点属性列表",
        "operationId": "attributeListUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«AttributeDTO»»",
              "$ref": "#/definitions/ResultDTO«List«AttributeDTO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/batchCreate": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "批量新增节点",
        "operationId": "batchCreateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationCreateDTOList",
            "description": "organizationCreateDTOList",
            "required": true,
            "schema": {
              "type": "array",
              "items": {
                "originalRef": "创建资源节点",
                "$ref": "#/definitions/创建资源节点"
              }
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/batchUpdateResource": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "批量更新资源节点",
        "operationId": "batchUpdateResourceUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationUpdateResourceDTO",
            "description": "organizationUpdateResourceDTO",
            "required": true,
            "schema": {
              "originalRef": "修改资源DTO",
              "$ref": "#/definitions/修改资源DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/create": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "创建组织/资源绑定组织",
        "operationId": "createUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationCreateDTO",
            "description": "organizationCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "创建组织节点DTO",
              "$ref": "#/definitions/创建组织节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/create.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "新建组织节点",
        "description": "目前惟客云体系内，默认根节点为租户节点，应用为租户下级节点，目前创建的组织挂载在应用层级下；",
        "operationId": "createV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationCreateDTO",
            "description": "organizationCreateDTO",
            "required": true,
            "schema": {
              "originalRef": "创建组织节点DTO",
              "$ref": "#/definitions/创建组织节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/delete": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "删除组织节点",
        "operationId": "deleteUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "description": "id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/delete.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "删除组织节点",
        "description": "有下级的组织节点无法删除；调用删除接口时需确定，所删除的组织在业务系统中的依赖已经全部解除；删除的逆向操作一般在业务系统不支持，停用组织节点建议使用管理组织节点状态接口；",
        "operationId": "deleteV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "description": "id",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/deleteByResourceId": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据资源id删除节点",
        "operationId": "deleteByResourceIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationDeleteResourceDTO",
            "description": "organizationDeleteResourceDTO",
            "required": true,
            "schema": {
              "originalRef": "删除资源节点DTO",
              "$ref": "#/definitions/删除资源节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/disableByResourceId": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据资源id禁用",
        "operationId": "disableByResourceIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationUpdateByResourceIdDTO",
            "description": "organizationUpdateByResourceIdDTO",
            "required": true,
            "schema": {
              "originalRef": "根据资源id启用、禁用节点",
              "$ref": "#/definitions/根据资源id启用、禁用节点"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/enableByResourceId": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据资源id启用节点",
        "operationId": "enableByResourceIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationUpdateByResourceIdDTO",
            "description": "organizationUpdateByResourceIdDTO",
            "required": true,
            "schema": {
              "originalRef": "根据资源id启用、禁用节点",
              "$ref": "#/definitions/根据资源id启用、禁用节点"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/getAppIdByOrgId": {
      "get": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据组织id查询所属的应用id",
        "operationId": "getAppIdByOrgIdUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "orgId",
            "in": "query",
            "description": "orgId",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/getResourceOrgInfo": {
      "get": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据资源id查找当前组织信息",
        "operationId": "getResourceOrgInfoUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "resourceId",
            "in": "query",
            "description": "resourceId",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«组织列表dto»",
              "$ref": "#/definitions/ResultDTO«组织列表dto»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/list": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "组织列表，不分页",
        "operationId": "listUsingPOST_5",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织查询query",
              "$ref": "#/definitions/组织查询query"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织列表dto»»",
              "$ref": "#/definitions/ResultDTO«List«组织列表dto»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/list.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "组织列表，不分页",
        "operationId": "listV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织查询query",
              "$ref": "#/definitions/组织查询query"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织列表dto»»",
              "$ref": "#/definitions/ResultDTO«List«组织列表dto»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/node/copy": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "复制组织节点",
        "description": "支持将指定节点及其子节点复制到另一个节点下；只会复制组织信息，不会同步复制组织负责人信息；",
        "operationId": "copyUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationCopyDTO",
            "description": "organizationCopyDTO",
            "required": true,
            "schema": {
              "originalRef": "复制组织节点DTO",
              "$ref": "#/definitions/复制组织节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/node/manager.status": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "管理组织节点状态",
        "description": "可启用/停用组织节点；支持同步禁用下级；禁用组织时支持选择是否同步禁用下级组织，但是不支持同步启用；",
        "operationId": "managerOrganizationNodeStatusUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationManagerStatusDTO",
            "description": "organizationManagerStatusDTO",
            "required": true,
            "schema": {
              "originalRef": "管理组织节点状态DTO",
              "$ref": "#/definitions/管理组织节点状态DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/node/page.query": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "查询组织节点列表",
        "description": "以列表方式返回，无上下层级关系；支持分页；支持指定组织IDS；支持组织节点名称筛选；",
        "operationId": "pageQueryNodeListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织查询query",
              "$ref": "#/definitions/组织查询query"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«组织列表dto»»",
              "$ref": "#/definitions/PageResultDTO«List«组织列表dto»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/node/query.tree": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "查询组织树",
        "description": "支持查询的指定上级组织全部下级节点；支持根据组织节点状态进行筛选；支持设置只返回下一层级；",
        "operationId": "queryNodeTreeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织树查询条件，可以返回整颗树，也可以懒加载只返回两层",
              "$ref": "#/definitions/组织树查询条件，可以返回整颗树，也可以懒加载只返回两层"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树»»",
              "$ref": "#/definitions/ResultDTO«List«组织树»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/query/lazyLoadingTree": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "组织树查询，懒加载",
        "operationId": "queryTreeLazyLoadingUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "懒加载，组织树查询条件",
              "$ref": "#/definitions/懒加载，组织树查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树»»",
              "$ref": "#/definitions/ResultDTO«List«组织树»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/query/lazyLoadingTree.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "组织树查询，懒加载",
        "operationId": "queryTreeLazyLoadingV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "懒加载，组织树查询条件",
              "$ref": "#/definitions/懒加载，组织树查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树»»",
              "$ref": "#/definitions/ResultDTO«List«组织树»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/query/tree": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "组织树查询，返回整颗树",
        "operationId": "queryTreeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织树查询条件，返回整颗树",
              "$ref": "#/definitions/组织树查询条件，返回整颗树"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树»»",
              "$ref": "#/definitions/ResultDTO«List«组织树»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/query/tree.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "新建组织节点",
        "description": "目前惟客云体系内，默认根节点为租户节点，应用为租户下级节点，目前创建的组织挂载在应用层级下；",
        "operationId": "queryTreeV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织树查询条件，返回整颗树",
              "$ref": "#/definitions/组织树查询条件，返回整颗树"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树»»",
              "$ref": "#/definitions/ResultDTO«List«组织树»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/queryParentNames": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "根据idList查所属的节点名字，返回/拼接的所属节点名字",
        "operationId": "queryParentNamesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "OrganizationNodeParentNamesQuery",
              "$ref": "#/definitions/OrganizationNodeParentNamesQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织列表vo»»",
              "$ref": "#/definitions/ResultDTO«List«组织列表vo»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/update": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "更新组织/ 更新资源绑定组织",
        "operationId": "updateUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationUpdateDTO",
            "description": "organizationUpdateDTO",
            "required": true,
            "schema": {
              "originalRef": "修改组织节点DTO",
              "$ref": "#/definitions/修改组织节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/org/update.v2": {
      "post": {
        "tags": [
          "节点相关rpc"
        ],
        "summary": "编辑组织节点",
        "description": "编辑组织节点",
        "operationId": "updateV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationUpdateDTO",
            "description": "organizationUpdateDTO",
            "required": true,
            "schema": {
              "originalRef": "修改组织节点DTO",
              "$ref": "#/definitions/修改组织节点DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/orgManager/list": {
      "post": {
        "tags": [
          "组织负责人相关rpc"
        ],
        "summary": "查询组织节点的负责人",
        "description": "根据组织节点ID查询对应负责人；",
        "operationId": "listUsingPOST_4",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织负责人查询条件",
              "$ref": "#/definitions/组织负责人查询条件"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«OrganizationManagerListDTO»»",
              "$ref": "#/definitions/ResultDTO«List«OrganizationManagerListDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/orgManager/save": {
      "post": {
        "tags": [
          "组织负责人相关rpc"
        ],
        "summary": "设置组织节点的负责人",
        "description": "可设置多个组织负责人；取消负责人时可设置为空；依赖：负责人信息需要查询账号中心用户列表来获取",
        "operationId": "saveUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "organizationManagerSaveDTO",
            "description": "organizationManagerSaveDTO",
            "required": true,
            "schema": {
              "originalRef": "设置组织负责人DTO",
              "$ref": "#/definitions/设置组织负责人DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/add": {
      "post": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "添加组织树配置",
        "operationId": "addUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "createDTO",
            "description": "createDTO",
            "required": true,
            "schema": {
              "originalRef": "新增组织树配置",
              "$ref": "#/definitions/新增组织树配置"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/check": {
      "get": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "查询组织树配置是否已有配置数据权限,true 存在，false 不存在",
        "operationId": "checkUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "treeCode",
            "in": "query",
            "description": "treeCode",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/delete": {
      "post": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "删除组织树配置",
        "operationId": "deleteUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "deleteDTO",
            "description": "deleteDTO",
            "required": true,
            "schema": {
              "originalRef": "删除组织树配置",
              "$ref": "#/definitions/删除组织树配置"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/detail": {
      "get": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "组织树配置详情",
        "operationId": "detailUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "treeCode",
            "in": "query",
            "description": "treeCode",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«组织树配置列表»",
              "$ref": "#/definitions/ResultDTO«组织树配置列表»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/list": {
      "post": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "组织树配置列表，不分页查询",
        "operationId": "listUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织树配置查询",
              "$ref": "#/definitions/组织树配置查询"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«组织树配置列表»»",
              "$ref": "#/definitions/ResultDTO«List«组织树配置列表»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/page/list": {
      "post": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "组织树配置列表，分页查询",
        "operationId": "listUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "组织树配置查询",
              "$ref": "#/definitions/组织树配置查询"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«组织树配置列表»»",
              "$ref": "#/definitions/PageResultDTO«List«组织树配置列表»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-organization/rpc/treeConfig/update": {
      "post": {
        "tags": [
          "组织树相关RPC"
        ],
        "summary": "更新组织树配置",
        "operationId": "updateUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "updateDTO",
            "description": "updateDTO",
            "required": true,
            "schema": {
              "originalRef": "更新组织树配置",
              "$ref": "#/definitions/更新组织树配置"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    }
  },
  "definitions": {
    "AttributeDTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string"
        },
        "attrName": {
          "type": "string"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "nodeNumber": {
          "type": "integer",
          "format": "int64"
        },
        "sysConfig": {
          "type": "integer",
          "format": "int32"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time"
        }
      },
      "title": "AttributeDTO"
    },
    "AttributeListPageQuery": {
      "type": "object",
      "required": [
        "tenantId"
      ],
      "properties": {
        "attrName": {
          "type": "string",
          "description": "组织属性名称"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "originalRef": "SortingField",
            "$ref": "#/definitions/SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "AttributeListPageQuery",
      "description": "组织属性查询条件"
    },
    "OrganizationLevelListDTO": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "levelCode": {
          "type": "string",
          "description": "层级编码"
        },
        "levelName": {
          "type": "string",
          "description": "层级别名"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "层级数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "OrganizationLevelListDTO"
    },
    "OrganizationManagerListDTO": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "managerId": {
          "type": "integer",
          "format": "int64",
          "description": "负责人id"
        },
        "managerName": {
          "type": "string",
          "description": "负责人名称"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "OrganizationManagerListDTO"
    },
    "OrganizationNodeParentNamesQuery": {
      "type": "object",
      "properties": {
        "isolationParentIds": {
          "type": "array",
          "description": "隔离id",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "parentIds": {
          "type": "array",
          "description": "父节点id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "OrganizationNodeParentNamesQuery"
    },
    "PageResultDTO«List«AttributeDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "AttributeDTO",
            "$ref": "#/definitions/AttributeDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«AttributeDTO»»"
    },
    "PageResultDTO«List«组织列表dto»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织列表dto",
            "$ref": "#/definitions/组织列表dto"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«组织列表dto»»"
    },
    "PageResultDTO«List«组织树配置列表»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织树配置列表",
            "$ref": "#/definitions/组织树配置列表"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«组织树配置列表»»"
    },
    "ResultDTO«List«AttributeDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "AttributeDTO",
            "$ref": "#/definitions/AttributeDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«AttributeDTO»»"
    },
    "ResultDTO«List«OrganizationLevelListDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "OrganizationLevelListDTO",
            "$ref": "#/definitions/OrganizationLevelListDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«OrganizationLevelListDTO»»"
    },
    "ResultDTO«List«OrganizationManagerListDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "OrganizationManagerListDTO",
            "$ref": "#/definitions/OrganizationManagerListDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«OrganizationManagerListDTO»»"
    },
    "ResultDTO«List«long»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«long»»"
    },
    "ResultDTO«List«string»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«string»»"
    },
    "ResultDTO«List«组织列表dto»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织列表dto",
            "$ref": "#/definitions/组织列表dto"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«组织列表dto»»"
    },
    "ResultDTO«List«组织列表vo»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织列表vo",
            "$ref": "#/definitions/组织列表vo"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«组织列表vo»»"
    },
    "ResultDTO«List«组织树»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织树",
            "$ref": "#/definitions/组织树"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«组织树»»"
    },
    "ResultDTO«List«组织树配置列表»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "组织树配置列表",
            "$ref": "#/definitions/组织树配置列表"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«组织树配置列表»»"
    },
    "ResultDTO«Map«long,List«string»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "object",
          "additionalProperties": {
            "type": "array",
            "items": {
              "type": "string"
            }
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«Map«long,List«string»»»"
    },
    "ResultDTO«boolean»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "boolean"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«boolean»"
    },
    "ResultDTO«long»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int64"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«long»"
    },
    "ResultDTO«组织列表dto»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "originalRef": "组织列表dto",
          "$ref": "#/definitions/组织列表dto"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«组织列表dto»"
    },
    "ResultDTO«组织树配置列表»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "originalRef": "组织树配置列表",
          "$ref": "#/definitions/组织树配置列表"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«组织树配置列表»"
    },
    "SortingField": {
      "type": "object",
      "properties": {
        "asc": {
          "type": "boolean",
          "description": "是否升序, 默认升序"
        },
        "column": {
          "type": "string",
          "description": "排序字段"
        }
      },
      "title": "SortingField"
    },
    "TenantOutSideEntityMap": {
      "type": "object",
      "properties": {
        "outsideEntityId": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "TenantOutSideEntityMap"
    },
    "TreeConfigAttributeDTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "属性编码"
        },
        "attrName": {
          "type": "string",
          "description": "属性名字"
        }
      },
      "title": "TreeConfigAttributeDTO"
    },
    "保存数据权限dto": {
      "type": "object",
      "properties": {
        "nodeIds": {
          "type": "array",
          "description": "节点id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "outsideEntityId": {
          "type": "string",
          "description": "外部实体id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        },
        "treeCode": {
          "type": "string",
          "description": "treeCode 组织树配置编码"
        }
      },
      "title": "保存数据权限dto"
    },
    "修改组织属性DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "属性编码"
        },
        "attrName": {
          "type": "string",
          "description": "属性名称"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "主键"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id,rpc时必传"
        }
      },
      "title": "修改组织属性DTO"
    },
    "修改组织节点DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "主键"
        },
        "linkageUpdateStatus": {
          "type": "boolean",
          "description": "是否联动判断状态"
        },
        "nodeCode": {
          "type": "string",
          "description": " 节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点， 1是，0 否"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名称"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "显示顺序"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "修改组织节点DTO"
    },
    "修改资源DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "linkageUpdateStatus": {
          "type": "boolean",
          "description": "是否联动判断状态"
        },
        "nodeCode": {
          "type": "string",
          "description": " 节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点， 1是，0 否"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名称"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型，org 组织，resource 资源"
        },
        "parentIdList": {
          "type": "array",
          "description": "父id集合",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "显示顺序"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "修改资源DTO"
    },
    "创建组织层级DTO": {
      "type": "object",
      "properties": {
        "levelCode": {
          "type": "string",
          "description": "层级编码"
        },
        "levelName": {
          "type": "string",
          "description": "层级别名"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "层级数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id,RPC调用时必传"
        }
      },
      "title": "创建组织层级DTO"
    },
    "创建组织节点DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "linkageUpdateStatus": {
          "type": "boolean",
          "description": "是否联动判断状态"
        },
        "nodeCode": {
          "type": "string",
          "description": "节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点， 1是，0 否"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名称"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型，org 组织，resource 资源"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "显示顺序"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id，上级节点为0时不能为空"
        }
      },
      "title": "创建组织节点DTO"
    },
    "创建资源节点": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "linkageUpdateStatus": {
          "type": "boolean",
          "description": "是否联动判断状态"
        },
        "nodeCode": {
          "type": "string",
          "description": "节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点， 1是，0 否"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名称"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型，org 组织，resource 资源"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "显示顺序"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id，上级节点为0时不能为空"
        }
      },
      "title": "创建资源节点"
    },
    "删除组织树配置": {
      "type": "object",
      "required": [
        "treeCode"
      ],
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树编码，唯一标识"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "删除组织树配置"
    },
    "删除资源节点DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "节点属性"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "删除资源节点DTO"
    },
    "复制组织节点DTO": {
      "type": "object",
      "properties": {
        "sourceNodeList": {
          "type": "array",
          "description": "源节点详情列表",
          "items": {
            "originalRef": "来源节点详情",
            "$ref": "#/definitions/来源节点详情"
          }
        },
        "targetNodeList": {
          "type": "array",
          "description": "目标节点列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "复制组织节点DTO"
    },
    "层级分页查询条件": {
      "type": "object",
      "properties": {
        "levelName": {
          "type": "string",
          "description": "层级名字"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "层级分页查询条件"
    },
    "懒加载，组织树查询条件": {
      "type": "object",
      "required": [
        "treeCode",
        "type"
      ],
      "properties": {
        "afterSearchIsShowParent": {
          "type": "boolean",
          "description": " 搜索后是否展示上级"
        },
        "attrCodeList": {
          "type": "array",
          "description": "指定展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离的父id，根据隔离的父id，往下找",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "linkage": {
          "type": "boolean",
          "description": "是否联动"
        },
        "needCountAttrCode": {
          "type": "array",
          "description": "需要统计的节点属性",
          "items": {
            "type": "string"
          }
        },
        "nodeNames": {
          "type": "array",
          "description": "节点名称",
          "items": {
            "type": "string"
          }
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "节点状态，1 启用 0禁用"
        },
        "notShowAttrCodeList": {
          "type": "array",
          "description": "指定不展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "outsideEntityId": {
          "type": "array",
          "description": "外部实体id。外部实体：业务系统的，例如：角色，账号",
          "items": {
            "type": "string"
          }
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点"
        },
        "perView": {
          "type": "boolean",
          "description": "是否预览模式"
        },
        "showResource": {
          "type": "boolean",
          "description": "是否展示资源：默认true展示资源"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树配置code:treeCode"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "description": "组织树查询类型：4：查询组织树，不根据权限过滤，展示全部的节点，懒加载5：查询组织树，根据权限过滤，懒加载，只展示有权限的节点6：查询组织树，根据权限过滤，懒加载，展示有权限的节点的所有的上级"
        }
      },
      "title": "懒加载，组织树查询条件"
    },
    "数据权限查询条件": {
      "type": "object",
      "properties": {
        "attrCodeList": {
          "type": "array",
          "description": "节点属性",
          "items": {
            "type": "string"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离id",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "outsideEntityId": {
          "type": "array",
          "description": "外部实体id，(业务系统的，例如：角色，账号)",
          "items": {
            "type": "string"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树配置code"
        }
      },
      "title": "数据权限查询条件"
    },
    "新增组织属性DTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "属性编码"
        },
        "attrName": {
          "type": "string",
          "description": "属性名称"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id,rpc时必传"
        }
      },
      "title": "新增组织属性DTO"
    },
    "新增组织树配置": {
      "type": "object",
      "required": [
        "attrCodeList",
        "configStatus",
        "linkage",
        "treeCode",
        "treeName"
      ],
      "properties": {
        "attrCodeList": {
          "type": "array",
          "description": "节点属性list",
          "items": {
            "type": "string"
          }
        },
        "configDesc": {
          "type": "string",
          "description": "描述"
        },
        "configStatus": {
          "type": "integer",
          "format": "int32",
          "description": "状态，0-禁用，1 启用"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "linkage": {
          "type": "integer",
          "format": "int32",
          "description": "节点是否联动，0 否，1是"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树编码，唯一标识"
        },
        "treeName": {
          "type": "string",
          "description": "组织树名称"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "新增组织树配置"
    },
    "更新组织层级DTO": {
      "type": "object",
      "properties": {
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "主键"
        },
        "levelCode": {
          "type": "string",
          "description": "层级编码"
        },
        "levelName": {
          "type": "string",
          "description": "层级别名"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "层级数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id,RPC调用时必传"
        }
      },
      "title": "更新组织层级DTO"
    },
    "更新组织树配置": {
      "type": "object",
      "required": [
        "attrCodeList",
        "configStatus",
        "linkage",
        "treeCode",
        "treeName"
      ],
      "properties": {
        "attrCodeList": {
          "type": "array",
          "description": "节点属性list",
          "items": {
            "type": "string"
          }
        },
        "configDesc": {
          "type": "string",
          "description": "描述"
        },
        "configStatus": {
          "type": "integer",
          "format": "int32",
          "description": "状态，0-禁用，1 启用"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "linkage": {
          "type": "integer",
          "format": "int32",
          "description": "节点是否联动，0 否，1是"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树编码，唯一标识"
        },
        "treeName": {
          "type": "string",
          "description": "组织树名称"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "更新组织树配置"
    },
    "来源节点详情": {
      "type": "object",
      "properties": {
        "copyType": {
          "type": "integer",
          "format": "int32",
          "description": "克隆类型"
        },
        "nodeId": {
          "type": "integer",
          "format": "int64",
          "description": "节点id"
        }
      },
      "title": "来源节点详情"
    },
    "根据资源id启用、禁用节点": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "节点属性"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "根据资源id启用、禁用节点"
    },
    "管理组织节点状态DTO": {
      "type": "object",
      "properties": {
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "组织节点主键id"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "subordinateNodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否同步下级节点状态，1 同步，0 不同步"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "description": "更新人id"
        }
      },
      "title": "管理组织节点状态DTO"
    },
    "组织列表dto": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "attrName": {
          "type": "string",
          "description": "属性名字"
        },
        "childrenNum": {
          "type": "integer",
          "format": "int64",
          "description": "下级节点总数"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "delFlag": {
          "type": "integer",
          "format": "int32",
          "description": " 删除标记，1-已删除，0-未删除"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "自增id"
        },
        "levelName": {
          "type": "string",
          "description": "层级别名"
        },
        "nodeCode": {
          "type": "string",
          "description": " 节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点， 1是，0 否"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "节点级别"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名字"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "是否启用，1 启用，0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型，org 组织，resource 资源"
        },
        "outsideEntityId": {
          "type": "string",
          "description": "外部实体id，(业务系统的，例如：角色，账号)"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "parentIds": {
          "type": "string",
          "description": "上级组织ids"
        },
        "parentName": {
          "type": "string",
          "description": "上级组织名字"
        },
        "permission": {
          "type": "integer",
          "format": "int32",
          "description": "是否有权限,1 是，0 否"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "显示顺序"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "组织列表dto"
    },
    "组织列表vo": {
      "type": "object",
      "properties": {
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "自增id"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名字"
        },
        "parentId": {
          "type": "string",
          "description": "上级组织id"
        },
        "parentIds": {
          "type": "string",
          "description": "节点对应的parentIds"
        },
        "parentName": {
          "type": "string",
          "description": "上级组织名字。比如：华南大区/珠海"
        },
        "parentNames": {
          "type": "string",
          "description": "上级组织名字。比如：华南大区/珠海"
        }
      },
      "title": "组织列表vo"
    },
    "组织查询query": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识code"
        },
        "attrCodeList": {
          "type": "array",
          "description": "指定展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "ids": {
          "type": "array",
          "description": "节点id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离的父id，根据隔离的父id，往下找",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "isolationParentIdsList": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "nodeCode": {
          "type": "string",
          "description": "组织编码"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "节点层级"
        },
        "nodeNames": {
          "type": "array",
          "description": "多个节点名",
          "items": {
            "type": "string"
          }
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "状态，1 启用 0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型，org 组织，resource 资源"
        },
        "notShowAttrCodeList": {
          "type": "array",
          "description": "指定不展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "parentIdsList": {
          "type": "array",
          "description": "上级节点ids 的 列表",
          "items": {
            "type": "string"
          }
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "resourceIdList": {
          "type": "array",
          "description": "资源ids",
          "items": {
            "type": "string"
          }
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "showResource": {
          "type": "boolean",
          "description": "是否展示资源：默认true展示资源"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "originalRef": "SortingField",
            "$ref": "#/definitions/SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "组织查询query"
    },
    "组织树": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织属性标识"
        },
        "attrName": {
          "type": "string",
          "description": "组织属性名字"
        },
        "children": {
          "type": "array",
          "description": "下级组织",
          "items": {
            "originalRef": "组织树",
            "$ref": "#/definitions/组织树"
          }
        },
        "childrenNum": {
          "type": "integer",
          "format": "int64",
          "description": "下级节点总数"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "自增id"
        },
        "levelName": {
          "type": "string",
          "description": "层级名称"
        },
        "needCountAttrCode": {
          "type": "object",
          "description": "统计的节点属性的数量",
          "additionalProperties": {
            "type": "integer",
            "format": "int64"
          }
        },
        "nodeCode": {
          "type": "string",
          "description": "节点编码(唯一标识)"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点,1是，0否"
        },
        "nodeLevel": {
          "type": "integer",
          "format": "int32",
          "description": "节点级别"
        },
        "nodeName": {
          "type": "string",
          "description": "节点名字"
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "状态，1 启用，0 禁用"
        },
        "nodeType": {
          "type": "string",
          "description": "节点类型,org 组织,resource 资源"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父id"
        },
        "parentIds": {
          "type": "string",
          "description": "上级节点ids"
        },
        "parentName": {
          "type": "string",
          "description": "父节点名字"
        },
        "permission": {
          "type": "integer",
          "format": "int32",
          "description": "是否有权限,1 是，0 否"
        },
        "resourceChildrenNum": {
          "type": "integer",
          "format": "int64",
          "description": "所有下级资源总数"
        },
        "resourceId": {
          "type": "string",
          "description": "资源id"
        },
        "resourceNextChildrenNum": {
          "type": "integer",
          "format": "int64",
          "description": "所在本节点的下一层的资源总数"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "节点顺序"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "组织树"
    },
    "组织树查询条件，可以返回整颗树，也可以懒加载只返回两层": {
      "type": "object",
      "required": [
        "treeCode",
        "type"
      ],
      "properties": {
        "afterSearchIsShowParent": {
          "type": "boolean",
          "description": " 搜索后是否展示上级"
        },
        "attrCodeList": {
          "type": "array",
          "description": "指定展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离的父id，根据隔离的父id，往下找",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "linkage": {
          "type": "boolean",
          "description": "是否联动"
        },
        "needCountAttrCode": {
          "type": "array",
          "description": "需要统计的节点属性",
          "items": {
            "type": "string"
          }
        },
        "nodeNames": {
          "type": "array",
          "description": "节点名称",
          "items": {
            "type": "string"
          }
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "节点状态，1 启用 0禁用"
        },
        "notShowAttrCodeList": {
          "type": "array",
          "description": "指定不展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "outsideEntityId": {
          "type": "array",
          "description": "外部实体id。外部实体：业务系统的，例如：角色，账号",
          "items": {
            "type": "string"
          }
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点"
        },
        "perView": {
          "type": "boolean",
          "description": "是否预览模式"
        },
        "searchOrgIdList": {
          "type": "array",
          "description": "用来查询的组织id列表,type = 1时参数生效",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "showResource": {
          "type": "boolean",
          "description": "是否展示资源：默认true展示资源"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树配置code:treeCode"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "description": "组织树查询类型：1：查询组织树，不根据权限过滤，展示全部的节点，2：查询组织树，根据权限过滤3：查询组织树，根据权限过滤，展示有权限的节点的所有的上级4：查询组织树，不根据权限过滤，展示全部的节点，懒加载5：查询组织树，根据权限过滤，懒加载，只展示有权限的节点6：查询组织树，根据权限过滤，懒加载，展示有权限的节点的所有的上级"
        }
      },
      "title": "组织树查询条件，可以返回整颗树，也可以懒加载只返回两层"
    },
    "组织树查询条件，返回整颗树": {
      "type": "object",
      "required": [
        "treeCode",
        "type"
      ],
      "properties": {
        "afterSearchIsShowParent": {
          "type": "boolean",
          "description": " 搜索后是否展示上级"
        },
        "attrCodeList": {
          "type": "array",
          "description": "指定展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离的父id，根据隔离的父id，往下找",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "linkage": {
          "type": "boolean",
          "description": "是否联动"
        },
        "needCountAttrCode": {
          "type": "array",
          "description": "需要统计的节点属性",
          "items": {
            "type": "string"
          }
        },
        "nodeStatus": {
          "type": "integer",
          "format": "int32",
          "description": "节点状态，1 启用 0禁用"
        },
        "notShowAttrCodeList": {
          "type": "array",
          "description": "指定不展示的节点属性",
          "items": {
            "type": "string"
          }
        },
        "outsideEntityId": {
          "type": "array",
          "description": "外部实体id。外部实体：业务系统的，例如：角色，账号",
          "items": {
            "type": "string"
          }
        },
        "perView": {
          "type": "boolean",
          "description": "是否预览模式"
        },
        "searchOrgIdList": {
          "type": "array",
          "description": "用来查询的组织id列表,type = 1时参数生效",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "showResource": {
          "type": "boolean",
          "description": "是否展示资源：默认true展示资源"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树配置code:treeCode"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "description": "组织树查询类型：1：查询组织树，不根据权限过滤，展示全部的节点，2：查询组织树，根据权限过滤3：查询组织树，根据权限过滤，展示有权限的节点的所有的上级"
        }
      },
      "title": "组织树查询条件，返回整颗树"
    },
    "组织树配置列表": {
      "type": "object",
      "properties": {
        "attributeList": {
          "type": "array",
          "description": "节点属性列表",
          "items": {
            "originalRef": "TreeConfigAttributeDTO",
            "$ref": "#/definitions/TreeConfigAttributeDTO"
          }
        },
        "configDesc": {
          "type": "string",
          "description": "描述"
        },
        "configStatus": {
          "type": "integer",
          "format": "int32",
          "description": "状态，0-禁用，1 启用"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "linkage": {
          "type": "integer",
          "format": "int32",
          "description": "节点是否联动，0 否，1是"
        },
        "sysConfig": {
          "type": "integer",
          "format": "int32",
          "description": "是否系统配置,是否系统配置，0 否，1是"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树编码，唯一标识"
        },
        "treeName": {
          "type": "string",
          "description": "组织树名称"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "组织树配置列表"
    },
    "组织树配置查询": {
      "type": "object",
      "properties": {
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "originalRef": "SortingField",
            "$ref": "#/definitions/SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "treeCode": {
          "type": "string",
          "description": "组织树配置编码"
        },
        "treeName": {
          "type": "string",
          "description": "组织树配置名字"
        }
      },
      "title": "组织树配置查询"
    },
    "组织负责人查询条件": {
      "type": "object",
      "properties": {
        "organizationId": {
          "type": "integer",
          "format": "int64",
          "description": "组织id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id，上级节点为0时不能为空"
        }
      },
      "title": "组织负责人查询条件"
    },
    "设置组织负责人DTO": {
      "type": "object",
      "properties": {
        "managerIds": {
          "type": "array",
          "description": "负责人ids",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "organizationId": {
          "type": "integer",
          "format": "int64",
          "description": "组织id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id，上级节点为0时不能为空"
        }
      },
      "title": "设置组织负责人DTO"
    },
    "通过节点id列表,查询所在节点和下级节点的数据": {
      "type": "object",
      "properties": {
        "ids": {
          "type": "array",
          "description": "节点id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "isolationParentIds": {
          "type": "array",
          "description": "隔离id",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "通过节点id列表,查询所在节点和下级节点的数据"
    }
  }
}', 1, '2024-07-18 17:49:55');
INSERT INTO wd_ipaas.dw_swagger_info (id, swagger_name, description, swagger_url, api_amount, api_group_id, api_domain_name, create_user, create_time, update_time, lessee_id, execute_status, import_type, swagger_json, resp_mapping_rule, parse_time) VALUES (7, 'wd-commerce', '', '', 43, 2, 'http://wd-commerce.wakecloud.svc.cluster.local:8080', '13012345678', '2024-07-23 12:03:59', '2024-07-23 12:04:15', 3, 2, 1, '{
  "swagger": "2.0",
  "info": {
    "description": "交易-小程序端接口文档",
    "version": "1.0",
    "title": "交易-小程序端接口文档"
  },
  "host": "172.26.63.64:17816",
  "tags": [
    {
      "name": "[APP-STAFF]交易-核销码",
      "description": "App Staff Write Off Code Controller"
    },
    {
      "name": "[App]业态通-交易",
      "description": "Ytt Order Controller"
    },
    {
      "name": "[App]业态通-退款",
      "description": "Ytt Order Refund Controller"
    },
    {
      "name": "[App]交易-优惠券",
      "description": "App Coupon Controller"
    },
    {
      "name": "[App]交易-订单",
      "description": "App Order Controller"
    },
    {
      "name": "[App]交易-订单-评价",
      "description": "App Order Evaluate Controller"
    },
    {
      "name": "[App]售后相关接口",
      "description": "App Refund Controller"
    },
    {
      "name": "[App]购物车",
      "description": "App Shopping Cart Controller"
    },
    {
      "name": "[app]交易-物流",
      "description": "Logistics Controller"
    },
    {
      "name": "[integration]业态通-订单同步",
      "description": "Ytt Order Sync Controller"
    },
    {
      "name": "app-write-off-code-controller",
      "description": "App Write Off Code Controller"
    }
  ],
  "paths": {
    "/commerce/app/coupon/query/able-coupons": {
      "post": {
        "tags": [
          "[App]交易-优惠券"
        ],
        "summary": "查询可用优惠券",
        "description": "查询可用优惠券",
        "operationId": "calPriceUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "requestOrderDTO",
            "description": "requestOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/请求下单DTO",
              "originalRef": "请求下单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«可用优惠券DTO»",
              "originalRef": "ResultDTO«可用优惠券DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/logistics/query/logistics/info": {
      "post": {
        "tags": [
          "[app]交易-物流"
        ],
        "summary": "查询物流信息",
        "description": "查询物流信息",
        "operationId": "queryLogisticsInfoUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderQueryDTO",
            "description": "orderQueryDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/查询订单对象",
              "originalRef": "查询订单对象"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«WKQueryOrderResponse»",
              "originalRef": "ResultDTO«WKQueryOrderResponse»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/logistics/query/logistics/trace": {
      "post": {
        "tags": [
          "[app]交易-物流"
        ],
        "summary": "查询物流轨迹",
        "description": "查询物流轨迹",
        "operationId": "queryLogisticsTraceUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "wkQueryOrderRequest",
            "description": "wkQueryOrderRequest",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单轨迹查询对象",
              "originalRef": "订单轨迹查询对象"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«WKQueryRouteResponse»",
              "originalRef": "ResultDTO«WKQueryRouteResponse»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/logistics/rpc/receive": {
      "post": {
        "tags": [
          "[app]交易-物流"
        ],
        "summary": "买家收货",
        "description": "买家收货",
        "operationId": "buyerItemReceiveUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "buyerItemReceiveDTO",
            "description": "buyerItemReceiveDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/买家收货参数",
              "originalRef": "买家收货参数"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/additional/evaluate": {
      "post": {
        "tags": [
          "[App]交易-订单-评价"
        ],
        "summary": "追评",
        "operationId": "orderItemAdditionalEvaluateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "dtoList",
            "description": "dtoList",
            "required": true,
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/OrderItemAdditionalEvaluateDTO",
                "originalRef": "OrderItemAdditionalEvaluateDTO"
              }
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/cal-price": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "计算价格",
        "description": "计算价格",
        "operationId": "calPriceUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "requestOrderDTO",
            "description": "requestOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/请求下单DTO",
              "originalRef": "请求下单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«订单DTO»",
              "originalRef": "ResultDTO«订单DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/cancel-order": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "取消订单",
        "description": "取消订单",
        "operationId": "cancelOrderUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderDTO",
            "description": "orderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单DTO",
              "originalRef": "订单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/evaluate": {
      "post": {
        "tags": [
          "[App]交易-订单-评价"
        ],
        "summary": "评价",
        "operationId": "orderItemEvaluateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "dtoList",
            "description": "dtoList",
            "required": true,
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/OrderItemEvaluateDTO",
                "originalRef": "OrderItemEvaluateDTO"
              }
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/order": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "下单",
        "description": "下单",
        "operationId": "orderUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "requestOrderDTO",
            "description": "requestOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/请求下单DTO",
              "originalRef": "请求下单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«订单DTO»",
              "originalRef": "ResultDTO«订单DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/pay": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "调起支付",
        "description": "调起支付",
        "operationId": "payUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderDTO",
            "description": "orderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单DTO",
              "originalRef": "订单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«支付凭证DTO»",
              "originalRef": "ResultDTO«支付凭证DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/query/detail": {
      "get": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "查询订单详情",
        "description": "查询订单详情",
        "operationId": "detailUsingGET_4",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "orderNo",
            "in": "query",
            "description": "订单编号",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«C端订单详情»",
              "originalRef": "ResultDTO«C端订单详情»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/query/detail/last": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "用户最近一次下单订单详情",
        "description": "用户最近一次下单订单详情",
        "operationId": "queryMemberLastOrderDetailUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/MemberLastOrderQuery",
              "originalRef": "MemberLastOrderQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«C端订单详情»",
              "originalRef": "ResultDTO«C端订单详情»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/query/list": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "查询订单列表",
        "description": "查询订单列表",
        "operationId": "listUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/AppOrderPageQuery",
              "originalRef": "AppOrderPageQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«C端订单列表»»",
              "originalRef": "PageResultDTO«List«C端订单列表»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/query/statistics": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "c端订单统计(待付款数量/待发货数量/待收货数量/待核销数量)",
        "description": "c端订单统计(待付款数量/待发货数量/待收货数量/待核销数量)",
        "operationId": "orderStatisticsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/c端订单统计查询参数",
              "originalRef": "c端订单统计查询参数"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«c端订单统计»",
              "originalRef": "ResultDTO«c端订单统计»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/order/update/appointment-time": {
      "post": {
        "tags": [
          "[App]交易-订单"
        ],
        "summary": "修改预约时间",
        "description": "修改预约时间",
        "operationId": "updateAppointmentTimeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderItemAppointmentTimeDTO",
            "description": "orderItemAppointmentTimeDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单商品预约时间DTO0",
              "originalRef": "订单商品预约时间DTO0"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/cal.refund.amount": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "C端计算可退金额",
        "description": "C端计算可退金额",
        "operationId": "calRefundAmountUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderRefundCalAmountRequest",
            "description": "orderRefundCalAmountRequest",
            "required": true,
            "schema": {
              "$ref": "#/definitions/OrderRefundCalAmountRequest",
              "originalRef": "OrderRefundCalAmountRequest"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«OrderRefundCalAmountResponse»",
              "originalRef": "ResultDTO«OrderRefundCalAmountResponse»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/cancel": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "撤销退款申请",
        "description": "撤销退款申请",
        "operationId": "cancelUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "cancelRefundOrderDTO",
            "description": "cancelRefundOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/撤销退款申请DTO",
              "originalRef": "撤销退款申请DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/query/refundDetail": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "C端退款订单详情",
        "description": "C端退款订单详情",
        "operationId": "refundListUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "refundOrderNo",
            "in": "query",
            "description": "退款订单编号",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«退款订单DTO»",
              "originalRef": "ResultDTO«退款订单DTO»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/query/refundList": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "C端退款订单列表",
        "description": "C端退款订单列表",
        "operationId": "refundListUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/退款订单查询query",
              "originalRef": "退款订单查询query"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«退款订单DTO»»",
              "originalRef": "PageResultDTO«List«退款订单DTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/refund.create": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "C端退款订单创建",
        "description": "C端退款订单创建",
        "operationId": "createUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderRefundDTO",
            "description": "orderRefundDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/创建退款单DTO",
              "originalRef": "创建退款单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«OrderRefundCreateResponse»",
              "originalRef": "ResultDTO«OrderRefundCreateResponse»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/return.goods": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "用户退货",
        "description": "用户退货",
        "operationId": "returnGoodsUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "returnGoodsDTO",
            "description": "returnGoodsDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/用户退货参数",
              "originalRef": "用户退货参数"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/shipping_company_list": {
      "get": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "物流公司列表",
        "description": "物流公司列表",
        "operationId": "queryShippingCompanyListUsingGET_1",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«List«物流公司DTO»»",
              "originalRef": "ResultDTO«List«物流公司DTO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/refund/upload": {
      "post": {
        "tags": [
          "[App]售后相关接口"
        ],
        "summary": "上传素材",
        "description": "上传素材",
        "operationId": "uploadUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "file",
            "description": "file",
            "required": false,
            "schema": {
              "type": "string",
              "format": "binary"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«UploadResult»",
              "originalRef": "ResultDTO«UploadResult»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/add": {
      "post": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "添加购物车",
        "description": "添加购物车",
        "operationId": "addShoppingCartUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "request",
            "description": "request",
            "required": true,
            "schema": {
              "$ref": "#/definitions/AddShoppingCartRequest",
              "originalRef": "AddShoppingCartRequest"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«long»",
              "originalRef": "ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/delete": {
      "post": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "删除购物车",
        "description": "删除购物车",
        "operationId": "deleteShoppingCartUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "request",
            "description": "request",
            "required": true,
            "schema": {
              "$ref": "#/definitions/DeleteShoppingCartRequest",
              "originalRef": "DeleteShoppingCartRequest"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«int»",
              "originalRef": "ResultDTO«int»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/query/count": {
      "get": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "查询购物车数量",
        "description": "查询购物车数量",
        "operationId": "queryCountUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "appBuId",
            "in": "query",
            "description": "应用业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buId",
            "in": "query",
            "description": "业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buyingMode",
            "in": "query",
            "description": "购买方式 0现金 1积分 2余额",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "deliveryMode",
            "in": "query",
            "description": "配送方式：0-快递；1-自提；2-同城配送",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "id",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "offset",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "pageNo",
            "in": "query",
            "description": "页码(不能为空)",
            "required": true,
            "type": "integer",
            "format": "int32",
            "x-example": 1
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "每页数量(不能为空)",
            "required": true,
            "type": "integer",
            "maximum": 200,
            "exclusiveMaximum": false,
            "format": "int32",
            "x-example": 10
          },
          {
            "name": "searchCount",
            "in": "query",
            "description": "是否查询总条数",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].asc",
            "in": "query",
            "description": "是否升序, 默认升序",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].column",
            "in": "query",
            "description": "排序字段",
            "required": false,
            "type": "string"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "租户ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "uniqueAccountId",
            "in": "query",
            "description": "小程序用户ID",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«int»",
              "originalRef": "ResultDTO«int»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/query/list": {
      "get": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "查询购物车",
        "description": "查询购物车",
        "operationId": "queryShoppingCartUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "appBuId",
            "in": "query",
            "description": "应用业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buId",
            "in": "query",
            "description": "业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buyingMode",
            "in": "query",
            "description": "购买方式 0现金 1积分 2余额",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "deliveryMode",
            "in": "query",
            "description": "配送方式：0-快递；1-自提；2-同城配送",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "id",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "offset",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "pageNo",
            "in": "query",
            "description": "页码(不能为空)",
            "required": true,
            "type": "integer",
            "format": "int32",
            "x-example": 1
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "每页数量(不能为空)",
            "required": true,
            "type": "integer",
            "maximum": 200,
            "exclusiveMaximum": false,
            "format": "int32",
            "x-example": 10
          },
          {
            "name": "searchCount",
            "in": "query",
            "description": "是否查询总条数",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].asc",
            "in": "query",
            "description": "是否升序, 默认升序",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].column",
            "in": "query",
            "description": "排序字段",
            "required": false,
            "type": "string"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "租户ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "uniqueAccountId",
            "in": "query",
            "description": "小程序用户ID",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«ShoppingCartDTO»»",
              "originalRef": "PageResultDTO«List«ShoppingCartDTO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/query/list/groupByBuId": {
      "get": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "buId分组查询购物车",
        "description": "buId分组查询购物车",
        "operationId": "queryShoppingCartGroupByBuIdUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "appBuId",
            "in": "query",
            "description": "应用业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buId",
            "in": "query",
            "description": "业务单元ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "buyingMode",
            "in": "query",
            "description": "购买方式 0现金 1积分 2余额",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "deliveryMode",
            "in": "query",
            "description": "配送方式：0-快递；1-自提；2-同城配送",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "id",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "offset",
            "in": "query",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "pageNo",
            "in": "query",
            "description": "页码(不能为空)",
            "required": true,
            "type": "integer",
            "format": "int32",
            "x-example": 1
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "每页数量(不能为空)",
            "required": true,
            "type": "integer",
            "maximum": 200,
            "exclusiveMaximum": false,
            "format": "int32",
            "x-example": 10
          },
          {
            "name": "searchCount",
            "in": "query",
            "description": "是否查询总条数",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].asc",
            "in": "query",
            "description": "是否升序, 默认升序",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "sortingFields[0].column",
            "in": "query",
            "description": "排序字段",
            "required": false,
            "type": "string"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "租户ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "uniqueAccountId",
            "in": "query",
            "description": "小程序用户ID",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«CommerceShoppingCartGroupingDTO»»",
              "originalRef": "PageResultDTO«List«CommerceShoppingCartGroupingDTO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/shoppingCart/update": {
      "post": {
        "tags": [
          "[App]购物车"
        ],
        "summary": "更新购物车",
        "description": "更新购物车",
        "operationId": "updateShoppingCartUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "request",
            "description": "request",
            "required": true,
            "schema": {
              "$ref": "#/definitions/UpdateShoppingCartRequest",
              "originalRef": "UpdateShoppingCartRequest"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«long»",
              "originalRef": "ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/staff/write-off-code/query/detail": {
      "get": {
        "tags": [
          "[APP-STAFF]交易-核销码"
        ],
        "summary": "查询订单详情",
        "description": "查询订单详情",
        "operationId": "detailUsingGET_5",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "orderNo",
            "in": "query",
            "description": "订单编号",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«C端订单详情»",
              "originalRef": "ResultDTO«C端订单详情»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/staff/write-off-code/query/order/list": {
      "post": {
        "tags": [
          "[APP-STAFF]交易-核销码"
        ],
        "summary": "查询员工管辖下的订单列表",
        "description": "查询员工管辖下的订单列表",
        "operationId": "listUsingPOST_4",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/StaffOrderQuery",
              "originalRef": "StaffOrderQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«C端订单列表»»",
              "originalRef": "PageResultDTO«List«C端订单列表»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/staff/write-off-code/query/order/write-off-record": {
      "post": {
        "tags": [
          "[APP-STAFF]交易-核销码"
        ],
        "summary": "查询订单的核销记录",
        "description": "查询订单的核销记录",
        "operationId": "recordUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/查询订单核销记录QUERY",
              "originalRef": "查询订单核销记录QUERY"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«List«订单核销记录DTO»»",
              "originalRef": "ResultDTO«List«订单核销记录DTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/staff/write-off-code/statistics": {
      "post": {
        "tags": [
          "[APP-STAFF]交易-核销码"
        ],
        "summary": "核销数据统计",
        "description": "核销数据统计",
        "operationId": "statisticsUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/WriteOffStatisticsQuery",
              "originalRef": "WriteOffStatisticsQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«WriteOffStatisticsDTO»",
              "originalRef": "ResultDTO«WriteOffStatisticsDTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/staff/write-off-code/verify": {
      "post": {
        "tags": [
          "[APP-STAFF]交易-核销码"
        ],
        "summary": "核销订单",
        "description": "核销订单",
        "operationId": "verifyWriteOffCodeUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "verifyWriteOffCodeDTO",
            "description": "verifyWriteOffCodeDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/核销DTO",
              "originalRef": "核销DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/write-off-code/record": {
      "get": {
        "tags": [
          "app-write-off-code-controller"
        ],
        "summary": "查询订单的核销记录",
        "description": "查询订单的核销记录",
        "operationId": "recordsUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "orderNo",
            "in": "query",
            "description": "orderNo",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«List«订单商品核销记录VO»»",
              "originalRef": "ResultDTO«List«订单商品核销记录VO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/write-off-code/record/list/groupByActionCode": {
      "get": {
        "tags": [
          "app-write-off-code-controller"
        ],
        "summary": "查询订单核销记录，根据核销动作唯一标识分组展示（不分页）",
        "description": "查询订单核销记录，根据核销动作唯一标识分组展示（不分页）",
        "operationId": "recordGroupByActionCodeUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "orderNo",
            "in": "query",
            "description": "orderNo",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«List«核销动作唯一标识分组核销记录DTO»»",
              "originalRef": "ResultDTO«List«核销动作唯一标识分组核销记录DTO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/write-off-code/record/page/list": {
      "post": {
        "tags": [
          "app-write-off-code-controller"
        ],
        "summary": "分页查询核销记录",
        "description": "分页查询核销记录",
        "operationId": "writeOffRecordPageListUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "$ref": "#/definitions/OrderWriteOffRecordPageQuery",
              "originalRef": "OrderWriteOffRecordPageQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/PageResultDTO«List«OrderWriteOffPageRecordDTO»»",
              "originalRef": "PageResultDTO«List«OrderWriteOffPageRecordDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/order/cal-price": {
      "post": {
        "tags": [
          "[App]业态通-交易"
        ],
        "summary": "计算价格",
        "description": "计算价格",
        "operationId": "calPriceUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "requestOrderDTO",
            "description": "requestOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/请求下单DTO0",
              "originalRef": "请求下单DTO0"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«订单DTO»",
              "originalRef": "ResultDTO«订单DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/order/order": {
      "post": {
        "tags": [
          "[App]业态通-交易"
        ],
        "summary": "下单",
        "description": "下单",
        "operationId": "orderUsingPOST_2",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "yttRequestOrderDTO",
            "description": "yttRequestOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/请求下单DTO",
              "originalRef": "请求下单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«订单DTO»",
              "originalRef": "ResultDTO«订单DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/order/pay": {
      "post": {
        "tags": [
          "[App]业态通-交易"
        ],
        "summary": "调起支付",
        "description": "调起支付",
        "operationId": "payUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "orderDTO",
            "description": "orderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单DTO",
              "originalRef": "订单DTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«支付凭证DTO»",
              "originalRef": "ResultDTO«支付凭证DTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/order/refund/check": {
      "post": {
        "tags": [
          "[App]业态通-退款"
        ],
        "summary": "检查是否能退款",
        "description": "检查是否能退款",
        "operationId": "canRefundUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "yttRefundOrderCheckDTO",
            "description": "yttRefundOrderCheckDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单退款检查",
              "originalRef": "订单退款检查"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/order/refund/do": {
      "post": {
        "tags": [
          "[App]业态通-退款"
        ],
        "summary": "退款",
        "description": "退款",
        "operationId": "doRefundUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "yttRefundOrderDTO",
            "description": "yttRefundOrderDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/订单退款",
              "originalRef": "订单退款"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«YttRefundResponse»",
              "originalRef": "ResultDTO«YttRefundResponse»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/commerce/app/ytt/sync/order": {
      "post": {
        "tags": [
          "[integration]业态通-订单同步"
        ],
        "summary": "线下订单同步",
        "description": "线下订单同步",
        "operationId": "orderUsingPOST_3",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "yttOrderSyncDTO",
            "description": "yttOrderSyncDTO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/YttOrderSyncDTO",
              "originalRef": "YttOrderSyncDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/ResultDTO«boolean»",
              "originalRef": "ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    }
  },
  "definitions": {
    "ActivityInfoDTO": {
      "type": "object",
      "properties": {
        "timeoutDuration": {
          "type": "integer",
          "format": "int64",
          "description": "超时时长，单位：秒"
        }
      },
      "title": "ActivityInfoDTO"
    },
    "AddShoppingCartRequest": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元ID"
        },
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元ID"
        },
        "buyingMode": {
          "type": "integer",
          "format": "int32",
          "description": "购买方式 0 现金 1积分 2余额"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "配送方式 0-快递；1-自提；2-同城配送"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemNo": {
          "type": "string",
          "description": "商品标识"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型(0无，1分销)"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "商品SKU标识"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "小程序用户ID"
        }
      },
      "title": "AddShoppingCartRequest",
      "description": "添加购物车参数"
    },
    "AmountCompisitionDTO": {
      "type": "object",
      "properties": {
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "金额"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "金额类型"
        }
      },
      "title": "AmountCompisitionDTO"
    },
    "AppOrderPageQuery": {
      "type": "object",
      "properties": {
        "activityTypes": {
          "type": "array",
          "description": "营销类型:0可售商品、1拼团、2秒杀、3砍价、4幸运大转盘、5活动预约、6优惠劵、7积分商城",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buIds": {
          "type": "array",
          "description": "业务单元id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式（0快递，1自提，2同城配送）,当orderStatus为40时，为了要区分待收货和待核销两个状态的订单，需要传该参数。待收货列表传0，待自提列表传1"
        },
        "deliveryModeList": {
          "type": "array",
          "description": "提货方式（0快递，1自提，2同城配送）,当orderStatus为40时，为了要区分待收货和待核销两个状态的订单，需要传该参数。待收货列表传0，待自提列表传1",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "evaluateStatusList": {
          "type": "array",
          "description": "订单评价状态 1 未评价 2 已评价 3 已追评  4 关闭追评",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "excludeActivityType": {
          "type": "array",
          "description": "排除的商品活动类型:0可售商品、1拼团、2秒杀、3砍价、4幸运大转盘、5活动预约、6优惠劵、7积分商城",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isParent": {
          "type": "integer",
          "format": "int32",
          "description": "是否查询父单，0否，1是,默认只查询子单列表，未经过拆单的订单也算子单"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "memberId": {
          "type": "string",
          "description": "会员id"
        },
        "multipleQuery": {
          "type": "string",
          "description": "综合查询字段（会员名，会员手机号，订单编号）"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "orderItemStatusList": {
          "type": "array",
          "description": "订单商品状态集合，10(待支付)、20(待确认)、30(待发货)、40(待收货/待自提/待核销/待使用)、45(部分使用)、50(已完成)、-10(冻结)、-20(禁用)、-30(已过期)",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态，不传查询全部（10-待支付、20-待确认、30-待发货、40-待收货/待自提/待核销、50-已完成、-1-已取消）"
        },
        "orderStatusList": {
          "type": "array",
          "description": "订单状态集合，当c端订单选择待收货时传入这个属性，而不用orderStatus（30-待发货、40-待收货/待自提）",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "parentOrderNo": {
          "type": "string",
          "description": "父单编号"
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "$ref": "#/definitions/SortingField",
            "originalRef": "SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        }
      },
      "title": "AppOrderPageQuery",
      "description": "C端订单列表查询参数"
    },
    "CommerceShoppingCartGroupingDTO": {
      "type": "object",
      "properties": {
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "shoppingCartList": {
          "type": "array",
          "description": "购物车信息集合",
          "items": {
            "$ref": "#/definitions/ShoppingCartDTO",
            "originalRef": "ShoppingCartDTO"
          }
        }
      },
      "title": "CommerceShoppingCartGroupingDTO"
    },
    "C端订单列表": {
      "type": "object",
      "properties": {
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "店铺ID"
        },
        "cashPaid": {
          "type": "integer",
          "format": "int64",
          "description": "实付金额，订单现金合计+订单余额合计（分）"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式（0快递，1自提，2同城配送）"
        },
        "evaluateStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单评价状态 1 待评价 2 待追评 3/4关闭评价"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "订单ID"
        },
        "orderItems": {
          "type": "array",
          "description": "订单商品集合",
          "items": {
            "$ref": "#/definitions/订单商品",
            "originalRef": "订单商品"
          }
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态"
        },
        "orderTime": {
          "type": "string",
          "format": "date-time",
          "description": "下单时间"
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型 0:普通商品订单 1:充值订单 2:微信视频号订单"
        },
        "parentOrderNo": {
          "type": "string",
          "description": "父单编号"
        },
        "shopName": {
          "type": "string",
          "description": "店铺名称"
        },
        "totalBalance": {
          "type": "integer",
          "format": "int64",
          "description": "订单余额合计（分）"
        },
        "totalCash": {
          "type": "integer",
          "format": "int64",
          "description": "订单现金合计（分）"
        },
        "totalIntegral": {
          "type": "integer",
          "format": "int64",
          "description": "订单积分合计"
        }
      },
      "title": "C端订单列表"
    },
    "C端订单详情": {
      "type": "object",
      "properties": {
        "activityName": {
          "type": "string",
          "description": "活动名称"
        },
        "activityNo": {
          "type": "string",
          "description": "活动编号"
        },
        "activityType": {
          "type": "integer",
          "format": "int32",
          "description": "商品渠道类型(0可售商品、1拼团、2秒杀、3砍价、4幸运大转盘)"
        },
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "售后期"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buyEntrance": {
          "type": "integer",
          "format": "int32",
          "description": "购买入口:（0立即购买，1购物车）"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "cancelTime": {
          "type": "string",
          "format": "date-time",
          "description": "取消时间"
        },
        "cashPaid": {
          "type": "integer",
          "format": "int64",
          "description": "实付金额，订单现金合计+订单余额合计（分）"
        },
        "childOrders": {
          "type": "array",
          "description": "子单列表",
          "items": {
            "$ref": "#/definitions/订单DTO",
            "originalRef": "订单DTO"
          }
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式:快递0,自提1,同城配送2"
        },
        "deliveryTime": {
          "type": "string",
          "format": "date-time",
          "description": "发货时间"
        },
        "doneTime": {
          "type": "string",
          "format": "date-time",
          "description": "完成时间"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isParent": {
          "type": "integer",
          "format": "int32",
          "description": "是否父单，默认否，1是0否"
        },
        "orderBuInfoDTO": {
          "description": "店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "orderIp": {
          "type": "string",
          "description": "下单ip"
        },
        "orderItems": {
          "type": "array",
          "description": "订单商品列表",
          "items": {
            "$ref": "#/definitions/订单商品",
            "originalRef": "订单商品"
          }
        },
        "orderLogistics": {
          "type": "array",
          "description": "订单物流组成",
          "items": {
            "$ref": "#/definitions/OrderLogisticsDTO",
            "originalRef": "OrderLogisticsDTO"
          }
        },
        "orderMember": {
          "description": "会员信息",
          "$ref": "#/definitions/订单会员信息",
          "originalRef": "订单会员信息"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderNotes": {
          "type": "string",
          "description": "订单备注"
        },
        "orderPriceCompositions": {
          "type": "array",
          "description": "订单价格组成",
          "items": {
            "$ref": "#/definitions/订单价格组成",
            "originalRef": "订单价格组成"
          }
        },
        "orderProms": {
          "type": "array",
          "description": "订单优惠组成",
          "items": {
            "$ref": "#/definitions/订单优惠",
            "originalRef": "订单优惠"
          }
        },
        "orderReceiveInfo": {
          "description": "收货信息",
          "$ref": "#/definitions/订单收货信息",
          "originalRef": "订单收货信息"
        },
        "orderRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单退款状态,无退款10,部分退款20,全部退款30"
        },
        "orderSource": {
          "type": "integer",
          "format": "int32",
          "description": "订单来源:(0微信小程序，1app，2,h5)"
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态:待支付:10,待确认20,待发货30,待收货/待自提/待核销40,已完成50,已取消-1"
        },
        "orderTime": {
          "type": "string",
          "format": "date-time",
          "description": "下单时间"
        },
        "orderTradeBillDTOS": {
          "type": "array",
          "description": "支付单信息",
          "items": {
            "$ref": "#/definitions/订单的支付单",
            "originalRef": "订单的支付单"
          }
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型"
        },
        "originalPrice": {
          "type": "integer",
          "format": "int64",
          "description": "商品原价"
        },
        "parentOrderNo": {
          "type": "string",
          "description": "父单号"
        },
        "payTime": {
          "type": "string",
          "format": "date-time",
          "description": "支付时间"
        },
        "postage": {
          "type": "integer",
          "format": "int64",
          "description": "快递费"
        },
        "relationFee": {
          "type": "integer",
          "format": "int64",
          "description": "关联金额"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "totalBalance": {
          "type": "integer",
          "format": "int64",
          "description": "余额抵扣"
        },
        "totalCash": {
          "type": "integer",
          "format": "int64",
          "description": "微信支付"
        },
        "totalDiscount": {
          "type": "integer",
          "format": "int64",
          "description": "共优惠价格"
        },
        "totalIntegral": {
          "type": "integer",
          "format": "int64",
          "description": "积分抵扣"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        },
        "writeOffTime": {
          "type": "array",
          "description": "核销时间集合",
          "items": {
            "$ref": "Error-ModelName{namespace=\'java.time\', name=\'LocalDateTime\'}",
            "originalRef": "Error-ModelName{namespace=\'java.time\', name=\'LocalDateTime\'}"
          }
        }
      },
      "title": "C端订单详情"
    },
    "DeleteShoppingCartRequest": {
      "type": "object",
      "properties": {
        "idList": {
          "type": "array",
          "description": "购物车id集合",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        }
      },
      "title": "DeleteShoppingCartRequest",
      "description": "添加购物车参数"
    },
    "DeliveryInfo": {
      "type": "object",
      "properties": {
        "context": {
          "type": "string"
        },
        "courierName": {
          "type": "string"
        },
        "courierPhone": {
          "type": "string"
        },
        "distance": {
          "type": "string"
        },
        "ftime": {
          "type": "string"
        },
        "logisticsType": {
          "type": "string"
        },
        "time": {
          "type": "string"
        }
      },
      "title": "DeliveryInfo"
    },
    "MarketActivityDTO": {
      "type": "object",
      "properties": {
        "activityInfoDTO": {
          "description": "活动信息",
          "$ref": "#/definitions/ActivityInfoDTO",
          "originalRef": "ActivityInfoDTO"
        },
        "activityName": {
          "type": "string",
          "description": "活动名称"
        },
        "activityNo": {
          "type": "string",
          "description": "活动编号"
        },
        "channelType": {
          "type": "integer",
          "format": "int32",
          "description": "活动渠道类型"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "MarketActivityDTO"
    },
    "MemberLastOrderQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用id"
        },
        "buIds": {
          "type": "array",
          "description": "业务单元id",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "excludeOrderStatusList": {
          "type": "array",
          "description": "排除的订单状态:待支付:10,待确认20,待发货30,待收货/待自提/待核销40,已完成50,已取消-1",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "memberId": {
          "type": "string",
          "description": "会员id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        }
      },
      "title": "MemberLastOrderQuery"
    },
    "OrderItemAdditionalEvaluateDTO": {
      "type": "object",
      "properties": {
        "content": {
          "type": "string",
          "description": "评价内容"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "imageUrlList": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "skuBarcode": {
          "type": "string",
          "description": "sku编码"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "OrderItemAdditionalEvaluateDTO"
    },
    "OrderItemEvaluateDTO": {
      "type": "object",
      "properties": {
        "content": {
          "type": "string",
          "description": "评价内容"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "grade": {
          "type": "integer",
          "format": "int32",
          "description": "评分等级",
          "minimum": 1,
          "maximum": 5,
          "exclusiveMinimum": false,
          "exclusiveMaximum": false
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "imageUrlList": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "isAnonymous": {
          "type": "integer",
          "format": "int32",
          "description": "是否匿名 0 署名 1 匿名",
          "minimum": 0,
          "maximum": 1,
          "exclusiveMinimum": false,
          "exclusiveMaximum": false
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "skuBarcode": {
          "type": "string",
          "description": "sku编码"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "OrderItemEvaluateDTO"
    },
    "OrderItemFreightDTO": {
      "type": "object",
      "properties": {
        "freightFee": {
          "type": "integer",
          "format": "int64"
        },
        "freightTemplateId": {
          "type": "integer",
          "format": "int64"
        },
        "freightTemplateInfo": {
          "type": "string"
        },
        "freightType": {
          "type": "integer",
          "format": "int32"
        },
        "weight": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "OrderItemFreightDTO"
    },
    "OrderLogisticsDTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64"
        },
        "buId": {
          "type": "integer",
          "format": "int64"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "logisticsCompany": {
          "type": "string"
        },
        "logisticsCompanyNo": {
          "type": "string"
        },
        "logisticsNo": {
          "type": "string"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64"
        },
        "orderNo": {
          "type": "string"
        },
        "refundOrderNo": {
          "type": "string"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        },
        "type": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "OrderLogisticsDTO"
    },
    "OrderPriceTotalDTO": {
      "type": "object",
      "properties": {
        "cashPaid": {
          "type": "integer",
          "format": "int64",
          "description": "实付金额，订单现金合计+订单余额合计（分）"
        },
        "originalPrice": {
          "type": "integer",
          "format": "int64",
          "description": "商品原价"
        },
        "postage": {
          "type": "integer",
          "format": "int64",
          "description": "快递费"
        },
        "totalBalance": {
          "type": "integer",
          "format": "int64",
          "description": "余额抵扣"
        },
        "totalCash": {
          "type": "integer",
          "format": "int64",
          "description": "微信支付"
        },
        "totalDiscount": {
          "type": "integer",
          "format": "int64",
          "description": "共优惠价格"
        }
      },
      "title": "OrderPriceTotalDTO"
    },
    "OrderRefundAmountResponse": {
      "type": "object",
      "properties": {
        "refundAmount": {
          "type": "integer",
          "format": "int64",
          "description": "退款金额"
        },
        "refundAmountType": {
          "type": "integer",
          "format": "int32",
          "description": "退款金额类型"
        }
      },
      "title": "OrderRefundAmountResponse"
    },
    "OrderRefundCalAmountRequest": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "orderRefundItemList": {
          "type": "array",
          "description": "订单商品",
          "items": {
            "$ref": "#/definitions/OrderRefundItemCreateDTO",
            "originalRef": "OrderRefundItemCreateDTO"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "OrderRefundCalAmountRequest"
    },
    "OrderRefundCalAmountResponse": {
      "type": "object",
      "properties": {
        "refundAmountTotal": {
          "type": "integer",
          "format": "int64",
          "description": "退款金额总和（现金+余额）"
        },
        "refundIntegralTotal": {
          "type": "integer",
          "format": "int64",
          "description": "退款积分总和"
        },
        "refundOrderAmountList": {
          "type": "array",
          "description": "退款金额价格组成",
          "items": {
            "$ref": "#/definitions/OrderRefundAmountResponse",
            "originalRef": "OrderRefundAmountResponse"
          }
        }
      },
      "title": "OrderRefundCalAmountResponse"
    },
    "OrderRefundCreateResponse": {
      "type": "object",
      "properties": {
        "refundOrderNo": {
          "type": "string"
        }
      },
      "title": "OrderRefundCreateResponse"
    },
    "OrderRefundItemCreateDTO": {
      "type": "object",
      "properties": {
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编码"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品Id"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "商品规格id"
        }
      },
      "title": "OrderRefundItemCreateDTO"
    },
    "OrderWriteOffItemDTO": {
      "type": "object",
      "properties": {
        "barcode": {
          "type": "string"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "itemAttribute": {
          "type": "string"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32"
        },
        "itemInfo": {
          "type": "string"
        },
        "itemName": {
          "type": "string"
        },
        "itemNo": {
          "type": "string"
        },
        "orderNo": {
          "type": "string"
        },
        "salePrice": {
          "type": "integer",
          "format": "int64"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "thumbnail": {
          "type": "string"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "OrderWriteOffItemDTO"
    },
    "OrderWriteOffPageRecordDTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "cashPaid": {
          "type": "integer",
          "format": "int64"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "currentWriteOffCode": {
          "type": "string",
          "description": "核销验证码"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展字段"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "memberLogo": {
          "type": "string",
          "description": "会员头像"
        },
        "memberName": {
          "type": "string",
          "description": "会员名称"
        },
        "memberPhone": {
          "type": "string",
          "description": "会员手机号"
        },
        "orderItemList": {
          "type": "array",
          "description": "订单商品列表",
          "items": {
            "$ref": "#/definitions/订单商品",
            "originalRef": "订单商品"
          }
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态"
        },
        "orderTime": {
          "type": "string",
          "format": "date-time",
          "description": "下单时间"
        },
        "payTime": {
          "type": "string",
          "format": "date-time",
          "description": "支付时间"
        },
        "postage": {
          "type": "integer",
          "format": "int64"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "totalBalance": {
          "type": "integer",
          "format": "int64"
        },
        "totalCash": {
          "type": "integer",
          "format": "int64"
        },
        "totalIntegral": {
          "type": "integer",
          "format": "int64"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        },
        "writeOffById": {
          "type": "integer",
          "format": "int64",
          "description": "核销人Id"
        },
        "writeOffByName": {
          "type": "string",
          "description": "核销人名字"
        },
        "writeOffCodeAuthId": {
          "type": "integer",
          "format": "int64",
          "description": "核销授权码id"
        },
        "writeOffCount": {
          "type": "integer",
          "format": "int32",
          "description": "核销次数"
        },
        "writeOffItemList": {
          "type": "array",
          "description": "核销商品列表",
          "items": {
            "$ref": "#/definitions/OrderWriteOffItemDTO",
            "originalRef": "OrderWriteOffItemDTO"
          }
        },
        "writeOffStatus": {
          "type": "integer",
          "format": "int32",
          "description": "核销状态 (0:失败 1:成功)"
        },
        "writeOffTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销日期"
        },
        "writeOffType": {
          "type": "integer",
          "format": "int32",
          "description": "核销类型"
        },
        "wxOpenId": {
          "type": "string",
          "description": "会员openId"
        }
      },
      "title": "OrderWriteOffPageRecordDTO",
      "description": "订单核销记录"
    },
    "OrderWriteOffRecordPageQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buIdList": {
          "type": "array",
          "description": "业务单元id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "currentWriteOffCode": {
          "type": "string",
          "description": "核销验证码"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "keywords": {
          "type": "string",
          "description": "关键字查询，支持订单编号、会员手机号，会员名称，核销员名称"
        },
        "memberName": {
          "type": "string",
          "description": "会员姓名*/"
        },
        "memberPhone": {
          "type": "string",
          "description": "会员手机号*/"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "$ref": "#/definitions/SortingField",
            "originalRef": "SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "uniqueAccountId": {
          "type": "integer",
          "format": "int64",
          "description": "会员一账通id*/"
        },
        "writeOffById": {
          "type": "integer",
          "format": "int64",
          "description": "核销人id"
        },
        "writeOffByName": {
          "type": "string",
          "description": "核销人名称"
        },
        "writeOffCodeAuthId": {
          "type": "integer",
          "format": "int64",
          "description": "核销授权码id"
        },
        "writeOffStatus": {
          "type": "integer",
          "format": "int32",
          "description": "核销状态 (0:失败 1:成功)"
        },
        "writeOffType": {
          "type": "integer",
          "format": "int32",
          "description": ")"
        }
      },
      "title": "OrderWriteOffRecordPageQuery",
      "description": "订单核销记录查询对象"
    },
    "PageResultDTO«List«CommerceShoppingCartGroupingDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/CommerceShoppingCartGroupingDTO",
            "originalRef": "CommerceShoppingCartGroupingDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«CommerceShoppingCartGroupingDTO»»"
    },
    "PageResultDTO«List«C端订单列表»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/C端订单列表",
            "originalRef": "C端订单列表"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«C端订单列表»»"
    },
    "PageResultDTO«List«OrderWriteOffPageRecordDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/OrderWriteOffPageRecordDTO",
            "originalRef": "OrderWriteOffPageRecordDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«OrderWriteOffPageRecordDTO»»"
    },
    "PageResultDTO«List«ShoppingCartDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ShoppingCartDTO",
            "originalRef": "ShoppingCartDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«ShoppingCartDTO»»"
    },
    "PageResultDTO«List«退款订单DTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/退款订单DTO",
            "originalRef": "退款订单DTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«退款订单DTO»»"
    },
    "ResultDTO«C端订单详情»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/C端订单详情",
          "originalRef": "C端订单详情"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«C端订单详情»"
    },
    "ResultDTO«List«核销动作唯一标识分组核销记录DTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/核销动作唯一标识分组核销记录DTO",
            "originalRef": "核销动作唯一标识分组核销记录DTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«核销动作唯一标识分组核销记录DTO»»"
    },
    "ResultDTO«List«物流公司DTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/物流公司DTO",
            "originalRef": "物流公司DTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«物流公司DTO»»"
    },
    "ResultDTO«List«订单商品核销记录VO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/订单商品核销记录VO",
            "originalRef": "订单商品核销记录VO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«订单商品核销记录VO»»"
    },
    "ResultDTO«List«订单核销记录DTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/订单核销记录DTO",
            "originalRef": "订单核销记录DTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«订单核销记录DTO»»"
    },
    "ResultDTO«OrderRefundCalAmountResponse»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/OrderRefundCalAmountResponse",
          "originalRef": "OrderRefundCalAmountResponse"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«OrderRefundCalAmountResponse»"
    },
    "ResultDTO«OrderRefundCreateResponse»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/OrderRefundCreateResponse",
          "originalRef": "OrderRefundCreateResponse"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«OrderRefundCreateResponse»"
    },
    "ResultDTO«UploadResult»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/UploadResult",
          "originalRef": "UploadResult"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«UploadResult»"
    },
    "ResultDTO«WKQueryOrderResponse»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/WKQueryOrderResponse",
          "originalRef": "WKQueryOrderResponse"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«WKQueryOrderResponse»"
    },
    "ResultDTO«WKQueryRouteResponse»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/WKQueryRouteResponse",
          "originalRef": "WKQueryRouteResponse"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«WKQueryRouteResponse»"
    },
    "ResultDTO«WriteOffStatisticsDTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/WriteOffStatisticsDTO",
          "originalRef": "WriteOffStatisticsDTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«WriteOffStatisticsDTO»"
    },
    "ResultDTO«YttRefundResponse»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/YttRefundResponse",
          "originalRef": "YttRefundResponse"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«YttRefundResponse»"
    },
    "ResultDTO«boolean»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "boolean"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«boolean»"
    },
    "ResultDTO«c端订单统计»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/c端订单统计",
          "originalRef": "c端订单统计"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«c端订单统计»"
    },
    "ResultDTO«int»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int32"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«int»"
    },
    "ResultDTO«long»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int64"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«long»"
    },
    "ResultDTO«可用优惠券DTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/可用优惠券DTO",
          "originalRef": "可用优惠券DTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«可用优惠券DTO»"
    },
    "ResultDTO«支付凭证DTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/支付凭证DTO",
          "originalRef": "支付凭证DTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«支付凭证DTO»"
    },
    "ResultDTO«订单DTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/订单DTO",
          "originalRef": "订单DTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«订单DTO»"
    },
    "ResultDTO«退款订单DTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/退款订单DTO",
          "originalRef": "退款订单DTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«退款订单DTO»"
    },
    "ShoppingCartDTO": {
      "type": "object",
      "properties": {
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元ID"
        },
        "buyingMode": {
          "type": "integer",
          "format": "int32",
          "description": "购买方式 0 现金 1积分 2余额"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "配送方式 0-快递；1-自提；2-同城配送"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "购物车ID"
        },
        "integral": {
          "type": "integer",
          "format": "int64",
          "description": "商品抵扣积分"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemImg": {
          "type": "string",
          "description": "商品图片"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "itemNo": {
          "type": "string",
          "description": "商品标识"
        },
        "itemPrice": {
          "type": "integer",
          "format": "int64",
          "description": "商品价格"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型(0无，1分销)"
        },
        "saleChannelId": {
          "type": "string",
          "description": "营销活动商品对应的活动id,门店创建的可售商品默认为0"
        },
        "saleChannelType": {
          "type": "integer",
          "format": "int32",
          "description": "销售渠道类型， 1 门店普通销售"
        },
        "sellOut": {
          "type": "integer",
          "format": "int32",
          "description": "售罄状态 1售罄 0未售罄"
        },
        "shelf": {
          "type": "integer",
          "format": "int32",
          "description": "上下架 1上架 0下架"
        },
        "skuDesc": {
          "type": "string",
          "description": "商品sku描述 譬如：红色|256G"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "商品SKU标识"
        },
        "skuLabelPrice": {
          "type": "integer",
          "format": "int64",
          "description": "商品标签价"
        },
        "stock": {
          "type": "integer",
          "format": "int32",
          "description": "可用库存"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "小程序用户ID"
        }
      },
      "title": "ShoppingCartDTO"
    },
    "SortingField": {
      "type": "object",
      "properties": {
        "asc": {
          "type": "boolean",
          "description": "是否升序, 默认升序"
        },
        "column": {
          "type": "string",
          "description": "排序字段"
        }
      },
      "title": "SortingField"
    },
    "StaffOrderQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buIds": {
          "type": "array",
          "description": "业务单元id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "deliveryModeList": {
          "type": "array",
          "description": "提货方式（0快递，1自提，2同城配送）,当orderStatus为40时，为了要区分待收货和待核销两个状态的订单，需要传该参数。待收货列表传0，待自提列表传1",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "multipleQuery": {
          "type": "string",
          "description": "综合查询字段（会员名，会员手机号，订单编号）"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "orderItemStatusList": {
          "type": "array",
          "description": "订单商品状态集合，10(待支付)、20(待确认)、30(待发货)、40(待收货/待自提/待核销/待使用)、45(部分使用)、50(已完成)、-10(冻结)、-20(禁用)、-30(已过期)",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "orderStatusList": {
          "type": "array",
          "description": "订单状态集合",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "$ref": "#/definitions/SortingField",
            "originalRef": "SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "StaffOrderQuery"
    },
    "UpdateShoppingCartRequest": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元ID"
        },
        "barcode": {
          "type": "string",
          "description": "更新BARCODE(商品条码)"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元ID"
        },
        "buyingMode": {
          "type": "integer",
          "format": "int32",
          "description": "更新购买方式"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "更新配送方式"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "购物车ID"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量，为0会自动删除"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "更新SKU_ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "小程序用户ID"
        }
      },
      "title": "UpdateShoppingCartRequest",
      "description": "添加购物车参数"
    },
    "UploadResult": {
      "type": "object",
      "properties": {
        "bucket": {
          "type": "string"
        },
        "cdnUrl": {
          "type": "string"
        },
        "fileKey": {
          "type": "string"
        },
        "fileUrl": {
          "type": "string"
        },
        "localUrl": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        },
        "thumbnailUrl": {
          "type": "string"
        },
        "uploadMsg": {
          "type": "string"
        }
      },
      "title": "UploadResult"
    },
    "WKQueryOrderResponse": {
      "type": "object",
      "properties": {
        "company": {
          "type": "string"
        },
        "condition": {
          "type": "string"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/DeliveryInfo",
            "originalRef": "DeliveryInfo"
          }
        },
        "ischeck": {
          "type": "integer",
          "format": "int32"
        },
        "message": {
          "type": "string"
        },
        "orderNo": {
          "type": "string"
        },
        "state": {
          "type": "integer",
          "format": "int32"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "waybillId": {
          "type": "string"
        }
      },
      "title": "WKQueryOrderResponse"
    },
    "WKQueryRouteResponse": {
      "type": "object",
      "properties": {
        "trackUrl": {
          "type": "string"
        }
      },
      "title": "WKQueryRouteResponse"
    },
    "WriteOffStatisticsDTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "endTime": {
          "type": "string",
          "format": "date-time",
          "description": "统计结束时间"
        },
        "newOrders": {
          "type": "integer",
          "format": "int32",
          "description": "新增订单数"
        },
        "newRefundOrders": {
          "type": "integer",
          "format": "int32",
          "description": "新增退款订单数"
        },
        "startTime": {
          "type": "string",
          "format": "date-time",
          "description": "统计开始时间"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "writeOffBalanceAmount": {
          "type": "integer",
          "format": "int64",
          "description": "核销余额总额"
        },
        "writeOffCashAmount": {
          "type": "integer",
          "format": "int64",
          "description": "核销现金总额"
        },
        "writeOffIntegralAmount": {
          "type": "integer",
          "format": "int64",
          "description": "核销积分总额"
        },
        "writeOffOrderCount": {
          "type": "integer",
          "format": "int32",
          "description": "核销订单数"
        }
      },
      "title": "WriteOffStatisticsDTO"
    },
    "WriteOffStatisticsQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用单元id"
        },
        "buIds": {
          "type": "array",
          "description": "业务单元id列表",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "endTime": {
          "type": "string",
          "format": "date-time",
          "description": "统计的结束时间，为空则查当日"
        },
        "startTime": {
          "type": "string",
          "format": "date-time",
          "description": "统计的开始时间，为空则查当日"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "writeOffById": {
          "type": "integer",
          "format": "int64",
          "description": "核销人的id"
        }
      },
      "title": "WriteOffStatisticsQuery"
    },
    "YttOrderSyncDTO": {
      "type": "object",
      "properties": {
        "buId": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "buyerMsg": {
          "type": "string",
          "refType": null
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "discountAmount": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "extraInfo": {
          "type": "string",
          "refType": null
        },
        "items": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/YttOrderSyncItemDTO",
            "originalRef": "YttOrderSyncItemDTO"
          },
          "refType": "YttOrderSyncItemDTO"
        },
        "memberId": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "orderNotes": {
          "type": "string",
          "refType": null
        },
        "orderSource": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "orderTime": {
          "type": "string",
          "refType": null
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "orderUpdateTime": {
          "type": "string",
          "refType": null
        },
        "outOrderNo": {
          "type": "string",
          "refType": null
        },
        "outTransactionId": {
          "type": "string",
          "refType": null
        },
        "payAmount": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "payStatus": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "payTime": {
          "type": "string",
          "refType": null
        },
        "phone": {
          "type": "string",
          "refType": null
        },
        "proms": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/订单优惠DTO",
            "originalRef": "订单优惠DTO"
          },
          "refType": "订单优惠DTO"
        },
        "storeId": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "storeName": {
          "type": "string",
          "refType": null
        },
        "totalAmount": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "wxOpenId": {
          "type": "string",
          "refType": null
        },
        "wxUnionId": {
          "type": "string",
          "refType": null
        }
      },
      "title": "YttOrderSyncDTO"
    },
    "YttOrderSyncItemDTO": {
      "type": "object",
      "properties": {
        "barcode": {
          "type": "string",
          "refType": null
        },
        "categoryId": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "categoryName": {
          "type": "string",
          "refType": null
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "refType": null
        },
        "itemName": {
          "type": "string",
          "refType": null
        },
        "itemNo": {
          "type": "string",
          "refType": null
        },
        "originPrice": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "payPrice": {
          "type": "integer",
          "format": "int64",
          "refType": null
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "refType": null
        }
      },
      "title": "YttOrderSyncItemDTO"
    },
    "YttRefundResponse": {
      "type": "object",
      "properties": {
        "refundDetail": {
          "type": "object",
          "additionalProperties": {
            "type": "integer",
            "format": "int64"
          }
        },
        "refundStatus": {
          "type": "boolean"
        }
      },
      "title": "YttRefundResponse"
    },
    "c端订单统计": {
      "type": "object",
      "properties": {
        "refundOrderCount": {
          "type": "integer",
          "format": "int32",
          "description": "退款订单数量"
        },
        "toBeWrittenOffCount": {
          "type": "integer",
          "format": "int32",
          "description": "待核销数量"
        },
        "totalOrders": {
          "type": "integer",
          "format": "int32",
          "description": "总订单数"
        },
        "waitDeliverCount": {
          "type": "integer",
          "format": "int32",
          "description": "待发货数量"
        },
        "waitPayCount": {
          "type": "integer",
          "format": "int32",
          "description": "待付款数量"
        },
        "waitReceiveCount": {
          "type": "integer",
          "format": "int32",
          "description": "待收货数量"
        }
      },
      "title": "c端订单统计"
    },
    "c端订单统计查询参数": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "excludeActivityType": {
          "type": "array",
          "description": "排除在外的活动类型列表",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "excludeRefundStatusList": {
          "type": "array",
          "description": "排除在外的退款单状态列表",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "memberId": {
          "type": "string",
          "description": "用户id"
        },
        "orderTimeEnd": {
          "type": "string",
          "format": "date-time",
          "description": "下单结束时间"
        },
        "orderTimeStart": {
          "type": "string",
          "format": "date-time",
          "description": "下单开始时间"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "c端订单统计查询参数"
    },
    "买家收货参数": {
      "type": "object",
      "properties": {
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "售后截至期"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "logisticIds": {
          "type": "array",
          "description": "物流id集合",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderType": {
          "type": "string",
          "description": "订单类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "买家收货参数"
    },
    "创建退款单DTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "explain": {
          "type": "string",
          "description": "退款说明"
        },
        "explainImgList": {
          "type": "array",
          "description": "退款图片说明",
          "items": {
            "type": "string"
          }
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "单据类型: 0普通订单 1充值订单 2微信视频号订单"
        },
        "reason": {
          "type": "string",
          "description": "退款原因"
        },
        "reasonKey": {
          "type": "string",
          "description": "退款原因key"
        },
        "refundAmountList": {
          "type": "array",
          "description": "订单价格组成",
          "items": {
            "$ref": "#/definitions/退款订单金额项",
            "originalRef": "退款订单金额项"
          }
        },
        "refundOrderItems": {
          "type": "array",
          "description": "售后商品列表",
          "items": {
            "$ref": "#/definitions/OrderRefundItemCreateDTO",
            "originalRef": "OrderRefundItemCreateDTO"
          }
        },
        "refundOrderNo": {
          "type": "string",
          "description": "退款订单编号(售后单编号)"
        },
        "refundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "退款订单状态:待店铺审核1,待平台审核/待财务审核2,待退货3,待确认4,待退款5,退款成功6,退款取消7,退款拒绝8"
        },
        "refundTheAudit": {
          "type": "boolean",
          "description": "商品未发货/未核销前退款申请是否需要审核"
        },
        "refundType": {
          "type": "integer",
          "format": "int32",
          "description": "退款类型(售后类型):仅退款0,退货退款1"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "创建退款单DTO"
    },
    "可用优惠券DTO": {
      "type": "object",
      "properties": {
        "coupon": {
          "type": "string",
          "description": "优惠券信息，json格式，直接从优惠券服务查询出"
        }
      },
      "title": "可用优惠券DTO"
    },
    "售后商品": {
      "type": "object",
      "properties": {
        "actualReceiveCount": {
          "type": "integer",
          "format": "int32",
          "description": "实际收货数量"
        },
        "canSale": {
          "type": "integer",
          "format": "int32",
          "description": "是否可售"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "inboundCount": {
          "type": "integer",
          "format": "int32",
          "description": "入库数量"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编码"
        },
        "logisticsDTO": {
          "description": "物流信息，需要退货时才有",
          "$ref": "#/definitions/物流信息",
          "originalRef": "物流信息"
        },
        "orderItemCountProm": {
          "type": "integer",
          "format": "int64",
          "description": "退款商品优惠合计"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品Id"
        },
        "orderItemInfoDTO": {
          "description": "商品信息",
          "$ref": "#/definitions/订单商品详情",
          "originalRef": "订单商品详情"
        },
        "orderItemPriceCompositionDTOList": {
          "type": "array",
          "description": "退款商品价格组成",
          "items": {
            "$ref": "#/definitions/订单商品价格组成",
            "originalRef": "订单商品价格组成"
          }
        },
        "orderItemPromDTOList": {
          "type": "array",
          "description": "退款商品优惠组成",
          "items": {
            "$ref": "#/definitions/订单商品优惠",
            "originalRef": "订单商品优惠"
          }
        },
        "orderItemRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品退款状态:无退款10,退款中20,退款成功30,退款失败40,退款取消50,已过期-10"
        },
        "orderItemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品状态:待支付10,待确认20,待发货30,待收获/待自提/待核销/待使用40,部分使用45,已经完成50,冻结-10,禁用-20,已过期-30"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "退款订单编码"
        },
        "relationFee": {
          "type": "integer",
          "format": "int64",
          "description": "关联金额"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "商品规格id"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "售后商品"
    },
    "商品预约信息": {
      "type": "object",
      "properties": {
        "appointmentEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约结束时间"
        },
        "appointmentStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约开始时间"
        }
      },
      "title": "商品预约信息"
    },
    "店铺信息": {
      "type": "object",
      "properties": {
        "address": {
          "type": "string",
          "description": "详细地址"
        },
        "areaId": {
          "type": "integer",
          "format": "int64",
          "description": "区域id"
        },
        "areaName": {
          "type": "string",
          "description": "区域名称"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buName": {
          "type": "string",
          "description": "店铺名称"
        },
        "buPhone": {
          "type": "string",
          "description": "店铺电话"
        },
        "cityId": {
          "type": "integer",
          "format": "int64",
          "description": "城市id"
        },
        "cityName": {
          "type": "string",
          "description": "城市名称"
        },
        "provinceId": {
          "type": "integer",
          "format": "int64",
          "description": "省份id"
        },
        "provinceName": {
          "type": "string",
          "description": "省名"
        }
      },
      "title": "店铺信息"
    },
    "店铺信息0": {
      "type": "object",
      "properties": {
        "address": {
          "type": "string",
          "description": "详细地址"
        },
        "areaId": {
          "type": "integer",
          "format": "int64",
          "description": "区域id"
        },
        "areaName": {
          "type": "string",
          "description": "区域名称"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buName": {
          "type": "string",
          "description": "店铺名称"
        },
        "buPhone": {
          "type": "string",
          "description": "店铺电话"
        },
        "cityId": {
          "type": "integer",
          "format": "int64",
          "description": "城市id"
        },
        "cityName": {
          "type": "string",
          "description": "城市名称"
        },
        "provinceId": {
          "type": "integer",
          "format": "int64",
          "description": "省份id"
        },
        "provinceName": {
          "type": "string",
          "description": "省名"
        }
      },
      "title": "店铺信息0"
    },
    "撤销退款申请DTO": {
      "type": "object",
      "properties": {
        "refundOrderNo": {
          "type": "string",
          "description": "退款订单编号"
        }
      },
      "title": "撤销退款申请DTO"
    },
    "支付凭证DTO": {
      "type": "object",
      "properties": {
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "mwebUrl": {
          "type": "string",
          "description": "H5支付跳转链接"
        },
        "nonceStr": {
          "type": "string",
          "description": "随机数"
        },
        "packagePrepayId": {
          "type": "string",
          "description": "订单详情扩展字符串,package在java中为关键字"
        },
        "paySign": {
          "type": "string",
          "description": "支付签名"
        },
        "prepayId": {
          "type": "string",
          "description": "预支付id"
        },
        "signType": {
          "type": "string",
          "description": "签名类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "timeStamp": {
          "type": "string",
          "description": "时间戳"
        },
        "tuneUpPay": {
          "type": "integer",
          "format": "int32",
          "description": "是否需要调起支付，1需要，0不需要（自动支付成功，用于0元支付）"
        }
      },
      "title": "支付凭证DTO"
    },
    "支付单金额组成DTO": {
      "type": "object",
      "properties": {
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "价格"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "价格类型: 0现金，1积分，2余额"
        }
      },
      "title": "支付单金额组成DTO"
    },
    "查询订单对象": {
      "type": "object",
      "required": [
        "deliveryType",
        "orderId"
      ],
      "properties": {
        "appId": {
          "type": "object",
          "description": "应用Id"
        },
        "company": {
          "type": "object",
          "description": "查询的快递公司的编码"
        },
        "deliveryId": {
          "type": "object",
          "description": "快递Id"
        },
        "deliveryType": {
          "type": "object",
          "description": "注册快递类型"
        },
        "departureCity": {
          "type": "object",
          "description": "出发地城市"
        },
        "destinationCity": {
          "type": "object",
          "description": "目的地城市"
        },
        "epId": {
          "type": "object",
          "description": "企业Id"
        },
        "orderId": {
          "type": "object",
          "description": "订单Id"
        },
        "orderType": {
          "type": "object",
          "description": "订单类型"
        },
        "phone": {
          "type": "object",
          "description": "收件人或寄件人的手机号"
        },
        "resultv2": {
          "type": "integer",
          "format": "int32"
        },
        "userId": {
          "type": "object",
          "description": "用户Id"
        },
        "waybillId": {
          "type": "object",
          "description": "运单Id"
        }
      },
      "title": "查询订单对象",
      "description": "查询订单对象"
    },
    "查询订单核销记录QUERY": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "$ref": "#/definitions/SortingField",
            "originalRef": "SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "查询订单核销记录QUERY"
    },
    "核销DTO": {
      "type": "object",
      "properties": {
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "售后有效期"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单号"
        },
        "verifier": {
          "type": "integer",
          "format": "int64",
          "description": "核销人id"
        },
        "verifierName": {
          "type": "string",
          "description": "核销人姓名"
        },
        "verifyOrderItems": {
          "type": "array",
          "description": "核销商品项",
          "items": {
            "$ref": "#/definitions/核销商品DTO",
            "originalRef": "核销商品DTO"
          }
        },
        "verifyType": {
          "type": "integer",
          "format": "int32",
          "description": "核销类型 0:后台核销 1:C端扫码核销"
        }
      },
      "title": "核销DTO"
    },
    "核销动作唯一标识分组核销记录DTO": {
      "type": "object",
      "properties": {
        "actionCode": {
          "type": "string",
          "description": "核销动作唯一标识"
        },
        "recordList": {
          "type": "array",
          "description": "核销记录集合",
          "items": {
            "$ref": "#/definitions/订单核销记录DTO",
            "originalRef": "订单核销记录DTO"
          }
        }
      },
      "title": "核销动作唯一标识分组核销记录DTO"
    },
    "核销商品": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "name": {
          "type": "string",
          "description": "商品名称"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        },
        "writeOffCount": {
          "type": "integer",
          "format": "int32",
          "description": "核销次数"
        }
      },
      "title": "核销商品"
    },
    "核销商品DTO": {
      "type": "object",
      "properties": {
        "count": {
          "type": "integer",
          "format": "int32",
          "description": "核销次数"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品项id"
        }
      },
      "title": "核销商品DTO"
    },
    "核销码DTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "authStatus": {
          "type": "integer",
          "format": "int32",
          "description": "授权状态(0:未授权 1:已授权)"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "唯一标识"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "originCount": {
          "type": "integer",
          "format": "int32",
          "description": "核销码原始核销总数"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "状态（是否有效，0无效，1有效）"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "writeOffCodeAuthDTO": {
          "description": "核销码授权DTO",
          "$ref": "#/definitions/核销码授权DTO",
          "originalRef": "核销码授权DTO"
        },
        "writeOffEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销有效期 - 结束时间"
        },
        "writeOffStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销有效期 - 开始时间"
        }
      },
      "title": "核销码DTO"
    },
    "核销码授权DTO": {
      "type": "object",
      "properties": {
        "ableCount": {
          "type": "integer",
          "format": "int32",
          "description": "当前剩余可用次数"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "authType": {
          "type": "integer",
          "format": "int32",
          "description": "授权类型"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "code": {
          "type": "string",
          "description": "验证码"
        },
        "frozenCount": {
          "type": "integer",
          "format": "int32",
          "description": "冻结次数"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "唯一标识"
        },
        "originCount": {
          "type": "integer",
          "format": "int32",
          "description": "原始可用次数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        },
        "writeOffCodeId": {
          "type": "integer",
          "format": "int64",
          "description": "核销码id"
        },
        "writtenOffCount": {
          "type": "integer",
          "format": "int32",
          "description": "已核销次数"
        }
      },
      "title": "核销码授权DTO"
    },
    "物流信息": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "物流id"
        },
        "logisticsCompany": {
          "type": "string",
          "description": "快递公司 (物流公司)"
        },
        "logisticsCompanyNo": {
          "type": "string",
          "description": "物流公司编码"
        },
        "logisticsNo": {
          "type": "string",
          "description": "快递单号 (物流编号)"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "退款单号"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "物流状态:已发货2, 已收货3"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "物流信息"
    },
    "物流公司DTO": {
      "type": "object",
      "properties": {
        "companyName": {
          "type": "string",
          "description": "公司名"
        },
        "companyNo": {
          "type": "string",
          "description": "物流公司编号"
        }
      },
      "title": "物流公司DTO"
    },
    "用户退货参数": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "logisticsCompany": {
          "type": "string",
          "description": "物流公司"
        },
        "logisticsCompanyNo": {
          "type": "string",
          "description": "物流公司编码"
        },
        "logisticsNo": {
          "type": "string",
          "description": "物流编号"
        },
        "orderItemIds": {
          "type": "array",
          "description": "商品id集合",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "单据类型: 0普通订单 1充值订单 2微信视频号订单"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "售后单编号"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "用户退货参数"
    },
    "订单DTO": {
      "type": "object",
      "properties": {
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "售后截止时间"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "引用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buyEntrance": {
          "type": "integer",
          "format": "int32"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "cancelTime": {
          "type": "string",
          "format": "date-time",
          "description": "取消时间"
        },
        "childOrders": {
          "type": "array",
          "description": "子单列表",
          "items": {
            "$ref": "#/definitions/订单DTO",
            "originalRef": "订单DTO"
          }
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式"
        },
        "doneTime": {
          "type": "string",
          "format": "date-time",
          "description": "完成时间"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isCalPrice": {
          "type": "boolean"
        },
        "isParent": {
          "type": "integer",
          "format": "int32",
          "description": "是否父单，默认否，1是0否"
        },
        "marketActivityDTO": {
          "description": "营销活动信息",
          "$ref": "#/definitions/MarketActivityDTO",
          "originalRef": "MarketActivityDTO"
        },
        "orderBuInfoDTO": {
          "description": "店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "orderIp": {
          "type": "string",
          "description": "下单ip"
        },
        "orderItems": {
          "type": "array",
          "description": "订单商品列表",
          "items": {
            "$ref": "#/definitions/订单商品DTO",
            "originalRef": "订单商品DTO"
          }
        },
        "orderMemberDTO": {
          "description": "会员信息",
          "$ref": "#/definitions/订单会员DTO",
          "originalRef": "订单会员DTO"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderNotes": {
          "type": "string",
          "description": "订单备注"
        },
        "orderPriceCompositions": {
          "type": "array",
          "description": "订单价格组成",
          "items": {
            "$ref": "#/definitions/订单价格组成DTO",
            "originalRef": "订单价格组成DTO"
          }
        },
        "orderPriceTotalDTO": {
          "description": "订单价格合计（给前端显示）",
          "$ref": "#/definitions/OrderPriceTotalDTO",
          "originalRef": "OrderPriceTotalDTO"
        },
        "orderProms": {
          "type": "array",
          "description": "订单优惠组成",
          "items": {
            "$ref": "#/definitions/订单优惠DTO",
            "originalRef": "订单优惠DTO"
          }
        },
        "orderReceiveInfo": {
          "description": "收货信息",
          "$ref": "#/definitions/订单收货信息DTO",
          "originalRef": "订单收货信息DTO"
        },
        "orderRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单退款状态"
        },
        "orderSource": {
          "type": "integer",
          "format": "int32"
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态"
        },
        "orderTime": {
          "type": "string",
          "format": "date-time",
          "description": "下单时间"
        },
        "orderTradeBillDTOS": {
          "type": "array",
          "description": "订单支付信息",
          "items": {
            "$ref": "#/definitions/订单的支付单信息DTO",
            "originalRef": "订单的支付单信息DTO"
          }
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型"
        },
        "parentOrderNo": {
          "type": "string",
          "description": "父单号"
        },
        "path": {
          "type": "string",
          "description": "订单跳转路径"
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "description": "支付渠道"
        },
        "payTime": {
          "type": "string",
          "format": "date-time",
          "description": "支付时间"
        },
        "payType": {
          "type": "integer",
          "format": "int32",
          "description": "支付方式"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "订单DTO"
    },
    "订单DTO0": {
      "type": "object",
      "properties": {
        "activityName": {
          "type": "string",
          "description": "活动名称"
        },
        "activityNo": {
          "type": "string",
          "description": "活动编号"
        },
        "activityType": {
          "type": "integer",
          "format": "int32",
          "description": "商品渠道类型(0可售商品、1拼团、2秒杀、3砍价、4幸运大转盘)"
        },
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "售后期"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buyEntrance": {
          "type": "integer",
          "format": "int32",
          "description": "购买入口:（0立即购买，1购物车）"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "cancelTime": {
          "type": "string",
          "format": "date-time",
          "description": "取消时间"
        },
        "cashPaid": {
          "type": "integer",
          "format": "int64",
          "description": "实付金额，订单现金合计+订单余额合计（分）"
        },
        "childOrders": {
          "type": "array",
          "description": "子单列表",
          "items": {
            "$ref": "#/definitions/订单DTO0",
            "originalRef": "订单DTO0"
          }
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式:快递0,自提1,同城配送2"
        },
        "doneTime": {
          "type": "string",
          "format": "date-time",
          "description": "完成时间"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isParent": {
          "type": "integer",
          "format": "int32",
          "description": "是否父单，默认否，1是0否"
        },
        "orderBuInfoDTO": {
          "description": "店铺信息",
          "$ref": "#/definitions/店铺信息0",
          "originalRef": "店铺信息0"
        },
        "orderIp": {
          "type": "string",
          "description": "下单ip"
        },
        "orderItems": {
          "type": "array",
          "description": "订单商品列表",
          "items": {
            "$ref": "#/definitions/订单商品",
            "originalRef": "订单商品"
          }
        },
        "orderLogistics": {
          "type": "array",
          "description": "订单物流组成",
          "items": {
            "$ref": "#/definitions/OrderLogisticsDTO",
            "originalRef": "OrderLogisticsDTO"
          }
        },
        "orderMember": {
          "description": "会员信息",
          "$ref": "#/definitions/订单会员信息",
          "originalRef": "订单会员信息"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderNotes": {
          "type": "string",
          "description": "订单备注"
        },
        "orderPriceCompositions": {
          "type": "array",
          "description": "订单价格组成",
          "items": {
            "$ref": "#/definitions/订单价格组成",
            "originalRef": "订单价格组成"
          }
        },
        "orderProms": {
          "type": "array",
          "description": "订单优惠组成",
          "items": {
            "$ref": "#/definitions/订单优惠",
            "originalRef": "订单优惠"
          }
        },
        "orderReceiveInfo": {
          "description": "收货信息",
          "$ref": "#/definitions/订单收货信息",
          "originalRef": "订单收货信息"
        },
        "orderRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单退款状态,无退款10,部分退款20,全部退款30"
        },
        "orderSource": {
          "type": "integer",
          "format": "int32",
          "description": "订单来源:(0微信小程序，1app，2,h5)"
        },
        "orderStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单状态:待支付:10,待确认20,待发货30,待收货/待自提/待核销40,已完成50,已取消-1"
        },
        "orderTime": {
          "type": "string",
          "format": "date-time",
          "description": "下单时间"
        },
        "orderTradeBillDTOS": {
          "type": "array",
          "description": "支付单信息",
          "items": {
            "$ref": "#/definitions/订单的支付单",
            "originalRef": "订单的支付单"
          }
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型"
        },
        "parentOrderNo": {
          "type": "string",
          "description": "父单号"
        },
        "payTime": {
          "type": "string",
          "format": "date-time",
          "description": "支付时间"
        },
        "postage": {
          "type": "integer",
          "format": "int64",
          "description": "快递费"
        },
        "relationFee": {
          "type": "integer",
          "format": "int64",
          "description": "关联金额"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "totalBalance": {
          "type": "integer",
          "format": "int64",
          "description": "订单余额合计（分）"
        },
        "totalCash": {
          "type": "integer",
          "format": "int64",
          "description": "订单现金合计（分）"
        },
        "totalIntegral": {
          "type": "integer",
          "format": "int64",
          "description": "订单积分合计"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "订单DTO0"
    },
    "订单价格组成": {
      "type": "object",
      "properties": {
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "价格"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "价格类型:现金0,积分1,余额2"
        },
        "sourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型:商品0,运费1,运费险2"
        }
      },
      "title": "订单价格组成"
    },
    "订单价格组成DTO": {
      "type": "object",
      "properties": {
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "价格"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "价格类型，0现金，1积分，2余额"
        },
        "sourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型：0商品，1运费，2运费险"
        }
      },
      "title": "订单价格组成DTO"
    },
    "订单优惠": {
      "type": "object",
      "properties": {
        "promDesc": {
          "type": "string",
          "description": "优惠描述"
        },
        "promNo": {
          "type": "string",
          "description": "优惠编号"
        },
        "promPrice": {
          "type": "integer",
          "format": "int64",
          "description": "优惠价格"
        },
        "promType": {
          "type": "integer",
          "format": "int32",
          "description": "优惠类型:10 店铺满减券优惠 11 店铺折扣券优惠 12 店铺礼品券优惠 13 店铺运费券优惠 20 平台满减券优惠 21 平台折扣券优惠\\n22 平台礼品券优惠 30 秒杀优惠 31 拼团优惠 32 团长优惠 33 砍价优惠 40 会员折扣优惠 41 会员余额抵扣"
        }
      },
      "title": "订单优惠"
    },
    "订单优惠DTO": {
      "type": "object",
      "properties": {
        "discount": {
          "type": "integer",
          "format": "int32",
          "description": "折扣（去除百分号,例9折就是90）",
          "refType": null
        },
        "promDesc": {
          "type": "string",
          "description": "优惠描述",
          "refType": null
        },
        "promNo": {
          "type": "string",
          "description": "优惠编号",
          "refType": null
        },
        "promPrice": {
          "type": "integer",
          "format": "int64",
          "description": "优惠金额",
          "refType": null
        },
        "promType": {
          "type": "integer",
          "format": "int32",
          "description": "优惠类型",
          "refType": null
        },
        "thresholdFee": {
          "type": "integer",
          "format": "int64",
          "description": "门槛金额（满减券）",
          "refType": null
        }
      },
      "title": "订单优惠DTO"
    },
    "订单会员DTO": {
      "type": "object",
      "properties": {
        "level": {
          "type": "integer",
          "format": "int32",
          "description": "会员等级"
        },
        "memberBalance": {
          "type": "integer",
          "format": "int64",
          "description": "会员余额"
        },
        "memberId": {
          "type": "integer",
          "format": "int64",
          "description": "会员id"
        },
        "memberLogo": {
          "type": "string",
          "description": "会员头像"
        },
        "memberName": {
          "type": "string",
          "description": "会员姓名"
        },
        "memberPhone": {
          "type": "string",
          "description": "会员手机号"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        },
        "wxOpenId": {
          "type": "string",
          "description": "wxOpenId"
        }
      },
      "title": "订单会员DTO"
    },
    "订单会员信息": {
      "type": "object",
      "properties": {
        "memberId": {
          "type": "integer",
          "format": "int64",
          "description": "会员id"
        },
        "memberLogo": {
          "type": "string",
          "description": "会员头像"
        },
        "memberName": {
          "type": "string",
          "description": "会员姓名"
        },
        "memberPhone": {
          "type": "string",
          "description": "会员手机号"
        },
        "uniqueAccountId": {
          "type": "integer",
          "format": "int64",
          "description": "会员一账通id"
        },
        "wxOpenId": {
          "type": "string",
          "description": "微信openId"
        }
      },
      "title": "订单会员信息"
    },
    "订单商品": {
      "type": "object",
      "properties": {
        "actualReceiveCount": {
          "type": "integer",
          "format": "int32",
          "description": "实际收货数量"
        },
        "afterSalesEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "订单商品售后期"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "appointmentTimeDTO": {
          "description": "预约时间",
          "$ref": "#/definitions/订单商品预约时间DTO",
          "originalRef": "订单商品预约时间DTO"
        },
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "canSale": {
          "type": "integer",
          "format": "int32",
          "description": "是否还能售卖"
        },
        "deliveryTime": {
          "type": "string",
          "format": "date-time",
          "description": "发货时间"
        },
        "doneTime": {
          "type": "string",
          "format": "date-time",
          "description": "完成时间"
        },
        "evaluateStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单评价状态 1 待评价 2 待追评 3/4关闭评价"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品ID"
        },
        "inboundCount": {
          "type": "integer",
          "format": "int32",
          "description": "入库数量"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "itemVersion": {
          "type": "string",
          "description": "下单时的商品版本"
        },
        "logisticsDTO": {
          "description": "物流信息",
          "$ref": "#/definitions/物流信息",
          "originalRef": "物流信息"
        },
        "logisticsId": {
          "type": "integer",
          "format": "int64",
          "description": "物流id"
        },
        "logisticsNo": {
          "type": "string",
          "description": "物流单号"
        },
        "oldOrderItemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品状态:待支付10,待确认20,待发货30,待收获/待自提/待核销/待使用40,部分使用45,已经完成50,冻结-10,禁用-20,已过期-30"
        },
        "orderItemCountProm": {
          "type": "integer",
          "format": "int64",
          "description": "商品优惠合计"
        },
        "orderItemInfoDTO": {
          "description": "下单商品的快照信息",
          "$ref": "#/definitions/订单商品详情",
          "originalRef": "订单商品详情"
        },
        "orderItemPriceCompositions": {
          "type": "array",
          "description": "价格组成(商品实收)",
          "items": {
            "$ref": "#/definitions/订单商品价格组成",
            "originalRef": "订单商品价格组成"
          }
        },
        "orderItemProms": {
          "type": "array",
          "description": "优惠组成",
          "items": {
            "$ref": "#/definitions/订单商品优惠",
            "originalRef": "订单商品优惠"
          }
        },
        "orderItemRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品退款状态:无退款10,退款中20,退款成功30,退款失败40,退款取消50,已过期-10"
        },
        "orderItemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品状态:待支付10,待确认20,待发货30,待收获/待自提/待核销/待使用40,部分使用45,已经完成50,冻结-10,禁用-20,已过期-30"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "receiveTime": {
          "type": "string",
          "format": "date-time",
          "description": "收货时间"
        },
        "relationFee": {
          "type": "integer",
          "format": "int64",
          "description": "关联金额"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型"
        },
        "sku": {
          "type": "string",
          "description": "商品规格"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "skuId"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "writeOffCodeDTO": {
          "description": "核销码DTO",
          "$ref": "#/definitions/核销码DTO",
          "originalRef": "核销码DTO"
        }
      },
      "title": "订单商品"
    },
    "订单商品DTO": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64"
        },
        "appointmentTimeDTO": {
          "description": "预约时间",
          "$ref": "#/definitions/商品预约信息",
          "originalRef": "商品预约信息"
        },
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "categoryId": {
          "type": "integer",
          "format": "int64",
          "description": "分类id"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式：0快递，1自提，2同城配送"
        },
        "deliveryTime": {
          "type": "string",
          "format": "date-time",
          "description": "发货时间"
        },
        "doneTime": {
          "type": "string",
          "format": "date-time",
          "description": "完成时间"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "itemBuInfoDTO": {
          "description": "店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "itemVersion": {
          "type": "string",
          "description": "下单时的商品版本"
        },
        "logisticsId": {
          "type": "integer",
          "format": "int64",
          "description": "物流id"
        },
        "orderItemInfoDTO": {
          "description": "下单时的商品快照信息",
          "$ref": "#/definitions/订单商品快照DTO",
          "originalRef": "订单商品快照DTO"
        },
        "orderItemPriceCompositions": {
          "type": "array",
          "description": "订单商品价格组成",
          "items": {
            "$ref": "#/definitions/订单商品价格组成DTO",
            "originalRef": "订单商品价格组成DTO"
          }
        },
        "orderItemProms": {
          "type": "array",
          "description": "订单商品优惠组成",
          "items": {
            "$ref": "#/definitions/订单商品优惠DTO",
            "originalRef": "订单商品优惠DTO"
          }
        },
        "orderItemRefundStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品退款状态"
        },
        "orderItemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品状态"
        },
        "orderReceiveInfo": {
          "description": "收货信息",
          "$ref": "#/definitions/订单收货信息DTO",
          "originalRef": "订单收货信息DTO"
        },
        "path": {
          "type": "string",
          "description": "商品跳转路径"
        },
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "参与计算的商品价格"
        },
        "receiveTime": {
          "type": "string",
          "format": "date-time",
          "description": "收货时间/自提时间"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型(0无，1分销)"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "skuId"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "订单商品DTO"
    },
    "订单商品价格组成": {
      "type": "object",
      "properties": {
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "价格"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "价格类型:现金0,积分1,余额2"
        },
        "sourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型:商品0,运费1,运费险2"
        }
      },
      "title": "订单商品价格组成"
    },
    "订单商品价格组成DTO": {
      "type": "object",
      "properties": {
        "price": {
          "type": "integer",
          "format": "int64",
          "description": "价格"
        },
        "priceType": {
          "type": "integer",
          "format": "int32",
          "description": "价格类型,0现金，1积分，2余额"
        },
        "sourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型：0商品，1运费，2运费险"
        }
      },
      "title": "订单商品价格组成DTO"
    },
    "订单商品优惠": {
      "type": "object",
      "properties": {
        "promDesc": {
          "type": "string",
          "description": "优惠描述"
        },
        "promNo": {
          "type": "string",
          "description": "优惠编号"
        },
        "promPrice": {
          "type": "integer",
          "format": "int64",
          "description": "优惠价格"
        },
        "promType": {
          "type": "integer",
          "format": "int32",
          "description": "优惠类型"
        }
      },
      "title": "订单商品优惠"
    },
    "订单商品优惠DTO": {
      "type": "object",
      "properties": {
        "promDesc": {
          "type": "string",
          "description": "优惠描述"
        },
        "promNo": {
          "type": "string",
          "description": "优惠编号"
        },
        "promPrice": {
          "type": "integer",
          "format": "int64",
          "description": "优惠价格"
        },
        "promType": {
          "type": "integer",
          "format": "int32",
          "description": "优惠类型"
        }
      },
      "title": "订单商品优惠DTO"
    },
    "订单商品快照DTO": {
      "type": "object",
      "properties": {
        "activityNo": {
          "type": "string"
        },
        "allCategoryName": {
          "type": "string",
          "description": "所有父级分类名，所有父级和子级，“/”分隔，eg:饮品/果汁饮料/橙汁"
        },
        "appointmentType": {
          "type": "integer",
          "format": "int32",
          "description": "预约方式，1需要预约，0无需预约"
        },
        "image": {
          "type": "string",
          "description": "图片"
        },
        "integral": {
          "type": "integer",
          "format": "int64",
          "description": "积分"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "itemPrice": {
          "type": "integer",
          "format": "int64",
          "description": "售价"
        },
        "itemSkuType": {
          "type": "integer",
          "format": "int32",
          "description": "商品sku类型，跟随商品服务保持一致"
        },
        "itemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "商品下单时的状态"
        },
        "itemType": {
          "type": "integer",
          "format": "int32",
          "description": "商品类型，沿用商品提供"
        },
        "notAllowRefund": {
          "type": "integer",
          "format": "int32",
          "description": "是否不支持退款，0支持，1不支持"
        },
        "orderItemFreightDTO": {
          "$ref": "#/definitions/OrderItemFreightDTO",
          "originalRef": "OrderItemFreightDTO"
        },
        "orderItemWriteOffTermDTO": {
          "$ref": "#/definitions/订单商品核销时间段DTO",
          "originalRef": "订单商品核销时间段DTO"
        },
        "scoreRuleId": {
          "type": "integer",
          "format": "int64",
          "description": "积分规则id"
        },
        "specifications": {
          "type": "string",
          "description": "规格描述"
        }
      },
      "title": "订单商品快照DTO"
    },
    "订单商品核销时间段DTO": {
      "type": "object",
      "properties": {
        "writeOffEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销结束时间"
        },
        "writeOffStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销开始时间"
        }
      },
      "title": "订单商品核销时间段DTO"
    },
    "订单商品核销时间段DTO0": {
      "type": "object",
      "properties": {
        "writeOffEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销结束时间"
        },
        "writeOffStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销开始时间"
        }
      },
      "title": "订单商品核销时间段DTO0"
    },
    "订单商品核销记录VO": {
      "type": "object",
      "properties": {
        "verifier": {
          "type": "string",
          "description": "核销人"
        },
        "writeOffCount": {
          "type": "integer",
          "format": "int32",
          "description": "核销次数"
        },
        "writeOffTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销时间"
        },
        "writeOffType": {
          "type": "integer",
          "format": "int32",
          "description": "核销类型 0:后台核销 1:扫码核销"
        }
      },
      "title": "订单商品核销记录VO"
    },
    "订单商品详情": {
      "type": "object",
      "properties": {
        "appointmentType": {
          "type": "integer",
          "format": "int32",
          "description": "预约方式，1需要预约，0无需预约"
        },
        "image": {
          "type": "string",
          "description": "商品图片"
        },
        "integral": {
          "type": "integer",
          "format": "int64",
          "description": "积分"
        },
        "itemCountPrice": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品/退款商品应收"
        },
        "itemName": {
          "type": "string",
          "description": "商品名称"
        },
        "itemPrice": {
          "type": "integer",
          "format": "int64",
          "description": "商品售价"
        },
        "itemSkuType": {
          "type": "integer",
          "format": "int32",
          "description": "商品sku类型，跟随商品服务保持一致"
        },
        "itemStatus": {
          "type": "integer",
          "format": "int32",
          "description": "商品的快照状态，上架或下架，需要商品服务提供"
        },
        "itemType": {
          "type": "integer",
          "format": "int32",
          "description": "商品类型:沿用商品服务提供"
        },
        "notAllowRefund": {
          "type": "integer",
          "format": "int32",
          "description": "是否不支持退款，0支持，1不支持"
        },
        "orderItemWriteOffTermDTO": {
          "description": "订单商品核销时间段",
          "$ref": "#/definitions/订单商品核销时间段DTO",
          "originalRef": "订单商品核销时间段DTO"
        },
        "scoreRuleId": {
          "type": "integer",
          "format": "int64",
          "description": "积分规则id"
        },
        "specifications": {
          "type": "string",
          "description": "规格"
        }
      },
      "title": "订单商品详情"
    },
    "订单商品预约时间DTO": {
      "type": "object",
      "properties": {
        "appointmentEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约结束时间"
        },
        "appointmentStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约开始时间"
        }
      },
      "title": "订单商品预约时间DTO"
    },
    "订单商品预约时间DTO0": {
      "type": "object",
      "required": [
        "orderItemId",
        "orderNo"
      ],
      "properties": {
        "appointmentEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约结束时间"
        },
        "appointmentStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约开始时间"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        }
      },
      "title": "订单商品预约时间DTO0"
    },
    "订单商品预约时间DTO1": {
      "type": "object",
      "required": [
        "orderItemId",
        "orderNo"
      ],
      "properties": {
        "appointmentEndTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约结束时间"
        },
        "appointmentStartTime": {
          "type": "string",
          "format": "date-time",
          "description": "预约开始时间"
        },
        "orderItemId": {
          "type": "integer",
          "format": "int64",
          "description": "订单商品id"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        }
      },
      "title": "订单商品预约时间DTO1"
    },
    "订单收货信息": {
      "type": "object",
      "properties": {
        "address": {
          "type": "string",
          "description": "详细地址"
        },
        "addressId": {
          "type": "integer",
          "format": "int64",
          "description": "地址id"
        },
        "areaId": {
          "type": "integer",
          "format": "int64",
          "description": "区域id"
        },
        "areaName": {
          "type": "string",
          "description": "区域名称"
        },
        "cityId": {
          "type": "integer",
          "format": "int64",
          "description": "城市id"
        },
        "cityName": {
          "type": "string",
          "description": "城市名称"
        },
        "provinceId": {
          "type": "integer",
          "format": "int64",
          "description": "省id"
        },
        "provinceName": {
          "type": "string",
          "description": "省名"
        },
        "receiveName": {
          "type": "string",
          "description": "联系人姓名"
        },
        "receivePhone": {
          "type": "string",
          "description": "联系人手机号"
        }
      },
      "title": "订单收货信息"
    },
    "订单收货信息DTO": {
      "type": "object",
      "properties": {
        "address": {
          "type": "string",
          "description": "详细地址"
        },
        "addressId": {
          "type": "integer",
          "format": "int64",
          "description": "地址id"
        },
        "areaId": {
          "type": "integer",
          "format": "int64",
          "description": "区域id"
        },
        "areaName": {
          "type": "string",
          "description": "区域名称"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "cityId": {
          "type": "integer",
          "format": "int64",
          "description": "市名"
        },
        "cityName": {
          "type": "string",
          "description": "城市名"
        },
        "provinceId": {
          "type": "integer",
          "format": "int64",
          "description": "省id"
        },
        "provinceName": {
          "type": "string",
          "description": "省名"
        },
        "receiveName": {
          "type": "string",
          "description": "收货人姓名"
        },
        "receivePhone": {
          "type": "string",
          "description": "收货人手机号"
        }
      },
      "title": "订单收货信息DTO"
    },
    "订单核销记录DTO": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        },
        "verifier": {
          "type": "string",
          "description": "核销人"
        },
        "writeOffItem": {
          "type": "array",
          "description": "核销商品信息",
          "items": {
            "$ref": "#/definitions/核销商品",
            "originalRef": "核销商品"
          }
        },
        "writeOffTime": {
          "type": "string",
          "format": "date-time",
          "description": "核销时间"
        },
        "writeOffType": {
          "type": "integer",
          "format": "int32",
          "description": "核销类型 0:后台核销 1:扫码核销"
        }
      },
      "title": "订单核销记录DTO"
    },
    "订单的支付单": {
      "type": "object",
      "properties": {
        "amountCompositions": {
          "type": "array",
          "description": "支付金额组成",
          "items": {
            "$ref": "#/definitions/AmountCompisitionDTO",
            "originalRef": "AmountCompisitionDTO"
          }
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "支付单ID"
        },
        "mode": {
          "type": "integer",
          "format": "int32",
          "description": "0：支付，1：退款"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "outTradeNo": {
          "type": "string",
          "description": "第三方支付流水(微信/支付宝支付单号)"
        },
        "payCenterTradeNo": {
          "type": "string",
          "description": "支付中心交易流水号"
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "description": "支付渠道:微信支付1,支付宝2"
        },
        "payNo": {
          "type": "string",
          "description": "支付单号"
        },
        "payType": {
          "type": "string",
          "description": "支付方式:现金0,积分1,余额2"
        },
        "payTypeCn": {
          "type": "string",
          "description": "支付方式中文"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "退款单号"
        },
        "stage": {
          "type": "integer",
          "format": "int32",
          "description": "支付阶段:1(第一阶段)、2(第二阶段)、99(最后阶段)"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "支付单状态:待发起（待支付或待退款）0,成功1,失败2"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "tradeTime": {
          "type": "string",
          "format": "date-time",
          "description": "交易时间"
        }
      },
      "title": "订单的支付单"
    },
    "订单的支付单信息DTO": {
      "type": "object",
      "properties": {
        "amountCompositions": {
          "type": "array",
          "description": "支付金额组成",
          "items": {
            "$ref": "#/definitions/支付单金额组成DTO",
            "originalRef": "支付单金额组成DTO"
          }
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "引用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "mode": {
          "type": "integer",
          "format": "int32",
          "description": "交易类型:0支付，1退款"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderPaymentVoucherDTO": {
          "description": "支付凭证，用于调起支付",
          "$ref": "#/definitions/支付凭证DTO",
          "originalRef": "支付凭证DTO"
        },
        "outTradeNo": {
          "type": "string",
          "description": "第三方支付流水"
        },
        "payCenterTradeNo": {
          "type": "string",
          "description": "支付中心流水号"
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "description": "支付渠道"
        },
        "payNo": {
          "type": "string",
          "description": "支付单号"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "退款单号"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "tradeTime": {
          "type": "string",
          "format": "date-time",
          "description": "交易时间"
        }
      },
      "title": "订单的支付单信息DTO"
    },
    "订单轨迹查询对象": {
      "type": "object",
      "required": [
        "deliveryType",
        "orderId"
      ],
      "properties": {
        "deliveryType": {
          "type": "object",
          "description": "注册快递类型"
        },
        "orderId": {
          "type": "object",
          "description": "订单Id"
        }
      },
      "title": "订单轨迹查询对象",
      "description": "订单轨迹查询对象"
    },
    "订单退款": {
      "type": "object",
      "properties": {
        "auditPersonId": {
          "type": "string",
          "description": "审核人id"
        },
        "auditPersonName": {
          "type": "string",
          "description": "审核人名称"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "项目ID"
        },
        "confirmRefundReason": {
          "type": "string",
          "description": "退款原因"
        },
        "items": {
          "type": "array",
          "description": "退款商品列表",
          "items": {
            "$ref": "#/definitions/订单退款商品列表",
            "originalRef": "订单退款商品列表"
          }
        },
        "memberId": {
          "type": "integer",
          "format": "int64",
          "description": "会员id"
        },
        "orderRefundAmountList": {
          "type": "array",
          "description": "退款订单金额项,退款退积分请填写此项",
          "items": {
            "$ref": "#/definitions/退款订单金额项",
            "originalRef": "退款订单金额项"
          }
        },
        "outOrderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "outTransactionId": {
          "type": "string",
          "description": "支付方交易单号，微信，支付宝单号"
        },
        "payAmount": {
          "type": "integer",
          "format": "int64",
          "description": "订单支付金额，单位：分"
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "description": "支付渠道"
        },
        "phone": {
          "type": "string",
          "description": "会员手机号"
        },
        "refundAmount": {
          "type": "integer",
          "format": "int64",
          "description": "退款金额，单位：分，只需退现金，填写此项"
        },
        "refundOrderNo": {
          "type": "string",
          "description": "业态通退款单号"
        },
        "storeId": {
          "type": "integer",
          "format": "int64",
          "description": "店铺ID"
        },
        "storeName": {
          "type": "string",
          "description": "店铺名称"
        },
        "totalAmount": {
          "type": "integer",
          "format": "int64",
          "description": "订单总计金额，单位：分"
        }
      },
      "title": "订单退款"
    },
    "订单退款商品列表": {
      "type": "object",
      "properties": {
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "退款商品数量"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "skuId"
        }
      },
      "title": "订单退款商品列表"
    },
    "订单退款检查": {
      "type": "object",
      "properties": {
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "项目ID"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        }
      },
      "title": "订单退款检查"
    },
    "请求下单DTO": {
      "type": "object",
      "required": [
        "outOrderNo"
      ],
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式：0快递，1自提，2同城配送"
        },
        "memberId": {
          "type": "integer",
          "format": "int64",
          "description": "会员id"
        },
        "orderIp": {
          "type": "string",
          "description": "下单ip"
        },
        "orderSource": {
          "type": "integer",
          "format": "int32",
          "description": "订单来源（0微信小程序,1:app,2:h5）"
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型：0普通商品订单 , 2微信视频号订单"
        },
        "outOrderNo": {
          "type": "string"
        },
        "phone": {
          "type": "string",
          "description": "会员手机号"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "请求下单DTO"
    },
    "请求下单DTO0": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "buyEntrance": {
          "type": "integer",
          "format": "int32",
          "description": "购买入口，0直接购买，1购物车"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "clubId": {
          "type": "integer",
          "format": "int64",
          "description": "会员俱乐部id"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式：0快递，1自提，2同城配送"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "isCalPrice": {
          "type": "boolean"
        },
        "memberId": {
          "type": "integer",
          "format": "int64",
          "description": "会员id"
        },
        "orderIp": {
          "type": "string",
          "description": "下单ip"
        },
        "orderNotes": {
          "type": "string",
          "description": "订单备注"
        },
        "orderSource": {
          "type": "integer",
          "format": "int32",
          "description": "订单来源（0微信小程序,1:app,2:h5）"
        },
        "orderType": {
          "type": "integer",
          "format": "int32",
          "description": "订单类型：0普通商品订单 , 2微信视频号订单"
        },
        "outOrderNo": {
          "type": "string"
        },
        "path": {
          "type": "string",
          "description": "订单跳转路径"
        },
        "payChannel": {
          "type": "integer",
          "format": "int32",
          "description": "支付渠道"
        },
        "requestMarketActivityDTO": {
          "description": "请求下单的活动DTO",
          "$ref": "#/definitions/请求的营销活动DTO",
          "originalRef": "请求的营销活动DTO"
        },
        "requestOrderBuInfoDTO": {
          "description": "下单店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "requestOrderItemDTOS": {
          "type": "array",
          "description": "请求下单商品列表",
          "items": {
            "$ref": "#/definitions/请求下单的商品DTO",
            "originalRef": "请求下单的商品DTO"
          }
        },
        "requestPromDTOS": {
          "type": "array",
          "description": "请求下单的优惠列表",
          "items": {
            "$ref": "#/definitions/请求下单的优惠列表",
            "originalRef": "请求下单的优惠列表"
          }
        },
        "requestReceiveInfoDTO": {
          "description": "收货信息",
          "$ref": "#/definitions/请求下单的收货信息",
          "originalRef": "请求下单的收货信息"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "uniqueAccountId": {
          "type": "string",
          "description": "会员一账通id"
        }
      },
      "title": "请求下单DTO0"
    },
    "请求下单的优惠列表": {
      "type": "object",
      "properties": {
        "promDesc": {
          "type": "string",
          "description": "优惠描述"
        },
        "promNo": {
          "type": "string",
          "description": "优惠编号"
        },
        "promPrice": {
          "type": "integer",
          "format": "int64",
          "description": "优惠价格，单位：分（目前仅用余额支付时传此参数）"
        },
        "promType": {
          "type": "integer",
          "format": "int32",
          "description": "优惠类型"
        }
      },
      "title": "请求下单的优惠列表"
    },
    "请求下单的商品DTO": {
      "type": "object",
      "properties": {
        "appointmentTimeDTO": {
          "description": "预约时间",
          "$ref": "#/definitions/订单商品预约时间DTO",
          "originalRef": "订单商品预约时间DTO"
        },
        "barcode": {
          "type": "string",
          "description": "商品条码"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式：0快递，1自提，2同城配送"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "itemCount": {
          "type": "integer",
          "format": "int32",
          "description": "商品数量"
        },
        "itemNo": {
          "type": "string",
          "description": "商品编号"
        },
        "path": {
          "type": "string",
          "description": "商品跳转路径"
        },
        "relationId": {
          "type": "string",
          "description": "订单商品关联的归属id"
        },
        "relationType": {
          "type": "integer",
          "format": "int32",
          "description": "订单商品关联归属类型(0无，1分销)"
        },
        "requestItemBuInfoDTO": {
          "description": "商品所属店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "requestReceiveInfoDTO": {
          "description": "收货信息",
          "$ref": "#/definitions/请求下单的收货信息",
          "originalRef": "请求下单的收货信息"
        },
        "skuId": {
          "type": "integer",
          "format": "int64",
          "description": "skuId"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "请求下单的商品DTO"
    },
    "请求下单的收货信息": {
      "type": "object",
      "properties": {
        "address": {
          "type": "string",
          "description": "详细地址"
        },
        "addressId": {
          "type": "integer",
          "format": "int64",
          "description": "地址id"
        },
        "areaId": {
          "type": "integer",
          "format": "int64",
          "description": "区域id"
        },
        "areaName": {
          "type": "string",
          "description": "区域名称"
        },
        "buyerMsg": {
          "type": "string",
          "description": "买家留言"
        },
        "cityId": {
          "type": "integer",
          "format": "int64",
          "description": "市id"
        },
        "cityName": {
          "type": "string",
          "description": "市名"
        },
        "provinceId": {
          "type": "integer",
          "format": "int64",
          "description": "省id"
        },
        "provinceName": {
          "type": "string",
          "description": "省名"
        },
        "receiveName": {
          "type": "string",
          "description": "联系人姓名"
        },
        "receivePhone": {
          "type": "string",
          "description": "联系人手机号"
        }
      },
      "title": "请求下单的收货信息"
    },
    "请求的营销活动DTO": {
      "type": "object",
      "properties": {
        "activityName": {
          "type": "string",
          "description": "活动名称"
        },
        "activityNo": {
          "type": "string",
          "description": "活动编号"
        },
        "channelType": {
          "type": "integer",
          "format": "int32",
          "description": "营销活动渠道类型"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "请求的营销活动DTO"
    },
    "退款订单DTO": {
      "type": "object",
      "properties": {
        "activityName": {
          "type": "string"
        },
        "activityNo": {
          "type": "string"
        },
        "activityType": {
          "type": "integer",
          "format": "int32"
        },
        "actualRefundAmount": {
          "type": "integer",
          "format": "int64",
          "description": "实际退款时管理员填写的退款金额"
        },
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用业务单元id"
        },
        "auditNode": {
          "type": "integer",
          "format": "int32",
          "description": "当前退款订单审核节点:当店铺审核同意/不同意时为1、当平台审核同意/不同意时为2、当确认退款同意/不同意时为5"
        },
        "buId": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "confirmRefundFee": {
          "type": "integer",
          "format": "int64",
          "description": "退款合计，单位分"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "deliveryMode": {
          "type": "integer",
          "format": "int32",
          "description": "提货方式,快递0,自提1,同城配送2"
        },
        "extraInfo": {
          "type": "string"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "lastAuditTime": {
          "type": "string",
          "format": "date-time",
          "description": "最后一次店铺/平台审核退款申请时间"
        },
        "logisticsList": {
          "type": "array",
          "description": "物流信息",
          "items": {
            "$ref": "#/definitions/物流信息",
            "originalRef": "物流信息"
          }
        },
        "orderBuInfoDTO": {
          "description": "店铺信息",
          "$ref": "#/definitions/店铺信息",
          "originalRef": "店铺信息"
        },
        "orderItems": {
          "type": "array",
          "description": "售后商品列表",
          "items": {
            "$ref": "#/definitions/售后商品",
            "originalRef": "售后商品"
          }
        },
        "orderMemberDTO": {
          "description": "会员信息",
          "$ref": "#/definitions/订单会员信息",
          "originalRef": "订单会员信息"
        },
        "orderNo": {
          "type": "string",
          "description": "订单编号"
        },
        "orderProms": {
          "type": "array",
          "description": "订单优惠组成",
          "items": {
            "$ref": "#/definitions/订单优惠",
            "originalRef": "订单优惠"
          }
        },
        "orderReceiveInfo": {
          "description": "收货信息",
          "$ref": "#/definitions/订单收货信息",
          "originalRef": "订单收货信息"
        },
        "orderRefundAmountDTOList": {
          "type": "array",
          "description": "订单价格组成",
          "items": {
            "$ref": "#/definitio', 0, '2024-07-23 12:03:59');
INSERT INTO wd_ipaas.dw_swagger_info (id, swagger_name, description, swagger_url, api_amount, api_group_id, api_domain_name, create_user, create_time, update_time, lessee_id, execute_status, import_type, swagger_json, resp_mapping_rule, parse_time) VALUES (9, 'wd-app', '', '', 25, 4, 'http://wd-app.wakecloud.svc.cluster.local:8080', '13012345678', '2024-08-30 15:22:24', '2024-08-30 15:24:01', 3, 2, 1, '{
  "swagger": "2.0",
  "info": {
    "description": "应用管理对外提供的rpc接口文档，目前通过openfeign提供rpc",
    "version": "1.0",
    "title": "应用管理对外提供的rpc接口文档"
  },
  "host": "wdcloud-test.huamaocloud.com",
  "tags": [
    {
      "name": "app-config-rpc-service-impl",
      "description": "App Config Rpc Service Impl"
    },
    {
      "name": "app-rpc-service-impl",
      "description": "App Rpc Service Impl"
    },
    {
      "name": "tenant-rpc-service-impl",
      "description": "Tenant Rpc Service Impl"
    },
    {
      "name": "业务单元相关能力",
      "description": "Business Unit Rpc Service Impl"
    },
    {
      "name": "主营行业管理",
      "description": "App Industry Category Rpc Service Impl"
    }
  ],
  "paths": {
    "/wd-app/rpc/app/queryAllBu": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询应用下所有业务单元列表",
        "operationId": "queryAllBuUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "appBuId",
            "in": "query",
            "description": "appBuId",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«SubBuDTO»»",
              "$ref": "#/definitions/ResultDTO«List«SubBuDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryAllIndustry": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "查询所有的行业类目",
        "operationId": "queryAllIndustryUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«行业类目»»",
              "$ref": "#/definitions/ResultDTO«List«行业类目»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryAppBaseInfoByTenantId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据租户id查询应用信息",
        "operationId": "queryAppBaseInfoByTenantIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«AppBaseInfoDTO»»",
              "$ref": "#/definitions/ResultDTO«List«AppBaseInfoDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryAppBuIdsByTenantId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据租户id查询应用id列表",
        "operationId": "queryAppBuIdsByTenantIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryByBatchAppId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据应用id批量查询应用信息",
        "operationId": "queryByBatchAppIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "appIdList",
            "in": "query",
            "description": "appIdList",
            "required": true,
            "type": "array",
            "items": {
              "type": "integer",
              "format": "int64"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«AppInfoDTO»»",
              "$ref": "#/definitions/ResultDTO«List«AppInfoDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryByBatchBuId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据业务单元id批量查询应用信息(单租户)",
        "operationId": "queryByBatchBuIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "buIdList",
            "in": "query",
            "description": "buIdList",
            "required": true,
            "type": "array",
            "items": {
              "type": "integer",
              "format": "int64"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«AppInfoDTO»»",
              "$ref": "#/definitions/ResultDTO«List«AppInfoDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryByTenantBatchBuId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据业务单元id批量查询应用信息(多租户)",
        "operationId": "queryByTenantBatchBuIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "tenantAppInfoReqDTOList",
            "description": "tenantAppInfoReqDTOList",
            "required": true,
            "schema": {
              "type": "array",
              "items": {
                "originalRef": "TenantAppInfoReqDTO",
                "$ref": "#/definitions/TenantAppInfoReqDTO"
              }
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«TenantAppInfoDTO»»",
              "$ref": "#/definitions/ResultDTO«List«TenantAppInfoDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/app/queryCorpAppBuIdByTenantId": {
      "post": {
        "tags": [
          "app-rpc-service-impl"
        ],
        "summary": "根据租户id查询集团AppBuId",
        "operationId": "queryCorpAppBuIdByTenantIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/appConfig/addAppConfig": {
      "post": {
        "tags": [
          "app-config-rpc-service-impl"
        ],
        "summary": "添加应用设置",
        "operationId": "addAppConfigUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "appConfigDTO",
            "description": "appConfigDTO",
            "required": true,
            "schema": {
              "originalRef": "AppConfigDTO",
              "$ref": "#/definitions/AppConfigDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/appConfig/deleteAppConfig": {
      "post": {
        "tags": [
          "app-config-rpc-service-impl"
        ],
        "summary": "删除应用设置",
        "operationId": "deleteAppConfigUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "appConfigDTO",
            "description": "appConfigDTO",
            "required": true,
            "schema": {
              "originalRef": "AppConfigDTO",
              "$ref": "#/definitions/AppConfigDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/appConfig/modifyAppConfig": {
      "post": {
        "tags": [
          "app-config-rpc-service-impl"
        ],
        "summary": "修改应用设置",
        "operationId": "modifyAppConfigUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "appConfigDTO",
            "description": "appConfigDTO",
            "required": true,
            "schema": {
              "originalRef": "AppConfigDTO",
              "$ref": "#/definitions/AppConfigDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/appConfig/querySingleAppConfig": {
      "post": {
        "tags": [
          "app-config-rpc-service-impl"
        ],
        "summary": "查询单个应用设置",
        "operationId": "querySingleAppConfigUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "appConfigDTO",
            "description": "appConfigDTO",
            "required": true,
            "schema": {
              "originalRef": "AppConfigDTO",
              "$ref": "#/definitions/AppConfigDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«AppConfigDTO»",
              "$ref": "#/definitions/ResultDTO«AppConfigDTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/create": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "新增业务单元",
        "operationId": "createBusinessUnitUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitDTO",
            "description": "businessUnitDTO",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitDTO",
              "$ref": "#/definitions/BusinessUnitDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/create.v2": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "创建业务单元",
        "description": "创建业务单元，目前惟客云组织体系内，业务单元需挂载在组织下，支持一个业务单元关联多个上级组织；",
        "operationId": "createBusinessUnitV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitDTO",
            "description": "businessUnitDTO",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitDTO",
              "$ref": "#/definitions/BusinessUnitDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«long»",
              "$ref": "#/definitions/ResultDTO«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/delete": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "删除业务单元",
        "operationId": "deleteBusinessUnitUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "deleteDTO",
            "description": "deleteDTO",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitDeleteDTO",
              "$ref": "#/definitions/BusinessUnitDeleteDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/modify": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "更新业务单元",
        "operationId": "modifyBusinessUnitUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitDTO",
            "description": "businessUnitDTO",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitDTO",
              "$ref": "#/definitions/BusinessUnitDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/modify.v2": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "修改业务单元",
        "description": "修改业务单元",
        "operationId": "modifyBusinessUnitV2UsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitDTO",
            "description": "businessUnitDTO",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitDTO",
              "$ref": "#/definitions/BusinessUnitDTO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«boolean»",
              "$ref": "#/definitions/ResultDTO«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/page.query.businessList": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询业务单元列表",
        "description": "查询业务单元列表；支持按指定组织过滤；支持按业务单元状态过滤；支持Ids查询，支持分页",
        "operationId": "pageQueryBusinessListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitQuery",
            "description": "businessUnitQuery",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitQuery",
              "$ref": "#/definitions/BusinessUnitQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«BusinessUnitDTO»»",
              "$ref": "#/definitions/PageResultDTO«List«BusinessUnitDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/page.query.information": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询业务单元信息列表",
        "description": "查询指定业务单元详细信息；支持一次性查询多个业务单元的信息；支持分页",
        "operationId": "pageQueryInformationUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitQuery",
            "description": "businessUnitQuery",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitQuery",
              "$ref": "#/definitions/BusinessUnitQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«BusinessUnitDTO»»",
              "$ref": "#/definitions/PageResultDTO«List«BusinessUnitDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/parent/tree": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询某个业务单元下的所有上层父节点",
        "operationId": "queryBusinessParentTreeListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitQuery",
            "description": "businessUnitQuery",
            "required": true,
            "schema": {
              "originalRef": "AppBusinessTreeQuery",
              "$ref": "#/definitions/AppBusinessTreeQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«BusinessUnitDTO»»",
              "$ref": "#/definitions/ResultDTO«List«BusinessUnitDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/query": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询单个业务单元",
        "operationId": "querySingleBusinessUnitUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "singleQuery",
            "description": "singleQuery",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitSingleQuery",
              "$ref": "#/definitions/BusinessUnitSingleQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«BusinessUnitDTO»",
              "$ref": "#/definitions/ResultDTO«BusinessUnitDTO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/business/queryByBusinessList": {
      "post": {
        "tags": [
          "业务单元相关能力"
        ],
        "summary": "查询业务单元集合",
        "operationId": "queryBusinessListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "businessUnitQuery",
            "description": "businessUnitQuery",
            "required": true,
            "schema": {
              "originalRef": "BusinessUnitQuery",
              "$ref": "#/definitions/BusinessUnitQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«BusinessUnitDTO»»",
              "$ref": "#/definitions/ResultDTO«List«BusinessUnitDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/industry/category/page": {
      "post": {
        "tags": [
          "主营行业管理"
        ],
        "summary": "分页查询行业",
        "description": "分页查询行业",
        "operationId": "appPageQryUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "query",
            "description": "query",
            "required": true,
            "schema": {
              "originalRef": "AppIndustryCategoryQuery",
              "$ref": "#/definitions/AppIndustryCategoryQuery"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "PageResultDTO«List«行业类目»»",
              "$ref": "#/definitions/PageResultDTO«List«行业类目»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/tenant/queryByBatchTenantId": {
      "post": {
        "tags": [
          "tenant-rpc-service-impl"
        ],
        "summary": "根据应用id批量查询应用信息",
        "operationId": "queryByBatchTenantIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantIdList",
            "in": "query",
            "description": "tenantIdList",
            "required": true,
            "type": "array",
            "items": {
              "type": "integer",
              "format": "int64"
            },
            "collectionFormat": "multi"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«TenantInfoDTO»»",
              "$ref": "#/definitions/ResultDTO«List«TenantInfoDTO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    },
    "/wd-app/rpc/tenant/queryWithoutAppByTenantIdList": {
      "post": {
        "tags": [
          "tenant-rpc-service-impl"
        ],
        "summary": "根据一批tenantId筛选出没有应用的租户",
        "operationId": "queryWithoutAppByTenantIdListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantIdList",
            "in": "query",
            "description": "tenantIdList",
            "required": true,
            "type": "array",
            "items": {
              "type": "integer",
              "format": "int64"
            },
            "collectionFormat": "multi"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "originalRef": "ResultDTO«List«long»»",
              "$ref": "#/definitions/ResultDTO«List«long»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        }
      }
    }
  },
  "definitions": {
    "AppBaseInfoDTO": {
      "type": "object",
      "properties": {
        "appName": {
          "type": "string",
          "description": "业务单元名称"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "应用（业务单元）Id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "AppBaseInfoDTO"
    },
    "AppBusinessTreeQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "buId"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "AppBusinessTreeQuery",
      "description": "业务单元分页查询"
    },
    "AppConfigDTO": {
      "type": "object",
      "required": [
        "appBuId",
        "configKey",
        "tenantId"
      ],
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用Id"
        },
        "configKey": {
          "type": "string",
          "description": "配置key"
        },
        "configName": {
          "type": "string",
          "description": "配置名称"
        },
        "configValue": {
          "type": "string",
          "description": "配置value"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户ID"
        }
      },
      "title": "AppConfigDTO",
      "description": "应用配置DTO"
    },
    "AppIndustryCategoryQuery": {
      "type": "object",
      "properties": {
        "description": {
          "type": "string",
          "description": "描述"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "行业类目id"
        },
        "isLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "description": "层级"
        },
        "name": {
          "type": "string",
          "description": "行业类目名"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "上级唯一标识"
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "originalRef": "SortingField",
            "$ref": "#/definitions/SortingField"
          }
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "状态"
        }
      },
      "title": "AppIndustryCategoryQuery",
      "description": "应用分页查询"
    },
    "AppInfoDTO": {
      "type": "object",
      "properties": {
        "appDescribeInfo": {
          "type": "string",
          "description": "应用简介/描述"
        },
        "appExpireStatus": {
          "type": "integer",
          "format": "int32",
          "description": "应用租期状态 0-已过期 1-未到期"
        },
        "appImage": {
          "type": "string",
          "description": "应用图标"
        },
        "appName": {
          "type": "string",
          "description": "业务单元名称"
        },
        "appStatus": {
          "type": "integer",
          "format": "int32",
          "description": "应用状态 0-禁用 1-启用"
        },
        "expireTime": {
          "type": "string",
          "format": "date-time",
          "description": "业务单元到期时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "应用（业务单元）Id"
        },
        "industryCategoryDesc": {
          "type": "string",
          "description": "主营行业/行业分类描述信息"
        },
        "industryCategoryId": {
          "type": "integer",
          "format": "int64",
          "description": "主营行业/行业分类ID"
        },
        "industryCategoryName": {
          "type": "string",
          "description": "主营行业/行业分类名称"
        },
        "keyFlag": {
          "type": "string",
          "description": "唯一标识(UMP:代表集团应用)"
        },
        "nodeId": {
          "type": "integer",
          "format": "int64",
          "description": "组织架构的节点ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "AppInfoDTO",
      "description": "应用列表DTO"
    },
    "BusinessUnitDTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "组织架构的业务单元属性属性"
        },
        "buName": {
          "type": "string",
          "description": "业务单元名称名称"
        },
        "buStatus": {
          "type": "integer",
          "format": "int32",
          "description": "应用状态 0-禁用 1-启用"
        },
        "buType": {
          "type": "integer",
          "format": "int32",
          "description": "业务单元类型 0-应用"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "internalEnterpriseStatus": {
          "type": "integer",
          "format": "int32"
        },
        "merchantStatus": {
          "type": "integer",
          "format": "int32"
        },
        "nodeCode": {
          "type": "string",
          "description": "组织架构的节点编码"
        },
        "nodeId": {
          "type": "integer",
          "format": "int64",
          "description": "组织架构的节点Id"
        },
        "nodeLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "在组织架构是否叶子节点， 1是，0 否"
        },
        "nodeParentId": {
          "type": "integer",
          "format": "int64",
          "description": "组织架构父节点ID"
        },
        "nodeParentIdList": {
          "type": "array",
          "description": "组织架构父节点ID（支持一个业务单元挂多个组织节点的情况）",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "orgNodeInfoDTOList": {
          "type": "array",
          "description": "组织架构父节点ID（支持一个业务单元挂多个组织节点的情况）",
          "items": {
            "originalRef": "OrgNodeInfoDTO",
            "$ref": "#/definitions/OrgNodeInfoDTO"
          }
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点id  0代表一级"
        },
        "seq": {
          "type": "integer",
          "format": "int32",
          "description": "在组织结构的显示顺序，不传默认最高优先级"
        },
        "syncOrg": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "BusinessUnitDTO"
    },
    "BusinessUnitDeleteDTO": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "业务单元属性"
        },
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "syncOrg": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "BusinessUnitDeleteDTO"
    },
    "BusinessUnitQuery": {
      "type": "object",
      "properties": {
        "appBuId": {
          "type": "integer",
          "format": "int64",
          "description": "应用id"
        },
        "buIdList": {
          "type": "array",
          "description": "buIdList",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "buName": {
          "type": "string",
          "description": "业务单元名称名称"
        },
        "buStatus": {
          "type": "integer",
          "format": "int32",
          "description": "应用状态 0-禁用 1-启用"
        },
        "buType": {
          "type": "string",
          "description": "业务单元类型 0-应用"
        },
        "buTypeList": {
          "type": "array",
          "description": "业务单元类型 BusinessUnitTypeEnum",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "createBy": {
          "type": "string",
          "description": "创建人员"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "offset": {
          "type": "integer",
          "format": "int32"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页码(不能为空)"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "每页数量(不能为空)",
          "maximum": 200,
          "exclusiveMaximum": false
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点id  0代表一级"
        },
        "searchCount": {
          "type": "boolean",
          "description": "是否查询总条数"
        },
        "sortingFields": {
          "type": "array",
          "description": "排序",
          "items": {
            "originalRef": "SortingField",
            "$ref": "#/definitions/SortingField"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "updateBy": {
          "type": "string",
          "description": "修改人员"
        }
      },
      "title": "BusinessUnitQuery",
      "description": "业务单元分页查询"
    },
    "BusinessUnitSingleQuery": {
      "type": "object",
      "properties": {
        "attrCode": {
          "type": "string",
          "description": "节点属性"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点id  0代表一级"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        }
      },
      "title": "BusinessUnitSingleQuery",
      "description": "单个业务单元查询"
    },
    "OrgNodeInfoDTO": {
      "type": "object",
      "properties": {
        "nodeId": {
          "type": "integer",
          "format": "int64",
          "description": "组织架构id"
        },
        "orgNodeParentIds": {
          "type": "string",
          "description": "组织架构父节点ID，节点间使用,分隔"
        }
      },
      "title": "OrgNodeInfoDTO"
    },
    "PageResultDTO«List«BusinessUnitDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "BusinessUnitDTO",
            "$ref": "#/definitions/BusinessUnitDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«BusinessUnitDTO»»"
    },
    "PageResultDTO«List«行业类目»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "cursor": {
          "type": "integer",
          "format": "int64"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "行业类目",
            "$ref": "#/definitions/行业类目"
          }
        },
        "msg": {
          "type": "string"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "success": {
          "type": "boolean"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "PageResultDTO«List«行业类目»»"
    },
    "ResultDTO«AppConfigDTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "originalRef": "AppConfigDTO",
          "$ref": "#/definitions/AppConfigDTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«AppConfigDTO»"
    },
    "ResultDTO«BusinessUnitDTO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "originalRef": "BusinessUnitDTO",
          "$ref": "#/definitions/BusinessUnitDTO"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«BusinessUnitDTO»"
    },
    "ResultDTO«List«AppBaseInfoDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "AppBaseInfoDTO",
            "$ref": "#/definitions/AppBaseInfoDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«AppBaseInfoDTO»»"
    },
    "ResultDTO«List«AppInfoDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "AppInfoDTO",
            "$ref": "#/definitions/AppInfoDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«AppInfoDTO»»"
    },
    "ResultDTO«List«BusinessUnitDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "BusinessUnitDTO",
            "$ref": "#/definitions/BusinessUnitDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«BusinessUnitDTO»»"
    },
    "ResultDTO«List«SubBuDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "SubBuDTO",
            "$ref": "#/definitions/SubBuDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«SubBuDTO»»"
    },
    "ResultDTO«List«TenantAppInfoDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "TenantAppInfoDTO",
            "$ref": "#/definitions/TenantAppInfoDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«TenantAppInfoDTO»»"
    },
    "ResultDTO«List«TenantInfoDTO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "TenantInfoDTO",
            "$ref": "#/definitions/TenantInfoDTO"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«TenantInfoDTO»»"
    },
    "ResultDTO«List«long»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«long»»"
    },
    "ResultDTO«List«行业类目»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "originalRef": "行业类目",
            "$ref": "#/definitions/行业类目"
          }
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«List«行业类目»»"
    },
    "ResultDTO«boolean»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "boolean"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«boolean»"
    },
    "ResultDTO«long»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int64"
        },
        "msg": {
          "type": "string"
        },
        "success": {
          "type": "boolean"
        }
      },
      "title": "ResultDTO«long»"
    },
    "SortingField": {
      "type": "object",
      "properties": {
        "asc": {
          "type": "boolean",
          "description": "是否升序, 默认升序"
        },
        "column": {
          "type": "string",
          "description": "排序字段"
        }
      },
      "title": "SortingField"
    },
    "SubBuDTO": {
      "type": "object",
      "properties": {
        "buName": {
          "type": "string",
          "description": "业务单元名称名称"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "业务单元id"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父节点ID"
        }
      },
      "title": "SubBuDTO"
    },
    "TenantAppInfoDTO": {
      "type": "object",
      "properties": {
        "appInfoDTOList": {
          "type": "array",
          "description": "应用信息集合",
          "items": {
            "originalRef": "AppInfoDTO",
            "$ref": "#/definitions/AppInfoDTO"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "TenantAppInfoDTO",
      "description": "应用列表DTO"
    },
    "TenantAppInfoReqDTO": {
      "type": "object",
      "properties": {
        "buIdList": {
          "type": "array",
          "description": "业务单元Id集合",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        }
      },
      "title": "TenantAppInfoReqDTO",
      "description": "应用列表DTO"
    },
    "TenantInfoDTO": {
      "type": "object",
      "properties": {
        "businessUnitNum": {
          "type": "integer",
          "format": "int32",
          "description": "可配置的应用数量"
        },
        "directorMail": {
          "type": "string",
          "description": "负责人邮箱"
        },
        "directorName": {
          "type": "string",
          "description": "负责人姓名"
        },
        "directorPhone": {
          "type": "string",
          "description": "负责人电话"
        },
        "expireTime": {
          "type": "string",
          "format": "date-time",
          "description": "过期时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "租户Id"
        },
        "nodeId": {
          "type": "integer",
          "format": "int64",
          "description": "关联组织架构的节点ID"
        },
        "tenantExpireStatus": {
          "type": "integer",
          "format": "int32",
          "description": "租户租期状态 0-已过期 1-未到期"
        },
        "tenantName": {
          "type": "string",
          "description": "租户名称"
        },
        "tenantStatus": {
          "type": "integer",
          "format": "int32",
          "description": "租户状态 0-禁用 1-启用"
        },
        "tenantType": {
          "type": "integer",
          "format": "int32",
          "description": "租户类型 0-体验类型 1-付费类型"
        }
      },
      "title": "TenantInfoDTO",
      "description": "租户信息DTO"
    },
    "行业类目": {
      "type": "object",
      "properties": {
        "createBy": {
          "type": "string",
          "description": "创建人"
        },
        "createTime": {
          "type": "string",
          "format": "date-time",
          "description": "创建时间"
        },
        "description": {
          "type": "string",
          "description": "描述"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "行业类目id"
        },
        "isLeaf": {
          "type": "integer",
          "format": "int32",
          "description": "是否叶子节点"
        },
        "keyFlag": {
          "type": "string",
          "description": "唯一标识(UMP:代表集团应用)"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "description": "层级"
        },
        "name": {
          "type": "string",
          "description": "行业类目名"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "上级唯一标识"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "状态"
        },
        "updateBy": {
          "type": "string",
          "description": "更新人"
        },
        "updateTime": {
          "type": "string",
          "format": "date-time",
          "description": "更新时间"
        }
      },
      "title": "行业类目"
    }
  }
}', 0, '2024-08-30 15:22:24');
