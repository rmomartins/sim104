//============================================================================
//
//	Copyright(c) 2008. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: IEC104.java  11/08/2008
//
// 	Autor:  M. Alejandro García (malejandrogarcia@yahoo.es)
//
//	Descripción: Constantes de IEC104
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


/**
* <p>
*  Constantes y variables general del protocolo IEC-104
* </p>
*
* <p>
* <b>REVISIONES:</b>
* </p>
*
* @author Alejandro Garcia (malejandrogarcia@yahoo.es)
* @version 1.0
*/
public final class IEC104 {

	/** Puerto de escucha conexion 104 */
	public static final int PORT_104 = 2404;
	/** T1  */
	public static final int T1 = 5;
	/** T2  */
	public static final int T2 = 15;
	/** T3  */
	public static final int T3 = 60;
	/** T4  */
	public static final int T4 = 30;

	/** U_TESTFR_ACT  */
	public static final int U_TESTFR_ACT = 0x43;
	/** U_TESTFR_CON  */
	public static final int U_TESTFR_CON = 0x83;
	/** U_STOPDT_ACT  */
	public static final int U_STOPDT_ACT = 0x13;
	/** U_STOPDT_CON  */
	public static final int U_STOPDT_CON = 0x23;



}
