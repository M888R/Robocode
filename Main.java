package ms;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

public class Main extends AdvancedRobot
{
    private AdvancedEnemyBot enemy = new AdvancedEnemyBot();
    private int gunTurnError = 5;
    private byte scanFlip = 1;
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.red,Color.blue,Color.green); // body,gun,radar
                setAdjustRadarForRobotTurn(true);
                enemy.reset();
		while(true) {
                        if (enemy.getName() == "") {
                                setTurnRadarRight(360);
                        }
                        else {
                                double radarTurn = getHeading() - getRadarHeading() + enemy.getBearing();
                                radarTurn += 25 * scanFlip;
                                setTurnRadarRight(radarTurn);
                                scanFlip *= -1;

                        
		                        //setTurnGunRight(normalizeBearing(enemy.getBearing() + getHeading() - getGunHeading()));
                                predictTurnGun(calcTime(enemy));
		                        checkFire(Math.min(400 / ((enemy.getDistance() != 0.0) ? enemy.getDistance() : 400), 3));
                        }
                        execute();
                }
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
                if (enemy.none() || e.getName().equals(enemy.getName()) || e.getDistance() < enemy.getDistance()) {
                    enemy.update(e, this);
                }
	}

    public void onRobotDeath(RobotDeathEvent event) { 
                if (event.getName().equals(enemy.getName())) {
                    enemy.reset();
                }
    }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}
        
    public void checkFire(double firePower) {
        if(getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) <= gunTurnError) setFire(firePower);
    }

    public double normalizeBearing(double angle) {
        while(angle > 180) angle -= 360;
        while(angle < -180) angle += 360;
        return angle;
    }

    public long calcTime(AdvancedEnemyBot enemy) {
        // calculate firepower based on distance
        double firePower = Math.min(500 / enemy.getDistance(), 3);
        // calculate speed of bullet
        double bulletSpeed = 20 - firePower * 3;
        // distance = rate * time, solved for time
        long time = (long)(enemy.getDistance() / bulletSpeed);

        return time;
    }

    public void predictTurnGun(long time) {
        // calculate gun turn to predicted x,y location
        double futureX = enemy.getFutureX(time);
        double futureY = enemy.getFutureY(time);
        double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
        // turn the gun to the predicted x,y location
        setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
    }

    public double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }
}
