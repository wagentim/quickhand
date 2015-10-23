package cn.wagentim.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.wagentim.connection.GetPageContent;

public final class Parser
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
				processFile(file, new ContentHandler());
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
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if( qName.equals("site") )
			{
				// new site start
				logger.error("process the site...");
			}
		}
	}
}
