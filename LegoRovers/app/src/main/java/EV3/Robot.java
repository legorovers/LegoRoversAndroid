package EV3;

import android.os.SystemClock;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.remote.ev3.RemoteRequestRegulatedMotor;
import lejos.robotics.RegulatedMotor;

public class Robot extends BasicRobot
{
    RemoteRequestRegulatedMotor motorL;
    RemoteRequestRegulatedMotor motorR;
    RemoteRequestRegulatedMotor motor;
    RemoteRequestPilot pilot;

	EASSUltrasonicSensor uSensor;
	EASSRGBColorSensor cSensor;

    private StringBuilder messages;
	private int stepNo;

    private boolean closed = false;
    private boolean straight = false;

    int ultra_port = 2;
    int color_port = 3;

    int slow_turn = 70;
    int fast_turn = 80;
    int travel_speed = 10;

	private void updateSetNo(int i)
	{
		stepNo = i;
	}

    public Robot()
    {
		stepNo = 0;
		messages = new StringBuilder();

    }

    public void connectToRobot(String address) throws Exception
    {
		messages.setLength(0);

        connect(address);
		closed = false;
        RemoteRequestEV3 brick = getBrick();


        String ultra_portstring = "S" + ultra_port;
        String color_portstring = "S" + color_port;

        try {
            messages.append("Connecting to Ultrasonic Sensor" + '\n');
            uSensor = new EASSUltrasonicSensor(brick, ultra_portstring);
            messages.append("Connected to Sensor " + '\n');
            setSensor(ultra_port, uSensor);
            stepNo++;
        } catch (Exception e) {
            brick.disConnect();
            stepNo++;
            throw e;
        }


        try {
            messages.append("Connecting to Colour Sensor " + '\n');
            cSensor = new EASSRGBColorSensor(brick, color_portstring);
            messages.append("Connected to Sensor " + '\n');
            stepNo++;
            setSensor(color_port, cSensor);
        } catch (Exception e) {
            uSensor.close();
            brick.disConnect();
            stepNo++;
            throw e;
        }

        try {
            messages.append("Creating Pilot " + '\n');
            // Creating motors as well as pilot in order to allow turning on the spot.
            motorR = (RemoteRequestRegulatedMotor) brick.createRegulatedMotor("B", 'L');
            motorL = (RemoteRequestRegulatedMotor) brick.createRegulatedMotor("C", 'L');
            motorR.setSpeed(200);
            motorL.setSpeed(200);
            pilot = (RemoteRequestPilot) brick.createPilot(7, 20, "C", "B " + '\n');
            messages.append("Created Pilot " + '\n');
            stepNo++;
        } catch (Exception e) {
            uSensor.close();
            cSensor.close();
            brick.disConnect();
            stepNo++;
            throw e;
        }

        try {
            messages.append("Contacting Medium Motor " + '\n');
            motor = (RemoteRequestRegulatedMotor) brick.createRegulatedMotor("A", 'M');
            messages.append("Created Medium Motor " + '\n');
            stepNo++;
        } catch (Exception e) {
            //uSensor.close();
            //cSensor.close();
            motorR.close();
            motorL.close();
            brick.disConnect();
            stepNo++;
            throw e;
        }
    }

    public void close() {
		messages.setLength(0);
        updateSetNo(0);
        if (! closed) {
            super.disconnected = true;
            try {
                motor.stop();
				messages.append("   Closing Jaw Motor " + '\n');
				motor.close();

				stepNo++;
                SystemClock.sleep(10);
				motorR.stop();
				motorL.stop();
				messages.append("   Closing Right Motor " + '\n');
				motorR.close();

				stepNo++;
                SystemClock.sleep(10);
				messages.append("   Closing Left Motor " + '\n');
				motorL.close();

				stepNo++;
                SystemClock.sleep(10);
                pilot.stop();
                messages.append("   Closing Pilot " + '\n');
                pilot.close();
                SystemClock.sleep(10);
            } catch (Exception e) {

            }
            messages.append("   Closing Remaining Sensors" + '\n');
			super.close();
			stepNo++;
        }
        closed = true;
    }


    /**
     * Get the medium motor that control the dinosaur's jaws.
     * @return
     */
    public RegulatedMotor getMotor() {
        return motor;
    }

    /**
     * Move forward
     */
    public void forward() {
        pilot.setTravelSpeed(travel_speed);
        if (!straight) {
            straight = true;
        }

        pilot.forward();
    }

    /**
     * Move forward a short distance.
     */
    public void short_forward() {
        pilot.setTravelSpeed(travel_speed);
        if (!straight) {
            straight = true;
        }
        pilot.travel(10);
    }


    /**
     * Move backward
     */
    public void backward() {
        pilot.setTravelSpeed(travel_speed);
        if (!straight) {
            straight = true;
        }
        pilot.backward();
    }

    /**
     * Move backward a short distance.
     */
    public void short_backward() {
        pilot.setTravelSpeed(travel_speed);
        if (!straight) {
            straight = true;
        }
        pilot.travel(-10);
    }

    /**
     * Stop.
     */
    public void stop() {
        pilot.stop();
    }

    /**
     * Turn left on the spot.
     */
    public void left() {
        motorR.setSpeed(fast_turn);
        motorL.setSpeed(fast_turn);
        motorR.backward();
        motorL.forward();
        straight = false;
    }

    /**
     * Turn left through an angle (approx 90 on the wheeled robots).
     */
    public void short_left() {
        pilot.setRotateSpeed(travel_speed);
        pilot.rotate(-90);
        straight = false;
    }

    /**
     * Move left around stopped wheel.
     */
    public void forward_left() {
        motorL.setSpeed(slow_turn);
        motorL.forward();
        motorR.stop();
        straight = false;
    }

    /**
     * Turn right on the spot.
     */
    public void right() {
        motorR.setSpeed(fast_turn);
        motorL.setSpeed(fast_turn);
        motorR.forward();
        motorL.backward();
        straight = false;
    }

    /**
     * Turn a short distance right (approx 90 on a wheeled robot)
     */
    public void short_right() {
        pilot.setRotateSpeed(travel_speed);
        pilot.rotate(90);
        straight = false;
    }


    /**
     * Turn right around stopped left whell.
     */
    public void forward_right() {
        motorR.setSpeed(slow_turn);
        motorR.forward();
        motorL.stop();
        straight = false;
    }

    /**
     * Snap jaws to scare something.
     */
    public void scare() {
        int pos = motor.getTachoCount();
        motor.rotateTo(pos + 20);
        motor.waitComplete();
        motor.rotateTo(pos);
        motor.waitComplete();
        motor.rotateTo(pos + 20);
        motor.waitComplete();
        motor.rotateTo(pos);
        motor.waitComplete();
    }

    /**
     * Rotate right motor some angle.
     * @param d
     */
    public void turn(int d) {
        motorR.rotate(d);
    }

    public EASSRGBColorSensor getRGBSensor() {
        return cSensor;
    }

	public EASSUltrasonicSensor getuSensor()
	{
		return uSensor;
	}

	public String getMessages()
	{
		return messages.toString();
	}

	public int getStepNo()
	{
		return stepNo;
	}
}
