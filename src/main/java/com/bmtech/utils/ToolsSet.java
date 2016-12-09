package com.bmtech.utils;

import java.io.File;

public class ToolsSet {
	/**
	 * remove file, if it is a directory, first remove all of this children
	 * @param file
	 */
	public static void removeFile(File file){
		if(file.isDirectory()){
			File[]fs=file.listFiles();
			for(int i=0;i<fs.length;i++){
				removeFile(fs[i]);
			}
		}
		file.delete();
	}
}
