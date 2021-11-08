package com.jan.web.fileservice;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LocalFileService implements FileService
{
    public static final String BASE_DIRECTORY = "/Users/jan/app_files/";

    @Override
    public List<FileInformation> getAllFiles()
    {
        List<FileInformation> fileInformationList = null;
        try (Stream<Path> walk = Files.walk(Paths.get(BASE_DIRECTORY)))
        {
            fileInformationList = walk.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(file -> new FileInformation(
                            file.getAbsolutePath().substring(BASE_DIRECTORY.length()),
                            file.length(),
                            file.lastModified())).collect(Collectors.toList());
        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
        System.out.println(fileInformationList.get(0).getKey());
        return fileInformationList;
    }
}
