package com.bmtech.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.bmtech.utils.Consoler;
import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.counter.Counter;
import com.bmtech.utils.counter.NumCount;
import com.bmtech.utils.io.ConfigReader;
import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.segment.Segment;
import com.bmtech.utils.segment.TokenHandler;

public class Areas {
	public static void main(String[] arg){
		Areas as=Areas.instance;
	}
	public static final Areas instance = new Areas();
	private String []pattern = new String[]{
			"����.+","������.+","����.+", "������.+","����.+", "������.+",
			"������.+", "����.+", "����.+", "������.+","����.+","����",
			"���.+", "������", "������+", "������.+", "��������.+", "�ɹ���.+",
			"ԣ����.+", "����.+", "����.+", "����.+", "������.+", "����.+",
			"ë����.+", "����.+", "������.+", "����.+", "������","������","����",
			"������", "������", "�ɹ�.+","������.+", "������", "������.+",".��.��",
			"����.+", "������", "����", "����.+", "����", "����+", 
			"׳��.+", "׳�� ", "����.+", "ˮ��.+","׳��", "������", "���Ӷ���.+",
			"����.+", "����","����", "����", "������.+"
	};
	public final HashMap<String, Area>countryMap = new HashMap<String, Area>();
	public final HashMap<String, Area>cityMap = new HashMap<String, Area>();
	public final HashMap<String, Area>provinceMap = new HashMap<String, Area>();
	public final HashMap<Integer, Area>idMap = new HashMap<Integer, Area>();
	public static final int UNKOWN = 2703;//δ����
	public static final int PROVINCE = 1;
	public static final int CITY = 2;
	public static final int COUNTRY = 3;
	public final AreaSegment segment = new AreaSegment();

	public Area getArea(int code) {
		return idMap.get(code);
	}
	public class AreaSegment extends Segment{
		private HashMap<String, Short> map;
		private HashMap<String, Short> mapReverse;
		private AreaSegment() {
			map = new HashMap<String, Short>();
			mapReverse = new HashMap<String, Short>();
			load("config/segment/area_lexicon", map, false, 4);
			load("config/segment/area_lexicon", mapReverse, true, 4);
		}
		private void addItem(String item) {
			addItem(map, item, false, 4);
			addItem(mapReverse, item, false, 4);
		}
		public TokenHandler segment(String source){
			TokenHandler th = Segment.segment(source, map, false);
			TokenHandler th2 = Segment.segment(source, mapReverse, true);
			th.combine(th2);
			return th;
		}
		public TokenHandler segment(String paramString, boolean paramBoolean)
		{
			TokenHandler localTokenHandler;
			if (paramBoolean)
				localTokenHandler = Segment.segment(paramString, this.mapReverse, paramBoolean);
			else
				localTokenHandler = Segment.segment(paramString, this.map, paramBoolean);
			return localTokenHandler;
		}
	}
	/**
	 * first check if it is a province, then city then country
	 * @param key
	 * @return
	 */
	public Area getAreaByName(String key) {
		Area a = this.provinceMap.get(key);
		if(a == null) {
			a = this.getCityByName(key);
		}
		if(a == null)
			a = this.getCountryByName(key);
		return a;
	}
	public class Area{
		public int type ;
		public Area par;
		public String name;
		public int id;
		int parId;
		Area capital;
		//		ArrayList<Area>child = new ArrayList<Area>();
		Area(int id, String name, int parId){
			this.id = id;
			this.parId = parId;
			this.name = name;
		}

		public String toString(){
			StringBuilder sb = new StringBuilder();
			if(type == PROVINCE){
				sb.append("ʡ:");
			}else if(type == CITY){
				sb.append("������");
			}else{
				sb.append("��:");
			}
			sb.append(name);
			return sb.toString();
		}
		public boolean isParOf(Area a) {
			if(a == null)
				return false;
			if(a == this) {
				return true;
			}
			if(a.par == this) {
				return true;
			}
			if(a.par != null) {
				if(a.par.par == this) {
					return true;
				}
			}

			return false;
		}
	}

