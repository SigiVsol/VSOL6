package be.vsol.util;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

    public static boolean checkSha256(byte[] bytes, String checksum) {
        String sha256 = getSha256(bytes);

        return sha256 != null && sha256.equalsIgnoreCase(checksum);
    }

    public static boolean checkSha256(File file, String checksum) {
        byte[] bytes = FileSys.readBytes(file);
        return checkSha256(bytes, checksum);
    }

    public static String getSha256(File file) {
        byte[] bytes = FileSys.readBytes(file);
        return getSha256(bytes);
    }

    public static String getSha256(String string) {
        return getSha256(string.getBytes());
    }

    public static String getSha256(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = messageDigest.digest(bytes);

            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean equals(File a, File b) {
        String checkA = getSha256(a);
        String checkB = getSha256(b);
        return checkA.equals(checkB);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
