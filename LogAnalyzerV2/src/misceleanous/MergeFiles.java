package misceleanous;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class MergeFiles {
	private static void mergeFiles(String relation, int numFiles,
			int compareIndex) {
		try {
			ArrayList<FileReader> mergefr = new ArrayList<FileReader>();
			ArrayList<BufferedReader> mergefbr = new ArrayList<BufferedReader>();
			ArrayList<Integer[]> filerows = new ArrayList<Integer[]>();
			FileWriter fw = new FileWriter(relation + "_sorted.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			String[] header;

			boolean someFileStillHasRows = false;

			for (int i = 0; i < numFiles; i++) {
				mergefr.add(new FileReader(relation + "_chunk" + i + ".csv"));
				mergefbr.add(new BufferedReader(mergefr.get(i)));
				// get each one past the header
				header = mergefbr.get(i).readLine().split(",");

				if (i == 0)
					bw.write(flattenArray(header, ",") + "\n");

				// get the first row
				String line = mergefbr.get(i).readLine();
				if (line != null) {
					filerows.add(getIntsFromStringArray(line.split(",")));
					someFileStillHasRows = true;
				} else {
					filerows.add(null);
				}

			}

			Integer[] row;
			int cnt = 0;
			while (someFileStillHasRows) {
				Integer min;
				int minIndex = 0;

				row = filerows.get(0);
				if (row != null) {
					min = row[compareIndex];
					minIndex = 0;
				} else {
					min = null;
					minIndex = -1;
				}

				// check which one is min
				for (int i = 1; i < filerows.size(); i++) {
					row = filerows.get(i);
					if (min != null) {

						if (row != null && row[compareIndex] < min) {
							minIndex = i;
							min = filerows.get(i)[compareIndex];
						}
					} else {
						if (row != null) {
							min = row[compareIndex];
							minIndex = i;
						}
					}
				}

				if (minIndex < 0) {
					someFileStillHasRows = false;
				} else {
					// write to the sorted file
					bw.append(flattenArray(filerows.get(minIndex), ",") + "\n");

					// get another row from the file that had the min
					String line = mergefbr.get(minIndex).readLine();
					if (line != null) {
						filerows.set(minIndex,
								getIntsFromStringArray(line.split(",")));
					} else {
						filerows.set(minIndex, null);
					}
				}
				// check if one still has rows
				for (int i = 0; i < filerows.size(); i++) {

					someFileStillHasRows = false;
					if (filerows.get(i) != null) {
						if (minIndex < 0) {
							puts("mindex lt 0 and found row not null"
									+ flattenArray(filerows.get(i), " "));
							System.exit(-1);
						}
						someFileStillHasRows = true;
						break;
					}
				}

				// check the actual files one more time
				if (!someFileStillHasRows) {

					// write the last one not covered above
					for (int i = 0; i < filerows.size(); i++) {
						if (filerows.get(i) == null) {
							String line = mergefbr.get(i).readLine();
							if (line != null) {

								someFileStillHasRows = true;
								filerows.set(i,
										getIntsFromStringArray(line.split(",")));
							}
						}

					}
				}

			}

			// close all the files
			bw.close();
			fw.close();
			for (int i = 0; i < mergefbr.size(); i++)
				mergefbr.get(i).close();
			for (int i = 0; i < mergefr.size(); i++)
				mergefr.get(i).close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private static Integer[] getIntsFromStringArray(String[] split) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String flattenArray(Integer[] arr, String delimiter)
	{
		String result = "";
		for (int i=0; i<arr.length; i++)
			result+=arr[i] + delimiter;

		if (result.endsWith(","))
			result=result.substring(0,result.length()-1);

		return result.trim();
	}
}