	private Areas(){
		try {
			String str="F:/qianlima_test/LuceneTest/WebRoot/config/areas/areas";
			//LineReader lr = new LineReader("./config/areas/areas","utf8");
			LineReader lr = new LineReader(str,"utf8");
			while(lr.hasNext()){
				String line = lr.next().trim();
				if(line.length() == 0){
					continue;
				}
				String[]seg = line.split(",");//1 , ���� , 0
				String sId = seg[0].trim();//1
				String rawName = seg[1].trim();//���� 
				String strParId = seg[2].trim();//0
				int id = Integer.parseInt(sId);//area Id
				int parId = Integer.parseInt(strParId);// area parentId
				String nameOrg = rawName;
				String name = nameOrg;
				if(rawName.length() > 5){//��area.name�а���pattern�е����Ĺ��˵�
					for(String s : pattern){
						if(name.length() == 0){
							name = nameOrg;
							break;
						}
						name = name.replaceAll(s, "");
					}
				}

				Area a = new Area(id, name, parId);//��id��name��parentId����Area����
				this.idMap.put(id, a);
			}
			loadCapital();

		} catch (Exception e) {
			BmtLogger.instance().log(e, "loading areas failure");
			e.printStackTrace();
		}
		this.countryMap.remove("����");
		Area bj = this.provinceMap.get("����");
		this.provinceMap.put("�׶�", bj);
		this.segment.addItem("�׶�");
	}
	/**
	 * ����ʡ��
	 */
	private void loadCapital() {
		Collection<Area> col = idMap.values();
		for(Area a : col){
			if(UNKOWN == a.id)//δ�������
				continue;
				segment.addItem(a.name);
			if(a.parId == 0) {//�ϼ�id=0��ʡ����
				a.type = PROVINCE;
				a.par = null;
				this.provinceMap.put(a.name, a);//����ʡmap
				if(a.name.startsWith("����")) {
					this.provinceMap.put("����", a);
					segment.addItem("����");
				}

				if(a.name.endsWith("��") || a.name.endsWith("ʡ")){
					String str = a.name.substring(0, a.name.length() -1);
					this.provinceMap.put(str, a);
					segment.addItem(str);

				}else {
					this.provinceMap.put(a.name + "ʡ", a);
					segment.addItem(a.name + "ʡ");
				}
				continue;
			}
			Area t = idMap.get(a.parId);
			a.par = t;
			if(t.parId == 0) {
				a.type = CITY;
				this.cityMap.put(a.name, a);
				if(a.name.length()> 2 && (a.name.endsWith("��") || a.name.endsWith("��")|| a.name.endsWith("��")|| a.name.endsWith("����"))){
					if(a.name.endsWith("����")) {
						String tmp = a.name.substring(0, a.name.length() - 2);
						this.cityMap.put(tmp, a);
						segment.addItem(tmp);
					}else {
						String tmp = a.name.substring(0, a.name.length() - 1);
						this.cityMap.put(tmp, a);
						segment.addItem(tmp);
					}
				}else {
					this.cityMap.put(a.name + "��", a);
					segment.addItem(a.name + "��");
				}
				continue;
			} 

			a.type = COUNTRY;
			this.countryMap.put(a.name, a);
			if(a.name.length()> 2 && (a.name.endsWith("��") ||a.name.endsWith("��")|| a.name.endsWith("��")|| a.name.endsWith("����"))){
				if(a.name.endsWith("����")) {
					String tmp = a.name.substring(0, a.name.length() - 2);
					this.countryMap.put(tmp, a);
					segment.addItem(tmp);
				}else {
					String tmp = a.name.substring(0, a.name.length() - 1);
					this.countryMap.put(tmp, a);
					segment.addItem(tmp);
				}
			}
		}
		try{
			LineReader lr = new LineReader("config/areas/alias", Charsets.UTF8_CS);
			while(lr.hasNext()){
				String line = lr.next();
				line = line.trim();
				if(line.length() == 0)
					continue;
				if(line.startsWith("#"))
					continue;
				String[]strs = line.split(" ");
				if(strs.length == 3){
					try{
						int id = Integer.parseInt(strs[0]);
						for(String name1 : strs){
							name1 = name1.trim();
							if(name1.length() > 1){
								Area hasDefined = this.getAreaByName(name1);
								if(null == hasDefined){
									Area a = this.getArea(id);
									if(a.type == PROVINCE){
										this.provinceMap.put(name1, a);
										segment.addItem(name1);
									}else if(a.type == CITY){
										this.cityMap.put(name1, a);
										segment.addItem(name1);
									}else if(a.type == COUNTRY){
										this.countryMap.put(name1, a);
										segment.addItem(name1);
									}else{
										System.out.println("unknown type:" + a.type);
									}
									
								}else{
									BmtLogger.instance().log(LogLevel.Fine, "already has define %s" , hasDefined);
								}
							}

						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		addCapital();
	}

	private void addCapital() {
		ConfigReader cr = new ConfigReader("./config/areas/capital.conf", 
				"main", "utf8");
		ArrayList<KeyValuePair<String, String>>  lst = cr.getAllConfig();
		for(KeyValuePair<String, String> pair : lst) {
			Area prv = this.provinceMap.get(pair.key);
			Area city = this.getCityByName(pair.value);
			prv.capital = city;
		}
		Collection<Area> areas = this.countryMap.values();
		for(Area a : areas) {
			if(a.name.startsWith(a.par.name)) {
				a.par.capital = a;
			}
		}
	}
	public final int InArea = 4;
	public final int OutArea = 1;
	public final int AllMatch = 4;
	public final int HalfMatch = 2;
	public final int TitleMatch = 4;
	public final int contentMatch = 1;
	public final int configBoost = 1;
	private int simScore(Area par, Area child) {
		if(child == null)
			return 0;
		if(par == null) {
			return OutArea;
		}else {
			if(par.isParOf(child))
				return this.InArea;
			else return this.OutArea;
		}
	}
	class CounterGroup{
		Counter<Area> cntCountry = new Counter<Area>();
		Counter<Area> cntCity = new Counter<Area>();
		Counter<Area> cntProvince = new Counter<Area>();
	}
	public List<Entry<Area, NumCount>> getAreas(final Area par, 
			String title, String content){
		CounterGroup grp = new CounterGroup();
		if(par != null) {
			switch(par.type) {
			case COUNTRY:
				grp.cntCountry.count(par, InArea * AllMatch * TitleMatch);
				break;
			case CITY:
				grp.cntCity.count(par, InArea * AllMatch * TitleMatch);
				break;
			case PROVINCE:
				grp.cntProvince.count(par, InArea * AllMatch * TitleMatch);
				break;
			}
		}
		if(title !=null) {
			title = title.replace("ʾ����", "");
			title = title.replace("��Ǩ����", "");
		}
		if(content !=null) {
			content = content.replace("ʾ����", "");
			content = content.replace("��Ǩ����", "");
		}
		getAreas(par, grp, title, TitleMatch);
		getAreas(par, grp, content, contentMatch);
		return guessAreas(grp.cntCountry.topEntry(5), 
				grp.cntCity.topEntry(5), 
				grp.cntProvince.topEntry(5));

	}
	private void getAreas(final Area par, CounterGroup grp,
			String input, final int baseScore){
		if(input == null)
			return;
		TokenHandler th = segment.segment(input);
		String str;

		while(th.hasNext()) {
			str = th.next();
			if(str.length() < 2)
				continue;
			Area p = this.getProvinceByName(str);
			int score = 0;
			if(p != null) {
				if(str.endsWith("ʡ") || str.endsWith("��") ) {
					score = AllMatch;
				}else {
					score = HalfMatch;
				}
				grp.cntProvince.count(p, 
						score * baseScore * simScore(par, p));
			}

			Area c = this.getCityByName(str);
			if(c != null) {
				if(p != null && (c.par == p)) {

				}else {
					if(str.endsWith("��")) {
						score = AllMatch;
					}else {
						score = HalfMatch;	
					}
					grp.cntCity.count(c, 
							score * baseScore * simScore(par, c));
				}
			}

			Area ct = this.getCountryByName(str);
			if(ct != null) {
				if((p != null && c != null && (c.par == p))
						|| (c != null && (ct.par == c))) {

				}else {
					if(str.endsWith("��") || str.endsWith("��")) {
						score = AllMatch;
					}else {
						score =  HalfMatch;	
					}
					grp.cntCountry.count(ct, 
							score * baseScore * simScore(par, ct));
				}
			}

		}
	}

	private List<Entry<Area, NumCount>> guessAreas(
			List<Entry<Area, NumCount>> country,
			List<Entry<Area, NumCount>> city,
			List<Entry<Area, NumCount>> province){

		Counter<Area>cnter = new Counter<Area>();
		Set<Area>set = new HashSet<Area>();
		for(Entry<Area, NumCount> e : country) {
			Area a = e.getKey();
			Area par = a.par;
			cnter.count(a, e.getValue());
			for(Entry<Area, NumCount> ee : city) {
				if(par == ee.getKey()) {
					cnter.count(a, ee.getValue());
					set.add(ee.getKey());
					break;
				}
			}

			par = par.par;
			for(Entry<Area, NumCount> ee : province) {
				if(par == ee.getKey()) {
					cnter.count(a, ee.getValue());;
					set.add(ee.getKey());
					break;
				}
			}
		}
		try {
			for(Entry<Area, NumCount> e : city) {
				if(set.contains(e.getKey())) {
					continue;
				}
				Area a = e.getKey();
				Area par = a.par;
				cnter.count(a, e.getValue());
				for(Entry<Area, NumCount> ee : province) {
					if(par == ee.getKey()) {
						cnter.count(a, ee.getValue());;
						set.add(ee.getKey());
						break;
					}
				}
			}
		}catch(Exception eee) {
			eee.printStackTrace();
		}
		try {
			for(Entry<Area, NumCount> e : city) {
				if(set.contains(e.getKey())) {
					continue;
				}
				Area a = e.getKey();
				if(a.capital == null) {
					cnter.count(a, e.getValue());
					continue;
				}
				cnter.count(a.capital, e.getValue());
			}
			for(Entry<Area, NumCount> e : province) {
				if(set.contains(e.getKey())) {
					continue;
				}
				Area a = e.getKey();

				if(a.capital == null) {
					cnter.count(a, e.getValue());
					continue;
				}
				if(a.capital.capital == null) {
					cnter.count(a, e.getValue());
					continue;
				}
				cnter.count(a.capital.capital, e.getValue());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return cnter.topEntry(5);
	}

	public int getAreaCode(String key, String title, String content) {
		Area a;
		if(key != null) {
			key = key.trim();
			if(key.length() ==0)
				key = null;
		}
		if(key == null) {
			a = null;
		}else {
			a = this.getProvinceByName(key);
			if(a == null) {
				a = this.getCityByName(key);
			}
			if(a == null) {
				a = this.getCountryByName(key);
			}
		}
		List<Entry<Area, NumCount>> lst = getAreas(a, title, content);
		if(lst.size() == 0) {
			if(title.contains("����") || title.contains("�й�")
					|| title.contains("��")|| title.contains("����")) {

			}
			return UNKOWN;
		}
		return lst.get(0).getKey().id;
	}

	/**
	 * get the Area by a name(may be an alias)
	 * @param str
	 * @return
	 */
	public Area getCountryByName(String str){
		if(str == null)
			return null;
		str = str.trim(); 
		return this.countryMap.get(str);
	}
	public Area getCityByName(String str){
		if(str == null)
			return null;
		str = str.trim(); 
		return this.cityMap.get(str);
	}
	public Area getProvinceByName(String str){
		if(str == null)
			return null;
		str = str.trim(); 
		return this.provinceMap.get(str);
	}

	/**
	 * test whether it works
	 */
	public static void test() {
		while(true){
			String s = Consoler.readString(":");
			Area a = instance.getCountryByName(s);
			System.out.println(s + ":" + a);
		}
	}
	/**
	 * ��area��nameȥ����/��/ʡ
	 * @param a
	 * @return
	 */
	public static String aliaName(Area a) {
		if(a == null) {
			return null;
		}
		if(a.name.length() > 2) {//
			if( a.name.endsWith("��") ||a.name.endsWith("��")||a.name.endsWith("ʡ")) {
				return a.name.substring(0,a.name.length() - 1);
			}
		}
		return null;
	}
	private static HashMap<Integer,List<Area>>childs = new HashMap<Integer,List<Area>>();
	public synchronized static List<Area> getChilds(int areaId){
		if(childs.size() == 0) {
			Iterator<Area> itr = Areas.instance.idMap.values().iterator();
			while(itr.hasNext()) {
				Area a = itr.next();
				if(a.parId > 0) {
					List<Area> lst = childs.get(a.parId);
					if(lst == null) {
						lst = new ArrayList<Area>();
						childs.put(a.parId, lst);
					}
					lst.add(a);
				}
			}
			itr = Areas.instance.idMap.values().iterator();
			while(itr.hasNext()) {
				Area a = itr.next();
				if(a.parId != 0) {
					continue;
				}
				List<Area> lst = childs.get(a.id);
				List<Area> toAdd = new ArrayList<Area> ();
				for(Area asub : lst) {
					List<Area>subList = childs.get(asub.id);
					if(subList == null) {
						System.out.println(asub);
						continue;
					}
					toAdd.addAll(subList);
				}
				lst.addAll(toAdd);
			}
		}
		return childs.get(areaId);
	}

}
