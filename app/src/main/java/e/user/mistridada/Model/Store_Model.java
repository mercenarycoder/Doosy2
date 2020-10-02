package e.user.mistridada.Model;

public class Store_Model {

    String address, email, image, merchant, name, password, status, type, phone,menu;
    Double latitude, longitude;

    public Store_Model() {
    }

    public Store_Model(String address, String email, String image, String merchant,
                       String name, String password, String status, String type,
                       String phone,String menu, String latitude, String longitude) {
        this.address = address;
        this.email = email;
        this.image = image;
        this.merchant = merchant;
        this.name = name;
        this.password = password;
        this.status = status;
        this.type = type;
        this.phone = phone;
        if(!latitude.equals("")) {
            this.latitude =Double.parseDouble(latitude);
        }
        else
        {
            this.longitude=0.0;
        }
        this.menu=menu;
        if(!longitude.equals("")) {
            this.longitude =Double.parseDouble(longitude);
        }
        else
        {
            this.longitude=0.0;
        }
    }

    public String getMenu() {
        return menu;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
