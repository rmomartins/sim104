//============================================================================
//
//	Copyright(c) 2008. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: ScadaDataBase.java.java  11/08/2008
//
// 	Autor:  M. Alejandro García (malejandrogarcia@yahoo.es)
//
//	Descripción: La base de datos de puntos escada.
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

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;


/**
* La base de datos de puntos escada.
* </br>
* Esta clase contiene la base de datos de los puntos scada que tendra la remota o el sistema scada.
* La base de datos se carga mediante un fichero xml que contiene todos los elementos de la remota.
* SOLO SE PERMITE POR AHORA UNA LRU POR REMOTA!!
* La especificación del fichero xml viene indicado en el XML Schema "lru.xsd"
*
*
* <p>
* <b>REVISIONES:</b>
* </p>
*
* @author Alejandro Garcia (malejandrogarcia@yahoo.es)
* @version 1.0
*
*/
public class ScadaDataBase {

	private Logger logger = null;
	private String[] args = null;

	private static String sXMLDatabase = null;
	private static File fIn = null;
	private static FileInputStream fXMLInputStream = null;


	/** LRU. Logic Remote Unit */
	public  LRU lru = null;

	//Lista de puntos
	List<ScadaElement>  listaSP = null;
	List<ScadaElement>  listaDP = null;
	List<ScadaElement>  listaMS = null;
	List<ScadaElement>  listaST = null;


	//Tree-Hash de puntos
	public TreeMap<BigInteger,ScadaElement> treeSP = null;
	public TreeMap<BigInteger,ScadaElement> treeDP = null;
	public TreeMap<BigInteger,ScadaElement> treeMS = null;
	public TreeMap<BigInteger,ScadaElement> treeST = null;

	/**
	 * Constructor.
	 * Abre el fichero xml y lo parsea. Debe de respetar el esuquema que indique lru.xsd
	 * @param xmlFile
	 */
	public ScadaDataBase(String xmlFile) {
		super();


		  try {

			    logger = Logger.getLogger("sim104.ScadaDataBase");
				logger.addAppender(new ConsoleAppender(new TTCCLayout()));

				//TODO: Bajar nivel a INFO cuando este OK
				logger.setLevel(Level.DEBUG);

				logger.info("*Constructor ScadaDataBase*");
				logger.info("Cargando base de datos RTU: "+xmlFile);

 			  JAXBContext jaxbContext;
	            ScadaElement se = null;
	            Iterator<ScadaElement> it = null;

	            jaxbContext = JAXBContext.newInstance("es.realtimesystems.sim104");
	            //  Marshaller marshaller=jaxbContext.createMarshaller();

	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

	            /* Obtener la LRU del .xml; según compilación del lru.xsd */
	            lru = (LRU)unmarshaller.unmarshal(new File(xmlFile));


	            //Verificar el lru
	            logger.info("REMOTA: "+lru.getLRUNAME()+" "+
	            lru.getDEVNAME() + " "+
	            lru.getLRUNO()+" "+
	            lru.getRTUIPADDRSTR()+" "+
	            lru.getCOMMADDRNUM());

	            //Obtener lista de puntos y mostrar por pantalla
	            //+añadir a los treemap
	            //--------------------------------------------------------------------------
	            this.listaSP = lru.getSP();
	            this.listaDP = lru.getDP();
	            this.listaMS = lru.getMS();
	            this.listaST = lru.getST();

	            //Crear TreeMaps donde se almacenarán y manipularán los elementos scada
	            treeSP = new TreeMap<BigInteger, ScadaElement>();
	            treeDP = new TreeMap<BigInteger, ScadaElement>();
	            treeMS = new TreeMap<BigInteger, ScadaElement>();
	            treeST = new TreeMap<BigInteger, ScadaElement>();


	            //Mostrar SP
	            logger.info("Obteniendo SP...");
	            it = listaSP.iterator();
	            while (it.hasNext())
	            {
	            	se = (ScadaElement)it.next();
	            	logger.info(se.getINFOB1()+" "+se.getINFOB2()+" "+se.getINFOB3()+" "+se.getINFOELEM()+
	            			" "+se.getINFOINFO()+" "+se.getCOMMTYPE()+" "+se.getCOMTYPE()+" "+se.getMONADDRNUM()+" "+se.getCOMADDRNUM());

	            	//Añadir el elemento scada al árbol
	            	treeSP.put(se.getMONADDRNUM(), se);
	            }


	            //Mostrar DP
	            logger.info("Obteniendo DP...");
	            it = listaDP.iterator();
	            while (it.hasNext())
	            {
	            	se = (ScadaElement)it.next();
	            	logger.info(se.getINFOB1()+" "+se.getINFOB2()+" "+se.getINFOB3()+" "+se.getINFOELEM()+
	            			" "+se.getINFOINFO()+" "+se.getCOMMTYPE()+" "+se.getCOMTYPE()+" "+se.getMONADDRNUM()+" "+se.getCOMADDRNUM());

	            	//Añadir el elemento scada al árbol
	            	treeDP.put(se.getMONADDRNUM(), se);

	            }

	            //Mostrar MS
	            logger.info("Obteniendo MS...");
	            it = listaMS.iterator();
	            while (it.hasNext())
	            {
	            	se = (ScadaElement)it.next();
	            	logger.info(se.getINFOB1()+" "+se.getINFOB2()+" "+se.getINFOB3()+" "+se.getINFOELEM()+
	            			" "+se.getINFOINFO()+" "+se.getCOMMTYPE()+" "+se.getCOMTYPE()+" "+se.getMONADDRNUM()+" "+se.getCOMADDRNUM());

	            	//Añadir el elemento scada al árbol
	            	treeMS.put(se.getMONADDRNUM(), se);

	            }

	            //Mostrar ST
	            logger.info("Obteniendo ST...");
	            it = listaST.iterator();
	            while (it.hasNext())
	            {
	            	se = (ScadaElement)it.next();
	            	logger.info(se.getINFOB1()+" "+se.getINFOB2()+" "+se.getINFOB3()+" "+se.getINFOELEM()+
	            			" "+se.getINFOINFO()+" "+se.getCOMMTYPE()+" "+se.getCOMTYPE()+" "+se.getMONADDRNUM()+" "+se.getCOMADDRNUM());

	            	//Añadir el elemento scada al árbol
	            	treeST.put(se.getMONADDRNUM(), se);
	            }

	          //--------------------------------------------------------------------------

	        }
	        catch (JAXBException e) {
	                	logger.error(e.getMessage());
	                	e.printStackTrace();
	        }
	        catch (ClassCastException ex1)
	        {
	        	logger.error(ex1.getMessage());
	        	ex1.printStackTrace();
	        }



	}




}
