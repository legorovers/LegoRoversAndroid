package com.evolution.joecollenette.legorover;

import lejos.remote.ev3.RemoteRequestEV3;
import lr_evolution.BasicRobot;

/**
 * Created by joecollenette on 01/07/2015.
 */
public class Robot extends BasicRobot{

    public Robot(String address) throws Exception
    {
        super(address);

        try {
            RemoteRequestEV3 brick = getBrick();
        }
        catch(Exception e)
        {
            throw new Exception("Failed to get Brick");
        }
    }
}
