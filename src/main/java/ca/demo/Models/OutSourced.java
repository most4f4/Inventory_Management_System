package ca.demo.Models;

public class OutSourced extends Part {
    private String companyName;

    public OutSourced(int id, String name, double price, int stock, int min, int max, String companyMame) {
        super(id, name, price, stock, min, max);
        this.companyName = companyMame;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
