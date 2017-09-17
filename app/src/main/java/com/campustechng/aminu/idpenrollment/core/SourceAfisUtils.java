package com.campustechng.aminu.idpenrollment.core;

import com.campustechng.aminu.idpenrollment.sourceafis.simple.AfisEngine;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Fingerprint;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Person;

import java.util.ArrayList;

/**
 * Created by MuhammadAmin on 8/29/2017.
 */

public class SourceAfisUtils {

    private static AfisEngine afisEngine = new AfisEngine();
    public static byte[] generateTemplate(byte[] image, int width, int height) {

        Fingerprint fingerprint = new Fingerprint();
        try {
            fingerprint.setImage(Operations.convertImageTo2D(image,width,height));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Person person = new Person(fingerprint);
        afisEngine.extract(person);
        byte[] template = fingerprint.getIsoTemplate();
        return template;
    }

    public static Person identify(ArrayList<Person> database, Person person) {
        Iterable<Person> matches = afisEngine.identify(person, database);
        for(Person match:matches){
            System.out.println("Matched::"+match.getId());
            return match;
        }
        return  null;
    }
}
