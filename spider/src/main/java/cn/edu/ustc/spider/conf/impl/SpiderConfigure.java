package cn.edu.ustc.spider.conf.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.edu.ustc.spider.conf.inf.ISpiderConfigure;
import cn.edu.ustc.spider.conf.inf.IWebSite;
import cn.edu.ustc.spider.conf.inf.IWebSiteFactory;

public class SpiderConfigure implements ISpiderConfigure{
	private Document document=null;
	private List<IWebSite> websites=null;
	private IWebSiteFactory factory=null;
	
	public SpiderConfigure(String confPath,IWebSiteFactory factory)
	{
		this.websites=new LinkedList<>();
		setConfigureFile(confPath, factory);
	}
	
	@Override
	public void setConfigureFile(String confPath,IWebSiteFactory factory) {
		this.factory=factory;
		Logger logger=Logger.getLogger(getClass());
		SAXReader reader=new SAXReader();
		try {
			document=reader.read(new File(confPath));
		} catch (DocumentException e) {
			logger.error(e);
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IWebSite> getWebSites() {
		List<Element> siteList=document.selectNodes("/spider/websites/site");
		for(Element e:siteList)
		{
			IWebSite site=factory.createWebSite(e.attributeValue("name"));
			site.setPriority(Integer.valueOf(e.attributeValue("priority")));
			site.setSearchUrl(e.elementText("search-url"));
			site.setPageParams(e.elementText("page_param"));
			Element job=(Element)e.selectSingleNode("./result_list/info[@name='job']");
			site.setJobAt(job.getText());
			Element company=(Element)e.selectSingleNode("./result_list/info[@name='company']");
			site.setCompanyAt(company.getText());
			this.websites.add(site);
		}
		return this.websites;
	}

	@Override
	public void refresh() {
		
	}

}
