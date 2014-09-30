package com.trader.util;

public final class HexValueParser {
	public static int parseInt(String string) {
		String sub;
		try {
			sub = string.substring(0, 2);
		} catch (Exception e) {
			return Integer.parseInt(string);
		}
		
		if (sub.equals("0x") || sub.equals("0X")) {
			return Integer.parseInt(string.substring(2), 16);
		} else {
			return Integer.parseInt(string);
		}
	}
    public static byte[] fromHexString(final String encoded) {
        if ((encoded.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte result[] = new byte[encoded.length() / 2];
        final char enc[] = encoded.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            int ix = Integer.parseInt(curr.toString(), 16);
            result[i / 2] = (byte) ix;
        }
        return result;
    }
}
