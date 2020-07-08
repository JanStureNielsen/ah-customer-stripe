package ah.customer.stripe.helper;

import lombok.Getter;

import java.util.Collection;

@Getter
public class MergeHelper {

    private boolean changed = false;

    public <T> T update(T s1, T s2) {
        if (s1 == null) {
            changed = true;
            return s2;
        }
        if (!s1.equals(s2)) {
            changed = true;
            return s2;
        }
        return s1;
    }

    public static <T> T coalesce(T s1, T s2) {
        return s1 != null ? s1 : s2;
    }

    public static <T extends Collection<?>> T coalesce(T s1, T s2) {
        return s1 != null && !s1.isEmpty() ? s1 : s2;
    }

    public static String coalesce(String s1, String s2) {
        return s1 != null && !s1.trim().isEmpty() ? s1 : s2;
    }
}
