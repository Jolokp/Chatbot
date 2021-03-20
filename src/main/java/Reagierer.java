package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.w3c.dom.*;


public class Reagierer {
    
    private Character[] charList = new Character[]{'.','!','?'};
    private ArrayList<String[]> sätze;
    private Document doc;

    public Reagierer(Document doc, ArrayList<String[]> sätze)
    {
        this.doc = doc;
        this.sätze = sätze;
    }

    public String interpretieren() throws Exception 
    {
        String answer = "";
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
            
            if (correct)
            {
                //bearbeiten von sinn eines satzes
                answer += reagieren(value, sätze.get(i));
            } else
            {
                answer += "Keine Ahnung tbh";
            }
            
        }

        return answer;
    }
    
    public String reagieren(Node phrase, String[] satz) throws IOException, URISyntaxException, InterruptedException
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
        
        
        
        // Java Datei erstellen
        File action = new File("src/main/java/Aktion.java");
        //boolean lol = action.createNewFile();

        //Die Java datei füllen
        PrintWriter writer = new PrintWriter(action, "UTF-8");
        writer.write(
            "package main.java;\n"
          + "public class Aktion\n" 
          + "{\n"
          +     "\tprivate static String[] satz;\n"
          +     "\tpublic static void main(String[] args)\n"
          +     "\t{");
        writer.write(code);
        writer.write("}\n}");
        writer.close();

        //Compiled die oben erstellte Java datei
        Process compile = Runtime.getRuntime().exec("cmd /c javac \"" + action.getCanonicalPath() + "\"");
        
        // Warten bis der Process fertig ist
        compile.waitFor();
        

        String path = action.getCanonicalPath();

        // Path in Teile aufteilen
        String[] splitPath = path.split("\\\\");

        // Path, der bis zur Basis vom Classpath geht
        String rootPath = "";

        // package Struktur (gleichzeitig auch Ordnerstruktur) bis zur Klasse
        String classPath = "";

        // Path durchgehen
        for (int k = 0;k < splitPath.length;k++)
        {
            // Wenn bei src angekommen
            if (splitPath[k].equals("src"))
            {
                // nächsten Path füllen
                rootPath += "src";
                for (int l = k + 1;l < splitPath.length - 1;l++)
                {
                    classPath += splitPath[l] + ".";
                }
                classPath += "Aktion";
                break;
            }
            // Teil wieder hinzufügen
            rootPath += splitPath[k] + "\\";
        }

        String command = "java -cp \"" + rootPath + "\" " + classPath;
        String output = "";
        ProcessBuilder pBuilder = new ProcessBuilder();
        pBuilder.command("cmd.exe", "/c", command);
        Process run = pBuilder.start();
        InputStream is = run.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = reader.readLine()) != null) {
            output += line + "\n";
        }
        
        return output;
    }
}
