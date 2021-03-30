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
        Scanner scanner = new Scanner(System.in, "Cp850");
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
          public void run()
          {
            scanner.close();
          }
        });

        while(true)
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
            if(sätze.size() == 0)
            {
                String[] empty = new String[]{""};
                String[] neu = new String[aufgeteilt.length + 1];
                System.arraycopy(empty,0,neu,0,1);
                System.arraycopy(aufgeteilt,0,neu,1,aufgeteilt.length);
                sätze.add(aufgeteilt);
            }
            String art = "";

            for(int i = 0; i < sätze.size(); i++) 
            {
                if(sätze.get(i)[0].equals(Character.toString(charList[0]))) 
                {
                    art = "statement";
                }
                
                else if(sätze.get(i)[0].equals(Character.toString(charList[1]))) 
                {
                    art = "exclamation";
                }
                
                else if(sätze.get(i)[0].equals(Character.toString(charList[2]))) 
                {
                    art = "question";
                }
            
                else
                {
                    //art = "messages";
                    System.out.println("Ok");
                }

                Reagierer reagierer = new Reagierer(doc);
                System.out.print(reagierer.interpretieren(sätze.get(i), art));

                
            }  
            
            System.out.println();
            sätze.clear();
            inputStream.close();
            
        }

        
    }
    
    
}
