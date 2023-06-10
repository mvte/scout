package scout.sniper;

import scout.model.RutgersCourseDatabase;
import scout.model.RutgersSection;
import scout.model.URLType;

import java.util.ArrayList;


public class RutgersSnipe extends Snipe {

    /** pre-filled register form */
    private static final String register = "https://sims.rutgers.edu/webreg/editSchedule.htm?login=cas&semesterSelection=92023&indexList=%s";

    private final String index;

    public RutgersSnipe(String url) {
        this.index = url;
        this.url = String.format(register, index);
        this.itemName = parseItemName();
        this.users = new ArrayList<>();
        this.urlType = URLType.RUTGERS;
    }

    /**
     *
     * @return true if in stock
     */
    @Override
    public boolean inStock() {
        return RutgersCourseDatabase.getInstance().isOpen(index);
    }

    @Override
    public String parseItemName() {
        RutgersSection rs = RutgersCourseDatabase.getInstance().getSection(index);
        if(rs == null) {
            return "ITEM_NAME_NOT_FOUND";
        }

        return String.format("%s | section %s | %s", rs.getIndex(), rs.getSection(), rs.getTitle()).toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RutgersSnipe)) {
            return false;
        }

        return this.index.equals(((RutgersSnipe)obj).index);
    }

    @Override
    public int hashCode() {
        return index.hashCode();
    }

    public static void main(String[] args) {
        RutgersSnipe rutgersSnipe = new RutgersSnipe("15534");

        System.out.println(rutgersSnipe.getItemName());
        System.out.println(rutgersSnipe.inStock());
    }
}
