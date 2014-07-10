package com.saguaro.LogAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


public class Analyzer {

	private static HashMap<String, String> _argumentsMap = new HashMap<String, String>();

	private static HashMap<String, String> _noValueArgumentsMap = new HashMap<String, String>();
	// private static final String[] excludedFromSearch = { ".jar", ".zip",
	// ".project", ".classpath", ".jre", ".class", ".settings" };
	// private static List<String> _commands = ;

	private static boolean _clearDate = false;
	// private static boolean _exception = false;
	private static int _takeNextLines = 0;
	private static int _takePrevLines = 0;
	private static String _thread = null;

	private static String _outputFolder = "analyzerResult";

	/*
	 * private static String _date = null; private static String _period = null;
	 */
	private static String _inputFileName = null;

	private static String _fileExt = null;

	private static boolean _regEx = false;

	private static ArrayList<String> _find = new ArrayList<String>();
	private static ArrayList<String> _erase = new ArrayList<String>();

	private static boolean _notACommand = true;

	// private static AnimationThread _animator;
	
	// private static String newlineSeparator = System
	// .getProperty("line.separator");

	static {
		_argumentsMap.put("--inputFile", null);
		_argumentsMap.put("--clearDate", null);
		_argumentsMap.put("--jobId", null);
		_argumentsMap.put("--erase", null);
		_argumentsMap.put("--regEx", null);
		_argumentsMap.put("--inputFile", null);
		_argumentsMap.put("--fileExt", null);
		_argumentsMap.put("--normalizeTimestamps", null);
		_argumentsMap.put("--thread", null);
		_argumentsMap.put("--find", null);
		_argumentsMap.put("--exceptions", null);
		_argumentsMap.put("--takeNextLines", null);
		_argumentsMap.put("--takePrevLines", null);
	};

	// a map containing commands which requires no value
	static {
		_noValueArgumentsMap.put("--exceptions", "Exception");
	};

	public static void main(String[] args)
			throws ArgumentsException {
		try {
			init(args);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}

		validateArgs();

		setupOutputLocation();

		Outputer.startAnimation();

		try {
			LogParser.executeCommand();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Outputer.stopAnimation();
		}
	}

	

	private static void setupOutputLocation() {
		if (!_find.isEmpty()) {
			_outputFolder += "_" + _find.toString().replaceAll("\\W+", "_");
		} else if (_clearDate) {
			_outputFolder += "_" + "clearDate";
		}
		if (_erase.size() != 0) {
			_outputFolder += "_" + "erased";
		}

	}

	public static void validateArgs() throws ArgumentsException {
		StringBuffer sb = new StringBuffer();
		if (Analyzer.is_notACommand()) {
			printHelp(sb);
		}
	}
	
	
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

	/**
	 * Prints the help information to console; tells the user of LogAnalyzer how
	 * to use the commands.
	 * 
	 * @param sb
	 * @throws ArgumentsException
	 */

	private static void init(String[] args) throws IOException {
		List<String> arguments = Arrays.asList(args);
		int[] argsIndexes = new int[arguments.size()];
		// build up position indexes for arguments
		for (int i = 0; i < arguments.size(); i++) {
			String arg = arguments.get(i);
			if (isInputArgument(arg)) {
				argsIndexes[i] = 1;
				_notACommand = false;
			}
		}
		if (_notACommand) {
			return;
		}
		String value = null;
		for (int i = 0; i < arguments.size(); i++) {
			String argument = arguments.get(i);
			// value = _argumentsMap.get(argument);
			if (!isInputArgument(argument)) {
				continue;
			}
			int howManyPositions = howManyPositions(i, argsIndexes);
			value = grabValues(arguments, i, howManyPositions);
			// look into no value commands
			if (value == null) {
				value = _noValueArgumentsMap.get(argument);
			}
			_argumentsMap.put(argument, value);
		}

		if (_argumentsMap.get("--takeNextLines") != null) {
			try {
				_takeNextLines = Integer.parseInt(_argumentsMap
						.get("--takeNextLines"));
			} catch (NumberFormatException e) {
				System.out
						.println("_takeNextLines expects a value of type integer! For example: --takeNextLines 5 \n It is set by default to 0. ");
			}
		}

		if (_argumentsMap.get("--takePrevLines") != null) {
			try {
				_takePrevLines = Integer.parseInt(_argumentsMap
						.get("--takePrevLines"));
			} catch (NumberFormatException e) {
				System.out
						.println("_takePrevLines expects a value of type integer! For example: --takePrevLines 5 \n It is set by default to 0. ");
			}
		}

		_thread = _argumentsMap.get("--thread");

		// TODO: clarify this
		if (_argumentsMap.get("--jobId") != null) {
			populateList(_find, _argumentsMap.get("--jobId"));
			for (int index = 0; index < _find.size(); index++) {
				_find.set(
						index,
						(".*Job-j\\s\\d{9,}.*ID:\\s" + _find.get(index) + "\\sFrom.*To.*"));
			}
		} /*
		 * else if (_argumentsMap.get("--find") == null) { populateList(_find,
		 * ".*Job-j\\s\\d{9,}.*ID:.*From.*To.*"); }
		 */

		if (arguments.contains("--exceptions")) {
			_find.add("Exception");
			_find.add("\tat ");
		}
		if (_argumentsMap.get("--find") != null)
			populateList(_find, _argumentsMap.get("--find"));
		if (_argumentsMap.get("--thread") != null)
			populateList(_find, _argumentsMap.get("--thread"));

		populateList(_erase, _argumentsMap.get("--erase"));

		_clearDate = arguments.contains("--clearDate");
		_regEx = arguments.contains("--regEx");

		_fileExt = _argumentsMap.get("--fileExt");
		// _inputFileName = _argumentsMap.get("--inputFile");
		if (_inputFileName == null) {
			_inputFileName = (new File(".")).getCanonicalPath();
		}
		// LogParser.init();
	}

