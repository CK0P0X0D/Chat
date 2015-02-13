package ua.ckopoxod.chat;

public class Contact {

    int _id;
    String _message;
    String _youmy;

    public Contact(){
    }

    public Contact(int id, String message, String _youmy){
        this._id = id;
        this._message = message;
        this._youmy = _youmy;
    }

    public Contact(String message, String _youmy){
        this._message = message;
        this._youmy = _youmy;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getMessage(){
        return this._message;
    }

    public void setMessage(String message){
        this._message = message;
    }

    public String getYoumy(){
        return this._youmy;
    }

    public void setYoumy(String youmy){
        this._youmy = youmy;
    }

}
