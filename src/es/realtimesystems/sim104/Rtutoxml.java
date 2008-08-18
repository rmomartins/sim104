/**
 * Simulador 104
 *
 */
package es.realtimesystems.sim104;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

/**
 * @author Alejandro
 *
 */
public class Rtutoxml {

	private Logger logger = null;
	private String[] args = null;

	private static String sFichero = null;
	private static int iTamaño = 0;

	private static String sUsuario = null;
	private static String sPassword = null;
	private static String sHost = null;
	private static String sPort = null;
	private static String sSID = null;
	private static String sRemota = null;

	private static File fOut = null;
	private static FileOutputStream fOutStream = null;

	/**
	 *
	 * Constructor arg0= usuario arg1= password arg2= Host arg3= Puerto (1521
	 * por defecto en Oracle) arg4= SID arg5= Tabla a preguntar arg6= Nº de
	 * registros
	 *
	 * @param args
	 */
	public Rtutoxml(String arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5) {
		super();

		sUsuario = arg0;
		sPassword = arg1;
		sHost = arg2;
		sPort = arg3;
		sSID = arg4;
		sRemota = arg5;

		logger = Logger.getLogger("sim104.Rtutoxml");
		logger.addAppender(new ConsoleAppender(new TTCCLayout()));
		logger.setLevel(Level.INFO);

		logger.info("*Constructor*");
	}

	/**
	 * Ejecución
	 */
	private void run() {

		logger.info("*run*");

		String sQueryRemota = "select lru_name,info_b1,info_b2,info_b3,info_elem,info_info,mon_type,mon_addrnum,com_type,com_addrnum,pro_gc  from tclru_infdat where lru_name='"
				+ this.sRemota + "' and info_elem not in ('TcLruSta','EstRtuAA') order by  mon_addrnum";
		String sQueryLRU = "select distinct t.lru_name,t.dev_name,t.lru_no,t.commaddrnum,r.rtu_ip_addr_str from tclru t, tcroute r where r.dev_name=t.dev_name and t.lru_name='"
				+ this.sRemota + "'";
		String sXMLHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n";
		String sTag = null;
		String sTipo = null;

		long startTime = 0;
		long stopTime = 0;
		double dDuracion = 0.0;
		double dRatio = 0.0;
		String sRatio = null;
		int iRegistros = 0;

		Connection con;
		Statement stmt;
		Statement stmt2;
		String query = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String sOut = null;
		String xns = "xmlns:s104=\"http://www.realtimesystems.es/sim104\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.realtimesystems.es/sim104 lru.xsd ";


		try {
			logger.info("Consultando datos de remota " + this.sRemota);
			// Registrar el Driver JDBC Oracle
			logger.info("Cargando driver JDBC de Oracle...");
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

			// Establecer la conexión a la BD
			logger.info("Conectando a: [jdbc:oracle:thin:" + sUsuario + "/"
					+ sPassword + "@" + sHost + ":" + sPort + ":" + sSID
					+ "]");
			con = DriverManager.getConnection("jdbc:oracle:thin:" + sUsuario
					+ "/" + sPassword + "@" + sHost + ":" + sPort + ":"
					+ sSID);
			startTime = System.currentTimeMillis();

			// Crear un statement
			stmt = con.createStatement();
			stmt2 = con.createStatement();

			stmt.execute(sQueryRemota);
			rs1 = stmt.getResultSet();

			stmt2.execute(sQueryLRU);
			rs2 = stmt2.getResultSet();


			// Abrir fichero de salida
			sFichero = sRemota + ".xml";
			fOut = new File(sFichero);
			fOutStream = new FileOutputStream(fOut);

			fOutStream.write(sXMLHead.getBytes());
			rs2.next();


			sOut = "<s104:LRU LRU_NAME=\""+rs2.getString("lru_name");
			sOut = sOut + "\" DEV_NAME=\"" + rs2.getString("dev_name");
			sOut = sOut + "\" LRU_NO=\"" + rs2.getString("lru_no");
			sOut = sOut + "\" COMMADDRNUM=\"" + rs2.getString("commaddrnum");
			sOut = sOut + "\" RTU_IP_ADDR_STR=\"" + rs2.getString("rtu_ip_addr_str") + "\" "+xns+">\n";

			//logger.info(sOut);
			fOutStream.write(sOut.getBytes());

			while (rs1.next()) {
				sTipo = rs1.getString("mon_type");
				sOut = "<s104:" + sTipo;
				sOut = sOut + " INFO_B1=\"" + rs1.getString("info_b1");
				sOut = sOut + "\" INFO_B2=\"" + rs1.getString("info_b2");
				sOut = sOut + "\" INFO_B3=\"" + rs1.getString("info_b3");
				sOut = sOut + "\" INFO_ELEM=\"" + rs1.getString("info_elem");
				sOut = sOut + "\" INFO_INFO=\"" + rs1.getString("info_info");
				sOut = sOut + "\" MON_ADDRNUM=\"" + rs1.getString("mon_addrnum");
				sOut = sOut + "\" COM_TYPE=\"" + rs1.getString("com_type");
				if (rs1.getString("com_addrnum") == null )
					 sOut = sOut + "\" COM_ADDRNUM=\"0\"" ;
				else
					sOut = sOut + "\" COM_ADDRNUM=\"" + rs1.getString("com_addrnum") ;

				sOut = sOut + "\" PRO_GC=\"" + rs1.getString("pro_gc") + "\">";

				/*
				if(sTipo.equals("SP"))
					sOut = sOut + "0";
				else if(sTipo.equals("DP"))
					sOut = sOut + "00";
					*/

				sOut = sOut + "</s104:"+ sTipo+">\n";

				//logger.info(sOut);
				fOutStream.write(sOut.getBytes());
			}


			sOut = "</s104:LRU>\n";
			fOutStream.write(sOut.getBytes());

			stopTime = System.currentTimeMillis();
			logger.info("StartTime: " + startTime + " ms");
			logger.info("StopTime:  " + stopTime + " ms");
			logger.info("Duracion:  " + (stopTime - startTime) + " ms");
			dDuracion = ((stopTime - startTime) / 1000.0);

			// Cerrar los descriptores
			fOutStream.close();
			stmt.close();
			stmt2.close();
			con.close();

			logger.info("Conexion cerrada.-");

		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());

		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	}

