package com.saguaro.LogAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class LogParser {

	// private static String _outputFolder = "analyzerResult";
	// private static String _fileExt = null;
	private static final String[] excludedFromSearch = { ".jar", ".zip",
			".project", ".classpath", ".jre", ".class", ".settings" };

	private static boolean _exception = false;
	private static boolean _clearDate = false;
	// private static int _takeNextLines = 0;
	// private static int _takePrevLines = 0;
	// private static String _thread = null;

	// private static boolean _regEx = false;
	private static ArrayList<String> _find = new ArrayList<String>();
	private static ArrayList<String> _erase = new ArrayList<String>();

	private static String newlineSeparator = System
			.getProperty("line.separator");

	public static void executeCommand() throws IOException {
		logCommandBefore();
		try {
			File inputF = (new File(Analyzer.get_inputFileName()))
					.getAbsoluteFile();
			String filePath = inputF.getAbsolutePath();
			if (inputF.isDirectory()) {
				LogParser.handleDirectory(filePath);
			} else {
				LogParser.handleFile(filePath);
			}
		} catch (IOException e) {
			System.out.println("error : " + e.getMessage());
			e.printStackTrace();
		}
		logCommandAfter();
	}

	private static void logCommandBefore() throws IOException {
		StringBuffer result = new StringBuffer();

		Iterator<String> it = Analyzer.get_argumentsMap().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (Analyzer.get_argumentsMap().get(key) != null) {
				result.append("" + key)
						.append(" " + Analyzer.get_argumentsMap().get(key))
						.append(System.lineSeparator());
			}
		}
		System.out.println("Executing commands: ");
		System.out.println(result.toString());
		String o = Analyzer.get_inputFileName() + "_" + _find.toString();
		File inputF = (new File(Analyzer.get_inputFileName()))
				.getAbsoluteFile();

		if (inputF.isDirectory()) {
			o = new File(".").getCanonicalPath() + File.separator
					+ Analyzer.get_outputFolder();
		}
		System.out.println("The result will be put into " + o);
	}

	private static void logCommandAfter() {
		System.out.println(System.lineSeparator() + "Analyzer done!");
	}

	public static void handleDirectory(String inputFileName) throws IOException {

		File directory = new File(inputFileName);
		String[] files = directory.list();
		// the extensions[] specify which type of files should be skipped during
		// processing

		for (int i = 0; files != null && i < files.length; i++) {
			String file = files[i];
			if (file.equals(Analyzer.get_outputFolder())) {
				continue;
			}
			if (Analyzer.get_fileExt() != null) {
				if (!file.endsWith(Analyzer.get_fileExt()))
					continue;
			} else if (isExcluded(file)) {
				continue;
			}
			String pathS = inputFileName + File.separator + file;
			File pathF = new File(pathS);
			if (pathF.isDirectory()) {
				handleDirectory(pathS);
			} else {
				// if (!file.startsWith("extension"))
				// continue;
				handleFile(pathS);
			}
		}
	}

	private static boolean isExcluded(String file) {
		for (String ext : excludedFromSearch) {
			if (file.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	public static void handleFile(String inputFileName) throws IOException {
		String currentFolder = new File(".").getCanonicalPath()
				+ File.separator + Analyzer.get_outputFolder();
		File resultF = new File(currentFolder + File.separator
				+ getRelativePath(inputFileName)
				+ (new File(inputFileName)).getName());

		if (resultF.exists()) {
			resultF.delete();
		} else {
			resultF.getParentFile().mkdirs();
			resultF.createNewFile();
		}
		BufferedReader reader = new BufferedReader(
				new FileReader(inputFileName));
		BufferedWriter writer = new BufferedWriter(new FileWriter(resultF));
		Boolean newContentAdded = readFromWriteTo(reader, writer);

		if (!newContentAdded) {
			cleanEmpty(resultF);
		}

	}

	/**
	 * @param reader
	 * @param writer
	 * @param prevLinesBuffer
	 * @param lineToPrint
	 * @return
	 * @throws IOException
	 */
	private static boolean readFromWriteTo(BufferedReader reader,
			BufferedWriter writer) throws IOException {
		List<String> prevLinesBuffer = new ArrayList<String>();
		String lineToPrint = "";
		String line;
		String nextLine;
		Boolean newContentAdded = false;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			if (line.length() == 0) {
				continue;
			}

			if (Analyzer.get_takePrevLines() != 0) {
				if (prevLinesBuffer.size() < Analyzer.get_takePrevLines()) {
					prevLinesBuffer.add(line);
				} else {
					prevLinesBuffer.remove(0);
					prevLinesBuffer.add(line);
				}
			}

			if (takeLine(line)) {
				if (prevLinesBuffer.size() != 0)
					for (String lineInBuff : prevLinesBuffer)
						lineToPrint += processLine(lineInBuff)
								+ newlineSeparator;
				else {
					lineToPrint = processLine(line);// + newlineSeparator;
				}

				// TODO: Merge the next 2 if statements
				if (Analyzer.get_takeNextLines() != 0) {
					int count = 0;
					while (count <= Analyzer.get_takeNextLines()) {
						nextLine = reader.readLine();
						if (nextLine == null) {
							break;
						}
						lineToPrint += nextLine + newlineSeparator;
						count++;
					}
				}
				/*
				 * For exceptions and threads, we also print the stack trace
				 */
				if (_exception || Analyzer.get_thread() != null) {
					while (true) {
						nextLine = reader.readLine();
						if (nextLine == null) {
							break;
						}
						if (!nextLine.startsWith("20")) {
							lineToPrint += nextLine + newlineSeparator;
						} else {
							break;
						}
					}
				}

				writer.write(lineToPrint + newlineSeparator);
				newContentAdded = true;

			}
		}
		writer.flush();
		writer.close();
		reader.close();
		return newContentAdded;
	}

	/**
	 * Determine if the line is to be taken or not. To be taken means that:
	 * -there is nothing to find, AND a string to be erased; -there is nothing
	 * to find, AND --clearDate argument was given; -the line contains the
	 * string searched OR it matches the regex given as argument; - --fileExt
	 * argument was given so only the files that have the extension _fileExt
	 * will be processed.
	 * 
	 * @param line
	 *            , the line to be used in searching
	 * @return, a boolean telling that the line contains the string to be found.
	 */

	private static boolean takeLine(String line) {
		if (_erase.size() != 0 || _clearDate)
			return true;

		/*
		 * if(_fileExt != null && _find.size() != 0){ return true; }
		 */
		return found(line);
	}

	/**
	 * @param line
	 * @return true, if the line contains the string we search for OR it matches
	 *         the regular expression given as argument
	 */
	// TODO In case of exception or jobId, shoud not use String.matches(String
	// regex)!!! Fix this

	private static boolean found(String line) {
		for (String stringToFind : _find) {
			if (stringToFind != null) {
				if (line.indexOf(stringToFind) != -1)
					return true;
				if (Analyzer.is_regEx()) {
					try {
						if (line.matches(stringToFind))
							return true;
					} catch (PatternSyntaxException e) {
						System.out
								.println(" Regular expression's syntax is invalid!");
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Refine the line to be printed in log analyzer result.
	 * 
	 * @param line
	 *            , the line to be refined.
	 * @return, the same line, but refined.
	 */
	private static String processLine(String line) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (!_erase.isEmpty()) {
			Iterator<String> it = _erase.iterator();
			while (it.hasNext()) {
				line = line.replace(it.next(), "");
			}
		}

		if (_clearDate) {
			try {
				sdf.parse(line.substring(1, 10));
				line = line.substring(31, line.length());

			} catch (ParseException e) {
				return line;
			} catch (StringIndexOutOfBoundsException r) {
				return line;
			}
		}
		return line;
	}

	private static String getRelativePath(String inputFileName)
			throws IOException {
		String canP = new File(".").getCanonicalPath();
		inputFileName = inputFileName.substring(0,
				inputFileName.lastIndexOf(File.separator));
		inputFileName = inputFileName.substring(canP.length());
		if (!inputFileName.isEmpty()) {
			inputFileName = inputFileName + File.separator;
		}
		return inputFileName;
	}

	private static void cleanEmpty(File resultF) {
		resultF.delete();
		removeParents(resultF);
	}

	private static void removeParents(File resultF) {
		if (resultF.getName().equals(Analyzer.get_outputFolder())) {
			return;
		}
		String parent = resultF.getParent();
		File parentF = new File(parent);

		if (parentF.list().length == 0) {
			parentF.delete();
			removeParents(parentF);
		}

	}

	/*
	 * Possible mergeFiles algorithm
	 * 
	 * > We use a heap with the number of elements equal to the number of log
	 * files so we get a complexity of O(log n) where n is the number of log
	 * files
	 * 
	 * A heap may be a better and simpler implementation than a BST because it
	 * use less memory overhead (elements can be stored directly in an array,
	 * without having to allocate tree nodes and pointers and everything) at it
	 * worst case it still has a complexity of O(log N) beside a balanced tree
	 * that in the worst case scenario has a complexity of O(n) the number of
	 * elements in the heap should be equal to the number of log files
	 * 
	 * > we read the first records from all the files and insert them into the
	 * heap
	 * 
	 * //
	 * 
	 * > loop until no more records in any files // -> remove the max element
	 * from the heap
	 * 
	 * 
	 * -> write it to the master log file -> read the next record from the file
	 * that the previously max element belonged to ->if(noMoreRecordsinFile) ->
	 * remove file from filelist -> continue //
	 * 
	 * 
	 * /
	 */

	/*
	 * Search Algorithm
	 * 
	 * //maybe a good way is that we should combine the merge and search
	 * algorithm as we should get //faster results by searching in a single
	 * sorted file than searching in multiple files
	 */

}
