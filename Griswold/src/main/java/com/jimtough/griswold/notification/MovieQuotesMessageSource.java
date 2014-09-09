package com.jimtough.griswold.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieQuotesMessageSource implements NotificationMessageSource {

	private static final Logger logger =
			LoggerFactory.getLogger(MovieQuotesMessageSource.class);

	private static final String MOVIE_QUOTES_RESOURCE_FILE = "/movie-quotes.txt";
	
	private List<String> movieQuoteList = new ArrayList<String>();
	private int currentQuoteIndex = 0;
	
	public MovieQuotesMessageSource() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(
				MOVIE_QUOTES_RESOURCE_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String currentQuote;
		while ((currentQuote = br.readLine()) != null) {
			movieQuoteList.add(currentQuote);
		}
		if (movieQuoteList.isEmpty()) {
			throw new IllegalStateException("No movie quotes loaded");
		}
	}
	
	@Override
	public synchronized NotificationMessage getMessage() {
		if (currentQuoteIndex >= movieQuoteList.size()) {
			currentQuoteIndex = 0;
		}
		String nextQuote = movieQuoteList.get(currentQuoteIndex);
		currentQuoteIndex++;
		logger.debug("Returning next movie quote: " + nextQuote);
		return new NotificationMessage(nextQuote, NotificationImportance.TRIVIAL);
	}

}
