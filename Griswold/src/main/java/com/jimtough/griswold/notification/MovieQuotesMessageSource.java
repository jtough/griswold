package com.jimtough.griswold.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a list of famous movie quotes from the text file, then provides
 * them as notification messages in a sequential, repeating cycle.
 * 
 * @author JTOUGH
 */
public class MovieQuotesMessageSource implements NotificationMessageSource {

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
	public NotificationMessage offerMessage() {
		if (currentQuoteIndex >= movieQuoteList.size()) {
			currentQuoteIndex = 0;
		}
		String nextQuote = movieQuoteList.get(currentQuoteIndex);
		return new NotificationMessage(
				nextQuote, 
				NotificationImportance.TRIVIAL,
				NotificationCategory.INFO_NEUTRAL,
				NotificationIcon.COOL_STUFF);
	}

	@Override
	public void takeMessage(NotificationMessage notificationMessage) {
		currentQuoteIndex++;
	}

}
