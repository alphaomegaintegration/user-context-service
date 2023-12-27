package com.alpha.omega.user.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserLoad implements Serializable {

    @ApiModelProperty(required = false, value = "")
    String title;
    @ApiModelProperty(required = true, value = "")
    String first;
    @ApiModelProperty(required = true, value = "")
    String last;
    @ApiModelProperty(required = false, value = "")
    String streetNumber;
    @ApiModelProperty(required = false, value = "")
    String streetName;
    @ApiModelProperty(required = false, value = "")
    String city;
    @ApiModelProperty(required = false, value = "")
    String state;
    @ApiModelProperty(required = false, value = "")
    String country;
    @ApiModelProperty(required = false, value = "")
    String postcode;
    @ApiModelProperty(required = false, value = "")
    String latitude;
    @ApiModelProperty(required = false, value = "")
    String longitude;
    @ApiModelProperty(required = false, value = "")
    String offset;
    @ApiModelProperty(required = false, value = "")
    String description;
    @ApiModelProperty(required = true, value = "")
    String email;
    @ApiModelProperty(required = false, value = "")
    String externalId;
    @ApiModelProperty(required = false, value = "")
    String contextId;
    @ApiModelProperty(required = false, value = "")
    String role;
    String password;
}
