import com.saguaro.LogAnalyzer.Analyzer.ArgumentsException;
	public static void printHelp(StringBuffer sb) throws ArgumentsException {
		throw new ArgumentsException(sb.toString());
	static class ArgumentsException extends Throwable {
		public ArgumentsException(String message) {
	public static void validateArgs() throws ArgumentsException {
