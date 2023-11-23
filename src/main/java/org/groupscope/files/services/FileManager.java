package org.groupscope.files.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileManager {

    String createFolder(String path) throws IOException;

    MultipartFile uploadFile(String path, MultipartFile file) throws IOException;

    File findFile(String path, String name);

    List<File> findFilesByPath(String path);

    public List<byte[]> downloadFile(List<File> files) throws IOException;

    void deleteFile(String path, String name);

    void deleteFolder(String path) throws IOException;
}
