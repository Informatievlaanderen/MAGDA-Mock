package be.vlaanderen.vip.magda.client.utils;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashingUtils {

    @SneakyThrows
    public static String hashSha512(String s){
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(s.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder hashtext = new StringBuilder(no.toString(16));
        while (hashtext.length() < 128) {
            hashtext.insert(0, "0");
        }
        return hashtext.toString();
    }
}
