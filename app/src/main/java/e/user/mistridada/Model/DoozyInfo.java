package e.user.mistridada.Model;

public class DoozyInfo {
    String versionname, versioncode, cost, updatever, offer1, offer2, rest, restfare, usable;

    public DoozyInfo() {
    }

    public DoozyInfo(String versionname, String versioncode, String cost, String updatever, String offer1, String offer2, String rest, String restfare, String usable) {
        this.versionname = versionname;
        this.versioncode = versioncode;
        this.cost = cost;
        this.updatever = updatever;
        this.offer1 = offer1;
        this.offer2 = offer2;
        this.rest = rest;
        this.restfare = restfare;
        this.usable = usable;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getUpdatever() {
        return updatever;
    }

    public void setUpdatever(String updatever) {
        this.updatever = updatever;
    }

    public String getOffer1() {
        return offer1;
    }

    public void setOffer1(String offer1) {
        this.offer1 = offer1;
    }

    public String getOffer2() {
        return offer2;
    }

    public void setOffer2(String offer2) {
        this.offer2 = offer2;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    public String getRestfare() {
        return restfare;
    }

    public void setRestfare(String restfare) {
        this.restfare = restfare;
    }

    public String getUsable() {
        return usable;
    }

    public void setUsable(String usable) {
        this.usable = usable;
    }
}
