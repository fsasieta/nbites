#ifndef nbcontrol_h
#define nbcontrol_h

#include <pthread.h>

#define CONTROL_USLEEP_WAITING (100000)

#define CONTROL_PORT (30001)


//extern bool newCommand;
//extern std::string newTimeStamp, oldTimeStamp, streamedData;

namespace control {
    
    /*
     FLAGS
     */
    typedef enum {
        serv_connected,
        control_connected,
        
        fileio,
        
        SENSORS,
        GUARDIAN,
        COMM,
        LOCATION,
        ODOMETRY,
        OBSERVATIONS,
        LOCALIZATION,
        BALLTRACK,
        IMAGES,
        VISION,
        
        tripoint,
        
        //Num_flags must be last!  it has int value ( 'previous' + 1)
        //which, if it is last,
        //is equivalent to the number of flags previous listed.
        num_flags
    } flag_e;
    
    extern volatile uint8_t flags[num_flags];
    
    void control_init();
    
    extern pthread_t control_thread;

    //Global variables shared by the control and motion threads.
    //Only modified here, values are copied by the function called from other thread.
    extern bool newCommand;
    extern std::string newTimeStamp, oldTimeStamp, streamedData;
}

#endif //nbcontrol_h
