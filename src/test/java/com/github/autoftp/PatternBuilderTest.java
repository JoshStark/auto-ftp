package com.github.autoftp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class PatternBuilderTest {

	private PatternBuilder patternBuilder = new PatternBuilder();

	@Test
	public void builderShouldTurnQuestionMarksIntoSingleCharacterRegexMatcher() {

		String filter = "This?is?a filter";
		String expected = "This.{1}is.{1}a filter";

		assertThat(patternBuilder.buildFromFilterString(filter), is(equalTo(expected)));
	}

	@Test
	public void builderShouldTurnAsterixesIntoManyCharacterRegexMatcher() {

		String filter = "This*is*a filter";
		String expected = "This.+is.+a filter";

		assertThat(patternBuilder.buildFromFilterString(filter), is(equalTo(expected)));
	}

	@Test
	public void regexStringReturnedShouldBeAbleToActuallyMatchUsingRegexOperation() {

		String normalValue = "Clean Code.pdf";
		String filteredValue = "Clean?Code*";

		assertThat(normalValue.matches(patternBuilder.buildFromFilterString(filteredValue)), is(equalTo(true)));
	}

	@Test
	public void stringWithBothAsterixAndQuestionMarkShouldMatchProperly() {

		String anotherValue = "File Name with a Prefix12Then some text";
		String slightlyMoreComplicated = "File?Name*Prefix??Then some text";

		assertThat(anotherValue.matches(patternBuilder.buildFromFilterString(slightlyMoreComplicated)), is(equalTo(true)));
	}

}
