package io.github.chikyukido.lovelystats.util;

import java.util.concurrent.atomic.LongAdder;

public final class Instrumenter {
    public static boolean ENABLED = false;
    private static final Instrumenter INSTANCE = new Instrumenter();
    private static final int MAX_FUNCS = 16;

    private final String[] names = new String[MAX_FUNCS];
    private final LongAdder[] calls = new LongAdder[MAX_FUNCS];
    private final LongAdder[] timeNs = new LongAdder[MAX_FUNCS];

    private int registered = 0;

    private Instrumenter() {
        for (int i = 0; i < MAX_FUNCS; i++) {
            calls[i] = new LongAdder();
            timeNs[i] = new LongAdder();
        }
    }

    public static Instrumenter get() {
        return INSTANCE;
    }

    public static synchronized int register(String name) {
        int id = INSTANCE.registered++;
        if (id >= MAX_FUNCS) throw new IllegalStateException("Too many functions");
        INSTANCE.names[id] = name;
        return id;
    }

    public static long enter(int id) {
        if (!ENABLED) return 0L;
        return System.nanoTime();
    }

    public static void exit(int id, long startNs) {
        if (!ENABLED) return;
        INSTANCE.calls[id].increment();
        INSTANCE.timeNs[id].add(System.nanoTime() - startNs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);

        for (int i = 0; i < registered; i++) {
            long c = calls[i].sumThenReset();
            long t = timeNs[i].sumThenReset();

            if (c == 0) continue;

            long avgNs = t / c;

            sb.append(names[i]).append(": ")
                    .append(c).append(" calls, total ");

            if (t >= 1_000_000)
                sb.append(t / 1_000_000.0).append(" ms");
            else if (t >= 1_000)
                sb.append(t / 1_000.0).append(" us");
            else
                sb.append(t).append(" ns");

            sb.append(", avg ");

            if (avgNs >= 1_000_000)
                sb.append(avgNs / 1_000_000.0).append(" ms");
            else if (avgNs >= 1_000)
                sb.append(avgNs / 1_000.0).append(" us");
            else
                sb.append(avgNs).append(" ns");

            sb.append('\n');
        }

        return sb.toString();
    }

}
