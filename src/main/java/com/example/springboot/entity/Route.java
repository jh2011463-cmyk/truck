package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 线路表
 * </p>
 *
 * @author
 */
@Getter
@Setter
@TableName("route")
@ApiModel(value = "Route对象", description = "线路信息")
@ToString
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("线路名称")
    private String routeName;

    @ApiModelProperty("起点地址")
    private String startAddress;

    @ApiModelProperty("终点地址")
    private String endAddress;

    @ApiModelProperty("距离（公里）")
    private Double distance;

    @ApiModelProperty("预估时间（分钟）")
    private Double estimatedTime;

    @ApiModelProperty("拥堵等级（1-5）")
    private Integer trafficLevel;

    @ApiModelProperty("线路坐标（JSON格式）")
    private String routeCoordinates;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;

    @ApiModelProperty("线路评分")
    private Double routeScore;

    @ApiModelProperty("是否推荐（0-否，1-是）")
    private Integer isRecommended;

    @ApiModelProperty("备注")
    private String remark;

    public Route() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.isRecommended = 0;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}