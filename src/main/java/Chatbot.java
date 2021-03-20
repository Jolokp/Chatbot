package main.java;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class Chatbot 
{
    
    // javac -d classes -classpath classes src\main\java\Reagierer.java
    // javac -d classes -classpath classes src\main\java\Chatbot.java
    // java -classpath classes;src main.java.Chatbot

    private static Character[] charList = new Character[]{'.','!','?'};
    
    
    public static void main(String[] args) throws Exception
    {
        chatbotStart();
    }
    
    public static void chatbotStart() throws Exception
    {
        
        
        
        
        ArrayList<String[]> sätze = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        
        for(int y = 0; y < 2; y++)
        {

            InputStream inputStream = Chatbot.class.getResourceAsStream("/main/resources/data.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            
            String n = scanner.nextLine();
            n = n.toLowerCase();
            String[] aufgeteilt = n.split(" "); 
            int lastSatz = 0;
            for (int i = 0; i < aufgeteilt.length; i++)
            {
                if (Arrays.asList(charList).contains(aufgeteilt[i].charAt(aufgeteilt[i].length() - 1)))
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
                
            Reagierer reagierer = new Reagierer(doc, sätze);
            System.out.println(reagierer.interpretieren());
            sätze.clear();
            inputStream.close();
        }

        scanner.close();
    }
    

    
    
    
}
