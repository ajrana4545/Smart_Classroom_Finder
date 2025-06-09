public class SegmentTree {
    private int n;
    private boolean[] tree;

    public SegmentTree(int totalMinutes) {
        n = totalMinutes;
        tree = new boolean[4 * n];
    }

    public void markBusy(int start, int end) {
        update(1, 0, n - 1, start, end - 1);
    }

    public boolean isFree(int start, int end) {
        return query(1, 0, n - 1, start, end - 1);
    }

    private void update(int node, int l, int r, int ql, int qr) {
        if (r < ql || l > qr) return;
        if (ql <= l && r <= qr) {
            tree[node] = true;
            return;
        }
        int mid = (l + r) / 2;
        update(2 * node, l, mid, ql, qr);
        update(2 * node + 1, mid + 1, r, ql, qr);
        tree[node] = tree[2 * node] && tree[2 * node + 1];
    }

    private boolean query(int node, int l, int r, int ql, int qr) {
        if (r < ql || l > qr) return true;
        if (tree[node]) return false;
        if (l == r) return !tree[node];
        int mid = (l + r) / 2;
        return query(2 * node, l, mid, ql, qr) &&
               query(2 * node + 1, mid + 1, r, ql, qr);
    }
}
