#include "Log.h"
#include "exactio.h"
#include "nbdebug.h"
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
#include <exception>
#include <iostream>


using nblog::SExpr;
using nblog::Log;

namespace control {
    
    pthread_t control_thread;
    static bool STARTED = false;
    
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

    uint32_t cnc_setEngineParameters(Log * arg){

        size_t u = arg->data().size();
        bool success = receivedParams.ParseFromString(arg->data());
        if(!success){
            std::cerr << "Failed to parse parameters\n" << std::endl;
        }
        else{

            /*For now im building an sexpr. its possible that it will be better to leave everythind in protobuffs though
             */
            //std::cout << "These are the serialized bytes received (in binary):\n " << arg->data() << std::endl;

            SExpr s;

            /* Making engine sexpr */
            //There are 82 parameters, in case anyone is wondering
            SExpr vectorStandComPos_y = SExpr("vectorStandComPos_y", receivedParams.vectorstandcompos_y());
            SExpr vectorStandComPos_z = SExpr("vectorStandComPos_z", receivedParams.vectorstandcompos_z());
                                               
            SExpr standBodyTilt = SExpr("standBodyTilt", receivedParams.standbodytilt());
                                               
            SExpr vectorStandArmJointAngle_x = SExpr("vectorStandArmJointAngle_x", receivedParams.vectorstandarmjointangle_x());
            SExpr vectorStandArmJointAngle_y = SExpr("vectorStandArmJointAngle_y", receivedParams.vectorstandarmjointangle_y());
                                               
            SExpr standHardnessAnklePitch = SExpr("standHardnessAnklePitch", (int) receivedParams.standhardnessanklepitch());
            SExpr standHardnessAnkleRoll = SExpr("standHardnessAnkleRoll", (int) receivedParams.standhardnessankleroll());
                                               
            SExpr vectorWalkRef_x = SExpr("vectorWalkRef_x", receivedParams.vectorwalkref_x());
            SExpr vectorWalkRef_y = SExpr("vectorWalkRef_y", receivedParams.vectorwalkref_y());
                                               
            SExpr vectorWalkRefAtFullSpeed_x = SExpr("vectorWalkRefAtFullSpeed_x", receivedParams.vectorwalkrefatfullspeed_x());
            SExpr vectorWalkRefAtFullSpeed_y = SExpr("vectorWalkRefAtFullSpeed_y", receivedParams.vectorwalkrefatfullspeed_y());
                                               
            SExpr rangeWalkRefPlanningLimit_low = SExpr("rangeWalkRefPlanningLimit_low", receivedParams.rangewalkrefplanninglimit_low());
            SExpr rangeWalkRefPlanningLimit_high = SExpr("rangeWalkRefPlanningLimit_high", receivedParams.rangewalkrefplanninglimit_high());
                                               
            SExpr rangeWalkRefXLimit_low = SExpr("rangeWalkRefXLimit_low", receivedParams.rangewalkrefxlimit_low());
            SExpr rangeWalkRefXLimit_high = SExpr("rangeWalkRefXLimit_high", receivedParams.rangewalkrefxlimit_high());
                                               
            SExpr rangeWalkRefYLimit_low = SExpr("rangeWalkRefYLimit_low", receivedParams.rangewalkrefylimit_low());
            SExpr rangeWalkRefYLimit_high = SExpr("rangeWalkRefYLimit_high", receivedParams.rangewalkrefylimit_high());
                                               
            SExpr rangeWalkStepSizeXPlanningLimit_low = SExpr("rangeWalkStepSizeXPlanningLimit_low", receivedParams.rangewalkstepsizexplanninglimit_low());
            SExpr rangeWalkStepSizeXPlanningLimit_high = SExpr("rangeWalkStepSizeXPlanningLimit_high", receivedParams.rangewalkstepsizexplanninglimit_high());
                                               
            SExpr rangeWalkStepSizeXLimit_low = SExpr("rangeWalkStepSizeXLimit_low", receivedParams.rangewalkstepsizexlimit_low());
            SExpr rangeWalkStepSizeXLimit_high = SExpr("rangeWalkStepSizeXLimit_high", receivedParams.rangewalkstepsizexlimit_high());
                                               
            SExpr walkStepDuration = SExpr("walkStepDuration", receivedParams.walkstepduration());
            SExpr walkStepDurationAtFullSpeedX = SExpr("walkStepDurationAtFullSpeedX", receivedParams.walkstepdurationatfullspeedx());
            SExpr walkStepDurationAtFullSpeedY = SExpr("walkStepDurationAtFullSpeedY", receivedParams.walkstepdurationatfullspeedy());
                                               
            SExpr vectorWalkHeight_x = SExpr("vectorWalkHeight_x", receivedParams.vectorwalkheight_x());
            SExpr vectorWalkHeight_y = SExpr("vectorWalkHeight_y", receivedParams.vectorwalkheight_y());
                                               
            SExpr walkArmRotationAtFullSpeedX = SExpr("walkArmRotationAtFullSpeedX", receivedParams.walkarmrotationatfullspeedx());
                                               
            SExpr walkMovePhaseBeginning = SExpr("walkMovePhaseBeginning", receivedParams.walkmovephasebeginning());
            SExpr walkMovePhaseLength = SExpr("walkMovePhaseLength", receivedParams.walkmovephaselength());
                                               
            SExpr walkLiftPhaseBeginning = SExpr("walkLiftPhaseBeginning", receivedParams.walkliftphasebeginning());
            SExpr walkLiftPhaseLength = SExpr("walkLiftPhaseLength", receivedParams.walkliftphaselength());
                                               
            SExpr vectorWalkLiftOffSet_x = SExpr("vectorWalkLiftOffSet_x", receivedParams.vectorwalkliftoffset_x());
            SExpr vectorWalkLiftOffSet_y = SExpr("vectorWalkLiftOffSet_y", receivedParams.vectorwalkliftoffset_y());
            SExpr vectorWalkLiftOffSet_z = SExpr("vectorWalkLiftOffSet_z", receivedParams.vectorwalkliftoffset_z());
                                               
            SExpr vectorWalkLiftOffSetAtFullSpeedX_x = SExpr("vectorWalkLiftOffSetAtFullSpeedX_x", receivedParams.vectorwalkliftoffsetatfullspeedx_x());
            SExpr vectorWalkLiftOffSetAtFullSpeedX_y = SExpr("vectorWalkLiftOffSetAtFullSpeedX_y", receivedParams.vectorwalkliftoffsetatfullspeedx_y());
            SExpr vectorWalkLiftOffSetAtFullSpeedX_z = SExpr("vectorWalkLiftOffSetAtFullSpeedX_z", receivedParams.vectorwalkliftoffsetatfullspeedx_z());
                                               
            SExpr vectorWalkLiftOffSetAtFullSpeedY_x = SExpr("vectorWalkLiftOffSetAtFullSpeedY_x", receivedParams.vectorwalkliftoffsetatfullspeedy_x());
            SExpr vectorWalkLiftOffSetAtFullSpeedY_y = SExpr("vectorWalkLiftOffSetAtFullSpeedY_y", receivedParams.vectorwalkliftoffsetatfullspeedy_y());
            SExpr vectorWalkLiftOffSetAtFullSpeedY_z = SExpr("vectorWalkLiftOffSetAtFullSpeedY_z", receivedParams.vectorwalkliftoffsetatfullspeedy_z());
                                               
            SExpr vectorWalkLiftRotation_x = SExpr("vectorWalkLiftRotation_x", receivedParams.vectorwalkliftrotation_x());
            SExpr vectorWalkLiftRotation_y = SExpr("vectorWalkLiftRotation_y", receivedParams.vectorwalkliftrotation_y());
            SExpr vectorWalkLiftRotation_z = SExpr("vectorWalkLiftRotation_z", receivedParams.vectorwalkliftrotation_z());
                                               
            SExpr walkSupportRotation = SExpr("walkSupportRotation", receivedParams.walksupportrotation());
                                                                
            SExpr walkComLiftOffSet_x = SExpr("walkComLiftOffSet_x", receivedParams.walkcomliftoffset_x());
            SExpr walkComLiftOffSet_y = SExpr("walkComLiftOffSet_y", receivedParams.walkcomliftoffset_y());
            SExpr walkComLiftOffSet_z = SExpr("walkComLiftOffSet_z", receivedParams.walkcomliftoffset_z());
                                                                                                   
            SExpr walkComBodyRotation = SExpr("walkComBodyRotation", receivedParams.walkcombodyrotation());
                                                                                  
            SExpr speedMax_rot = SExpr("speedMax_rot", receivedParams.speedmax_rot());
            SExpr speedMax_Vector_x = SExpr("speedMax_Vector_x", receivedParams.speedmax_vector_x());
            SExpr speedMax_Vector_y = SExpr("speedMax_Vector_y", receivedParams.speedmax_vector_y());
                                                                                  
            SExpr speedMaxBackwards = SExpr("speedMaxBackwards", receivedParams.speedmaxbackwards());
                                                                                                  
            SExpr speedMaxChange_rot = SExpr("speedMaxChange_rot", receivedParams.speedmaxchange_rot());
            SExpr speedMaxChange_Vector_x = SExpr("speedMaxChange_Vector_x", receivedParams.speedmaxchange_vector_x());
            SExpr speedMaxChange_Vector_y = SExpr("speedMaxChange_Vector_y", receivedParams.speedmaxchange_vector_y());
                                                                                  
            SExpr balance = SExpr("balance", receivedParams.balance());
                                                                                  
            SExpr vectorBalanceBodyRotation_x = SExpr("vectorBalanceBodyRotation_x", receivedParams.vectorbalancebodyrotation_x());
            SExpr vectorBalanceBodyRotation_y = SExpr("vectorBalanceBodyRotation_y", receivedParams.vectorbalancebodyrotation_y());
                                                                                  
            SExpr vectorBalanceCom_x = SExpr("vectorBalanceCom_x", receivedParams.vectorbalancecom_x());
            SExpr vectorBalanceCom_y = SExpr("vectorBalanceCom_y", receivedParams.vectorbalancecom_y());
                                                                                  
            SExpr vectorBalanceComVelocity_x = SExpr("vectorBalanceComVelocity_x", receivedParams.vectorbalancecomvelocity_x());
            SExpr vectorBalanceComVelocity_y = SExpr("vectorBalanceComVelocity_y", receivedParams.vectorbalancecomvelocity_y());
                                                                                  
            SExpr vectorBalanceRef_x = SExpr("vectorBalanceRef_x", receivedParams.vectorbalanceref_x());
            SExpr vectorBalanceRef_y = SExpr("vectorBalanceRef_y", receivedParams.vectorbalanceref_y());
                                                                                  
            SExpr vectorBalanceNextRef_x = SExpr("vectorBalanceNextRef_x", receivedParams.vectorbalancenextref_x());
            SExpr vectorBalanceNextRef_y = SExpr("vectorBalanceNextRef_y", receivedParams.vectorbalancenextref_y());
                                                                                                        
            SExpr vectorBalanceStepSize_x = SExpr("vectorBalanceStepSize_x", receivedParams.vectorbalancestepsize_x());
            SExpr vectorBalanceStepSize_y = SExpr("vectorBalanceStepSize_y", receivedParams.vectorbalancestepsize_y());
                                                                                                        
            SExpr observerMeasurementDelay = SExpr("observerMeasurementDelay", receivedParams.observermeasurementdelay());
                                                                                  
            SExpr vectorObserverMeasurementDeviation_x = SExpr("vectorObserverMeasurementDeviation_x", receivedParams.vectorobservermeasurementdeviation_x());
            SExpr vectorObserverMeasurementDeviation_y = SExpr("vectorObserverMeasurementDeviation_y", receivedParams.vectorobservermeasurementdeviation_y());
                                                                                  
            SExpr vectorObserverProcessDeviation_x = SExpr("vectorObserverProcessDeviation_x", receivedParams.vectorobserverprocessdeviation_x());
            SExpr vectorObserverProcessDeviation_y = SExpr("vectorObserverProcessDeviation_y", receivedParams.vectorobserverprocessdeviation_y());
            SExpr vectorObserverProcessDeviation_z = SExpr("vectorObserverProcessDeviation_z", receivedParams.vectorobserverprocessdeviation_z());
            SExpr vectorObserverProcessDeviation_w = SExpr("vectorObserverProcessDeviation_w", receivedParams.vectorobserverprocessdeviation_w());
                                                                                  
            SExpr odometryScale_rot = SExpr("odometryScale_rot", receivedParams.odometryscale_rot());
            SExpr odometryScale_Vector_x = SExpr("odometryScale_Vector_x", receivedParams.odometryscale_vector_x());
            SExpr odometryScale_Vector_y = SExpr("odometryScale_Vector_y", receivedParams.odometryscale_vector_y());
                                                                                  
            SExpr gyroStateGain = SExpr("gyroStateGain", receivedParams.gyrostategain());
            SExpr gyroDerivativeGain = SExpr("gyroDerivativeGain", receivedParams.gyroderivativegain());
            SExpr gyroSmoothinge = SExpr("gyroSmoothing", receivedParams.gyrosmoothing());
                                                
            SExpr minRotationToReduceStepSize = SExpr("minRotationToReduceStepSize", receivedParams.minrotationtoreducestepsize());

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
            s.append(gyroSmoothinge);
            s.append(minRotationToReduceStepSize);

            std::cout << "[INFO] This is the formatted sexpr: " << std::endl;

            //Printing SExpr so we can check data received
            std::cout << s.print();
            std::string stringInFile = s.serialize();

        #ifdef NAOQI_2
            std::cout << "Saving V5 Walk Engine parameters" << std::endl;
            std::ofstream file("home/nao/nbites/Config/V5WalkEngineParameters");
            std::cout << stringInFile << std::endl;
            stringInFile >> file;
            file.close();
            std::cout << "Saving Done" << std::endl;
        #else
            std::cout << "Saving V4 Walk Engine parameters" << std::endl;
            std::ofstream file("home/nao/nbites/Config/V4WalkEngineParameters");
            std::cout << stringInFile << std::endl;
            stringInFile >> file;
            file.close();
            std::cout << "Saving Done" << std::endl;
        #endif


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
                        !(desc.get(0)->value() == "command") ||
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
