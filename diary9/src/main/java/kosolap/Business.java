package kosolap;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Kosolap on 31.03.2015.
 */
public class Business implements Comparable<Business> {

    private Integer id;
    private String name;
    private String wtd;
    private String category;
    private Integer didit;
    private Date date;

    public Business(String name, String wtd, String category, Date date) {
        this.name = name;
        this.wtd = wtd;
        this.category = category;
        this.date = date;
    }

    public Business() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWtd() {
        return wtd;
    }

    public void setWtd(String wtd) {
        this.wtd = wtd;
    }

    public void setDidit(Integer didit) {
        this.didit = didit;
    }

    public Integer getDidit() {
        return didit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        /*return "Business{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wtd='" + wtd + '\'' +
                ", category='" + category + '\'' +
                ", didit=" + didit +
                ", date=" + date +
                '}';
        */
        String diditStr = "";

        if(didit == null)
            diditStr = "не сделано";

        else diditStr = "сделано";


        return String.format("%d %s Категория: %s Описание: %s. Статус:%s. Дата:",id, name, category, wtd, diditStr) + date;

    }

    @Override
    public int compareTo(@NotNull Business o) {

        if(this.getDate().before(o.getDate()))
            return -1;
        else if(this.getDate().after(o.getDate())) return 1;
        else return 0;
    }
}
