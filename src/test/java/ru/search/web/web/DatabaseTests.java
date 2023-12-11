package ru.search.web.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.Database;
import ru.search.web.web.database.PageDocument;
import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.TokenDocumentIterator;

class DatabaseTests
{
	private static final int RESULT_COUNT = 500;

	private static final String DB_URL = "mongodb://localhost:27017";
	private static final String DB_NAME = "Search";
	
	private static final Database DATABASE = new Database(DB_URL, DB_NAME);
	private static MongoCollection<TokenDocument> TOKENS;
	private static MongoCollection<PageDocument> PAGES;

	@BeforeAll
	public static void setup() 
	{
		DATABASE.connect();
		TOKENS = DATABASE.openCollection("Tokens", TokenDocument.class);
		PAGES = DATABASE.openCollection("Documents", PageDocument.class);
	}

	@Test
	public void queryTest()
	{
		for (QueryData query : QueryData.ALL)
		{
			ISearchElement element = SearchParser.parse(query.text);
			assertEquals(query.text + "-0", query.text + "-" + checkQuery(element));
		}
	}

	private int checkQuery(ISearchElement element)
	{
		TokenDocumentIterator queryResult = new TokenDocumentIterator(
			element.getDocuments(TOKENS)
		);

		List<PageDocument> pages = queryResult.getDocuments(PAGES, RESULT_COUNT);

		if (pages.size() == 0)
		{
			return 2;
		}

		for (PageDocument page : pages)
		{
			if (!element.test(page.text()))
			{
				return 1;
			}
		}

		return 0;
	}

	@AfterAll
	public static void close()
	{
		DATABASE.close();
	}
}
