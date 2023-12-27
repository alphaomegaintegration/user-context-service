package com.alpha.omega.user.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.batch.core.JobExecution;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BatchUserResponse {

    @ApiModelProperty(required = true, value = "")
    JobExecution jobExecution;
}
