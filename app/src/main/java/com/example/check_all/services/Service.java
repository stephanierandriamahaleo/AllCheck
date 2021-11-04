package com.example.check_all.services;

import com.example.check_all.models.Badge;
import com.example.check_all.models.CoupeFile;
import com.example.check_all.models.Event;
import com.example.check_all.models.Ticket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Service {
    public static Ticket getTicket(String body) {
        JsonObject jsonBody = new Gson().fromJson(body, JsonObject.class);
        Ticket ticket = new Ticket();
        String nom = jsonBody.getAsJsonObject("result").get("nom").getAsString();
        if (jsonBody.getAsJsonObject("result").get("prenom") != null) {
            nom += " " + jsonBody.getAsJsonObject("result").get("prenom").getAsString();
        }
        String telephone = jsonBody.getAsJsonObject("result").get("phone").getAsString();
        String place = jsonBody.getAsJsonObject("result").get("place_id").getAsString();
        String section = jsonBody.getAsJsonObject("result").get("section_id").getAsString();

        if(!jsonBody.getAsJsonObject("result").get("check_date").isJsonNull()) {
            String checkDate = jsonBody.getAsJsonObject("result").get("check_date").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(checkDate);
                dateFormat.applyPattern("dd/MM/yyyy à HH:mm");
                ticket.setCheckDate(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!jsonBody.getAsJsonObject("result").get("date_pre_print_check").isJsonNull()) {
            String checkDate = jsonBody.getAsJsonObject("result").get("date_pre_print_check").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(checkDate);
                dateFormat.applyPattern("dd/MM/yyyy à HH:mm");
                ticket.setPrePrintCheckDate(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String evenement = jsonBody.getAsJsonObject("result").get("titre_evenement").getAsString();
        String idEvenement = jsonBody.getAsJsonObject("result").get("id_event").getAsString();

        ticket.setEvenement(evenement);
        ticket.setNom(nom);
        ticket.setTelephone(telephone);
        ticket.setPlace(place);
        ticket.setSection(section);
        return ticket;
    }

    public static Badge getBadge(String body) {
        JsonObject jsonBody = new Gson().fromJson(body, JsonObject.class);
        Badge badge = new Badge();
        String nom = jsonBody.getAsJsonObject("result").get("nom").getAsString();
        if (jsonBody.getAsJsonObject("result").get("prenom") != null) {
            nom += " " + jsonBody.getAsJsonObject("result").get("prenom").getAsString();
        }
        String telephone = jsonBody.getAsJsonObject("result").get("phone").getAsString();

        if(!jsonBody.getAsJsonObject("result").get("check_date").isJsonNull()) {
            String checkDate = jsonBody.getAsJsonObject("result").get("check_date").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(checkDate);
                dateFormat.applyPattern("dd/MM/yyyy à HH:mm");
                badge.setCheckDate(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String evenement = jsonBody.getAsJsonObject("result").get("titre_evenement").getAsString();
        String email = jsonBody.getAsJsonObject("result").get("email").getAsString();
        String titre = jsonBody.getAsJsonObject("result").get("titre").getAsString();
        if (!jsonBody.getAsJsonObject("result").get("porte").isJsonNull()) {
            String porte = jsonBody.getAsJsonObject("result").get("porte").getAsString();
            badge.setPorte(porte);
        }
        if (!jsonBody.getAsJsonObject("result").get("niveau").isJsonNull()) {
            String niveau = jsonBody.getAsJsonObject("result").get("niveau").getAsString();
            badge.setNiveau(niveau);
        }
        badge.setEvenement(evenement);
        badge.setNom(nom);
        badge.setTelephone(telephone);
        badge.setEmail(email);
        badge.setTitre(titre);

        return badge;
    }

    public static CoupeFile getCoupeFile(String body) {
        JsonObject jsonBody = new Gson().fromJson(body, JsonObject.class);
        CoupeFile coupeFile = new CoupeFile();
        String nom = "";
        if (jsonBody.getAsJsonObject("result").get("nom") != null) {
            nom += jsonBody.getAsJsonObject("result").get("nom").getAsString();
        }
        if (jsonBody.getAsJsonObject("result").get("prenom") != null) {
            nom += " " + jsonBody.getAsJsonObject("result").get("prenom").getAsString();
        }
        String telephone = jsonBody.getAsJsonObject("result").get("phone").getAsString();

        if(!jsonBody.getAsJsonObject("result").get("check_date").isJsonNull()) {
            String checkDate = jsonBody.getAsJsonObject("result").get("check_date").getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(checkDate);
                dateFormat.applyPattern("dd/MM/yyyy à HH:mm");
                coupeFile.setCheckDate(dateFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String evenement = jsonBody.getAsJsonObject("result").get("evenement").getAsString();
        String vehicleNumber = jsonBody.getAsJsonObject("result").get("numero_vehicle").getAsString();

        if (!jsonBody.getAsJsonObject("result").get("porte").isJsonNull()) {
            String porte = jsonBody.getAsJsonObject("result").get("porte").getAsString();
            coupeFile.setPorte(porte);
        }

        coupeFile.setEvenement(evenement);
        coupeFile.setNom(nom);
        coupeFile.setPhone(telephone);
        coupeFile.setNumeroVehicle(vehicleNumber);

        return coupeFile;
    }

    public static Event getEvent(JsonObject eventJsonObject) {
        Event event = new Event();

        event.setId(Integer.parseInt(eventJsonObject.get("id").getAsString()));
        event.setTitle(eventJsonObject.get("titre_evenement").getAsString());
        event.setDate(eventJsonObject.get("date_debut_event").getAsString());
        event.setPlace(eventJsonObject.get("nom_salle").getAsString());
        if(!eventJsonObject.get("image_event").isJsonNull()) {
            event.setPictureUrl(eventJsonObject.get("image_event").getAsString());
        }

        return event;
    }

}
