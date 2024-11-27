export const sqlPlacholderText = `<div>
<p>1、在sql中加入语法表达式之后，该表达式可以解析成接口参数，在接口调用时传入</p>
<p>2、目前支持的语法表达式：</p>
<div style="padding-left:5px">
  <p>1）type支持参数类型为：integer、long、double、boolean、string、array</p>
  <p>2）\${param:type} 表示设置可变参数（param为参数名称，type为参数类型），不可忽略（参数为空也会传递到sql中执行）
    如：<p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = \${tenantId:Long} ;</strong></p>
    以上sql解析之后获取到一个参数信息，参数名称为tenantId、参数类型为Long，当参数设置为空时也会传递到sql中
    举例：tenantId为空时，以上sql执行时解析为：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = ;</strong></p>
    tenantId = 1时，以上sql执行时解析为：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = 1 ;</strong></p>
  </p>
  <p>3）$param:type[ sql condition ] 表示设置动态可变参数（param为参数名称，type为参数类型，sql condition为sql片段，可为sql条件或子查询语句），可忽略（设置允许为空后，参数为空不会进入到sql中执行）
    当共用一个参数名称时，sql condition内部参数值可以用 ? 表示可变参数，否则会按照表达式解析出设置的参数
    如：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = \${tenantId:Long}  $attrName:string[ and attr_name = ?] ;</strong></p>
    等价于：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = \${tenantId:Long}  $attrName:string[ and attr_name = \${attrName:string}] ;</strong></p>
    以上sql解析之后获取到两个参数信息，（参数名称为tenantId、参数类型为Long、为空也会传递到sql中），（参数名称为attrName、参数类型为String、设置为空后不会传递到sql中）举例：tenantId = 1，attrName为空时，以上sql执行时解析为：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = 1 ;</strong></p>
    tenantId = 1，attrName = "组织" 时，以上sql执行时解析为：
    <p><strong>SELECT id,tenant_id,attr_name,attr_code,sys_config,create_time,create_by,update_by,update_time FROM org_attribute WHERE tenant_id = 1 and attr_name = '组织' ;</strong></p>
  </p>
  <div>
</div>`;
