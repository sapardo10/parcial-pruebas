package retrofit2;

import java.io.IOException;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;

final class OkHttpCall<T> implements Call<T> {
    @Nullable
    private final Object[] args;
    private volatile boolean canceled;
    @GuardedBy("this")
    @Nullable
    private Throwable creationFailure;
    @GuardedBy("this")
    private boolean executed;
    @GuardedBy("this")
    @Nullable
    private Call rawCall;
    private final ServiceMethod<T, ?> serviceMethod;

    OkHttpCall(ServiceMethod<T, ?> serviceMethod, @Nullable Object[] args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
    }

    public OkHttpCall<T> clone() {
        return new OkHttpCall(this.serviceMethod, this.args);
    }

    public synchronized Request request() {
        Call call = this.rawCall;
        if (call != null) {
            return call.request();
        } else if (this.creationFailure == null) {
            try {
                Call createRawCall = createRawCall();
                this.rawCall = createRawCall;
                return createRawCall.request();
            } catch (Throwable e) {
                Utils.throwIfFatal(e);
                this.creationFailure = e;
                throw e;
            } catch (IOException e2) {
                this.creationFailure = e2;
                throw new RuntimeException("Unable to create request.", e2);
            }
        } else if (this.creationFailure instanceof IOException) {
            throw new RuntimeException("Unable to create request.", this.creationFailure);
        } else if (this.creationFailure instanceof RuntimeException) {
            throw ((RuntimeException) this.creationFailure);
        } else {
            throw ((Error) this.creationFailure);
        }
    }

    public void enqueue(Callback<T> callback) {
        Utils.checkNotNull(callback, "callback == null");
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already executed.");
            }
            this.executed = true;
            Call call = this.rawCall;
            Throwable failure = this.creationFailure;
            if (call == null && failure == null) {
                try {
                    Call createRawCall = createRawCall();
                    this.rawCall = createRawCall;
                    call = createRawCall;
                } catch (Throwable t) {
                    Utils.throwIfFatal(t);
                    this.creationFailure = t;
                    failure = t;
                }
            }
        }
        if (failure != null) {
            callback.onFailure(this, failure);
            return;
        }
        if (this.canceled) {
            call.cancel();
        }
        call.enqueue(new OkHttpCall$1(this, callback));
    }

    public synchronized boolean isExecuted() {
        return this.executed;
    }

    public Response<T> execute() throws IOException {
        Call call;
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already executed.");
            }
            this.executed = true;
            if (this.creationFailure == null) {
                call = this.rawCall;
                if (call == null) {
                    try {
                        Call createRawCall = createRawCall();
                        this.rawCall = createRawCall;
                        call = createRawCall;
                    } catch (Throwable e) {
                        Utils.throwIfFatal(e);
                        this.creationFailure = e;
                        throw e;
                    }
                }
            } else if (this.creationFailure instanceof IOException) {
                throw ((IOException) this.creationFailure);
            } else if (this.creationFailure instanceof RuntimeException) {
                throw ((RuntimeException) this.creationFailure);
            } else {
                throw ((Error) this.creationFailure);
            }
        }
        if (this.canceled) {
            call.cancel();
        }
        return parseResponse(call.execute());
    }

    private Call createRawCall() throws IOException {
        Call call = this.serviceMethod.toCall(this.args);
        if (call != null) {
            return call;
        }
        throw new NullPointerException("Call.Factory returned null.");
    }

    Response<T> parseResponse(Response rawResponse) throws IOException {
        ResponseBody rawBody = rawResponse.body();
        Builder newBuilder = rawResponse.newBuilder();
        Response<T> contentType = rawBody.contentType();
        rawResponse = newBuilder.body(new OkHttpCall$NoContentResponseBody(contentType, rawBody.contentLength())).build();
        int code = rawResponse.code();
        if (code >= 200) {
            if (code < 300) {
                if (code != 204) {
                    if (code != 205) {
                        OkHttpCall$ExceptionCatchingRequestBody catchingBody = new OkHttpCall$ExceptionCatchingRequestBody(rawBody);
                        try {
                            return Response.success(this.serviceMethod.toResponse(catchingBody), rawResponse);
                        } catch (RuntimeException e) {
                            catchingBody.throwIfCaught();
                            throw e;
                        }
                    }
                }
                rawBody.close();
                return Response.success(null, rawResponse);
            }
        }
        try {
            contentType = Response.error(Utils.buffer(rawBody), rawResponse);
            return contentType;
        } finally {
            rawBody.close();
        }
    }

    public void cancel() {
        this.canceled = true;
        synchronized (this) {
            Call call = this.rawCall;
        }
        if (call != null) {
            call.cancel();
        }
    }

    public boolean isCanceled() {
        boolean z = true;
        if (this.canceled) {
            return true;
        }
        synchronized (this) {
            if (this.rawCall == null || !this.rawCall.isCanceled()) {
                z = false;
            }
        }
        return z;
    }
}
