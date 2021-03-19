package main.java;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

//import org.xml.sax.SAXException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
//import javax.tools.*;
import java.io.*;
import java.net.URL;

public class Chatbot 
{
    private static File inputFile;
    private static File dir;
    private static DocumentBuilderFactory dbFactory;
    private static DocumentBuilder dBuilder;
    private static Document doc;

    private static Character[] charList = new Character[]{'.','!','?'};
    private Random r = new Random();
    
    
    public static void main(String[] args) throws Exception
    {
        chatbotStart();
    }
    // public Chatbot() throws Exception
    // {
        
    // }
    
    public static void chatbotStart() throws Exception
    {
        InputStream inputStream = Chatbot.class.getResourceAsStream("/main/resources/data.xml");

        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        
        
        
        ArrayList<String[]> sätze = new ArrayList<>();
        
        //for(int y = 0; y < 3; y++)
        //{
            
            Scanner scanner = new Scanner(System.in);
            String n = scanner.nextLine();
            //scanner.close();
            //scanner = null;
            n = n.toLowerCase();
            String[] aufgeteilt = n.split(" "); 
            int lastSatz = 0;
            for(int i = 0; i < aufgeteilt.length; i++)
            {
                if(Arrays.asList(charList).contains(aufgeteilt[i].charAt(aufgeteilt[i].length() - 1)))
                {
                    //if(Arrays.asList(charList).contains(aufgeteilt[i].charAt(aufgeteilt[i].length() - 1
                    //Speichern neuen Satz
                    String[] satz = Arrays.copyOfRange(aufgeteilt,lastSatz,i + 1);
                    
                    //Entfernen von Satzzeichen aus dem Satz
                    int last = satz.length - 1;
                    satz[last] = satz[last].substring(0,satz[last].length() - 1);
                    
                    //Satzzeichen vor den Satz setzen.
                    String[] zeichen = new String[]{Character.toString(aufgeteilt[i].charAt(aufgeteilt[i].length() - 1))};
                    String[] joined = new String[satz.length + 1];
                    System.arraycopy(zeichen,0,joined,0,1);
                    System.arraycopy(satz,0,joined,1,satz.length);
                    
                    //Neuen Anfang setzen
                    lastSatz = i + 1;
                    
                    //Satz zu Arraylist hinzufügen
                    sätze.add(joined);
                }
            }
                
            Interpreter interpreter = new Interpreter(doc, sätze);
            interpreter.interpretieren();
            sätze.clear();
        //}
    }
    

    
    
    
}
