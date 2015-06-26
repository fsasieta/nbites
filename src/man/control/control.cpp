#include "Log.h"
#include "exactio.h"
#include "nbdebug.h"
#include "DebugConfig.h"
#include "control.h"
#include "../log/logging.h"

#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <limits.h>
#include <signal.h>
#include <time.h>

#include <map>
#include <string>
#include <vector>
#include <sstream>
#include <iostream>
#include <fstream>
#include <exception>

#include "../../share/logshare/SExpr.h"

#include <iostream>
#include <fstream>


using nblog::SExpr;
using nblog::Log;

namespace control {
    
    pthread_t control_thread;
    static bool STARTED = false;
    bool newWalkParameters = false; //Need to initialize bool somewhere
    
    uint32_t cnc_test(Log * arg) {
        printf("\tcnc_test:[%s] %lu bytes of data.\n", arg->description().c_str(),
               arg->data().size());
        return 0;
    }
    
    volatile uint8_t flags[num_flags];
    //expects two bytes of data:
    //  flag index
    //  new flag value (0 or 1)
    uint32_t cnc_setFlag(Log * arg) {
        
        size_t u = arg->data().size();
        
        printf("cnc_setFlag() len=%lu\n", u);
        
        if (u != 2) { //need (index, value)
            printf("cnc_setFlag() wrong number of bytes.\n");
            return 1;
        }
        
        uint8_t index = arg->data()[0];
        uint8_t value = arg->data()[1];
        
        if (index >= num_flags) {
            printf("cnc_setFlag() flag index OOB: %i\n", index);
            return 2;
        }
        
        if (value > 1) {
            printf("cnc_setFlag() flag value OOB: %i\n", value);
            return 3;
        }
        
        switch (index) {
            case serv_connected:
                printf("cnc_setFlag(): ERROR: CANNOT SET serv_connected!\n");
                break;
            case control_connected:
                printf("cnc_setFlag(): ERROR: CANNOT SET cnc_connected!\n");
                break;
            case fileio: //setting fileio
                if (value) {
                    memcpy(nblog::fio_start, nblog::total, sizeof(nblog::io_state_t) * NUM_LOG_BUFFERS);
                    
                    nblog::fio_upstart = time(NULL);
                }
                
                flags[fileio] = value;
                break;
                
            default:
                flags[index] = value;
                break;
        }
        
        return 0;
    }


    uint32_t cnc_setCameraParams(Log * arg) {
        size_t u = arg->data().size();
        bool success = receivedParams.ParseFromString(arg->data());
        if(!success) {
            std::cerr<<"Failed to Parse Params\n";
        } else {
            SExpr s;

            SExpr h = SExpr("hflip",receivedParams.h_flip());
            SExpr v = SExpr("vflip",receivedParams.v_flip());
            SExpr ae = SExpr("autoexposure",receivedParams.auto_exposure());
            SExpr b = SExpr("brightness",receivedParams.brightness());
            SExpr c = SExpr("contrast",receivedParams.contrast());
            SExpr sat = SExpr("saturation",receivedParams.saturation());
            SExpr hue = SExpr("hue",receivedParams.hue());
            SExpr sharp = SExpr("sharpness",receivedParams.sharpness());
            SExpr gamma = SExpr("gamma",receivedParams.gamma());
            SExpr awb = SExpr("auto_whitebalance",receivedParams.autowhitebalance());
            SExpr expo = SExpr("exposure",receivedParams.exposure());
            SExpr gain = SExpr("gain",receivedParams.gain());
            SExpr wb = SExpr("white_balance",receivedParams.whitebalance());
            SExpr ftb = SExpr("fade_to_black",receivedParams.fadetoblack());

            s.append(h);
            s.append(v);
            s.append(ae);
            s.append(b);
            s.append(c);
            s.append(sat);
            s.append(hue);
            s.append(sharp);
            s.append(gamma);
            s.append(awb);
            s.append(expo);
            s.append(gain);
            s.append(wb);
            s.append(ftb);

            std::string stringToWrite = s.serialize();

            #ifdef NAOQI_2
                std::cout<<"Saving as V5"<<std::endl;
                if(receivedParams.whichcamera() == "TOP"){
                    std::cout<<"TOP Params Received"<<std::endl;
                    std::ofstream file("/home/nao/nbites/Config/V5topCameraParams.txt");
                    std::cout<<stringToWrite<<std::endl;
                    file << stringToWrite;
                    file.close();
                    std::cout<<"Saving Done"<<std::endl;
                } else  {
                    std::cout<<"Bottom Params Received"<<std::endl;
                    std::ofstream file("/home/nao/nbites/Config/V5bottomCameraParams.txt");
                    std::cout<<stringToWrite<<std::endl;
                    file << stringToWrite;
                    file.close();
                    std::cout<<"Saving Done"<<std::endl;
                }
            #else
                std::cout<<"Saving as V4"<<std::endl;
                if(receivedParams.whichcamera() == "TOP"){
                    std::cout<<"TOP Params Received"<<std::endl;
                    std::ofstream file("/home/nao/nbites/Config/V4topCameraParams.txt");
                    file << stringToWrite;
                    file.close();
                    std::cout<<"Saving Done"<<std::endl;
                } else  {
                    std::cout<<"Bottom Params Received"<<std::endl;
                    std::ofstream file("/home/nao/nbites/Config/V4bottomCameraParams.txt");
                    file << stringToWrite;
                    file.close();
                    std::cout<<"Saving Done"<<std::endl;
                }
            #endif
        }
        return 0;
    }

