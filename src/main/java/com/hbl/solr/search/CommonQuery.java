package com.hbl.solr.search;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.hbl.solr.config.Config;
import com.hbl.solr.search.bean.QueryResult;

/**
 * 搜索类
 * @author Administrator
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonQuery {

	public static final Logger log = Logger.getLogger(CommonQuery.class);

	/**
	 * 查询条件
	 */
	private SolrQuery solrQuery;

	/**
	 * 服务器
	 */
	protected HttpSolrClient server;
	
	public CommonQuery(String urlPropertyName)   {
		solrQuery = new SolrQuery();
		server = new HttpSolrClient(Config.getString(urlPropertyName));
	}
	
	/**
	 * 通用查询
	 * @param query
	 * @param pageNo
	 * @param pagesize
	 * @return
	 * @throws QueryException
	 */
	public QueryResult queryDataCommon(String query,int pageNo,int pagesize) throws Exception {
		return queryDataCommon(query,pageNo,pagesize,null,false);
	}
	
	/**通用查询**/
	public QueryResult queryDataCommon(String query,int pageNo,int pagesize,String sortfield,boolean asc) throws Exception {
		return  queryDataCommon(query,pageNo,pagesize,sortfield,asc,null);
	}
	
	/**通用查询**/
	public QueryResult queryDataCommon(String query, int pageNo, int pagesize, String sortfield, boolean asc, 
			String keyword, String... fq) throws Exception {

		if (solrQuery == null)
			throw new Exception("query查询条件不能为空！");

		QueryResult result = new QueryResult();

		try {
			// 设置默认查询content
			solrQuery.setQuery(query);
			log.info("begin query,not pagination ");
			solrQuery.setStart(pageNo);//s
			solrQuery.setRows(pagesize);//n
			if(sortfield!=null&&sortfield.length()>0){
				solrQuery.setSort(sortfield, asc ? SolrQuery.ORDER.asc
						 : SolrQuery.ORDER.desc);
			}
			if(fq!=null&&fq.length>0){
				solrQuery.setFilterQueries(fq);
			}
			
			if(keyword!=null&&keyword.length()>0&&!keyword.equals("*")&&keyword!="*"){
				//加入高亮设置
				solrQuery.setParam("hl", true);
				solrQuery.setHighlightSimplePost("</font>");
				solrQuery.setHighlightSimplePre("<font color='#ff0000'>");
				solrQuery.setHighlightFragsize(100);
				solrQuery.addHighlightField("articleTitle");
				solrQuery.setHighlightSnippets(5);
				solrQuery.setParam("hl.", "articleTitle");
			}
			log.info("query condition：" + solrQuery.toString());
			
			QueryResponse response = server.query(solrQuery);
			
			SolrDocumentList docs = response.getResults();
			log.info("total docs ：" + docs.getNumFound());
			log.info("query times：" + response.getQTime() / 1000);
			
			
			Map<String, Map<String, List<String>>> map = response
					.getHighlighting();
			
			if(map!=null){
				for (SolrDocument doc : docs) {
					String id =doc.getFieldValue("id").toString();
					if (map.containsKey(id)) {
						if(map.get(id).containsKey("articleTitle"))
							doc.setField("articleTitle", map.get(id).get("articleTitle").get(0));
						
					}
				}
			}
			result.setHighlighting(map);
			result.setResultlist(docs);
			result.setTotalrecord(docs.getNumFound());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
}
