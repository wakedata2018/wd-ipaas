package com.wakedata.dw.open.model.domain;

import lombok.Data;

import java.util.List;


/**
 * @author tanzhi
 * @title DwOpenUserMenusDo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description 菜单表
 */
@Data
public class DwOpenUserMenusDo implements Comparable<DwOpenUserMenusDo>{

     private Long id;//主键.

    private String name;//名称.

    private String code; //权限编号

    private Long pId; //权限父编号

    private String pName; //权限父名字

    private String category; //权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view

    private String resourceType;//资源类型，[menu|button]

    private String url;//资源路径.

    private Integer sort;//排序

    private Integer levels;//级别

    private Integer status;//是否可用
    private String icon;
     List<DwOpenUserMenusDo> children;

    @Override
    public int compareTo(DwOpenUserMenusDo dwOpenUserMenusDo) {
        return  this.sort - dwOpenUserMenusDo.getSort();
    }

//    private Integer id;
//    private String menuName;
//    private String link;
//    private Integer parentMenuId;
//    private Integer order;
//    private String icon;
//
//    List<DwOpenUserMenusDo> children;

}
