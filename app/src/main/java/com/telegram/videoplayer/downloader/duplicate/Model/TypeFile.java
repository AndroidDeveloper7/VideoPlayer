package com.telegram.videoplayer.downloader.duplicate.Model;

public class TypeFile {

    public static int getType(String str) {
        if (str.endsWith(".apk")) {
            return 4;
        }
        if (str.endsWith(".zip")) {
            return 6;
        }
        if (str.endsWith(".vcf")) {
            return 5;
        }
        if (str.endsWith(".mp3") || str.endsWith(".aac") || str.endsWith(".amr") || str.endsWith(".m4a") || str.endsWith(".ogg") || str.endsWith(".wav") || str.endsWith(".flac")) {
            return 1;
        }
        if (str.endsWith(".3gp") || str.endsWith(".mp4") || str.endsWith(".mkv") || str.endsWith(".webm")) {
            return 2;
        }
        if (str.endsWith(".doc") || str.endsWith(".docx") || str.endsWith(".html") || str.endsWith(".txt") || str.endsWith(".xml") || str.endsWith(".xlsx")) {
            return 3;
        }
        if (str.endsWith(".jpg") || str.endsWith(".jpeg") || str.endsWith(".png") || str.endsWith(".bmp") || str.endsWith(".gif")) {
            return 0;
        }
        return str.endsWith(".pdf") ? 7 : 8;
    }
}
