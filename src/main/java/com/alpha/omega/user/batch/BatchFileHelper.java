package com.alpha.omega.user.batch;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileHelper {

    private static final Logger logger = LoggerFactory.getLogger(BatchFileHelper.class);

    private CSVReader CSVReader;
    private CSVWriter CSVWriter;
    private Reader fileReader;
    private Writer fileWriter;
    private Resource inputResource;
    private Resource outputResource;


    public String[] readLine() {
        try {
            if (CSVReader == null) initReader();
            String[] line = CSVReader.readNext();
            if (line == null) return null;
            return line;
        } catch (Exception e) {
            logger.error("Error while reading line in file: " + this.inputResource.toString());
            return null;
        }
    }

    public void writeLine(String[] line) {
        try {
            if (CSVWriter == null) initWriter();

            CSVWriter.writeNext(line);
        } catch (Exception e) {
            logger.error("Error while writing line in file: " +  this.outputResource.toString());
        }
    }

    private void initReader() throws Exception {
        if (inputResource == null) {
            throw new UserBatchException("Input Resource cannot be null for BatchFileHelper");
        }
        if (fileReader == null) fileReader = new InputStreamReader(inputResource.getInputStream());
        if (CSVReader == null) CSVReader = new CSVReader(fileReader);
    }

    private void initWriter() throws Exception {
        if (outputResource == null) {
            throw new UserBatchException("Output Resource cannot be null for BatchFileHelper");
        }

        if (!(outputResource instanceof WritableResource)){
            throw new UserBatchException("Output Resource must be of type WritableResource for BatchFileHelper");
        }
        WritableResource writableResource = (WritableResource)outputResource;
        if (fileWriter == null) fileWriter = new OutputStreamWriter(writableResource.getOutputStream());
        if (CSVWriter == null) CSVWriter = new CSVWriter(fileWriter);
    }

    public void closeWriter() {
        try {
            CSVWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            logger.error("Error while closing writer.");
        }
    }

    public void closeReader() {
        try {
            CSVReader.close();
            fileReader.close();
        } catch (IOException e) {
            logger.error("Error while closing reader.");
        }
    }

}
