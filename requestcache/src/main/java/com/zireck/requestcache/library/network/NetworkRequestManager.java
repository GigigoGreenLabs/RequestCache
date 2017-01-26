package com.zireck.requestcache.library.network;

import com.zireck.requestcache.library.model.RequestModel;

public interface NetworkRequestManager {
  void sendRequest(RequestModel requestModel, NetworkResponseCallback networkResponseCallback);
}
