package com.kabasonic.messenger.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvT97wQs:APA91bHt0r0dW9WWvcn6p1BSCTIFbJV0tmzALAmc5e-_DtBQi9_6fivzvDzkj1T1tsckdZmlo8ySZeCPr-5BGiGw7E-92Ezt_Inenju2Q_tKJV3HgHmZHEfwIacZ9kfQJiO3Z8a2rfiJ"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
