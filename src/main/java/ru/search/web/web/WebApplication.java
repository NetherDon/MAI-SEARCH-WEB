package ru.search.web.web;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.mongodb.MongoException;

import ru.search.web.web.database.Database;
import ru.search.web.web.database.PageDocument;
import ru.search.web.web.database.TokenDocument;

@SpringBootApplication
public class WebApplication 
{
	private static final String DB_URL = "mongodb://localhost:27017";
	private static final String DB_NAME = "Search";
	
	public static final Database DATABASE = new Database(DB_URL, DB_NAME);

	public static void main(String[] args) 
	{
		ConfigurableApplicationContext ctx = SpringApplication.run(WebApplication.class, args);
		ctx.addApplicationListener((ApplicationListener<ContextClosedEvent>) WebApplication::onClosed);
	}

	@Component
	public static class ApplicationStartup implements ApplicationRunner
	{

		@Override
		public void run(ApplicationArguments args) throws Exception 
		{
			try
			{
				DATABASE.connect();
				DATABASE.openDefaultCollections();
			}
			catch (MongoException exception)
			{
				return;
			}
		}
		
	}

	public static void onClosed(ContextClosedEvent event)
	{
		DATABASE.close();
		System.out.println("DB session closed");
	}
}
