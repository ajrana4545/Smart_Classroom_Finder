import java.io.*;
import java.util.*;

public class ClassroomFinder {
    private static final int START = 480;
    private static final int END = 1080;
    private static final int TOTAL_MINUTES = END - START;

    private Map<String, Map<String, SegmentTree>> schedule = new HashMap<>();
    private Set<String> allRooms = new HashSet<>();

    public void loadFromCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine(); // Skip header

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue;

            String room = parts[0].trim();
            String day = parts[1].trim();
            int start = timeToMinutes(parts[2].trim()) - START;
            int end = timeToMinutes(parts[3].trim()) - START;

            if (start < 0 || end > TOTAL_MINUTES || start >= end) continue;

            allRooms.add(room);
            schedule.putIfAbsent(day, new HashMap<>());
            Map<String, SegmentTree> daySchedule = schedule.get(day);
            daySchedule.putIfAbsent(room, new SegmentTree(TOTAL_MINUTES));
            daySchedule.get(room).markBusy(start, end);
        }

        br.close();
    }

    public List<String> getFreeRooms(String day, int start, int end) {
        start -= START;
        end -= START;
        List<String> freeRooms = new ArrayList<>();

        if (!schedule.containsKey(day)) return new ArrayList<>(allRooms);

        for (String room : allRooms) {
            SegmentTree tree = schedule.get(day).get(room);
            if (tree == null || tree.isFree(start, end)) {
                freeRooms.add(room);
            }
        }

        return freeRooms;
    }

    public static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}

class SegmentTree {
    private boolean[] tree;

    public SegmentTree(int size) {
        tree = new boolean[size];
    }

    public void markBusy(int start, int end) {
        for (int i = start; i < end; i++) {
            tree[i] = true;
        }
    }

    public boolean isFree(int start, int end) {
        for (int i = start; i < end; i++) {
            if (tree[i]) return false;
        }
        return true;
    }
}
