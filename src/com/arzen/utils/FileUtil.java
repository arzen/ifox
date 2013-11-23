package com.arzen.utils;

import java.io.File;

public class FileUtil {
	
	public static boolean isFileExit(String path){
        if(path == null){
            return false;
        }
        try{
            File f = new File(path);
            if(!f.exists()){
                return false;
            }
        }catch (Exception e) {
            // TODO: handle exception
        }
        return true;
    }
}
