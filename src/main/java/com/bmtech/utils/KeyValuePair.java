
package com.bmtech.utils;

/**
 * a container contains key<String>-value<String>pair
 * TC has no construct method, we can only instance it by
 * the static method <code>KeyValuePair parseLine(String str)</code>
 * @see parseLine
 * @author beiming
 *
 */
public class KeyValuePair<T,V> {

	public final T key;
	public final V value;

	public KeyValuePair(T key, V value)throws IllegalArgumentException{
		if(key == null){
			throw new IllegalArgumentException("KeyValuePair can not be null");
		}
		this.key = key;
		this.value = value;
	}
	public String getKeyString(){
		if(key == null)
			return null;
		return key.toString();
	}
	public String getValueString(){
		if(value == null)
			return null;
		return value.toString();
	}
	public String toString(){
		return key+" = "+value;
	}
	/**
	 * see {@link #parseLine(String)}
	 * @param str
	 * @return
	 */
	public static KeyValuePair<String,String> parse(String str){
		return parseLine(str);
	}
	/**
	 * we can instance TC by this static method.
	 * this method will parse a line constructed as "key=value"
	 * it will first trim this line,if the line if not null
	 * @param str the line need to be parsed
	 * @return if str in good format,will return a <code>KeyValuePair</code>
	 * instance.
	 * if str is null
	 *        starts with "#"
	 *        contains no "="
	 *        startss with "="
	 * then null will returned
	 */
	public static KeyValuePair<String,String> parseLine(String str){
		if(str==null)
			return null;
		KeyValuePair<String, String> kv;
		str = str.trim();
		if (str.startsWith("#")||str.startsWith("="))
			return null;

		int pos = str.indexOf('=');
		if (pos == -1)
			return null;
		kv = new KeyValuePair<String, String>(str.substring(0, pos).trim(),str.substring(pos + 1).trim());

		return kv;
	}
	/**
	 * split line with {'\n','\t',' ',	'\r','¡¡'}. First the line will be trimed.
	 * Key and Value will also be trimed.
	 * @param str
	 * @return return null if input is null,
	 */
	public static KeyValuePair<String, String> splitWithWhiteToken(String str){
		if(str==null)
			return null;
		str = str.trim();

		int pos = -1;
		
		int len = str.length();
		for(int i = 0; i < len; i ++){
			char c=str.charAt(i);
			if(' '==c||'\n'==c||'\t'==c||'\r'==c||'¡¡'==c){
				pos = i;
				break;
			}
		}
		
		if(pos == -1){
			return new KeyValuePair<String, String>(str, null);
		}

		return new KeyValuePair<String, String>(str.substring(0, pos).trim(),
				str.substring(pos + 1).trim());
	}

}
