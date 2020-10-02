package e.user.mistridada.Remote;

import e.user.mistridada.Model.FCMResponse;
import e.user.mistridada.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAnSxd9lQ:APA91bHOBXD1FohJuP0TcrlWEVZWCna7ZoLElRjd87xCkyuKpA3iKkfcVI_2mIb2-4ZjSX1VJD3DX1rBRZShxxSmix6qN-Q3F89A0Ubx-Xl23--P2rON54WnpzPauMBGfRHJfzMDDexQ"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
