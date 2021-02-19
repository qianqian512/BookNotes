package org.apache.dubbo.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.Result;

public class SyncResponse extends AppResponse {

	private static final long serialVersionUID = 1L;
	
	public SyncResponse() {
	}
	
	public SyncResponse(Object t) {
		super(t);
	}

    @Override
    public <U> CompletableFuture<U> thenApply(Function<Result, ? extends U> fn) {
        throw new UnsupportedOperationException("AppResponse represents an concrete business response, there will be no status changes, you should get internal values directly.");
    }

}
