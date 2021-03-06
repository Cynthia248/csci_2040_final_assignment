package chat_app;

import chat_app.server.Message;
import chat_app.server.messagetype;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jsonHandler {
    //saved every single time a message is sent

    private String json; //json formatted log
    private String fileName; //'database for messages'

    jsonHandler(String f, String json){
        fileName = f;
        this.json = json;
    }

    jsonHandler(String f)
    {
        fileName = f;
        this.json = "";
    }

    String getInfoToWrite(){
        //parse json to get client,users,msg,type,time only

        Pattern p = Pattern.compile("(?<=:)(.*?)(,)");
        //match after : to , (Json altered to make life easier :D)
        Matcher matcher = p.matcher(json);
        String s = "";
        while (matcher.find()) {
            s += matcher.group();
        }
        return s;
    }

    void generateHeader() throws Exception{
        //file.exists() will be done on chatUI. If doesn't exist, trigger this.

        FileWriter file = new FileWriter(new File(fileName));
        file.write("Client,Users,Message,MessageType,TimeStamps\n");
        file.close();
    }

    void writeJson() throws Exception{
        //Write into CSV

        FileWriter file = new FileWriter(new File(fileName),true);
        //fix formatting
        String toWrite = getInfoToWrite().substring(0, getInfoToWrite().length() - 1); //last letter is currently a ','
        toWrite += "\n";
        file.write(toWrite);
        file.close();
    }

    String readFile(){
        Scanner input = new Scanner(fileName);
        String line = "";
        while(input.hasNextLine()) {
            line += input.nextLine() + "\n";
        }
        return line;
    }


    //NOT TESTED (need Add UserID)
    ArrayList<Message> filterCertainUsers(ArrayList<String> Users) throws Exception{
        //Return List of Message class of those with certain Users('chatroom')

        ArrayList<Message> m = new ArrayList<>();



        Scanner input = new Scanner(new File(fileName));
        String data = "";
        while(input.hasNextLine()) {
            data += input.nextLine() + "\n";
        }
        String[] splitted = data.split(",");
        Scanner s = new Scanner(data).useDelimiter("\n");
        s.nextLine(); //skip header
        Boolean key = true;
        while(s.hasNextLine()) {
            String[] datas = s.nextLine().split(",");
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> datas:");
            //for(String d:datas) System.out.print(d + " !!! ");
            for (String u : Users) {
                //System.out.println("User: " + u);
                //System.out.println("datas[1]" + datas[2]);
                if (datas[1].contains(u)) {
                    //System.out.println("Entering whatever this is");
                    key = false;
                }
            }
            //String client, ArrayList<String> user, String message
            if(key) {
                //add appropriate class
                Message mm = new Message(datas[0],Users,datas[3],datas[1]);
                //set message type
                mm.SetType(messagetype.valueOf(datas[4]));
                m.add(mm);
            }
        }

        return m;
    }


    //TODO: ADDING THE CLIENT NAME AND MESSAGE INTO PANE (and maybe parsing the appropriate message type)
    //pass filterCertainUsers here to get respective users and messages
    public ArrayList<HashMap<String,String>>  createHistory(ArrayList<Message> m){
        //A function that loops through list of Messages, get clientname and the message

        ArrayList<HashMap<String,String>> message_List = new ArrayList<>();

        for(Message message:m){
            HashMap<String,String> messages = new HashMap<>();
            message.getClientName();
            message.getMessage();
            message.getType();
            if(message.getType()== messagetype.TEXT)
            {
                messages.put(message.getClientName(),message.getMessage());
            }
            message_List.add(messages);
            //I'll leave the linking to the pane to mushy :D
        }

    return message_List;
    }


}
