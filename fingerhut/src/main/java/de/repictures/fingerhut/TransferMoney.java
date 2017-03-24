package de.repictures.fingerhut;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TransferMoney extends HttpServlet {

    private DatastoreService datastore;
    private ArrayList<Entity> tempArrayList = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String intendedPurpose = URLDecoder.decode(req.getParameter("intendedpurpose"), "UTF-8");
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));
        DateTimeFormatter f = DateTimeFormat.forPattern("dd MM yyyy HH:mm:ss.SSSS");
        DateTime dateTime = f.parseDateTime(URLDecoder.decode(req.getParameter("datetime"), "UTF-8"));
        datastore = DatastoreServiceFactory.getDatastoreService();

        try {
            Key transferKey = KeyFactory.createKey("dateandtime", dateTime.toString());
            Entity transfer = new Entity("Transfer", transferKey);
            Entity sender = getEntity("accountnumber", senderAccountnumber, "Account");
            Entity receiver = getEntity("accountnumber", receiverAccountnumber, "Account");

            float senderBalance = sender != null ? (float) sender.getProperty("balance") : 0;
            if(senderBalance >= amount && receiver != null){ //Überweisung klappt
                transfer.setProperty("sender", sender.getKey());
                transfer.setProperty("receiver", receiver.getKey());
                transfer.setProperty("amount", amount);
                transfer.setProperty("datetime", dateTime);
                transfer.setProperty("purpose", intendedPurpose);
                datastore.put(transfer);

                Entity savedTransfer = datastore.get(transferKey);
                getTransferArrays(sender);
                tempArrayList.add(savedTransfer);
                sender.setProperty("transferarray", tempArrayList);
                datastore.put(sender);
                getTransferArrays(receiver);
                tempArrayList.add(savedTransfer);
                receiver.setProperty("transferarray", tempArrayList);
                datastore.put(receiver);

                resp.getWriter().println("3");
            } else if(receiver != null){ //Konto des Senders ist nicht ausreichend gedeckt
                resp.getWriter().println("1");
            } else { //Empfänger existiert nicht
                resp.getWriter().println("2");
            }

        } catch (Exception e){
            e.printStackTrace();
            resp.getWriter().println("0");
        }
    }

    private Entity getEntity(String kind, String name, String entityTitle){
        Key senderKey = KeyFactory.createKey(kind, name);
        Query loginQuery = new Query(entityTitle, senderKey);
        List<Entity> entityList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if(entityList.size() > 0){
            return entityList.get(0);
        } else {
            return null;
        }
    }

    private void getTransferArrays(Entity entity){
        tempArrayList = (ArrayList<Entity>) entity.getProperty("transferarray");
        if (tempArrayList == null) tempArrayList = new ArrayList<>();
    }
}
