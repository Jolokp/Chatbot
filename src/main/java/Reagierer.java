package main.java;

import org.w3c.dom.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class Reagierer {
    private Node phrase;
    private String[] satz;
    private Document doc;

    public Reagierer(Document doc, Node phrase, String[] satz)
    {
        this.doc = doc;
        this.phrase = phrase;
        this.satz = satz;
    }

    public void reagieren() throws IOException, URISyntaxException
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
        
        
        File dir = new File("src/main/java");
        File[] files = dir.listFiles();
        
        for (File file : files)
        {
            if (file.getName().startsWith("Aktion."))
            {
                System.out.println(file.getName());
                file.delete();
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("waited");
        
        File action = new File("src/main/java/Aktion.java");
        boolean lol = action.createNewFile();
        System.out.println(lol);
        //Die Java datei erstellen
        PrintWriter writer = new PrintWriter(action, "UTF-8");
        writer.write(
            "package main.java;\n"
          + "public class Aktion\n" 
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
        System.out.println("processing...");
        Runtime.getRuntime().exec("cmd /c javac \"" + action.getCanonicalPath() + "\"");
        System.out.println("finished");
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("waited");
        
        if (action.exists())
        { 
            Aktion aktion = new Aktion(satz);
            
        }
    }
    
}
