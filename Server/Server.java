package Server;
import java.net.*;
import com.google.gson.Gson;
import Clan.ClanClas;
import Clan.ClientClas;

import java.io.*;
import DataManage.*;

public class Server extends Thread
{
	final int PUERTOENTRADA = 8080;

	ServerSocket skServidor;
	DataOutputStream mensaje;
	DataInputStream entrada;
	File FficheroClan =new File("C:/Users/Gabriel/Documents/EclipseProjects/Pandora/Pandora-Under-Attack/DataBaseClan.txt");
	File FficheroClient =new File("C:/Users/Gabriel/Documents/EclipseProjects/Pandora/Pandora-Under-Attack/DataBaseClient.txt");
	File FficheroPasswords =new File("C:/Users/Gabriel/Documents/EclipseProjects/Pandora/Pandora-Under-Attack/DataBasePasswords.txt");

	public void run()
	{
		try
		{
			skServidor = new ServerSocket(PUERTOENTRADA);
			System.out.println("Escucho el puerto " + PUERTOENTRADA );
			for ( int numCli = 0; numCli < 100; numCli++) {
				Socket skCliente = skServidor.accept();
				System.out.println("Sirvo al cliente " + numCli);
				readC (skCliente, numCli);
				skCliente.close();
				skServidor.close();
			}
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}    
	} 
	protected void readC (Socket socket, int numCli)  
	{
		try 
		{
			InputStream entra = socket.getInputStream();
			DataInputStream flujo = new DataInputStream(entra);
			String jsondata = flujo.readUTF();
			Gson o = new Gson();

			if ((jsondata.contains("ClanName"))){
				ClanClas b = o.fromJson(jsondata, ClanClas.class);
				if ((TomarObjeto.Leer(FficheroClan, b.getName()))== "No se encontro la palabra solicitada"){
					WriteTXT.EcribirFichero(FficheroClan, jsondata);
				}else{
					writeC(socket, "Ya existe un clan con ese nombre");
				}
			}
			
			if (jsondata.contains("ClientName")){
				ClientClas b = o.fromJson(jsondata, ClientClas.class);
				if (jsondata.contains("newlogin")){		
					if ((TomarObjeto.Leer(FficheroClient, b.getName()))== "No se encontro la palabra solicitada"){
						WriteTXT.EcribirFichero(FficheroClient, jsondata);
						writeC(socket, "Registrado");
						
					} else {
						writeC(socket, "Ya estas registrado");
					}
					
				}
				if (b.getAction().equals("login")){
					if (((TomarObjeto.Leer(FficheroClient, b.getName()) == "No se encontro la palabra solicitada"))&& ((TomarObjeto.Leer(FficheroClient, b.getPassword()) == "No se encontro la palabra solicitada"))){
						writeC(socket, "No te has registrado");
					} else {
						writeC(socket, "De nuevo por aqui");
					}
				}
			}
			
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage()); 
		}
	}
	protected void writeC (Socket socket, String message)   
	{
		try 
		{
			DataOutputStream flujoc= new DataOutputStream(socket.getOutputStream());
			flujoc.writeUTF(message);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage()); 
		}
	}
}