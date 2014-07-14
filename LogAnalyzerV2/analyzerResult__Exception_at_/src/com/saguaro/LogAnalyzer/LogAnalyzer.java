import java.io.IOException;
import java.text.ParseException;
import java.util.regex.PatternSyntaxException;
		_noValueArgumentsMap.put("--exceptions", "Exception");
		} catch (IOException e) {
		} catch (ArgumentsException e) {
		} catch (IOException e) {
	private static void executeCommand() throws IOException {
		} catch (IOException e) {
	private static void logCommandBefore() throws IOException {
	private static void validateArgs() throws ArgumentsException {
	 * @throws ArgumentsException
	private static void printHelp(StringBuffer sb) throws ArgumentsException {
		throw new ArgumentsException(sb.toString());
	private static void init(String[] args) throws IOException {
			} catch (NumberFormatException e) {
			} catch (NumberFormatException e) {
		 * As we need the whole trace when we search only for Exception
			_find.add("Exception");	
	 * catch (ParseException e) { return false; } return true; }
			throws IOException {
	private static void handleFile(String inputFileName) throws IOException {
	 * @throws IOException
			BufferedWriter writer) throws IOException {
					} catch (PatternSyntaxException e) {
			} catch (ParseException e) {
			} catch (StringIndexOutOfBoundsException r) {
			throws IOException {
	public static class ArgumentsException extends Throwable {
		public ArgumentsException(String message) {
