package ms;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;


// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Eye - a robot by Manas Shah
 */
public class Eye extends AdvancedRobot
{
        AdvancedEnemyBot enemy = new AdvancedEnemyBot();
	
        
        String target = "";
        private int gunTurnError = 5;
        private int maxBulletMiss = 7;

	/**
	 * dodgeBullet: Dodge a bullet when it's fired at you.
	 */
    int sideToggle = 1;
    public void dodgeBullet(AdvancedEnemyBot event) {
		// use 'event' object for onScannedRobotEvent and dodge the bullet.
	        setTurnRight(event.getBearing() - 90 + (10 * sideToggle));
                setAhead(50 * sideToggle);
                sideToggle *= -1;
	}
        /**
         * isBulletFired: Check if bullet is fired.
         * Returns: True if bullet fired, False if not.
         */
        double currentEnergy = 100.0;
        public boolean isBulletFired(AdvancedEnemyBot scanEvent) {
                double prevEnergy = currentEnergy; // this basically doesn't work, you need to track individual robots.
                currentEnergy = scanEvent.getEnergy();
                double scanDifference = prevEnergy - currentEnergy;
                if((prevEnergy > currentEnergy) && (scanDifference <= 3.0)) {
                        return true;
                }
                else {
                        return false;
                }
        }
	/**
	 * run: Eye's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		setColors(Color.black, Color.darkGray, Color.white, Color.darkGray, Color.red);
		setAdjustRadarForRobotTurn(true);
		// Robot main loop
		while(true) {
			setTurnRadarRight(360);
                        if (!usePredict) {
                            double turnTo = enemy.getBearing() + getHeading() - getGunHeading();
                            turnTo = (turnTo > 180) ? -1 * (360 - turnTo) : turnTo;
		            setTurnGunRight(turnTo);
                        }
                        else {
                            predictTurnGun(calcTime(enemy));
                        }
		        checkFire(3.0);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		String currentScanned = e.getName();
		//target = (target == "") ? currentScanned : target;
                if (enemy.none() || e.getName().equals(enemy.getName()) || e.getDistance() < enemy.getDistance() - 50 || e.getName().equals(hitBy)) {
                        enemy.update(e, this);
                }
                if (isBulletFired(enemy)) {
        	        dodgeBullet(enemy);
                }
		//if (target != currentScanned) {
                //        execute();
                //        return;
		//}
  //               double turnTo = enemy.getBearing() + getHeading() - getGunHeading();
  //               turnTo = (turnTo > 180) ? -1 * (360 - turnTo) : turnTo;
		// setAdjustGunForRobotTurn(true);
		// setTurnGunRight(turnTo); 
		// setFire(3.0);
		execute();
	}

	public void onRobotDeath(RobotDeathEvent deadBot) {
		//target = (deadBot.getName() == target) ? "" : target;
                if (deadBot.getName().equals(enemy.getName())) {
                        enemy.reset();
                }

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
        private String hitBy = "";
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		//back(10);
                hitBy = e.getName(); 
                sideToggle *= -1;
                setFire(1.1);
                dodgeBullet(enemy); //augment later with enemyBot and input from the class
                execute();

	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}

    private int missCount = 0;
    private boolean usePredict = false;

    public void onBulletMiss(BulletMissedEvent event) {
       // missCount++;
       // if (missCount >= maxBulletMiss) {
       //     usePredict = !usePredict;
       // }
    }

    public void onWin(WinEvent event) {
        for(int i = 0; i <= 36; i++) {
            setTurnRight(30);
            setAhead(2);
            setFire(0.1);
            execute();
        }
    }

    public void checkFire(double firePower) {
        if(getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) <= gunTurnError) setFire(firePower);
    }
        public double normalizeBearing(double angle) {
                while(angle > 180) angle -= 360;
                while(angle < -180) angle += 360;
                return angle;
        }

        public void moveXY(double x, double y) {
                // and then there was trig
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
