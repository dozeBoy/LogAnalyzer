package com.saguaro.LogAnalyzer;

import com.saguaro.LogAnalyzer.Analyzer.ArgumentsException;

public class Outputer {
	public static void printHelp(StringBuffer sb) throws ArgumentsException {
		sb.append("No command found!").append(System.lineSeparator());
		sb.append("Usage: ").append(System.lineSeparator());
		sb.append("Search one or more words: --find text1 text2").append(
				System.lineSeparator());
		sb.append(
				"If you want to search by regular expression, you need to add the command --regEx: --find string1 string2 stringN [--regEx]")
				.append(System.lineSeparator());
		sb.append("Extracting the activity for a thread: --thread 2378267463")
				.append(System.lineSeparator());
		sb.append("For exceptions' stack trace: --exceptions ").append(
				System.lineSeparator());
		sb.append("--jobId 10000003").append(System.lineSeparator());
		sb.append("--clearDate").append(System.lineSeparator());
		sb.append("--erase string1 string2 stringN").append(
				System.lineSeparator());
		sb.append(
				"if you want only certain file to be processed, you can specify the desire extension --fileExt .xml")
				.append(System.lineSeparator());
		sb.append(
				"You can use --takeNextLines 4 or --takePrevLines 6 along with other commands in order to take more than the current line")
				.append(System.lineSeparator());
		throw new ArgumentsException(sb.toString());
	}

	static class ArgumentsException extends Throwable {
		private String message = "";

		public ArgumentsException(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	public static void validateArgs() throws ArgumentsException {
		StringBuffer sb = new StringBuffer();
		if (Analyzer.is_notACommand()) {
			printHelp(sb);
		}
	}
}
