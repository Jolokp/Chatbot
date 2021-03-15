package main.java;

public class Aktion
{
	private String[] satz;
	public Aktion(String[] satz)
	{
		this.satz = satz;
		execute();
	}

	public static void execute()
	{
			System.out.println("Mein Name ist Chatbot in cool");
		}
}