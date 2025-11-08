package pricing;

import java.util.List;

public class TimeIndexMapper {

    public static int lowerBound(List<PriceEntry> list, long fromTime) {
        int l = 0, r = list.size() - 1, ans = list.size();
        while (l <= r) {
            int mid = (l + r) / 2;
            if (list.get(mid).getTimestamp() >= fromTime) {
                ans = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return ans;
    }

    public static int upperBound(List<PriceEntry> list, long toTime) {
        int l = 0, r = list.size() - 1, ans = -1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (list.get(mid).getTimestamp() <= toTime) {
                ans = mid;
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return ans;
    }
}
