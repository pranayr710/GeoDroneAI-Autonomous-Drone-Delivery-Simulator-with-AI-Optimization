package pricing;

public class SegmentTree {

    public static class Node {
        public double min;
        public double max;
        public double sum;

        Node(double min, double max, double sum) {
            this.min = min;
            this.max = max;
            this.sum = sum;
        }

        static Node merge(Node left, Node right) {
            if (left == null) return right;
            if (right == null) return left;
            return new Node(
                    Math.min(left.min, right.min),
                    Math.max(left.max, right.max),
                    left.sum + right.sum
            );
        }
    }

    private final int n;
    private final Node[] tree;

    public SegmentTree(int n) {
        this.n = n;
        this.tree = new Node[4 * Math.max(1, n)];
    }

    public SegmentTree(double[] arr) {
        this.n = arr.length;
        this.tree = new Node[4 * Math.max(1, n)];
        if (n > 0) {
            build(arr, 1, 0, n - 1);
        }
    }

    private void build(double[] arr, int idx, int l, int r) {
        if (l == r) {
            tree[idx] = new Node(arr[l], arr[l], arr[l]);
            return;
        }
        int mid = (l + r) / 2;
        build(arr, 2 * idx, l, mid);
        build(arr, 2 * idx + 1, mid + 1, r);
        tree[idx] = Node.merge(tree[2 * idx], tree[2 * idx + 1]);
    }

    public Node query(int ql, int qr) {
        if (n == 0 || ql > qr) return null;
        ql = Math.max(ql, 0);
        qr = Math.min(qr, n - 1);
        if (ql > qr) return null;
        return query(1, 0, n - 1, ql, qr);
    }

    private Node query(int idx, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return null;
        if (ql <= l && r <= qr) return tree[idx];
        int mid = (l + r) / 2;
        Node left = query(2 * idx, l, mid, ql, qr);
        Node right = query(2 * idx + 1, mid + 1, r, ql, qr);
        return Node.merge(left, right);
    }
}
