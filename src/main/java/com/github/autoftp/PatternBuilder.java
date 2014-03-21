package com.github.autoftp;

public class PatternBuilder {

	public String buildFromFilterString(String filter) {
		return filter.replaceAll("\\?", ".{1}").replaceAll("\\*", ".+");
	}
}
