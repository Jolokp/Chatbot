package main.java;

import java.util.ArrayList;

import org.w3c.dom.*;


public class Interpreter {
    
    private Character[] charList = new Character[]{'.','!','?'};
    private ArrayList<String[]> sätze;
    private Document doc;

    public Interpreter(Document doc, ArrayList<String[]> sätze)
    {
        this.doc = doc;
        this.sätze = sätze;
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
            Reagierer reagierer = new Reagierer(doc, value,sätze.get(i));
            reagierer.reagieren();
            
        }
    }
    
}