    uint32_t cnc_setEngineParameters(Log * arg){

        size_t u = arg->data().size();
        bool success = receivedWalkParams.ParseFromString(arg->data());
        if(!success){
            std::cerr << "Failed to parse parameters\n" << std::endl;
        }
        else{

            /*For now im building an sexpr. its possible that it will be better to leave everythind in protobuffs though
             * NO: this is much easier to parse.
             */

            SExpr s;

            /* Making engine sexpr */
            //There are 82 parameters, in case anyone is wondering. BHuman defines 51, but several have multipe parts
            SExpr vectorStandComPos_y = SExpr("vectorStandComPos_y", receivedWalkParams.vectorstandcompos_y());
            SExpr vectorStandComPos_z = SExpr("vectorStandComPos_z", receivedWalkParams.vectorstandcompos_z());
                                               
            SExpr standBodyTilt = SExpr("standBodyTilt", receivedWalkParams.standbodytilt());
                                               
            SExpr vectorStandArmJointAngle_x = SExpr("vectorStandArmJointAngle_x", receivedWalkParams.vectorstandarmjointangle_x());
            SExpr vectorStandArmJointAngle_y = SExpr("vectorStandArmJointAngle_y", receivedWalkParams.vectorstandarmjointangle_y());
                                               
            SExpr standHardnessAnklePitch = SExpr("standHardnessAnklePitch", (int) receivedWalkParams.standhardnessanklepitch());
            SExpr standHardnessAnkleRoll = SExpr("standHardnessAnkleRoll", (int) receivedWalkParams.standhardnessankleroll());
            
            SExpr vectorWalkRef_x = SExpr("vectorWalkRef_x", receivedWalkParams.vectorwalkref_x());
            SExpr vectorWalkRef_y = SExpr("vectorWalkRef_y", receivedWalkParams.vectorwalkref_y());
                                               
            SExpr vectorWalkRefAtFullSpeed_x = SExpr("vectorWalkRefAtFullSpeed_x", receivedWalkParams.vectorwalkrefatfullspeed_x());
            SExpr vectorWalkRefAtFullSpeed_y = SExpr("vectorWalkRefAtFullSpeed_y", receivedWalkParams.vectorwalkrefatfullspeed_y());
                                               
            SExpr rangeWalkRefPlanningLimit_low = SExpr("rangeWalkRefPlanningLimit_low", receivedWalkParams.rangewalkrefplanninglimit_low());
            SExpr rangeWalkRefPlanningLimit_high = SExpr("rangeWalkRefPlanningLimit_high", receivedWalkParams.rangewalkrefplanninglimit_high());
                                               
            SExpr rangeWalkRefXLimit_low = SExpr("rangeWalkRefXLimit_low", receivedWalkParams.rangewalkrefxlimit_low());
            SExpr rangeWalkRefXLimit_high = SExpr("rangeWalkRefXLimit_high", receivedWalkParams.rangewalkrefxlimit_high());
                                               
            SExpr rangeWalkRefYLimit_low = SExpr("rangeWalkRefYLimit_low", receivedWalkParams.rangewalkrefylimit_low());
            SExpr rangeWalkRefYLimit_high = SExpr("rangeWalkRefYLimit_high", receivedWalkParams.rangewalkrefylimit_high());
                                               
            SExpr rangeWalkStepSizeXPlanningLimit_low = SExpr("rangeWalkStepSizeXPlanningLimit_low", receivedWalkParams.rangewalkstepsizexplanninglimit_low());
            SExpr rangeWalkStepSizeXPlanningLimit_high = SExpr("rangeWalkStepSizeXPlanningLimit_high", receivedWalkParams.rangewalkstepsizexplanninglimit_high());
                                               
            SExpr rangeWalkStepSizeXLimit_low = SExpr("rangeWalkStepSizeXLimit_low", receivedWalkParams.rangewalkstepsizexlimit_low());
            SExpr rangeWalkStepSizeXLimit_high = SExpr("rangeWalkStepSizeXLimit_high", receivedWalkParams.rangewalkstepsizexlimit_high());
                                               
            SExpr walkStepDuration = SExpr("walkStepDuration", receivedWalkParams.walkstepduration());
            SExpr walkStepDurationAtFullSpeedX = SExpr("walkStepDurationAtFullSpeedX", receivedWalkParams.walkstepdurationatfullspeedx());
            SExpr walkStepDurationAtFullSpeedY = SExpr("walkStepDurationAtFullSpeedY", receivedWalkParams.walkstepdurationatfullspeedy());
                                               
            SExpr vectorWalkHeight_x = SExpr("vectorWalkHeight_x", receivedWalkParams.vectorwalkheight_x());
            SExpr vectorWalkHeight_y = SExpr("vectorWalkHeight_y", receivedWalkParams.vectorwalkheight_y());
                                               
            SExpr walkArmRotationAtFullSpeedX = SExpr("walkArmRotationAtFullSpeedX", receivedWalkParams.walkarmrotationatfullspeedx());
                                               
            SExpr walkMovePhaseBeginning = SExpr("walkMovePhaseBeginning", receivedWalkParams.walkmovephasebeginning());
            SExpr walkMovePhaseLength = SExpr("walkMovePhaseLength", receivedWalkParams.walkmovephaselength());
                                               
            SExpr walkLiftPhaseBeginning = SExpr("walkLiftPhaseBeginning", receivedWalkParams.walkliftphasebeginning());
            SExpr walkLiftPhaseLength = SExpr("walkLiftPhaseLength", receivedWalkParams.walkliftphaselength());
                                               
            SExpr vectorWalkLiftOffSet_x = SExpr("vectorWalkLiftOffSet_x", receivedWalkParams.vectorwalkliftoffset_x());
            SExpr vectorWalkLiftOffSet_y = SExpr("vectorWalkLiftOffSet_y", receivedWalkParams.vectorwalkliftoffset_y());
            SExpr vectorWalkLiftOffSet_z = SExpr("vectorWalkLiftOffSet_z", receivedWalkParams.vectorwalkliftoffset_z());
                                               
            SExpr vectorWalkLiftOffSetAtFullSpeedX_x = SExpr("vectorWalkLiftOffSetAtFullSpeedX_x", receivedWalkParams.vectorwalkliftoffsetatfullspeedx_x());
            SExpr vectorWalkLiftOffSetAtFullSpeedX_y = SExpr("vectorWalkLiftOffSetAtFullSpeedX_y", receivedWalkParams.vectorwalkliftoffsetatfullspeedx_y());
            SExpr vectorWalkLiftOffSetAtFullSpeedX_z = SExpr("vectorWalkLiftOffSetAtFullSpeedX_z", receivedWalkParams.vectorwalkliftoffsetatfullspeedx_z());
                                               
            SExpr vectorWalkLiftOffSetAtFullSpeedY_x = SExpr("vectorWalkLiftOffSetAtFullSpeedY_x", receivedWalkParams.vectorwalkliftoffsetatfullspeedy_x());
            SExpr vectorWalkLiftOffSetAtFullSpeedY_y = SExpr("vectorWalkLiftOffSetAtFullSpeedY_y", receivedWalkParams.vectorwalkliftoffsetatfullspeedy_y());
            SExpr vectorWalkLiftOffSetAtFullSpeedY_z = SExpr("vectorWalkLiftOffSetAtFullSpeedY_z", receivedWalkParams.vectorwalkliftoffsetatfullspeedy_z());
                                               
            SExpr vectorWalkLiftRotation_x = SExpr("vectorWalkLiftRotation_x", receivedWalkParams.vectorwalkliftrotation_x());
            SExpr vectorWalkLiftRotation_y = SExpr("vectorWalkLiftRotation_y", receivedWalkParams.vectorwalkliftrotation_y());
            SExpr vectorWalkLiftRotation_z = SExpr("vectorWalkLiftRotation_z", receivedWalkParams.vectorwalkliftrotation_z());
                                               
            SExpr walkSupportRotation = SExpr("walkSupportRotation", receivedWalkParams.walksupportrotation());
                                                                
            SExpr walkComLiftOffSet_x = SExpr("walkComLiftOffSet_x", receivedWalkParams.walkcomliftoffset_x());
            SExpr walkComLiftOffSet_y = SExpr("walkComLiftOffSet_y", receivedWalkParams.walkcomliftoffset_y());
            SExpr walkComLiftOffSet_z = SExpr("walkComLiftOffSet_z", receivedWalkParams.walkcomliftoffset_z());
                                                                                                   
            SExpr walkComBodyRotation = SExpr("walkComBodyRotation", receivedWalkParams.walkcombodyrotation());
                                                                                  
            SExpr speedMax_rot = SExpr("speedMax_rot", receivedWalkParams.speedmax_rot());
            SExpr speedMax_Vector_x = SExpr("speedMax_Vector_x", receivedWalkParams.speedmax_vector_x());
            SExpr speedMax_Vector_y = SExpr("speedMax_Vector_y", receivedWalkParams.speedmax_vector_y());
                                                                                  
            SExpr speedMaxBackwards = SExpr("speedMaxBackwards", receivedWalkParams.speedmaxbackwards());
                                                                                                  
            SExpr speedMaxChange_rot = SExpr("speedMaxChange_rot", receivedWalkParams.speedmaxchange_rot());
            SExpr speedMaxChange_Vector_x = SExpr("speedMaxChange_Vector_x", receivedWalkParams.speedmaxchange_vector_x());
            SExpr speedMaxChange_Vector_y = SExpr("speedMaxChange_Vector_y", receivedWalkParams.speedmaxchange_vector_y());
                                                                                  
            SExpr balance = SExpr("balance", receivedWalkParams.balance());
                                                                                  
            SExpr vectorBalanceBodyRotation_x = SExpr("vectorBalanceBodyRotation_x", receivedWalkParams.vectorbalancebodyrotation_x());
            SExpr vectorBalanceBodyRotation_y = SExpr("vectorBalanceBodyRotation_y", receivedWalkParams.vectorbalancebodyrotation_y());
                                                                                  
            SExpr vectorBalanceCom_x = SExpr("vectorBalanceCom_x", receivedWalkParams.vectorbalancecom_x());
            SExpr vectorBalanceCom_y = SExpr("vectorBalanceCom_y", receivedWalkParams.vectorbalancecom_y());
                                                                                  
            SExpr vectorBalanceComVelocity_x = SExpr("vectorBalanceComVelocity_x", receivedWalkParams.vectorbalancecomvelocity_x());
            SExpr vectorBalanceComVelocity_y = SExpr("vectorBalanceComVelocity_y", receivedWalkParams.vectorbalancecomvelocity_y());
                                                                                  
            SExpr vectorBalanceRef_x = SExpr("vectorBalanceRef_x", receivedWalkParams.vectorbalanceref_x());
            SExpr vectorBalanceRef_y = SExpr("vectorBalanceRef_y", receivedWalkParams.vectorbalanceref_y());
                                                                                  
            SExpr vectorBalanceNextRef_x = SExpr("vectorBalanceNextRef_x", receivedWalkParams.vectorbalancenextref_x());
            SExpr vectorBalanceNextRef_y = SExpr("vectorBalanceNextRef_y", receivedWalkParams.vectorbalancenextref_y());
            
            SExpr vectorBalanceStepSize_x = SExpr("vectorBalanceStepSize_x", receivedWalkParams.vectorbalancestepsize_x());
            SExpr vectorBalanceStepSize_y = SExpr("vectorBalanceStepSize_y", receivedWalkParams.vectorbalancestepsize_y());
                                                                                                        
            SExpr observerMeasurementDelay = SExpr("observerMeasurementDelay", receivedWalkParams.observermeasurementdelay());
                                                                                  
            SExpr vectorObserverMeasurementDeviation_x = SExpr("vectorObserverMeasurementDeviation_x", receivedWalkParams.vectorobservermeasurementdeviation_x());
            SExpr vectorObserverMeasurementDeviation_y = SExpr("vectorObserverMeasurementDeviation_y", receivedWalkParams.vectorobservermeasurementdeviation_y());
                                                                                  
            SExpr vectorObserverProcessDeviation_x = SExpr("vectorObserverProcessDeviation_x", receivedWalkParams.vectorobserverprocessdeviation_x());
            SExpr vectorObserverProcessDeviation_y = SExpr("vectorObserverProcessDeviation_y", receivedWalkParams.vectorobserverprocessdeviation_y());
            SExpr vectorObserverProcessDeviation_z = SExpr("vectorObserverProcessDeviation_z", receivedWalkParams.vectorobserverprocessdeviation_z());
            SExpr vectorObserverProcessDeviation_w = SExpr("vectorObserverProcessDeviation_w", receivedWalkParams.vectorobserverprocessdeviation_w());
                                                                                  
            SExpr odometryScale_rot = SExpr("odometryScale_rot", receivedWalkParams.odometryscale_rot());
            SExpr odometryScale_Vector_x = SExpr("odometryScale_Vector_x", receivedWalkParams.odometryscale_vector_x());
            SExpr odometryScale_Vector_y = SExpr("odometryScale_Vector_y", receivedWalkParams.odometryscale_vector_y());
                                                                                  
            SExpr gyroStateGain = SExpr("gyroStateGain", receivedWalkParams.gyrostategain());
            SExpr gyroDerivativeGain = SExpr("gyroDerivativeGain", receivedWalkParams.gyroderivativegain());
            SExpr gyroSmoothing = SExpr("gyroSmoothing", receivedWalkParams.gyrosmoothing());
                                                
            SExpr minRotationToReduceStepSize = SExpr("minRotationToReduceStepSize", receivedWalkParams.minrotationtoreducestepsize());

            /* Appending to main sexpr */
            s.append(vectorStandComPos_y);
            s.append(vectorStandComPos_z);
            s.append(standBodyTilt);
            s.append(vectorStandArmJointAngle_x);
            s.append(vectorStandArmJointAngle_y);
            s.append(standHardnessAnklePitch);
            s.append(standHardnessAnkleRoll);
            s.append(vectorWalkRef_x);
            s.append(vectorWalkRef_y);
            s.append(vectorWalkRefAtFullSpeed_x);
            s.append(vectorWalkRefAtFullSpeed_y);
            s.append(rangeWalkRefPlanningLimit_low);
            s.append(rangeWalkRefPlanningLimit_high);
            s.append(rangeWalkRefXLimit_low);
            s.append(rangeWalkRefXLimit_high);
            s.append(rangeWalkRefYLimit_low);
            s.append(rangeWalkRefYLimit_high);
            s.append(rangeWalkStepSizeXPlanningLimit_low);
            s.append(rangeWalkStepSizeXPlanningLimit_high);
            s.append(rangeWalkStepSizeXLimit_low);
            s.append(rangeWalkStepSizeXLimit_high);
            s.append(walkStepDuration);
            s.append(walkStepDurationAtFullSpeedX);
            s.append(walkStepDurationAtFullSpeedY);
            s.append(vectorWalkHeight_x);
            s.append(vectorWalkHeight_y);
            s.append(walkArmRotationAtFullSpeedX);
            s.append(walkMovePhaseBeginning);
            s.append(walkMovePhaseLength);
            s.append(walkLiftPhaseBeginning);
            s.append(walkLiftPhaseLength);
            s.append(vectorWalkLiftOffSet_x);
            s.append(vectorWalkLiftOffSet_y);
            s.append(vectorWalkLiftOffSet_z);
            s.append(vectorWalkLiftOffSetAtFullSpeedX_x);
            s.append(vectorWalkLiftOffSetAtFullSpeedX_y);
            s.append(vectorWalkLiftOffSetAtFullSpeedX_z);
            s.append(vectorWalkLiftOffSetAtFullSpeedY_x);
            s.append(vectorWalkLiftOffSetAtFullSpeedY_y);
            s.append(vectorWalkLiftOffSetAtFullSpeedY_z);
            s.append(vectorWalkLiftRotation_x);
            s.append(vectorWalkLiftRotation_y);
            s.append(vectorWalkLiftRotation_z);
            s.append(walkSupportRotation);
            s.append(walkComLiftOffSet_x);
            s.append(walkComLiftOffSet_y);
            s.append(walkComLiftOffSet_z);
            s.append(walkComBodyRotation);
            s.append(speedMax_rot);
            s.append(speedMax_Vector_x);
            s.append(speedMax_Vector_y);
            s.append(speedMaxBackwards);
            s.append(speedMaxChange_rot);
            s.append(speedMaxChange_Vector_x);
            s.append(speedMaxChange_Vector_y);
            s.append(balance);
            s.append(vectorBalanceBodyRotation_x);
            s.append(vectorBalanceBodyRotation_y);
            s.append(vectorBalanceCom_x);
            s.append(vectorBalanceCom_y);
            s.append(vectorBalanceComVelocity_x);
            s.append(vectorBalanceComVelocity_y);
            s.append(vectorBalanceRef_x);
            s.append(vectorBalanceRef_y);
            s.append(vectorBalanceNextRef_x);
            s.append(vectorBalanceNextRef_y);
            s.append(vectorBalanceStepSize_x);
            s.append(vectorBalanceStepSize_y);
            s.append(observerMeasurementDelay);
            s.append(vectorObserverMeasurementDeviation_x);
            s.append(vectorObserverMeasurementDeviation_y);
            s.append(vectorObserverProcessDeviation_x);
            s.append(vectorObserverProcessDeviation_y);
            s.append(vectorObserverProcessDeviation_z);
            s.append(vectorObserverProcessDeviation_w);
            s.append(odometryScale_rot);
            s.append(odometryScale_Vector_x);
            s.append(odometryScale_Vector_y);
            s.append(gyroStateGain);
            s.append(gyroDerivativeGain);
            s.append(gyroSmoothing);
            s.append(minRotationToReduceStepSize);

            //std::cout << "[INFO] This is the formatted sexpr to be written to txt: " << std::endl;
            //Printing SExpr so we can check data received
            //std::cout << s.print() << std::endl;

            std::string stringInFile = s.serialize();

        #ifdef NAOQI_2
            std::cout << "[CONTROL] Saving V5 Walk Engine parameters" << std::endl;
            std::ofstream file("/home/nao/nbites/Config/V5WalkEngineParameters.txt");
            //std::cout << stringInFile << std::endl;
            file << stringInFile;
            file.close();
            std::cout << "[CONTROL] Saving Done" << std::endl;
        #else
            std::cout << "[CONTROL] Saving V4 Walk Engine parameters" << std::endl;
            std::ofstream file("/home/nao/nbites/Config/V4WalkEngineParameters.txt");
            //std::cout << stringInFile << std::endl;
            file << stringInFile;
            file.close();
            std::cout << "[CONTROL] Saving Done" << std::endl;

            /* Sanity Check on control thread writing capabilities.*/
            //Notice the ifstream instaed of the ofstream
            /*
            std::ifstream file2("/home/nao/nbites/Config/V4WalkEngineParameters.txt");
            std::cout << "[CONTROL] Managed to open the file just written to" << std::endl;
            std::stringstream ssfile;
            ssfile << file2.rdbuf();
            std::string fileString = ssfile.str();
            std::cout << "Printing filestring in control function thread of file just read:\n" << fileString << std::endl;
            ssize_t i = 0;
            std::cout << "Before calling read function in SExpr file" << std::endl; //Prints normally
            SExpr params = *SExpr::read(fileString, i);        //gdb says segfault happens in this line
            std::cout << "Printing parameters just written (hopefully) to robot .txt file\n" << params.print() << std::endl; //Doesn't print (and robot loses stiffneses and falls down)
            */

        #endif

        //Setting flag for motion module run function.
        newWalkParameters = true;
        }
        return 0;
    }


