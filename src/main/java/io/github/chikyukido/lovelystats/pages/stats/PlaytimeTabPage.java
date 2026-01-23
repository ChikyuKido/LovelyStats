package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.util.OwnFormat;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlaytimeTabPage extends TabPage {
    public PlaytimeTabPage(StatsPage parent, UUID playerUUID) {
        super(parent,playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages", "stats/playtime/playtime_page.ui");

        PlaytimeStats stats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);

        cb.set("#TotalPlaytime.Text", OwnFormat.formatTime(stats.getTotalPlaytime()));
        cb.set("#TotalActiveTime.Text", OwnFormat.formatTime(stats.getTotalActivePlaytime()));
        cb.set("#TotalIdleTime.Text", OwnFormat.formatTime(stats.getTotalIdlePlaytime()));
        cb.set("#TotalSessions.Text", String.valueOf(stats.getSessions().size()));

        double ratio = stats.getTotalPlaytime() == 0 ? 0
                : (stats.getTotalActivePlaytime() * 100.0 / stats.getTotalPlaytime());
        cb.set("#ActiveRatio.Text", String.format("%.0f%%", ratio));

        PlaytimeSession current = stats.getCurrentSession();
        if (current != null) {
            long currentTotalTime = current.getActiveTime() + current.getIdleTime();
            double curRatio = currentTotalTime == 0 ? 0
                    : (current.getActiveTime() * 100.0 / currentTotalTime);

            cb.set("#CurrentStarted.Text", OwnFormat.formatDate(current.getStartTime(), "MMM dd HH:mm"));
            cb.set("#CurrentPlaytime.Text", OwnFormat.formatTime(currentTotalTime));
            cb.set("#CurrentActive.Text", OwnFormat.formatTime(current.getActiveTime()));
            cb.set("#CurrentIdle.Text", OwnFormat.formatTime(current.getIdleTime()));
            cb.set("#CurrentActiveRatio.Text", String.format("%.0f%%", curRatio));
        }

        PlaytimeSession longest = stats.getSessions().stream()
                .max(Comparator.comparingLong(a -> a.getActiveTime() + a.getIdleTime()))
                .orElse(null);
        if (longest != null) {
            long longestTotalTime = longest.getActiveTime() + longest.getIdleTime();
            double longRatio = longestTotalTime == 0 ? 0
                    : (longest.getActiveTime() * 100.0 / longestTotalTime);

            cb.set("#LongestStarted.Text", OwnFormat.formatDate(longest.getStartTime(), "MMM dd HH:mm"));
            cb.set("#LongestEnded.Text", OwnFormat.formatDate(longest.getStopTime(), "MMM dd HH:mm"));
            cb.set("#LongestPlaytime.Text", OwnFormat.formatTime(longestTotalTime));
            cb.set("#LongestActive.Text", OwnFormat.formatTime(longest.getActiveTime()));
            cb.set("#LongestIdle.Text", OwnFormat.formatTime(longest.getIdleTime()));
            cb.set("#LongestActiveRatio.Text", String.format("%.0f%%", longRatio));
        }


        List<PlaytimePeriod> periods = getPeriods(playerUUID, PeriodType.DAY, 7);
        for (int i = 0; i < 7; i++) {
            PlaytimePeriod p = i < periods.size() ? periods.get(i) : null;
            cb.set("#Row"+(i+1)+" #Date" + (i+1) + ".Text", p != null ? p.label : "-");
            cb.set("#Row"+(i+1)+" #Playtime" + (i+1) + ".Text", p != null ?OwnFormat.formatTime(p.total) : "-");
            cb.set("#Row"+(i+1)+" #Active" + (i+1) + ".Text", p != null ? OwnFormat.formatTime(p.active) : "-");
            cb.set("#Row"+(i+1)+" #Idle" + (i+1) + ".Text", p != null ? OwnFormat.formatTime(p.idle) : "-");
        }

        List<PlaytimeSession> last3 = stats.getSessions();
        int size = last3.size();
        for (int i = 0; i < 3; i++) {
            PlaytimeSession s = i < size ? last3.get(size-1-i) : null;
            cb.set("#SessionRow"+(i+1)+" #Index" + (i+1) + ".Text", s != null ? String.valueOf(i+1) : "-");
            cb.set("#SessionRow"+(i+1)+" #Playtime" + (i+1) + ".Text", s != null ?   OwnFormat.formatTime(s.getActiveTime() + s.getIdleTime()) : "-");
            cb.set("#SessionRow"+(i+1)+" #Active" + (i+1) + ".Text", s != null ? OwnFormat.formatTime(s.getActiveTime()) : "-");
            cb.set("#SessionRow"+(i+1)+" #Idle" + (i+1) + ".Text", s != null ? OwnFormat.formatTime(s.getIdleTime()) : "-");
        }

        int[] distBuckets = getSessionDistribution(playerUUID);
        for (int i = 0; i < 5; i++) {
            cb.set("#Dist" + (i+1) + "Value.Text", "" + (i < distBuckets.length ? distBuckets[i] : 0));
        }

    }


    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {}
    private int[] getSessionDistribution(UUID playerUUID) {
        PlaytimeStats stats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);
        int[] buckets = new int[5];
        for (PlaytimeSession s : stats.getSessions()) {
            long total = s.getActiveTime() + s.getIdleTime();
            if (total < 900) buckets[0]++;
            else if (total < 1800) buckets[1]++;
            else if (total < 3600) buckets[2]++;
            else if (total < 7200) buckets[3]++;
            else buckets[4]++;
        }
        return buckets;
    }
    private List<PlaytimePeriod> getPeriods(UUID playerUUID, PeriodType type, int count) {
        PlaytimeStats stats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);
        List<PlaytimeSession> sessions = stats.getSessions();

        long now = System.currentTimeMillis() / 1000;
        List<PlaytimePeriod> periods = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long start, end;
            String label;

            switch (type) {
                case DAY -> {
                    long dayStart = now - i * 86400L;
                    start = dayStart - (dayStart % 86400L);
                    end = start + 86400L;
                    label = OwnFormat.formatDate(start, "MMM dd");
                }
                case WEEK -> {
                    long weekStart = now - i * 604800L;
                    start = weekStart - (weekStart % 604800L);
                    end = start + 604800L;
                    label = "Week " + OwnFormat.formatDate(start, "w");
                }
                case MONTH -> {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTimeInMillis(now * 1000L);
                    cal.add(java.util.Calendar.MONTH, -i);
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
                    start = cal.getTimeInMillis() / 1000;
                    cal.add(java.util.Calendar.MONTH, 1);
                    end = cal.getTimeInMillis() / 1000;
                    label = OwnFormat.formatDate(start, "MMM yyyy");
                }
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }

            long total = 0, active = 0, idle = 0;
            for (PlaytimeSession session : sessions) {
                long sStart = session.getStartTime();
                long sEnd = session.getStopTime() > 0 ? session.getStopTime() : now;

                if (sEnd > start && sStart < end) {
                    long overlap = Math.min(sEnd, end) - Math.max(sStart, start);
                    if (overlap <= 0) continue;

                    double activeRatio = session.getActiveTime() / (double) (session.getActiveTime() + session.getIdleTime());
                    active += (long) (overlap * activeRatio);
                    idle += overlap - (long) (overlap * activeRatio);
                    total += overlap;
                }
            }
            if(total > 0 ) {
                periods.add(new PlaytimePeriod(label, total, active, idle));
            }
        }

        return periods;
    }

    private enum PeriodType { DAY, WEEK, MONTH }

    private record PlaytimePeriod(String label, long total, long active, long idle) {
    }

}
