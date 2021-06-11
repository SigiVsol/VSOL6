package be.vsol.test.jurien;

import be.vsol.dicom.model.VR;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class DicomTagsReader {
    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("https://www.dicomlibrary.com/dicom/dicom-tags/");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            boolean startTable = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("id=\"table1\"")) {
                    startTable = true;
                    reader.readLine();
                    continue;
                }
                if (startTable) {
                    if (line.contains("</table>")) {
                        break;
                    }
                    line = line.replaceAll("</?tr>", "");
                    String[] row = line.split("</td>");
                    for (int i = 0; i < row.length; i++) {
                        row[i] = row[i].replaceAll(".*>", "");
                    }
                    String tag = row[0].replaceAll("[()]", "");
                    String vr = row[1].contains("or") ? row[1].substring(0, 2) : row[1];
                    System.out.println(row[2]);
                    row[2] = row[2].replaceAll("'s", "");
                    row[2] = row[2].replaceAll("\\(s\\)", "s");
                    row[2] = row[2].replaceAll("['()/,&-]", " ");
                    row[2] = row[2].replaceAll("\\s+", " ");
                    String name = Arrays.stream(row[2].split(" ")).reduce((s, s2) -> s + s2.substring(0, 1).toUpperCase() + s2.substring(1)).get();
                    if (name.isBlank())
                        name = tag.replaceAll(",", "_");
                    if (Character.isDigit(name.charAt(0)))
                        name = "_" + name;
                    if (row.length > 3 && row[3].equals("Retired"))
                        result.append("@Deprecated ");
                    result.append(name).append("(\"").append(tag).append("\", ");
                    if (VR.get(vr) == null)
                        result.append("null");
                    else
                        result.append("VR.").append(VR.get(vr));
                    result.append("),\n");
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("c://test//DicomTags.txt")) {
            fileOutputStream.write(result.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}