package com.example.springboot.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "Questionnaire对象", description = "")
public class Questionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("调查问卷主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("评价类型")
    private String type;

    @ApiModelProperty("调查问卷日期")
    private String date;

    @ApiModelProperty("详情")
    private String detail;

    @ApiModelProperty("客户id")
    private Integer userId;

    @ApiModelProperty("司机id")
    private Integer driverId;

    @ApiModelProperty("评价订单id")
    private Integer orderId;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String driverName;


}
