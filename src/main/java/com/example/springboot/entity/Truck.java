package com.example.springboot.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
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
@ApiModel(value = "Truck对象", description = "")
public class Truck implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("尺寸")
    private String size;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("车辆图片")
    private String img;


}