	private static void populateList(ArrayList<String> list, String string) {
		if (string != null) {
			StringTokenizer tokenizer = new StringTokenizer(string, " ");
			while (tokenizer.hasMoreElements()) {
				list.add(tokenizer.nextToken());
			}
		}
	}

	private static String grabValues(List<String> arguments, int start,
			int howManyPositions) {
		StringBuffer resultSB = new StringBuffer();
		int contor = 0;
		for (int i = start + 1; i < arguments.size() && howManyPositions > 0; i++) {
			String element = arguments.get(i);

			resultSB.append(element);

			if (contor + 1 < howManyPositions) {
				resultSB.append(" ");
			} else {
				break;
			}
			contor++;
		}
		String result = null;
		if (resultSB.length() > 0) {
			result = resultSB.toString();
		}
		return result;
	}

	private static int howManyPositions(int start, int[] argsIndexes) {
		for (int i = start + 1; i < argsIndexes.length; i++) {
			int element = argsIndexes[i];
			if (element == 1) {
				return i - 1 - start;
			}
		}
		return argsIndexes.length - start - 1;
	}

	/*
	 * private static boolean validateDate(String date) { SimpleDateFormat sdf =
	 * new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); try { sdf.parse(date); }
	 * catch (ParseException e) { return false; } return true; }
	 */

	/*
	 * private static void handleDirectory(String inputFileName) throws
	 * IOException {
	 * 
	 * File directory = new File(inputFileName); String[] files =
	 * directory.list(); // the extensions[] specify which type of files should
	 * be skipped during // processing
	 * 
	 * for (int i = 0; files != null && i < files.length; i++) { String file =
	 * files[i]; if (file.equals(_outputFolder)) { continue; } if (_fileExt !=
	 * null) { if (!file.endsWith(_fileExt)) continue; } else if
	 * (isExcluded(file)) { continue; } String pathS = inputFileName +
	 * File.separator + file; File pathF = new File(pathS); if
	 * (pathF.isDirectory()) { handleDirectory(pathS); } else { // if
	 * (!file.startsWith("extension")) // continue; handleFile(pathS); } } }
	 * 
	 * private static boolean isExcluded(String file) { for (String ext :
	 * excludedFromSearch) { if (file.endsWith(ext)) { return true; } } return
	 * false; }
	 * 
	 * private static void handleFile(String inputFileName) throws IOException {
	 * String currentFolder = new File(".").getCanonicalPath() + File.separator
	 * + _outputFolder; File resultF = new File(currentFolder + File.separator +
	 * getRelativePath(inputFileName) + (new File(inputFileName)).getName());
	 * 
	 * if (resultF.exists()) { resultF.delete(); } else {
	 * resultF.getParentFile().mkdirs(); resultF.createNewFile(); }
	 * BufferedReader reader = new BufferedReader( new
	 * FileReader(inputFileName)); BufferedWriter writer = new
	 * BufferedWriter(new FileWriter(resultF)); Boolean newContentAdded =
	 * readFromWriteTo(reader, writer);
	 * 
	 * if (!newContentAdded) { cleanEmpty(resultF); }
	 * 
	 * }
	 */

	/**
	 * @param reader
	 * @param writer
	 * @param prevLinesBuffer
	 * @param lineToPrint
	 * @return
	 * @throws IOException
	 */
	/*
	 * private static boolean readFromWriteTo(BufferedReader reader,
	 * BufferedWriter writer) throws IOException { List<String> prevLinesBuffer
	 * = new ArrayList<String>(); String lineToPrint = ""; String line; String
	 * nextLine; Boolean newContentAdded = false; while (true) { line =
	 * reader.readLine(); if (line == null) { break; } if (line.length() == 0) {
	 * continue; }
	 * 
	 * if (_takePrevLines != 0) { if (prevLinesBuffer.size() < _takePrevLines) {
	 * prevLinesBuffer.add(line); } else { prevLinesBuffer.remove(0);
	 * prevLinesBuffer.add(line); } }
	 * 
	 * if (takeLine(line)) { if (prevLinesBuffer.size() != 0) for (String
	 * lineInBuff : prevLinesBuffer) lineToPrint += processLine(lineInBuff) +
	 * newlineSeparator; else { lineToPrint = processLine(line);// +
	 * newlineSeparator; }
	 * 
	 * // TODO: Merge the next 2 if statements if (_takeNextLines != 0) { int
	 * count = 0; while (count <= _takeNextLines) { nextLine =
	 * reader.readLine(); if (nextLine == null) { break; } lineToPrint +=
	 * nextLine + newlineSeparator; count++; } } /* For exceptions and threads,
	 * we also print the stack trace
	 * 
	 * if (_exception || _thread != null) { while (true) { nextLine =
	 * reader.readLine(); if (nextLine == null) { break; } if
	 * (!nextLine.startsWith("20")) { lineToPrint += nextLine +
	 * newlineSeparator; } else { break; } } }
	 * 
	 * writer.write(lineToPrint + newlineSeparator); newContentAdded = true;
	 * 
	 * } } writer.flush(); writer.close(); reader.close(); return
	 * newContentAdded; }
	 */

