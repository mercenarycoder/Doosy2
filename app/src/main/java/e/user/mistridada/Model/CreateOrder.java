package e.user.mistridada.Model;

public class CreateOrder {

    String pickupaddress,deliveryaddress,packagecontent,customerid,customername,customerphone,pickupcharges,orderstatus,passedby,date,time,packagedetail,orderid;
    Double pickuplat,pickuplon,droplat,droplon;

    public CreateOrder() {
    }

    public CreateOrder(String pickupaddress, String deliveryaddress, String packagecontent, String customerid, String customername, String customerphone, String pickupcharges, String orderstatus, String passedby, String date, String time, String packagedetail, String orderid, Double pickuplat, Double pickuplon, Double droplat, Double droplon) {
        this.pickupaddress = pickupaddress;
        this.deliveryaddress = deliveryaddress;
        this.packagecontent = packagecontent;
        this.customerid = customerid;
        this.customername = customername;
        this.customerphone = customerphone;
        this.pickupcharges = pickupcharges;
        this.orderstatus = orderstatus;
        this.passedby = passedby;
        this.date = date;
        this.time = time;
        this.packagedetail = packagedetail;
        this.orderid = orderid;
        this.pickuplat = pickuplat;
        this.pickuplon = pickuplon;
        this.droplat = droplat;
        this.droplon = droplon;
    }

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getDeliveryaddress() {
        return deliveryaddress;
    }

    public void setDeliveryaddress(String deliveryaddress) {
        this.deliveryaddress = deliveryaddress;
    }

    public String getPackagecontent() {
        return packagecontent;
    }

    public void setPackagecontent(String packagecontent) {
        this.packagecontent = packagecontent;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public String getPickupcharges() {
        return pickupcharges;
    }

    public void setPickupcharges(String pickupcharges) {
        this.pickupcharges = pickupcharges;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getPassedby() {
        return passedby;
    }

    public void setPassedby(String passedby) {
        this.passedby = passedby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPackagedetail() {
        return packagedetail;
    }

    public void setPackagedetail(String packagedetail) {
        this.packagedetail = packagedetail;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Double getPickuplat() {
        return pickuplat;
    }

    public void setPickuplat(Double pickuplat) {
        this.pickuplat = pickuplat;
    }

    public Double getPickuplon() {
        return pickuplon;
    }

    public void setPickuplon(Double pickuplon) {
        this.pickuplon = pickuplon;
    }

    public Double getDroplat() {
        return droplat;
    }

    public void setDroplat(Double droplat) {
        this.droplat = droplat;
    }

    public Double getDroplon() {
        return droplon;
    }

    public void setDroplon(Double droplon) {
        this.droplon = droplon;
    }
}
