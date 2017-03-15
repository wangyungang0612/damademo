package com.shmingjiang.mltx.service;

import java.util.Map;

import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by wdongjia on 2016/9/2.
 */
public interface NetService {

    //打包入库，特殊接口，
    @POST("/api/interface/public/process_control.process/pack_store")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Observable<String> sendData(@QueryMap Map<String, String> options);

}
