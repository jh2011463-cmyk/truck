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
 * 历史线路表
 * </p>
 *
 * @author
 */
@Getter
@Setter
@TableName("route_history")
@ApiModel(value = "RouteHistory对象", description = "历史线路信息")
@ToString
public class RouteHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("线路ID")
    private Integer routeId;

    @ApiModelProperty("实际耗时（分钟）")
    private Double actualTime;

    @ApiModelProperty("交通状况（1-畅通，2-缓行，3-拥堵）")
    private Integer trafficCondition;

    @ApiModelProperty("司机ID")
    private Integer driverId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("天气状况")
    private String weatherCondition;

    @ApiModelProperty("平均速度（km/h）")
    private Double averageSpeed;

    @ApiModelProperty("燃油消耗（升）")
    private Double fuelConsumption;

    @ApiModelProperty("用户评分（1-5）")
    private Integer userRating;

    @ApiModelProperty("备注")
    private String remark;

    public RouteHistory() {
        this.createdTime = LocalDateTime.now();
    }
}