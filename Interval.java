public class Interval {
    int start;
    int end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean conflictsWith(Interval other) {
        return !(this.end <= other.start || other.end <= this.start);
    }
}
