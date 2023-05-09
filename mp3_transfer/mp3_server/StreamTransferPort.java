/*
 * Copyright (c) 2023 Ismael Mosquera Rivera
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
* StreamTransferPort.java
*
* Author: Ismael Mosquera Rivera
*/

/**
* This class is used to keep the port where the server will be listening.
*
* @author Ismael Mosquera Rivera
*/
public class StreamTransferPort
{

	/**
	* Method to get the port where the server is listening.
	*
	* @return int Port.
	*
	*/
public static int getPort()
{
return TRANSFER_PORT;
}

private StreamTransferPort(){}
private static final int TRANSFER_PORT = 11105;
}

// END
