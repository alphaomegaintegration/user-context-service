package com.alpha.omega.user.batch;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
class FileInfo {
    String fileInfo;
    String fileSize;
    Date lastModified;
}