	/**
	 * Imprime el mensaje de uso de la aplicación
	 */
	private static void uso() {
		System.out.println("");
		System.out.println("sim104 v1.0 by AGD");
		System.out
				.println("---------------------------------------------------------------------------");
		System.out
				.println("Uso: java sim104.Rtutoxml <usuario> <password> <Host> <Puerto> <SID> <REMOTA>");
		System.out.println("");
		System.out
				.println("Programa de extración de información de remota de Oracle-Spectrum y conversión a formato xml");
		System.out.println("<usuario>:\tUsuario de acceso a Oracle");
		System.out.println("<password>:\tPassword ");
		System.out
				.println("<Host>:    \tHost donde se ejecuta la instancia de Oracle");
		System.out
				.println("<Puerto>:\tPuerto de escucha del listener, generalmente 1521");
		System.out.println("<SID>:    \tSID de Oracle");
		System.out
				.println("<REMOTA>:\tNombre de la remota definida en Spectrum y Oracle");
		System.out.println("");
		System.out.println("Fichero de salida: <REMOTA>.xml");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Comprobar parámetros
		if (args.length != 6) {
			uso();
			return;
		}

		try {
			System.out.println("");
			System.out.println("sim104 v1.0 by AGD");
			System.out
					.println("----------------------------------------------------------------------");
			Rtutoxml gen = new Rtutoxml(args[0], args[1], args[2], args[3],
					args[4], args[5]);
			gen.run();

		}
		/*
		 * catch (IOException io) { System.out.print(io.getMessage()); }
		 */
		finally {

		}
	}

	/**
	 *
	 */
	public Rtutoxml() {
		super();
	}

}
