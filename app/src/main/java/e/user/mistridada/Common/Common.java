package e.user.mistridada.Common;

import e.user.mistridada.Model.Rider;
import e.user.mistridada.Model.Sender;
import e.user.mistridada.Remote.FCMClient;
import e.user.mistridada.Remote.IFCMService;
import e.user.mistridada.Remote.IGoogleAPI;
import e.user.mistridada.Remote.RetrofitClient;

public class Common {

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String mistri_tbl = "Mistri";
    public static final String user_mistri_tbl = "MistriInformation";
    public static final String electrician_tbl = "Electrician";
    public static final String user_electrician_tbl = "ElectricianInformation";
    public static final String plumber_tbl = "Plumber";
    public static final String user_plumber_tbl = "PlumberInformation";
    public static final String mechanic_tbl = "Mechanic";
    public static final String user_mechanic_tbl = "MechanicInformation";
    public static final String carpenter_tbl = "Carpenter";
    public static final String user_carpenter_tbl = "CarpenterInformation";
    public static final String painter_tbl = "Painter";
    public static final String user_painter_tbl = "PainterInformation";
    public static final String loader_tbl = "Loader";
    public static final String user_loader_tbl = "LoaderInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl= "Tokens";
    public static final String cleanerandpest_tbl = "CleanerAndPest";
    public static final String user_cleanerandpest_tbl = "CleanerpestInformation";
    public static final String create_order = "customerorder";
    public static final String searchpartner = "SearchPartner";
    public static final String non_veg = "nonveg";


    public static final String user_field= "rider_usr"; // we need different key with driver app because we have case: one phone installed both rider app and driver app
    public static final String pwd_field= "rider_pwd";
    public static final String phone_field= "rider_phone";

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final int PICK_IMAGE_REQUEST = 9999;
    public static Rider currentUser = new Rider();


    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
