package com.bmtech.utils.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * TC will automaticly close the file/buffer
 * @author beiming
 *
 */
public class LstFileTool {
	private String path=null;
	private String base;
	public LstFileTool(String str){
		this.base=str;
		this.path=str+"./list.lst";
		File fPath=new File(str);
		fPath.mkdirs();
		File file=new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
				file.setWritable(false);
				file=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			file.setWritable(false);
		}
	}
	/**
	 * getIndexPathList
	 * @return
	 */
	public ArrayList<String>getPathList(){		
		try {
			if(path==null)
				return null;
			File file=new File(path);
			file.setReadable(true);
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			ArrayList<String>list=new ArrayList<String>();
			String line;
			while(null!=(line=br.readLine())){
				line=line.trim();
				if(line.length()>0)
					list.add(line);
			}
			br.close();
			fr.close();			
			br=null;
			fr=null;
			file=null;
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	/**
	 * get the last index path.
	 * if not found, a new path will be created
	 * @Careful this path will not be put into list.lst
	 * @return
	 */
	public File getLastPath(){
		ArrayList<String>list=getPathList();
		if(list!=null&&list.size()>0)
			return new File(base+"./"+list.get(list.size()-1));
		return makeNewPath(base);
	}
	/**
	 * set the last Index path.
	 * @param line should be relevent path
	 */
	public void setLastPath(String line){
		if(line==null||line.trim().length()==0)
			return;
		try{
			File file=new File(path);
			file.setWritable(true);
			FileWriter fw=new FileWriter(file,true);

			fw.append(line.trim()+"\n");
			fw.flush();
			fw.close();
			fw=null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * get the basic path for all list files
	 * @return
	 */
	public String getFilePath(){
		return this.path;
	}

	public File makeTempPath(){
		return makeNewPath(base);
	}
	public static  void removeUnused(String path,int num,boolean dir) throws IOException{	
		String str=path;		
		str=str.trim();
		String currentFile=null;
		if(str!=null&&str.length()>0){
			File fl=new File(str);
			currentFile=fl.getAbsolutePath();
		}
		if(currentFile==null)
			throw new IOException("file :"+str+" Not Fountd");
		LstFileTool ipt=new LstFileTool(currentFile);


		if(num<1)
			num=1;
		ArrayList<String>lst=ipt.getPathList();
		if(lst!=null){
			for(int i=0;i<(lst.size()-num);i++){
				str=lst.get(i);
				str=str.trim();
				if(str!=null&&str.length()>0){
					File tf=new File(path+"/"+str);
					if(tf.getAbsolutePath().compareTo(currentFile)!=0){
						if(tf.exists()){
							if(dir&&tf.isDirectory()){						
								deleteFiles(tf);
							}
						}
						tf=null;
					}
				}
			}
		}
	}
	/**
	 * delete files
	 * @param f
	 */
	private static  void deleteFiles(File f){
		if(f==null||!f.exists())
			return;
		if(f.isDirectory()){
			File[]fs=f.listFiles();
			for(int i=0;i<fs.length;i++){
				deleteFiles(fs[i]);
			}
		}
		f.delete();
	}

	public static File makeNewPath(String parent){
		SimpleDateFormat sdf=new SimpleDateFormat("./yyyyMMddHHmmss.SSS");
		File file=new File(parent+sdf.format(new Date()));
		file.mkdirs();
		return file;
	}
	public static void main(String[]arg){
		makeNewPath("c:/");
	}

}
