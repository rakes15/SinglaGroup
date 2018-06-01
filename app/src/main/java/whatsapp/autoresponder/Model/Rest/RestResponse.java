package whatsapp.autoresponder.Model.Rest;

/**
 * Created by SAISHRADDHA on 22-05-2017.
 */

public class RestResponse<T> {

    private String message;
    private int flag;
    private T data;
    private int code;

    public T data() {
        return data;
    }

    public int flag() {
        return flag;
    }

    public String message() {
        return message;
    }

    public int code(){
        return code;
    }
}
