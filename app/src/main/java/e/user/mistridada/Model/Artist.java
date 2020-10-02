package e.user.mistridada.Model;

public class Artist {

    String name,phone,address,service;

    public Artist(){

    }

    public Artist(String name, String phone, String address, String service) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getService() {
        return service;
    }
}
