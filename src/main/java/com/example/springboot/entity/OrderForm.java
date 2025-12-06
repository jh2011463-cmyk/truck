package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 */
@Getter
@Setter
@TableName("order_form")
@ApiModel(value = "OrderForm对象", description = "")
public class OrderForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("下单时间")
    private String date;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("详情")
    private String detail;

    @ApiModelProperty("客户id")
    private Integer userId;

    @ApiModelProperty("司机id")
    private Integer driverId;

    @ApiModelProperty("车辆id")
    private Integer truckId;

    @ApiModelProperty("送达时间")
    private String sendTime;

    @ApiModelProperty("订单状态")
    private String status;

    @ApiModelProperty("用户安全等级")
    private Integer userLevel;

    //客户名称
    @TableField(exist = false)
    private String userName;

    //司机名称
    @TableField(exist = false)
    private String driverName;

    //车辆名称
    @TableField(exist = false)
    private String truckName;


}
