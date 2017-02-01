package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.model.RequestModel;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public interface NetworkRequestManager {
  Observable<Response<ResponseBody>> getRequestStreamFor(RequestModel requestModel);
}
