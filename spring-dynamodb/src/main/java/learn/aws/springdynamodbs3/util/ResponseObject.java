package learn.aws.springdynamodbs3.util;

import java.io.Serializable;

public class ResponseObject implements Serializable {
    private static final long serialVersionUID = 8188983417950399567L;
    private boolean error;
    private String message;
    private Object data;

    private ResponseObject(boolean error, String message, Object data) {
        super();
        this.error = error;
        this.data = data;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public Object getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean error;
        private String message = null;
        private Object data = null;

        public Builder setError(boolean error) {
            this.error = error;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public ResponseObject build() {
            return new ResponseObject(error, message, data);
        }
    }
}
