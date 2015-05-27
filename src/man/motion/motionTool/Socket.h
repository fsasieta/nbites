/* Socket class header
 * has getters and other function declarations
 *
 * Modified 02/17/15
 */

#include <iostream>

#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/select.h>
#include <sys/stat.h>

#include <fcntl.h> 
#include <netinet/in.h> 
#include <unistd.h> 

#include <assert.h>

using namespace std;

class Socket{
    int socket_fd;
	//address struct
    struct sockaddr_in address;
    public:
        //functions
        void createSocket(void); 
        int get_socket_fd(void){return socket_fd;}
        void bindSocket(int sfd, int portnumber);
        struct sockaddr_in getAddrStruct(){return address;}
};
