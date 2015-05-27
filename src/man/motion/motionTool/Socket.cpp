/*Socket class for use on the webserver assignment
 * code was put on a different file because webserver.cc
 * was getting too bloated
 * Header File has all the function declarations, as you 
 * would expect.
 */

#include "Socket.h"


//creates a socket file descriptor
void Socket::createSocket(void){
	socket_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);	
	if(socket_fd == -1){ 
		printf("Error creating socket\n");
		return ;
	}
}

//binds an address to a previously created socket.
void Socket::bindSocket(int sfd, int portnumber){
	struct sockaddr_in inputAddress; 
	inputAddress.sin_family = AF_INET;
	inputAddress.sin_port = htons(portnumber); //port number input by user.
	inputAddress.sin_addr.s_addr = htonl(INADDR_ANY);
	//Binding socket to address:
	if (bind(sfd, (struct sockaddr*) &inputAddress, sizeof(inputAddress)) != 0){
		printf("Error binding socket\n");
		assert(false && "Socket couldn't be bound");
		return ;
	}
	address = inputAddress;
	printf("Socket was opened and bound\n");

}