	/**
	 * @param line
	 * @return true, if the line contains the string we search for OR it matches
	 *         the regular expression given as argument
	 */
	// TODO In case of exception or jobId, shoud not use String.matches(String
	// regex)!!! Fix this
	/*
	 * private static boolean found(String line) { for (String stringToFind :
	 * _find) { if (stringToFind != null) { if (line.indexOf(stringToFind) !=
	 * -1) return true; if (_regEx) { try { if (line.matches(stringToFind))
	 * return true; } catch (PatternSyntaxException e) { System.out
	 * .println(" Regular expression's syntax is invalid!"); return false; } } }
	 * } return false; }
	 */
	/**
	 * Refine the line to be printed in log analyzer result.
	 * 
	 * @param line
	 *            , the line to be refined.
	 * @return, the same line, but refined.
	 */
	/*
	 * private static String processLine(String line) { SimpleDateFormat sdf =
	 * new SimpleDateFormat("yyyy-MM-dd");
	 * 
	 * if (!_erase.isEmpty()) { Iterator<String> it = _erase.iterator(); while
	 * (it.hasNext()) { line = line.replace(it.next(), ""); } }
	 * 
	 * if (_clearDate) { try { sdf.parse(line.substring(1, 10)); line =
	 * line.substring(31, line.length());
	 * 
	 * } catch (ParseException e) { return line; } catch
	 * (StringIndexOutOfBoundsException r) { return line; } } return line; }
	 */
	/*
	 * private static String getRelativePath(String inputFileName) throws
	 * IOException { String canP = new File(".").getCanonicalPath();
	 * inputFileName = inputFileName.substring(0,
	 * inputFileName.lastIndexOf(File.separator)); inputFileName =
	 * inputFileName.substring(canP.length()); if (!inputFileName.isEmpty()) {
	 * inputFileName = inputFileName + File.separator; } return inputFileName; }
	 */

	/*
	 * private static void cleanEmpty(File resultF) { resultF.delete();
	 * removeParents(resultF); }
	 */

	/*
	 * private static void removeParents(File resultF) { if
	 * (resultF.getName().equals(_outputFolder)) { return; } String parent =
	 * resultF.getParent(); File parentF = new File(parent);
	 * 
	 * if (parentF.list().length == 0) { parentF.delete();
	 * removeParents(parentF); }
	 * 
	 * }
	 */

	/*
	 * private static void fillUp(String toErase, String elements) {
	 * 
	 * }
	 */
	private static boolean isInputArgument(String argument) {
		return _argumentsMap.containsKey(argument);
	}

	public static class ArgumentsException extends Throwable {
		private String message = "";

		public ArgumentsException(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	

	public static String get_fileExt() {
		return _fileExt;
	}

	public static void set_fileExt(String _fileExt) {
		Analyzer._fileExt = _fileExt;
	}

	public static int get_takeNextLines() {
		return _takeNextLines;
	}

	public static void set_takeNextLines(int _takeNextLines) {
		Analyzer._takeNextLines = _takeNextLines;
	}

	public static int get_takePrevLines() {
		return _takePrevLines;
	}

	public static void set_takePrevLines(int _takePrevLines) {
		Analyzer._takePrevLines = _takePrevLines;
	}

	public static String get_thread() {
		return _thread;
	}

	public static void set_thread(String _thread) {
		Analyzer._thread = _thread;
	}

	public static boolean is_regEx() {
		return _regEx;
	}

	public static void set_regEx(boolean _regEx) {
		Analyzer._regEx = _regEx;
	}

	public static boolean is_notACommand() {
		return _notACommand;
	}

	public static void set_notACommand(boolean _notACommand) {
		Analyzer._notACommand = _notACommand;
	}

	public static String get_inputFileName() {
		return _inputFileName;
	}

	public static void set_inputFileName(String _inputFileName) {
		Analyzer._inputFileName = _inputFileName;
	}

	public static HashMap<String, String> get_argumentsMap() {
		return _argumentsMap;
	}

	public static void set_argumentsMap(HashMap<String, String> _argumentsMap) {
		Analyzer._argumentsMap = _argumentsMap;
	}

	public static String get_outputFolder() {
		return _outputFolder;
	}

	public static void set_outputFolder(String _outputFolder) {
		Analyzer._outputFolder = _outputFolder;
	}
}
