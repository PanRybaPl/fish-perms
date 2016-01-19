package pl.panryba.mc.permissions;

import java.util.Collection;

/**
 * @author PanRyba.pl
 */
public class StringUtils {
    public static String join(Collection<String> items, String separator) {
        StringBuilder b = new StringBuilder();

        boolean first = true;
        for(String item : items) {
            if(first) {
                first = false;
            } else {
                b.append(separator);
            }
            b.append(item);
        }

        return b.toString();
    }
}
