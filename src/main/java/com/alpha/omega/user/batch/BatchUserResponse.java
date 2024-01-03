package com.alpha.omega.user.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDateTime;

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
    @ApiModelProperty(required = true, value = "")
    JobInstance jobInstance;
    @ApiModelProperty(required = true, value = "")
    LocalDateTime createTime;
    @ApiModelProperty(required = true, value = "")
    ExecutionContext executionContext;
    @ApiModelProperty(required = true, value = "")
    ExitStatus exitStatus;
    @ApiModelProperty(required = true, value = "")
    String jobName;
}
