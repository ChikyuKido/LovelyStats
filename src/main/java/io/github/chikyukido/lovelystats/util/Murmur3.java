package io.github.chikyukido.lovelystats.util;

public final class Murmur3 {

    public static long hash64(String s) {
        byte[] data = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return hash64(data, data.length, 0);
    }

    private static long hash64(byte[] data, int len, int seed) {
        final long c1 = 0x87c37b91114253d5L;
        final long c2 = 0x4cf5ad432745937fL;

        long h1 = seed;
        long h2 = seed;

        int i = 0;
        while (i + 15 < len) {
            long k1 = getLong(data, i);
            long k2 = getLong(data, i + 8);

            k1 *= c1; k1 = Long.rotateLeft(k1, 31); k1 *= c2; h1 ^= k1;
            h1 = Long.rotateLeft(h1, 27); h1 += h2; h1 = h1 * 5 + 0x52dce729;

            k2 *= c2; k2 = Long.rotateLeft(k2, 33); k2 *= c1; h2 ^= k2;
            h2 = Long.rotateLeft(h2, 31); h2 += h1; h2 = h2 * 5 + 0x38495ab5;

            i += 16;
        }

        long k1 = 0, k2 = 0;
        switch (len & 15) {
            case 15: k2 ^= ((long) data[i + 14] & 0xff) << 48;
            case 14: k2 ^= ((long) data[i + 13] & 0xff) << 40;
            case 13: k2 ^= ((long) data[i + 12] & 0xff) << 32;
            case 12: k2 ^= ((long) data[i + 11] & 0xff) << 24;
            case 11: k2 ^= ((long) data[i + 10] & 0xff) << 16;
            case 10: k2 ^= ((long) data[i + 9] & 0xff) << 8;
            case 9:  k2 ^= ((long) data[i + 8] & 0xff);
                k2 *= c2; k2 = Long.rotateLeft(k2, 33); k2 *= c1; h2 ^= k2;
            case 8:  k1 ^= ((long) data[i + 7] & 0xff) << 56;
            case 7:  k1 ^= ((long) data[i + 6] & 0xff) << 48;
            case 6:  k1 ^= ((long) data[i + 5] & 0xff) << 40;
            case 5:  k1 ^= ((long) data[i + 4] & 0xff) << 32;
            case 4:  k1 ^= ((long) data[i + 3] & 0xff) << 24;
            case 3:  k1 ^= ((long) data[i + 2] & 0xff) << 16;
            case 2:  k1 ^= ((long) data[i + 1] & 0xff) << 8;
            case 1:  k1 ^= ((long) data[i] & 0xff);
                k1 *= c1; k1 = Long.rotateLeft(k1, 31); k1 *= c2; h1 ^= k1;
        }

        h1 ^= len;
        h2 ^= len;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;

        return h1;
    }

    private static long getLong(byte[] b, int i) {
        return ((long) b[i] & 0xff)
                | (((long) b[i + 1] & 0xff) << 8)
                | (((long) b[i + 2] & 0xff) << 16)
                | (((long) b[i + 3] & 0xff) << 24)
                | (((long) b[i + 4] & 0xff) << 32)
                | (((long) b[i + 5] & 0xff) << 40)
                | (((long) b[i + 6] & 0xff) << 48)
                | (((long) b[i + 7] & 0xff) << 56);
    }

    private static long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }
}
