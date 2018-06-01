package whatsapp.autoresponder.Model.Rest;

import java.util.ArrayList;

/**
 * Created by SAISHRADDHA on 22-05-2017.
 */

public class ListResponse<T> {

    private String message;
    private int flag;
    private ArrayList<T> data;
    private int code;

    public ArrayList<T> data() {
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
