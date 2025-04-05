package com.utilty;

import com.file_handling.Document;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serializer {

    public static <T> void save (String path, T obj) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
            ObjectOutputStream out = new ObjectOutputStream(gzipOut);
            out.writeObject(obj);
            out.close();
        }
        catch (Exception e) {
            System.out.println("Unable to save to path:" + path);
        }
    }

    public static <T> T load(String path) throws Exception{
        FileInputStream fileIn = new FileInputStream(path);
        GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
        ObjectInputStream in = new ObjectInputStream(gzipIn);
        T obj = (T) in.readObject();
        in.close();
        return obj;
    }
}
