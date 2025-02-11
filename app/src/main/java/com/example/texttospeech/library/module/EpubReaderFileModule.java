package com.example.texttospeech.library.module;

import android.content.Context;


import com.example.texttospeech.library.util.FileUtil;
import com.example.texttospeech.library.util.StringUtils;
import com.example.texttospeech.library.util.ZipUtil;

import java.io.File;
import java.io.IOException;

/**
 * Author: Hamed Taherpour
 * *
 * Created: 10/7/2020
 * *
 * Address: https://github.com/HamedTaherpour
 */
public class EpubReaderFileModule {

    private static final String META_INF_DIRECTORY = "META-INF/";
    private static final String CONTAINER_XML_FILE = "container.xml";
    private static final String CACHE_DIRECTORY = "book/";
    private String epubFilePath;
    private String unzipFilePath;
    private String otpFile;

    public EpubReaderFileModule(String epubFilePath) {
        this.epubFilePath = epubFilePath;
    }

    public void setUp(Context context) throws IOException {
        File targetDirectory = FileUtil.createFileCache(context, CACHE_DIRECTORY + StringUtils.getBaseName(epubFilePath));
        unzipFilePath = targetDirectory.getPath();
        ZipUtil.unzip(new File(epubFilePath), targetDirectory);
    }

    public void setOtpFile(String otpFile) {
        this.otpFile = otpFile;
    }

    public String getAbsolutePath() {
        return "file://" + getContentFolderPath();
    }

    public String getBaseHref(String href) {
        return getContentFolderPath() + href;
    }

    public String getOtpFilePath() {
        return getUnzipFolderPath() + otpFile;
    }

    public String getContainerFilePath() {
        return getMetaInfoFolderPath() + CONTAINER_XML_FILE;
    }

    private String getContentFolderPath() {
        return new File(getUnzipFolderPath() + otpFile).getParent() + "/";
    }

    private String getMetaInfoFolderPath() {
        return getUnzipFolderPath() + META_INF_DIRECTORY;
    }

    private String getUnzipFolderPath() {
        return unzipFilePath + "/";
    }

}
