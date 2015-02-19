package nbtool.behavior_sim.src;

import nbtool.io.CppIO;
import nbtool.util.U;

public class BehaviorInterpreter
{
    public BehaviorInterpreter()
    {
        CppIO.ref();
        U.protobufClassFromType("proto-ParticleSwarm");
    }
}