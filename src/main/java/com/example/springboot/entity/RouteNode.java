package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 线路节点表
 * </p>
 *
 * @author
 */
@Getter
@Setter
@TableName("route_node")
@ApiModel(value = "RouteNode对象", description = "线路节点信息")
@ToString
public class RouteNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("线路ID")
    private Integer routeId;

    @ApiModelProperty("节点顺序")
    private Integer nodeOrder;

    @ApiModelProperty("纬度")
    private Double latitude;

    @ApiModelProperty("经度")
    private Double longitude;

    @ApiModelProperty("节点地址")
    private String address;

    @ApiModelProperty("交通信息")
    private String trafficInfo;

    @ApiModelProperty("预计到达时间（分钟）")
    private Integer estimatedArrivalTime;

    @ApiModelProperty("节点类型（0-普通节点，1-关键节点）")
    private Integer nodeType;

    @ApiModelProperty("备注")
    private String remark;

    public RouteNode() {
        this.nodeType = 0;
    }
}