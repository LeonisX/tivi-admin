package md.leonis.tivi.admin.model;

public class Category {
    private Integer catid;
    private Integer parentid = 0;
    private String catcpu;
    private String catname;
    private String catdesc;
    private Integer posit = 0;
    private String icon;
    private Access access = Access.all;
    private String sort = "newsid";
    private Order ord = Order.asc;
    private YesNo rss = YesNo.yes;
    private Integer total = 0;

    public Category(int catid, int parentid, String catcpu, String catname, String catdesc, int posit, String icon, Access access, String sort, Order ord, YesNo rss, int total) {
        this.catid = catid;
        this.parentid = parentid;
        this.catcpu = catcpu;
        this.catname = catname;
        this.catdesc = catdesc;
        this.posit = posit;
        this.icon = icon;
        this.access = access;
        this.sort = sort;
        this.ord = ord;
        this.rss = rss;
        this.total = total;
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getCatcpu() {
        return catcpu;
    }

    public void setCatcpu(String catcpu) {
        this.catcpu = catcpu;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatdesc() {
        return catdesc;
    }

    public void setCatdesc(String catdesc) {
        this.catdesc = catdesc;
    }

    public Integer getPosit() {
        return posit;
    }

    public void setPosit(int posit) {
        this.posit = posit;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Order getOrd() {
        return ord;
    }

    public void setOrd(Order ord) {
        this.ord = ord;
    }

    public YesNo getRss() {
        return rss;
    }

    public void setRss(YesNo rss) {
        this.rss = rss;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Category{" +
                "catid=" + catid +
                ", parentid=" + parentid +
                ", catcpu='" + catcpu + '\'' +
                ", catname='" + catname + '\'' +
                ", catdesc='" + catdesc + '\'' +
                ", posit=" + posit +
                ", icon='" + icon + '\'' +
                ", access=" + access +
                ", sort='" + sort + '\'' +
                ", ord=" + ord +
                ", rss=" + rss +
                ", total=" + total +
                '}';
    }
}
