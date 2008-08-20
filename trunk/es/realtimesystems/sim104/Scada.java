//============================================================================
//
//	Copyright(c) 2008. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: Scada.java  11/08/2008
//
// 	Autor:  M. Alejandro García (malejandrogarcia@yahoo.es)
//
//	Descripción: Sistema Scada
//
//  Licencia:
//  This program is free software: you can redistribute it and/or modify it under
//  the terms of the GNU General Public License as published by the Free Software
//  Foundation, either version 3 of the License, or (at your option) any later
//  version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
//  details.
//
//  You should have received a copy of the GNU General Public License along with
//  this program. If not, see <http://www.gnu.org/licenses/>.
//
//----------------------------------------------------------------------------

package es.realtimesystems.sim104;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.*;

import PTMF.Address;
import PTMF.Buffer;
import PTMF.ParametroInvalidoExcepcion;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

/**
* <p>
* SIM104 es un software libre que implementa un pequeño sistema Scada para probar la conectividad del protocolo de telecontrol IEC 60870-5-104
* </p>
* <p>
* Scada, el front-end de Sistemas de Control que realiza el Telecontrol.
* En definitiva, un pequeño scada para realizar las pruebas de interconexión 104 con la remota RTU-104
* </p>
*
* <p>
* <b>REVISIONES:</b>
* </p>
*
* @author Alejandro Garcia (malejandrogarcia@yahoo.es)
* @version 1.0
*/

public class Scada {


	private Logger logger = null;
	private String[] args = null;

	private static String sXMLDatabase = null;

	/** Base de datos de puntos scada */
	private ScadaDataBase sdb = null;


	/** Socket general */
	//TODO: habra que crear una estructura o clase para almacenar los datos de todas las remotas a la
	//que conectamos. Este es generico. solo para una rtu.
	Socket s = null;

    /** la direccion IP de la LRU + el puerto 104*/
	/*TODO: Modificar para tener múltiples remotas*/
	private Address addressLRU = null;

	/**Buffer de Recepcion */
	private Buffer bufferRx  = null;

	/**Buffer de Transmision */
	private Buffer bufferTx  = null;

	/** Longitud en bytes del tamaño del buffer de recepcion/transmision */
	private int longBuffer = 1024*1024*1; //1MB

	/** Nº de secuencia enviado (send)*/
	private int VS = 0;

	/** Nº de secuencia recibido (receive)*/
	private int VR = 0;

	/** ACKS*/
	private int ACK = 0;


	private InputStream is = null;
	private OutputStream os = null;

	/**
	 * Constructor.
	 *
	 *
	 */
	public Scada(String arg0) {
		// TODO la idea es... abra que pasarle el fichero xml para generar la base de datos en este
		// lado. Despues habrá que intentar conectar y realizar una IG, etc, etc

		super();

		sXMLDatabase = arg0;

		logger = Logger.getLogger("sim104.Scada");
		logger.addAppender(new ConsoleAppender(new TTCCLayout()));

		//TODO: Bajar nivel a INFO cuando este OK
		logger.setLevel(Level.DEBUG);

		logger.info("*Constructor Scada*");
		//logger.info("Cargando base de datos RTU: "+sXMLDatabase);

		paserXMLDatabase(sXMLDatabase);

	}

	/**
     * Realiza el parser de la base de datos .xml y carga la base de datos
     * @param sFichero
     */
    private void paserXMLDatabase(String xmlFile)
    {

    	sdb = new ScadaDataBase(xmlFile);


    }

	/**
	 * Imprime el mensaje de uso de la aplicación
	 */
	private static void uso() {
		System.out.println("");
		System.out.println("sim104 v1.0 by AGD");
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("Uso: java sim104.Scada <remota.xml>");
		System.out.println("");
		System.out.println("Programa de Simulación de SCADA IEC-104");
		System.out.println("<remota.xml>:\tFichero .xml de configuración de la remota (base de datos)");
		System.out.println("");
	}

	/**
     * Bucle principal de procesamiento.
     */
	private void run()
	{
		logger.debug("en run...");

		try {
			//1. Obtener IPs de las remotas y conectar con ellas...
			//IP de la remota
			logger.info("IP RTU: "+sdb.lru.getRTUIPADDRSTR());
			this.addressLRU = new Address(sdb.lru.getRTUIPADDRSTR(),IEC104.PORT_104);



			//2. Conectar a la Remota
			logger.info("Conectando a: "+this.addressLRU);
			Socket s = new Socket(addressLRU.getInetAddress(),addressLRU.getPort());

			is = s.getInputStream();
			os = s.getOutputStream();

			// Crear buffer de procesamiento
			this.bufferRx = new Buffer(longBuffer);
			this.bufferTx = new Buffer(longBuffer);

			//Resetear variables

			while(s.isConnected())
			{
				//TODO: seguir por aqui
				Thread.sleep(2000);
				logger.info("en run...");

				//Segun estado... enviar lo que haya
				//Conectar y enviar la informacion de inicio
				//startdt
				sendStartDT(s,sdb.lru.commaddrnum,true);
				sendIG(s,sdb.lru.commaddrnum);

				//Leer, si hay algo...

			}

		} catch (InterruptedException e) {
			logger.info("Run interrumpido!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			logger.error("No se ha podido resolver la direccion IP de la remota. String: "+sdb.lru.getRTUIPADDRSTR());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}


	}


	/** Envía una petición de IG a la Remota
	 *@param os El Stream sobre el que se enviará la IG
	 * @param commAddr La common address de la remota*/
	private void sendIG(OutputStream os, BigInteger commAddr) {

		//TODO: Rellenar con datos del protocolo
		//		[APDU Application Protocol Data Unit]
		//		START 68H
		//		Length of the APDU (max. 253)
		//		Control field octet 1
		//		Control field octet 2
		//		Control field octet 3
		//		Control field octet 4
		//		ASDU definida en 101

		try {
			//68H
			this.bufferTx.addByte((byte)0x68);

			//Longitud. Se rellenará despues mas tarde.
			this.bufferTx.addByte((byte)0);



		} catch (ParametroInvalidoExcepcion e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	/** Envía una petición STARTDT a la remota. Trama I
	 * @param os El s sotreambre el que se enviará la IG
	 * @param commAddr La common address de la remota
	 * @param act Indica si es activación o confirmación*/
	private void sendStartDT(OutputStream os, BigInteger commAddr,boolean act) {


		//		[APDU Application Protocol Data Unit]
		//		START 68H
		//		Length of the APDU (max. 253)
		//		Control field octet 1
		//		Control field octet 2
		//		Control field octet 3
		//		Control field octet 4
		//		ASDU definida en 101

		try {
			//68H
			this.bufferTx.addByte((byte)0x68);

			//Longitud.
			this.bufferTx.addByte((byte)5);

			if(act)
				this.bufferTx.addInt(IEC104.U_STOPDT_ACT);
			else
				this.bufferTx.addInt(IEC104.U_STOPDT_CON);

			os.write(bufferTx.getBuffer(),0,bufferTx.getLength());

		} catch (ParametroInvalidoExcepcion e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Comprobar parámetros
		if (args.length != 1) {
			uso();
			return;
		}

		try {
			System.out.println("");
			System.out.println("[SCADA] sim104 v1.0 by AGD");
			System.out.println("----------------------------------------------------------------------");
			Scada sc = new Scada(args[0]);
			sc.run();

		}
		/*
		 * catch (IOException io) { System.out.print(io.getMessage()); }
		 */
		finally {

		}
	}

}
