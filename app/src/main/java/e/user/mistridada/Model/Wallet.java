package e.user.mistridada.Model;

public class Wallet {
    private String credits;

    public Wallet() {
    }

    public Wallet(String credits) {
        this.credits = credits;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }
}
