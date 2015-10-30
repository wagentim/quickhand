package cn.wagentim.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.wagentim.connection.GetPageContent;

public final class Parser implements IParserConstants
{
	private final Logger logger = LogManager.getLogger(GetPageContent.class);
	
	public void run(final List<File> files)
	{
		if( files.isEmpty() )
		{
			logger.error("The input xml file list is empty!!");
			return;
		}
		
		for( int i = 0; i < files.size(); i++ )
		{
			File file = files.get(i);
			
			if( null == file )
			{
				logger.error("The input file is NULL with the index: " + i);
				continue;
			}
			
			try
			{
				GetPageContent pageGetter = new GetPageContent();
				
				processFile(file, new ContentHandler(pageGetter) );
			}
			catch (ParserConfigurationException | SAXException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void processFile(File file, DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException
	{
		logger.error("process the file: " + file.getName());
		
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactor.newSAXParser();
	    parser.parse(file, handler);
	}
	
	private class ContentHandler extends DefaultHandler
	{
		private final GetPageContent pageGetter;
		private String pageContent;
		private List<String> urls;
		private List<String> results;
		private int currentPageLevel = 0;
		private Document doc = null;
		private Element findElement = null;
		private int returnType = -1;
		
		public ContentHandler(final GetPageContent pageGetter)
		{
			this.pageGetter = pageGetter;
			urls = new ArrayList<String>();
			results = new ArrayList<String>();
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if( qName.equals("site") )
			{
				// new site start
				logger.error("process the site...");
				pageGetter.run(localName);
				this.pageContent = pageGetter.getPageContent();
			}
			else if( qName.equals("page") )
			{
				currentPageLevel = Integer.parseInt(localName);
				doc = Jsoup.parse(pageContent);
			}
			else if( qName.equals("parser") )
			{
				Elements es = doc.select(localName);
				if( es.size() == 0 )
				{
					logger.error("Cannot find elements with the selection code: " + localName);
					return;
				}
				else
				{
					if( es.size() > 1 )
					{
						logger.error("Find more than one elements with the selection code: " + localName);
					}
					
					findElement = es.get(0);
				}
			}
			else if( qName.equals("return") )
			{
				returnType = Integer.parseInt(localName);
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if( qName.equals("block") )
			{
				if( LINK == returnType )
				{
					urls.add(findElement.text());
				}
				else if( TEXT == returnType )
				{
					results.add(findElement.text());
				}
			}
			else if( qName.equals("site") )
			{
				printResults("##############  Results  ##############", results);
				printResults("##############  Links  ##############", urls);
			}
		}

		private void printResults(String title, List<String> contents)
		{
			System.out.println(title);
			for( int i = 0;  i < contents.size(); i++ )
			{
				System.out.println(contents.get(i));
			}
		}
	}
}
