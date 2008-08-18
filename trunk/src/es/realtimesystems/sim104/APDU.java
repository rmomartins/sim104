//============================================================================
//
//	Copyright(c) 2008. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: APDU.java  17/08/2008
//
// 	Autor:  M. Alejandro García (malejandrogarcia@yahoo.es)
//
//	Descripción:
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

import PTMF.Buffer;
import PTMF.NumeroSecuencia;
import PTMF.PTMFExcepcion;
import PTMF.ParametroInvalidoExcepcion;



/**
 * APDU del protocolo IEC 60870-5-104.
 * </br>
 * Integra la cabecera ACPI y el ASDU. No lo dividimos por ahora por comodidad.
 * <strong>En recepcion:</strong>
 * APDU se utiliza para realizar el parser de un buffer y extraer los datos recibidos.
 * Se construye la clase y se invoca al parser utilizando metodos estaticos.
 *
 * <strong>En transmision:</strong>
 * APDU se utiliza para construir un buffer que se enviará por red. Para su construcción
 * se utiliza los metodos de la clase
 *
 *
 * <p>
 * <b>REVISIONES:</b>
 * </p>
 *
 * @author Alejandro Garcia (malejandrogarcia@yahoo.es)
 * @version 1.0
 */
public class APDU implements Cloneable{


 /**
  * Constructor por defecto.
  * Este constructor es para crear APDUs a partir del parser de un Buffer
  */
  protected APDU()
  {
	   super();
  }

  /**
   * Método clone del APDU.
   * @return El nuevo objeto clonado.
   */
  protected Object clone()
  {
    final String  mn = "APDU.clone()";
    APDU pkt = null;

    //
    // Clonar el TPDU, después clonar el buffer.
    //
    // EJEMPLO copia de campos --> pkt.PUERTO_MULTICAST = this.PUERTO_MULTICAST;

    return(pkt);
  }

  /**
   * Crea un APDU con la información facilitada.
   * @param puertoMulticast
   * @param puertoUnicast
   * @param idgl
   * @param dirIP
   * @param setIR
   * @param setACK
   * @param setFIN_CONEXION
   * @param setFIN_TRANSMISION
   * @param numeroRafaga
   * @param nSec numero secuencia
   * @param datos
   * @return objeto TPDUDatosNormal creado
   * @exception ParametroInvalidoExcepcion si alguno de los parámetros es erróneo.
   * @exception PTMFExcepcion si hay un error al crear el TPDUACK

   */
  static APDU crearAPDU (int puertoMulticast,
                                               int puertoUnicast,
                                               IDGL idgl,
                                               IPv4 dirIp,
                                               boolean setIR,
                                               boolean setACK,
                                               boolean setFIN_CONEXION,
                                               boolean setFIN_TRANSMISION,
                                               int numeroRafaga,
                                               NumeroSecuencia nSec,
                                               Buffer datos)
    throws ParametroInvalidoExcepcion, PTMFExcepcion
  {
    final  String mn = "TPDUDatosNormal.crearTPDUDatosNormal";

    APDU resultAPDU = null;

    // Crear el TPDUDatosNormal vacio
    resultTPDU = new TPDUDatosNormal (puertoMulticast,puertoUnicast,idgl,dirIp);
    // if (datos!=null)
    resultTPDU.BUFFERDATOS = (Buffer)datos;//.clone();
    //else
    //   resultTPDU.BUFFERDATOS = null;

    // Guardar los datos en la cabecera para cuando sean pedidos
    resultTPDU.IR  = (byte)(setIR  ? 1 : 0);
    resultTPDU.ACK = (byte)(setACK ? 1 : 0);
    resultTPDU.FIN_CONEXION = (byte)(setFIN_CONEXION ? 1 : 0);
    resultTPDU.FIN_TRANSMISION = (byte)(setFIN_TRANSMISION ? 1 : 0);
    resultTPDU.NUMERO_RAFAGA = numeroRafaga;
    resultTPDU.NUMERO_SECUENCIA = (NumeroSecuencia)nSec.clone();

    // Crear ID_TPDU_FUENTE
    resultTPDU.ID_TPDU_FUENTE = new ID_TPDU (resultTPDU.getID_SocketEmisor(),
                                              resultTPDU.getNumeroSecuencia());

    return resultTPDU;
  }


}
