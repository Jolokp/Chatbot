package main.java;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.SAXException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.tools.*;
import java.io.*;

public class Chatbot 
{
    private File inputFile;
    private File dir;
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    
    private Scanner scanner;
    private Random r = new Random();
    private String[] aufgeteilt;
    private ArrayList<String[]> sätze;
    private Character[] charList = new Character[]{'.','!','?'};
    
    
    
    public Chatbot() throws Exception
    {
        
    }
    
    public void chatbotStart() throws Exception
    {
        
        inputFile = new File("data.xml");
        dir = inputFile.getCanonicalFile().getParentFile();
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        
        
        
        sätze = new ArrayList<>();
        
        //for(int y = 0; y < 3; y++)
        //{
            
            scanner = new Scanner(System.in);
            String n = scanner.nextLine();
            scanner.close();
            scanner = null;
            n = n.toLowerCase();
            aufgeteilt = n.split(" "); 
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
                
            interpretieren();
            sätze.clear();
        //}
    }
    

    public void interpretieren() throws Exception 
    {
        
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
        
            Element base = (Element) doc.getElementsByTagName(art).item(0);
            NodeList nList = base.getElementsByTagName("phrase");
            boolean correct = true;
            Node value = null;
            
            
            
            
            //{("joris","hat"),("cock","nicht")}
            
            //for Schleife zum durchgehen der Phrasen
            for(int a = 0; a < nList.getLength(); a++)
            {
                //Variable um eventuell fortschreiten in der phrase zu speichern 
                //{("joris","hat"),("cock","nicht")}
                //von der ersten runden klammer dauerhaft in die zweite
                int aktuellesD = 0;
                
                //aufteilen der phrase am Komma
                ArrayList<String[]> satzteile = new ArrayList<>();
                String[] komma = nList.item(a).getTextContent().split(",");
                
                //einzelne teile nach spaces getrennt
                for(int b = 0; b < komma.length; b++)
                {
                    String[] spaces = komma[b].split(" ");
                    satzteile.add(spaces);
                }
                
                //for Schleife zum durchgehen des Arrays hier steht n = 1, weil der index 0
                //im array das satzzeichen ist.
                for(int c = 1; c < sätze.get(i).length; c++)
                {
                    
                    correct = true;
                    
                    //{("joris","hat"),("cock","nicht")} erste runde klammer überprüft und richtig 
                    //setzt bei aktuellesd = 1 wieder ein in arraylist
                    for(int d = aktuellesD; d < satzteile.size(); d++)
                    {
                        //geht aktuelle runde klammer durch und vergleicht parallel mit worten des satzes
                        for(int e = 0; e < satzteile.get(d).length; e++)
                        {
                            try 
                            {
                                //parallele überprüfung mit der gesamten runden klammer 
                                //wenn nicht gleich wird zum nächsten wort des satzes gesprungen
                                if(!satzteile.get(d)[e].equals(sätze.get(i)[c+e]))
                                {
                                    correct = false;
                                    break;
                                }
                            }
                            catch(Exception x) 
                            {
                                //{("joris","hat"),("cock","nicht")} Joris hat einen cock.
                                //tritt ein wenn runde klammer über den Satz hinausgeht
                                correct = false;
                                break;
                            }
                        }
                        if(!correct) 
                        {
                           break; 
                        }
                        
                        //falls runde klammer erfolgreich verglichen und richtig ist wird nächste runde klammer 
                        //genommen
                        aktuellesD++;
                        
                        //schon überprüfte worte werden mit der anzahl der elemente in der runden Klammer
                        //übersprungen
                        c = c + satzteile.get(d).length - 1;
                        break;
                    }
                }
                //die gesamte phrase wurde erfolgreich durchgegangen
                if(correct)
                {
                    value = nList.item(a);
                    break;
                }
                
            }
            
            //bearbeiten von sinn eines satzes
            reagieren(value,sätze.get(i));
            
        }
    }
    
    
    
    public void reagieren(Node phrase, String[] satz) throws Exception
    {
        //Die inputId rausfinden
        Element parent = (Element) phrase.getParentNode();
        String inputId = parent.getAttribute("inputId");
        
        //den entsprechenden Inhalt ermitteln
        
        NodeList aList = doc.getElementsByTagName("answer");
        String code = "";
        
        for (int j = 0; j < aList.getLength(); j++) 
        {
            Element e = (Element) aList.item(j);
            if(e.getAttribute("outputId").equals(inputId)) 
            {
                code = e.getTextContent(); 
                break;
            }
        }
        
        dir = inputFile.getCanonicalFile().getParentFile();
        File[] files = dir.listFiles();
        
        for (File file : files)
        {
            if (file.getName().startsWith("Aktion."))
            {
                System.out.println(file.getName());
                file.delete();
            }
        }
        
        File action = new File("Aktion.java");
        boolean lol = action.createNewFile();
        System.out.println(lol);
        //Die Java datei erstellen
        PrintWriter writer = new PrintWriter(action, "UTF-8");
        writer.write(
            "public class Aktion\n" 
          + "{\n"
          +     "\tprivate String[] satz;\n"
          +     "\tpublic Aktion(String[] satz)\n" 
          +     "\t{\n"
          +         "\t\tthis.satz = satz;\n"
          +         "\t\texecute();\n"
          +     "\t}\n\n"
          +     "\tpublic static void execute()\n"
          +     "\t{");
        writer.write(code);
        writer.write("}\n}");
        writer.close();
        //Compiled die oben erstellte Java datei und führt sie aus
        //JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println("processing...");
        Process compile = Runtime.getRuntime().exec("cmd /c javac \"" + action.getCanonicalPath() + "\"");
        //int b = compiler.run(null, null, null, "Aktion.java");
        System.out.println("finished");
        
        
        if (action.exists())
        {
            try
            {
                Aktion aktion = new Aktion(satz);
            } catch (Exception e)
            {
                
                
            }
        }
    }
    
    
    
}
