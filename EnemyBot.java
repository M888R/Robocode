package ms;
import robocode.*;

public class EnemyBot
{
    private double bearing;
    private double heading;
    private double distance;
    private double velocity;
    private double energy;
    private String name;

    EnemyBot() {
    	reset();
    }
    
    public void reset() {
        bearing = 0.0;
        heading = 0.0;
        distance = 0.0;
        velocity = 0.0;
        energy = 0.0;
        name = "";
    }

    public final void update(ScannedRobotEvent event) {
        bearing = event.getBearing();
        heading = event.getHeading();
        distance = event.getDistance();
        velocity = event.getVelocity();
        energy = event.getEnergy();
        name = event.getName();
    }

    public void changeName(String nameInput) {
        name = nameInput;
    }

    public double getBearing() {
        return bearing;
    }

    public double getHeading() {
        return heading;
    }

    public double getDistance() {
        return distance;
    }

    public double getVelocity() { 
        return velocity;
    }

    public double getEnergy() {
        return energy;
    }

    public String getName() { 
        return name;
    }
    
    public boolean none() {
        return (name == "") ? true : false;
    }
}
