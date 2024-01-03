package com.alpha.omega.user.batch;

import ch.qos.logback.core.util.FileUtil;
import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.alpha.omega.user.utils.Constants.COMMA;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoadToUserEntityItemLifecycleListener extends StepListenerSupport<UserLoad, UserEntity> implements JobExecutionListener{
    private static final Logger logger = LoggerFactory.getLogger(UserLoadToUserEntityItemLifecycleListener.class);

    WritableResource errorResource;
    WritableResource archiveResource;
    WritableResource sourceResource;
    String archiveSourcePath;
    String archiveDestinationPath;
    PrintWriter errorPrintWriter;

    @Builder.Default
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    UserRepository userRepository;

    @Override
    public void beforeProcess(UserLoad item) {
        super.beforeProcess(item);
    }

    @Override
    public void afterProcess(UserLoad item, UserEntity result) {
        super.afterProcess(item, result);
    }

    @Override
    public void onProcessError(UserLoad item, Exception e) {
        super.onProcessError(item, e);
    }

    @Override
    public void afterRead(UserLoad item) {
        super.afterRead(item);
    }

    @Override
    public void onSkipInProcess(UserLoad item, Throwable t) {
        super.onSkipInProcess(item, t);
    }

    @Override
    public void beforeRead() {
        super.beforeRead();
    }

    @Override
    public void onReadError(Exception ex) {
        // FlatFileParseException
        super.onReadError(ex);

        if (ex instanceof FlatFileParseException){
            FlatFileParseException filEx = (FlatFileParseException)ex;
            ex.getCause().printStackTrace();
            String buildCsVErrorLine = buildCsvErrorLine(filEx);
            logger.info("--------UserLoadToUserEntityItemLifecycleListener Got csvLine => {}",buildCsVErrorLine);
            errorPrintWriter.println(buildCsVErrorLine);
            errorPrintWriter.flush();
        }
    }

    static  String buildCsvErrorLine(FlatFileParseException filEx) {
        return new StringBuilder(filEx.getLineNumber())
                .append(COMMA)
                .append(filEx.getInput())
                .append(COMMA)
                .append(filEx.getMessage())
                .toString();
    }

    @Override
    public void beforeWrite(Chunk<? extends UserEntity> items) {
        super.beforeWrite(items);
    }

    @Override
    public void afterWrite(Chunk<? extends UserEntity> items) {
        super.afterWrite(items);
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends UserEntity> items) {
        super.onWriteError(exception, items);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.debug("######## UserLoadToUserEntityItemLifecycleListener beforeJob => {}",jobExecution);
        JobExecutionListener.super.beforeJob(jobExecution);
        try {
            this.errorPrintWriter = new PrintWriter(errorResource.getOutputStream());
        } catch (IOException e) {
            throw new UserBatchException("Could not create error resource!!!!! Stopping JobExecution "+jobExecution.toString(),e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        logger.debug("UserLoadToUserEntityItemLifecycleListener afterJob => {}",jobExecution.toString());
        try {
            saveAsFile(archiveResource.getURL().getPath());
            errorPrintWriter.close();
        } catch (IOException e) {
            throw new UserBatchException("Could not create archive file!",e);
        }
    }

    public FileInfo saveAsFile(String destPath) throws IOException {

        logger.debug("archiveResource.getURL() => {}",destPath);
        final Path path = Paths.get(destPath);
        if (!Files.exists(path)) {
            logger.info("File NOT exist, create it and it's missing parent folder :{}", path);
            File file = new File(path.toString());
            FileUtil.createMissingParentDirectories(file);
        }

        //FileCopyUtils.copy(archiveResource.getInputStream(), new FileOutputStream(path.toFile()));

        FileCopyUtils.copy(sourceResource.getInputStream(), archiveResource.getOutputStream());

        final FileInfo fileInfo = FileInfo.builder()
                .fileInfo(path.toFile().toString())
                .fileSize(FileUtils.byteCountToDisplaySize(Files.size(path)))
                .lastModified(new Date(path.toFile().lastModified()))
                .build();

        logger.debug("saved file to {}, file is:{}", path, fileInfo);
        return fileInfo;
    }


    @Override
    public void beforeChunk(ChunkContext context) {
        super.beforeChunk(context);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        super.afterChunk(context);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        super.afterChunkError(context);
    }

    @Override
    public void onSkipInRead(Throwable t) {
        super.onSkipInRead(t);
    }

    @Override
    public void onSkipInWrite(UserEntity item, Throwable t) {
        super.onSkipInWrite(item, t);
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        return super.afterStep(stepExecution);
    }
}
