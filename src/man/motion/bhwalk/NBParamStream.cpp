/* Figure out where to put this code I must
 *
 */

#include "NBParamStream.h"
#include <sys/ioctl.h>
#include <sys/mman.h>


using std::cout;
using std::endl;
using nblog:SExpr;

//Constructor for the file. reads the file that contains the SExpr first
//and instantiates the map that contains the values used.
WalkingengineParamTranscriber::WalkingEngineParamTranscriber()
    : file_mod_time(),
      first_time(1){
    
}


bool areParamsUpdated(){

    struct stat file_stats;
    std::string filepathUsed;
    #ifdef NAOQI_2
        filePathUsed = V5FILEPATH;
    #else
        filePathUsed = V4FILEPATH;
    #endif

    if(FILE *file = fopen(filePathUsed.c_str(), "r")) { //existence check
        fclose(file);
        int err = stat(filePathUsed.c_str(), &file_stats);
        if(first_time == 1) {
            std::cout << "[INFO] First time running params" << std::endl;
            file_mod_time = file_stats.st_mtime;
            first_time = 0;
        }
        if(!updatedParams()) {                   
            //need to read new params from file.
            //(since they changed since the last time we opened the file.)
            initWalkEngineParamSettings();
        }
    }
    else {
        std::cout << "[ERR] File does not exist" << std::endl;
    }
}

//Function checks whether the SEXpre file has been updated
bool WalkEngineParamTranscriber::updatedParams(){

    int timeDiff = std::difftime(file_stats.st_mtime, file_mod_time);
        if(time_diff > 0.0) {
            file_mod_time = file_stats.st_mtime;
            std::cout << "[INFO] New Mod. Time " << file_mod_time << std::endl;
            std::cout << "[INFO] Updating parameters used." << std::endl;
            return false;
        }
        return true;
}



void initWalkEngineParamSettings(){
    
    std::string filepath;

    #ifdef NAOQI_2
        filePathUsed = V5FILEPATH;
        std::cout << "[INFO] Walk Engine Parameters streaming" << std:endl;
        std::cout << "[INFO] NAOQI: V5" << std:endl;

    #else
        filePathUsed = V4FILEPATH;
        std::cout << "[INFO] Walk Engine Parameters streaming" << std:endl;
        std::cout << "[INFO] NAOQI: V4" << std:endl;

    #endif

    if(FILE *file = fopen(filePathUsed.c_str(), "r")) { //existence check
        fclose(file);
        std::ifstream inputFile(filepath);
        std::string readInFile((std::istreambuf_iterator<char>(inputFile)), std::istreambuf_iterator<char>());

        int i = 0;
        SExpr params = *SExpr::read(readInfile,i);

        //We read the parameters as an SExpr
        //if(params.count() = 2){
        //
        //}
    }
}
