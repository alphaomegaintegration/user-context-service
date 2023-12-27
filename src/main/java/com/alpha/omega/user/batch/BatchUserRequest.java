package com.alpha.omega.user.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BatchUserRequest {
    @ApiModelProperty(required = true, value = "")
    String correlationId;
    @ApiModelProperty(required = true, value = "")
    Map<String,String> jobParameters;
    @ApiModelProperty(required = false, value = "")
    List<UserLoad> users;
}
