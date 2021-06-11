package be.vsol.test.jurien;

import be.vsol.dicom.model.VR;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class TransferSyntaxReader {
    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("https://www.dicomlibrary.com/dicom/transfer-syntax/");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            boolean startTable = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Transfer Syntax UID")) {
                    startTable = true;
                    continue;
                }
                if (startTable) {
                    if (line.contains("</table>")) {
                        break;
                    }
                    line = line.replaceAll("</?tr>", "");
                    line = line.replaceAll("</?br>", "");
                    String[] row = line.split("</td>");
                    if(row.length < 2)
                        continue;
                    for (int i = 0; i < row.length; i++) {
                        row[i] = row[i].replaceAll("<.*>", "");
                    }
                    String uid = row[0];
                    row[1] = row[1].replaceAll(":.*", "");
                    row[1] = row[1].replaceAll("[(),\\[\\]]", "");
                    row[1] = row[1].replaceAll("[-/.]", " ");
                    row[1] = row[1].replaceAll("&#38;", "And");
                    row[1] = row[1].replaceAll("\\s+", " ");
                    String name = Arrays.stream(row[1].split(" ")).reduce((s, s2) -> s + s2.substring(0, 1).toUpperCase() + s2.substring(1)).get();
                    if (row.length > 2 && row[2].equals("Retired"))
                        result.append("@Deprecated ");
                    result.append(name).append("(\"").append(uid).append("\"),\n");
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("c://test//TransferSyntax.txt")) {
            fileOutputStream.write(result.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
