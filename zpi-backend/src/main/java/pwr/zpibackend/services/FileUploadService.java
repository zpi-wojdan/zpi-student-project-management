package pwr.zpibackend.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import pwr.zpibackend.utils.ImportStudents;

@Service
public class FileUploadService {

    public void processStudentFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);

        ImportStudents.processFile(tempFile.getAbsolutePath());
    }

    public void processEmployeeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        System.out.println("File is not empty");
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
    }

}