    /*
     THIS IS WHERE YOU PUT NEW CONTROL FUNCTIONS!
     
     note that the function prototype must exacty match what's listed below.
     */
    std::map<std::string, uint32_t (*)(Log *)> init_fmap() {
        std::map<std::string, uint32_t (*)(Log *)> ret;
        
        ret["test"] = &cnc_test;
        ret["setFlag"] = &cnc_setFlag;
        ret["setCameraParams"] = &cnc_setCameraParams;
        ret["walkingEngineParameterStream"] = &cnc_setEngineParameters;
        
        return ret;
    }
    std::map<std::string, uint32_t (*)(Log *)> fmap = init_fmap();
    
#define CHECK_RET(r) {if (r) goto connection_died;}
    
    void * cnc_loop(void * cntxt) {
        int listenfd = -1, connfd = -1;
        struct sockaddr_in serv_addr;
        
        //Network socket, TCP streaming protocol, default options
        listenfd = socket(AF_INET, SOCK_STREAM, 0);
        NBDEBUG( "control listenfd=[%i]\n", listenfd);
        //memset(&serv_addr, '0', sizeof(serv_addr));
        memset(&serv_addr, 0, sizeof(serv_addr));
        
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
        serv_addr.sin_port = htons(CONTROL_PORT);
        
        bind(listenfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
                
        //Accepting connections on this socket, no queue.
        listen(listenfd, 0);
        
        NBDEBUG("control listening... port = %i\n", CONTROL_PORT);
        
        for (;;) {
            connfd = block_accept(listenfd, CONTROL_USLEEP_WAITING);
            NBDEBUG("control FOUND CLIENT [%i]\n", connfd);
            nblog::cnc_upstart = time(NULL);
            flags[control_connected] = 1;
            
            for (;; usleep(CONTROL_USLEEP_WAITING)) {
                uint32_t ping = 0;
                uint32_t resp;
                
                CHECK_RET(send_exact(connfd, 4, (uint8_t *) &ping) )
                CHECK_RET(recv_exact(connfd, 4, (uint8_t *) &resp, IO_SEC_TO_BREAK))
                
                if (ntohl(resp) == 1) {
                    //cnc call coming in:
                    
                    Log * found = Log::recv(connfd, IO_SEC_TO_BREAK);
                    if (!found)
                        goto connection_died;
                    SExpr& desc = found->tree();
                    
                    if (desc.count() < 2 ||
                        !(desc.get(0)->isAtom()) ||
                        !(desc.get(0)->value() == nblog::COMMAND_FIRST_ATOM_S) ||
                        !(desc.get(1)->isAtom())    //command name
                        ) {
                        NBDEBUG("control: INVALID COMMAND LOG!\n");
                        delete found;
                        goto connection_died;
                    }
                    
                    std::string name = desc.get(1)->value();
                    
                    if (fmap.find(name) == fmap.end()) {
                        NBDEBUG( "control: could not find command[%s] in fmap!\n", name.c_str());
                        delete found;
                        goto connection_died;
                    }
                    uint32_t ret = -1;
                    try {
                        ret = fmap[name](found);
                    } catch (...) {
                        printf("ERROR: Caught exception while running control function %s!\n",
                               name.c_str());
                    }
                    NBDEBUG( "control command returned %i\n", ret);
                    delete found;
                    
                    uint32_t nret = htonl(ret);
                    CHECK_RET(send_exact(connfd, 4, &nret))
                    
                    NBDEBUG( "control call finished.\n\n");
                    
                } else {
                    if (resp != 0) {
                        NBDEBUG( "control got bad ping reply! %i\n", resp);
                        goto connection_died;
                    }
                    
                    usleep(CONTROL_USLEEP_WAITING);
                }
            }
            
            //Acts like an exception.
        connection_died:
            flags[control_connected] = 0;
            close(connfd);
            
            NBDEBUG("control loop broken, connection closed.\n");
        }
        
        return NULL;
    }

    
    void control_init() {
        NBDEBUG("control_init() with %i functions.\n", fmap.size());
        NBLassert(!STARTED);
        STARTED = true;
        
        bzero((void *) flags, num_flags);

        pthread_create(&control_thread, NULL, &cnc_loop, NULL);
        pthread_detach(control_thread);
    }
    
}
