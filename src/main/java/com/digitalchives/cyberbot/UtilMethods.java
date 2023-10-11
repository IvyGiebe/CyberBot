package com.digitalchives.cyberbot;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UtilMethods {

    public boolean isURL(String link){
        try {
            new URL(link).toURI();
            return true;
        }
        catch(URISyntaxException e){
            return false;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
