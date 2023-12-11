package ru.search.web.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.OperationSearchElement;
import ru.search.web.web.elements.SearchToken;
import ru.search.web.web.elements.operator.AndSearchOperator;
import ru.search.web.web.elements.operator.OrSearchOperator;

public class SearchTests 
{
    @Test
	public void tokenTest()
	{
		SearchToken token = new SearchToken("машину");
		assertTrue(token.test("МашиНу"));
		assertTrue(token.test("Я купил машину"));
		assertTrue(token.test("Я купил машину в кредит"));
		assertTrue(!token.test("Я купил хлеб"));
		assertTrue(token.test("Машину вчера угнали"));

		SearchToken token2 = new SearchToken("2.25");
		assertTrue(token2.test("2.25"));
		assertTrue(token2.test("2.25."));
		assertTrue(token2.test("-2.25-"));
		assertTrue(token2.test("2.25%"));
		assertTrue(token2.test("[2.25]"));
		assertTrue(token2.test("Ваш рекорд: 2.25 с."));
	}

	@Test
	public void orSearchTest()
	{
		OperationSearchElement or = new OperationSearchElement()
			.setOperator(OrSearchOperator.get())
			.add("машину")
			.add("кредит");

		assertTrue(or.test("МашиНу"));
		assertTrue(or.test("кРедИТ"));
		assertTrue(or.test("Я купил машину"));
		assertTrue(or.test("Я купил машину в кредит"));
		assertTrue(!or.test("Я купил хлеб"));
		assertTrue(or.test("Машину вчера угнали"));
		assertTrue(or.test("Я оформил кредит"));
		assertTrue(or.test("Кредит оплачен"));

        or.add("оформил", true);
        assertTrue(or.test("МашиНу"));
		assertTrue(or.test("кРедИТ"));
        assertTrue(!or.test("ОфоРМил"));
		assertTrue(or.test("Я купил машину"));
		assertTrue(or.test("Я купил машину в кредит"));
		assertTrue(!or.test("Я купил хлеб"));
		assertTrue(or.test("Машину вчера угнали"));
		assertTrue(!or.test("Я оформил кредит"));
		assertTrue(or.test("Кредит оплачен"));
	}

    @Test
    public void andSearchTest()
    {
        OperationSearchElement and = new OperationSearchElement().setOperator(AndSearchOperator.get()).add("машину").add("кредит");
        assertTrue(!and.test("МашиНу"));
		assertTrue(!and.test("кРедИТ"));
        assertTrue(and.test("МашИнУ ---+!; кРЕДИТ"));
		assertTrue(!and.test("Я купил машину"));
		assertTrue(and.test("Я купил машину в кредит"));
		assertTrue(!and.test("Я купил хлеб"));
		assertTrue(!and.test("Машину вчера угнали"));
		assertTrue(and.test("Я оформил кредит на машину"));
		assertTrue(!and.test("Кредит оплачен"));

        and.add("оформил", true);
        assertTrue(!and.test("МашиНу"));
		assertTrue(!and.test("кРедИТ"));
        assertTrue(and.test("МашИнУ ---+!; кРЕДИТ"));
        assertTrue(!and.test("машину КредИт оФорМИл"));
		assertTrue(!and.test("Я купил машину"));
		assertTrue(and.test("Я купил машину в кредит"));
		assertTrue(!and.test("Я купил хлеб"));
		assertTrue(!and.test("Машину вчера угнали"));
		assertTrue(!and.test("Я оформил кредит на машину"));
		assertTrue(!and.test("Кредит оплачен"));
    }

	@Test
	public void tokenReader()
	{
		for (QueryData query : QueryData.ALL)
		{
			TokenReader reader = new TokenReader(query.text);
			
			for (String token : query.tokens)
			{
				assertEquals(token, reader.nextToken());
			}
		}
	}

	@Test
	public void searchParserTest()
	{
		for (QueryData query : QueryData.ALL)
		{
			ISearchElement element = SearchParser.parse(query.text);
			assertEquals(query.parseResult, element.toString());
		}
	}
}
