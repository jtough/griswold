package com.jimtough.griswold;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * @deprecated TO BE DELETED... Replaced with MovieQuotesMessageSource
 * @author JTOUGH
 */
@Deprecated
public class MovieQuoteCycler {

	private static final Logger logger =
			LoggerFactory.getLogger(MovieQuoteCycler.class);
	
	private static final String MOVIE_QUOTES_RESOURCE_FILE = "/movie-quotes.txt";
	
	private List<String> movieQuoteList = new ArrayList<String>();
	private int currentQuoteIndex = 0;
	private final ReadOnlyStringWrapper currentMovieQuote;
	
	
	public MovieQuoteCycler(
			final ReadOnlyStringWrapper currentMovieQuote) 
			throws IOException {
		if (currentMovieQuote == null) {
			throw new IllegalArgumentException(
					"currentMovieQuote cannot be null");
		}
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
		this.currentMovieQuote = currentMovieQuote;
		cycleToNextQuote();
	}

	public synchronized void cycleToNextQuote() {
		if (currentQuoteIndex >= movieQuoteList.size()) {
			currentQuoteIndex = 0;
		}
		String nextQuote = movieQuoteList.get(currentQuoteIndex);
		currentQuoteIndex++;
		currentMovieQuote.setValue(nextQuote);
		logger.debug("Cycled to next quote: " + nextQuote);
	}

	public ReadOnlyStringProperty currentMovieQuoteProperty() {
		return currentMovieQuote.getReadOnlyProperty();
	}
	
}
