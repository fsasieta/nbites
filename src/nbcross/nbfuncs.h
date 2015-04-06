//
//  nbfuncs.h
//  nbcross
//
//  Created by Philip Koch on 11/28/14.
//

#ifndef __nbcross__nbfuncs__
#define __nbcross__nbfuncs__

#include <stdio.h>
#include <iostream>
#include <vector>

#include "../man/log/logio.h"

#include "Common.h"
#include "RoboGrams.h"

#include "GameState.pb.h"
#include "BallModel.pb.h"
#include "LedCommand.pb.h"
#include "WorldModel.pb.h"
#include "PMotion.pb.h"
#include "MotionStatus.pb.h"
#include "VisionField.pb.h"
#include "VisionRobot.pb.h"
#include "ButtonState.pb.h"
#include "FallStatus.pb.h"
#include "RobotLocation.pb.h"
#include "StiffnessControl.pb.h"
#include "Obstacle.pb.h"

typedef struct {
    const char * name;
    int (*func)(void) ;
    
    std::vector<const char * > args;
} nbfunc_t;

extern std::vector<nbfunc_t> FUNCS;

extern std::vector<logio::log_t> args;
extern std::vector<logio::log_t> rets;

void register_funcs();
void check_arguments(int func_index); //Checks that the arguments in <std::vector args> match
        //those listed in the func declaration FUNCS[func_index]

#endif /* defined(__nbcross__nbfuncs__) */
